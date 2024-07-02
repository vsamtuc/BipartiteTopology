package BipartiteTopologyAPI.engine.serial;

import BipartiteTopologyAPI.sites.NodeId;

import BipartiteTopologyAPI.GenericWrapper;
import BipartiteTopologyAPI.NodeInstance;

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
