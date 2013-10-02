
Here we would discuss a sample implementation of a demo storm topology that tries to make a mesh, which would be different from the normal linear topology representations that we would see in storm starter samples.

What we try to implement is a mesh structured topology which would look similar to the diagram below.
![mesh](http://mithunsatheesh.files.wordpress.com/2013/10/mesh.png)
We may demo the same concept using a topology in which each message from the spout travels through its own path as specified in it self. When the message reaches each bolt it check its path contained and sends it to the next bolt defined in the path. The components of demo topology is explained below with the relevant code. The code shows the implementation of a mesh topology which has 5 sub bolts inter connected to each other.

####1. Mesh Topology

In the topology random paths gets generated from a path generator bolt and gets emitted to a main decider bolt. Main decider takes the path from the message and figures out the first bolt in the path and emits to it. Sub bolts will continue to same process of emitting to the next bolt until the path array defined gets completely traversed. Once the path is completely traversed one of the sub bolt emits it to the output bolt for termination and may be write some output of operation performed.

```java

/**
* Build the topology
*/
TopologyBuilder objTopologyBuilder = new TopologyBuilder();

/**
* Generate random paths and feed the topology
*/
objTopologyBuilder.setSpout("PathSpout", new PathSpout());

/**
* Main decider bolt which maps to first sub bolt in path.
*/
objTopologyBuilder.setBolt("MainDecider", new BoltDecider(),1)
.shuffleGrouping("PathSpout");

/**
* Each sub bolt subscribes to all other sub bolts plus the main decider
*/
objTopologyBuilder.setBolt("Bolt1", new Bolt1(),1)
.shuffleGrouping("Bolt2","StreamBolt1")
.shuffleGrouping("Bolt3","StreamBolt1")
.shuffleGrouping("Bolt4","StreamBolt1")
.shuffleGrouping("Bolt5","StreamBolt1")
.shuffleGrouping("MainDecider","StreamBolt1");

objTopologyBuilder.setBolt("Bolt2", new Bolt2(),1)
.shuffleGrouping("Bolt1","StreamBolt2")
.shuffleGrouping("Bolt3","StreamBolt2")
.shuffleGrouping("Bolt4","StreamBolt2")
.shuffleGrouping("Bolt5","StreamBolt2")
.shuffleGrouping("MainDecider","StreamBolt2");

/**
* this has to repeat for each bolt till Bolt5
*.............................................
*/

/** 
* Output writer bolt subscribes to all the sub bolts 
*/
objTopologyBuilder.setBolt("OutputWriter", new BoltFinalOutput(),1)
.shuffleGrouping("Bolt1","StreamDbWriter")
.shuffleGrouping("Bolt2","StreamDbWriter")
.shuffleGrouping("Bolt3","StreamDbWriter")
.shuffleGrouping("Bolt4","StreamDbWriter")
.shuffleGrouping("Bolt5","StreamDbWriter");

/**
* setup the Storm configuration configuration object
*/
Config objStormConfig = new Config();
objStormConfig.setDebug(false);

/**
* Submit to local cluster - development mode
*/
LocalCluster objLocalCluster = new LocalCluster();
objLocalCluster.submitTopology("MeshTopology", objStormConfig, objTopologyBuilder.createTopology());

```

####2. Random path Spout

Spout implementation here emits a random path containing the possible bolts to follow through the topology. An example path message would like "[1,2,3]"

The emitted message goes to the main decider bolt.

```java
@Override
public void nextTuple() {
     Utils.sleep(100);
      String[] sentences = new String[] {
           "[1,2,3,4,5]",
           "[1,3,2,4,5]",
           "[1,5,3,4,2]",
           "[1,2,5,4,3]",
           "[1,4,3,2,5]",
           "[2,1,4,3,5]",
           "[2,1,5,3,4]",
           "[3,1,4,2,5]",
           "[3,1,5,2,4]",
           "[4,1,3,2,5]",
           "[4,1,5,2,4]",
           "[5,4,3,2,1]",
           "[5,4,1,2,3]"
      };
      String sentence = sentences[_rand.nextInt(sentences.length)];
      _collector.emit(new Values(sentence));
}
```

####3. Main Decider

The main Decider bolt receives the message from the spout and pops the last element from the path message received and emits to the bolt corresponding to the poped element from path message. So in case of "[1,2,3]", "3" is poped from message and the remaining message "[1,2]" is sent to the bolt3.

```java
@Override
public void execute(Tuple input) {	

	JSONArray data;	
	try {		
	     data = new JSONArray(input.getString(0));
	} catch (JSONException e) {
	     return;
	}
	
	/**
	* First Bolt to which we need to emit as per the path 
	*/
	String nextBolt = data.optString(data.length()-1);
	
	/**
	* remove first path from the array 
	*/
	data.remove(data.length()-1);
	System.err.println(data.toString());
	this.objCollector.emit("StreamBolt"+nextBolt,new Values(data.toString()));			

}
```

####4. Sub bolts

Sub bolts receives the message from the main decider and checks if the received path message is empty. If empty then it emits to the output bolt as the path is completed. If still elements are remaining in the path array then again last element is poped and then the rest of message is sent to the corresponding bolt.

```java
@Override
public void execute(Tuple tuple) {  	    	
	
	JSONArray data;	
	try {		
		data = new JSONArray(tuple.getString(0));
	} catch (JSONException e) {
		return;
	}		
	String nextBolt = data.optString(data.length()-1);
	data.remove(data.length()-1);
	System.err.println(data.toString());
	
	if(data.length()-1 == 0) {
		
		/**
		* Path completed so go to output bolt and terminate
		*/
		this.objCollector.emit("StreamDbWriter",new Values("output"));	
		
	} else {
	
		/**
		* emit to next bolt in path
		*/
		this.objCollector.emit("StreamBolt"+nextBolt,new Values(data.toString()));	
	
	}
	
}
```

####5. Output Bolt

Once the path is traversed on the mesh and the path array becomes empty, the flow goes to the output bolt which is the end of topology.
