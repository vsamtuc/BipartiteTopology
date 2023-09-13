package bistro.engine.serial;

import bistro.interfaces.Node;
import bistro.sites.NodeId;

import java.util.concurrent.LinkedBlockingDeque;

import bistro.GenericWrapper;
import bistro.NodeInstance;
import bistro.engine.thread.ThreadNetwork;

public class SerialNode<RIfc, QIfc> extends GenericWrapper {
   
    /**
     * Create a thread-based wrapper for a node instance.
     * 
     * @param nodeId    the node id of the wrapped node
     * @param node      the wrapped node
     * @param network   the network
     */
    public SerialNode(NodeId nodeId, NodeInstance<RIfc, QIfc> node, SerialNetwork network) {
        super(nodeId, node, network);
    }


}
