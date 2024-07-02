package BipartiteTopologyAPI.interfaces;

import java.io.Serializable;
import java.util.Map;

import BipartiteTopologyAPI.operations.RemoteCallIdentifier;
import BipartiteTopologyAPI.sites.NetworkDescriptor;
import BipartiteTopologyAPI.sites.NodeId;

public interface Network extends Serializable {

    /**
     * Deliver a message from source to destination.
     * 
     * @param source the source node
     * @param destination the destination node
     * @param rpc the remote method reveiving the message
     * @param message the message object
     */
    void send(NodeId source, NodeId destination, RemoteCallIdentifier rpc, Serializable message);

    /**
     * Send a message object to multiple recipients.
     * 
     * @param source  the source node
     * @param rpcMap  a map mapping destination -> rpc. 
     * @param message the message to deliver
     */
     void broadcast(NodeId source, Map<NodeId, RemoteCallIdentifier> rpcMap, Serializable message) ;

    /**
     * Return a descriptor for this network.
     * 
     * The descriptor contains only an Id, and the number of spokes and hubs.
     * 
     * @return
     */
    NetworkDescriptor describe();

}
