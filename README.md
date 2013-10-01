##MeshTopology
============

Sample topology to demonstrate a mesh in storm

What we try to implement is a mesh structured topology which would look like the diagram below.

![mesh](http://mithunsatheesh.files.wordpress.com/2013/10/mesh.png)

###How the topology works

####1. Spout

Spout implementation here emits a random path containing the possible bolts to follow through the topology. An example path message would like “[1,2,3]“

The emitted message goes to the main decider bolt.

####2. Main Decider

The main Decider bolt receives the message from the spout and pops the last element from the path message received and emits to the bolt corresponding to the poped element from path message. So in case of “[1,2,3]“, “3″ is poped from message and the remaining message “[1,2]” is sent to the bolt3.

####3. Sub bolts

Sub bolts receives the message from the main decider and checks if the received path message is empty. If empty then it emits to the output bolt as the path is completed. If still elements are remaining in the path array then again last element is poped and then the rest of message is sent to the corresponding bolt.

####4. Output Bolt

Once the path is traversed on the mesh and the path array becomes empty, the flow goes to the output bolt which is the end of topology.
