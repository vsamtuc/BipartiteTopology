package bistro.engine.thread.operators;

import bistro.GenericWrapper;
import bistro.NodeInstance;
import bistro.engine.thread.ThreadNetwork;
import bistro.engine.thread.ThreadNode;
import bistro.sites.NodeId;

import java.util.ArrayList;

public class ThreadHub<RIfc,QIfc> extends ThreadNode<RIfc, QIfc> implements Hub {

    /**
     * Create a thread-based wrapper for a node instance.
     *
     * @param nodeId  the node id of the wrapped node
     * @param node    the wrapped node
     * @param network the network
     */

    ArrayList<ThreadSpoke> spokes;


    public ThreadHub(NodeId nodeId, NodeInstance<RIfc,QIfc> node, ThreadNetwork network)  {
        super(nodeId, node, network);
        this.spokes=new ArrayList<ThreadSpoke>();

    }




}
