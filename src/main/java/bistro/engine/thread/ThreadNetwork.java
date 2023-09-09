package bistro.engine.thread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import bistro.NodeInstance;
import bistro.interfaces.Mergeable;
import bistro.interfaces.Network;
import bistro.interfaces.Node;
import bistro.operations.RemoteCallIdentifier;
import bistro.sites.NetworkDescriptor;
import bistro.sites.NodeId;
import bistro.sites.NodeType;

/**
 * A bistro network where each node is implemented in a separate thread in the same process.\
 * @param <SpokeIfc>  The spoke node remote interface. 
 * @param <HubIfc>    The hub node remote interface.
 * @param <QueryIfc>  The querier interface.
 */
public class ThreadNetwork <SpokeIfc, HubIfc, QueryIfc> implements Network {


    ThreadNode<HubIfc, QueryIfc> spokes[];  // List of spoke nodes
    ThreadNode<SpokeIfc, QueryIfc> hubs[];  // List of hub nodes



    protected ThreadNetwork(int nspokes, int nhubs) {
        //spokes = new ThreadNode<HubIfc, QueryIfc>[nspokes];
        //hubs = new ThreadNode<HubIfc, QueryIfc>[nhubs];
        spokes = new ThreadNode[nspokes];
        hubs = new ThreadNode[nhubs];
    }

    /**
     * Create and return a bistro ThreadNetwork object, with the given nodes.
     * 
     * @param spokes      Array of spoke nodes, these are instances of NodeInstance<HubIfc, QueryIfc>.
     * @param hubs        Array of hub nodes, these are instances of   NodeInstance<SpokeIfc, QueryIfc>
     * @return            The fully initialized ThreadNetwork object.
     */
    public static <SpokeIfc, HubIfc, QueryIfc> ThreadNetwork<SpokeIfc, HubIfc, QueryIfc> 
        create(NodeInstance<HubIfc, QueryIfc>[] spokes, 
        NodeInstance<SpokeIfc, QueryIfc>[] hubs) 
    {
        // Create the ThreadNetwork object
        ThreadNetwork<SpokeIfc, HubIfc, QueryIfc> tnet = new ThreadNetwork<>(spokes.length, hubs.length);

        // Create the spoke wrappers
        for(int i=0; i<spokes.length; i++) {
            NodeId nid = new NodeId(NodeType.SPOKE, i);
            var node = new ThreadNode<HubIfc, QueryIfc>(nid, spokes[i], tnet);
            tnet.spokes[i] = node;
        }

        // Create the hub wrappers
        for(int i=0; i<spokes.length; i++) {
            NodeId nid = new NodeId(NodeType.HUB, i);
            var node = new ThreadNode<SpokeIfc, QueryIfc>(nid, hubs[i], tnet);
            tnet.hubs[i] = node;
        }

        return tnet;
    }

    @Override
    public void send(NodeId source, NodeId destination, RemoteCallIdentifier rpc, Serializable message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'send'");
    }

    @Override
    public void broadcast(NodeId source, Map<NodeId, RemoteCallIdentifier> rpcMap, Serializable message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'broadcast'");
    }

    @Override
    public NetworkDescriptor describe() {
        return new NetworkDescriptor(0, spokes.length, hubs.length);
    }

}
