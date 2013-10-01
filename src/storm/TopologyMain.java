package storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;



/**
* Mesh Toplogy Sample
*/
public class TopologyMain {
			
	public static void main(String[] args) {			
		
		
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
		
		objTopologyBuilder.setBolt("Bolt3", new Bolt3(),1)
				.shuffleGrouping("Bolt1","StreamBolt3")
				.shuffleGrouping("Bolt2","StreamBolt3")
				.shuffleGrouping("Bolt4","StreamBolt3")
				.shuffleGrouping("Bolt5","StreamBolt3")
				.shuffleGrouping("MainDecider","StreamBolt3");
		
		objTopologyBuilder.setBolt("Bolt4", new Bolt4(),1)
				.shuffleGrouping("Bolt1","StreamBolt4")
				.shuffleGrouping("Bolt2","StreamBolt4")
				.shuffleGrouping("Bolt3","StreamBolt4")
				.shuffleGrouping("Bolt5","StreamBolt4")
				.shuffleGrouping("MainDecider","StreamBolt4");
		
		objTopologyBuilder.setBolt("Bolt5", new Bolt5(),1)
				.shuffleGrouping("Bolt1","StreamBolt5")
				.shuffleGrouping("Bolt2","StreamBolt5")
				.shuffleGrouping("Bolt3","StreamBolt5")
				.shuffleGrouping("Bolt4","StreamBolt5")
				.shuffleGrouping("MainDecider","StreamBolt4");
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
		Config  objStormConfig = new Config();
		objStormConfig.setDebug(false);
		
		/**
	     * Submit to local cluster - development mode
	     */
		LocalCluster objLocalCluster = new LocalCluster();
		objLocalCluster.submitTopology("MeshTopology", objStormConfig, objTopologyBuilder.createTopology());
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		objLocalCluster.shutdown();
				
		
		
		
	}

}






