package bistro.engine.thread;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import bistro.NodeInstance;
import bistro.engine.Message;
import bistro.engine.Query;
import bistro.engine.Tuple;
import bistro.interfaces.Network;
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

    int networkId = 0;
    ThreadNode<HubIfc, QueryIfc> spokes[];  // List of spoke nodes
    ThreadNode<SpokeIfc, QueryIfc> hubs[];  // List of hub nodes



    /**
     * Initialize the network object. 
     * 
     * This is a protected constructor. In order to create a network object, use the
     * static method 'create'.
     * 
     * @param nspokes
     * @param nhubs
     */
    protected ThreadNetwork(int networkId, int nspokes, int nhubs) {
        this.networkId = networkId;
        spokes = new ThreadNode[nspokes];
        hubs = new ThreadNode[nhubs];
    }

    /**
     * Create and return a bistro ThreadNetwork object, with the given id and nodes.
     * 
     * @param networkId   The id of the network.
     * @param spokes      Array of spoke nodes, these are instances of NodeInstance<HubIfc, QueryIfc>.
     * @param hubs        Array of hub nodes, these are instances of   NodeInstance<SpokeIfc, QueryIfc>
     * @return            The fully initialized ThreadNetwork object.
     */
    public static <SpokeIfc, HubIfc, QueryIfc> ThreadNetwork<SpokeIfc, HubIfc, QueryIfc> 
        create(
            int networkId,
            NodeInstance<HubIfc, QueryIfc>[] spokes, 
            NodeInstance<SpokeIfc, QueryIfc>[] hubs)
    {
        // Create the ThreadNetwork object
        ThreadNetwork<SpokeIfc, HubIfc, QueryIfc> tnet = new ThreadNetwork<>(networkId, spokes.length, hubs.length);

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


    /**
     * Create and return a bistro ThreadNetwork object, with the given nodes.
     * 
     * The network id is initialized to 0.
     * 
     * @param spokes      Array of spoke nodes, these are instances of NodeInstance<HubIfc, QueryIfc>.
     * @param hubs        Array of hub nodes, these are instances of   NodeInstance<SpokeIfc, QueryIfc>
     * @return            The fully initialized ThreadNetwork object.
     */
    public static <SpokeIfc, HubIfc, QueryIfc> ThreadNetwork<SpokeIfc, HubIfc, QueryIfc> 
        create(
            NodeInstance<HubIfc, QueryIfc>[] spokes, 
            NodeInstance<SpokeIfc, QueryIfc>[] hubs
        )
    {
        return create(0, spokes, hubs);
    }

    
    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    /** 
     * Return the network id of this network
     * @return the network id
     */
    public int getNetworkId() { return networkId; }


    public int numberOfSpokes() { return spokes.length; }
    public int numberOfHubs() { return hubs.length; }

    public ThreadNode<HubIfc, QueryIfc> getSpoke(int i) { return spokes[i]; }
    public ThreadNode<SpokeIfc, QueryIfc> getHub(int i) { return hubs[i]; }

    public ThreadNode<?, QueryIfc> getNode(NodeId nodeId) {
        var array = switch(nodeId.getNodeType()) {
            case HUB -> hubs;
            case SPOKE -> spokes;
        };
        var pos = nodeId.getNodeId();
        if(pos<0 || pos>=array.length)
            throw new RuntimeException("Node "+nodeId.toString()+" illegal (bad id)");
        return array[pos];        
    }


    public void sendQueryResponse( Serializable response){
        Query query= ((Query) response);

        System.out.println("Why i am here so many times");
    }


    @Override
    public void send(NodeId source, NodeId destination, RemoteCallIdentifier rpc, Serializable message) {
        if(destination == null){


            sendQueryResponse(message);
        }
        ThreadNode<?,?> dest = getNode(destination);
        Message msg = new Message(source, destination, rpc, message);
        dest.deliverMessage(msg);



    }

    public void sendTuple(NodeId spokeId, Serializable[] tuple){
        ThreadNode<?,?> dest = getNode(spokeId);
        Tuple tup= new Tuple(spokeId, tuple);
        dest.deliverMessage(tup);

    }

public void sendQuery(NodeId spokeId, RemoteCallIdentifier rpc, Serializable query){
        ThreadNode<?,?> dest = getNode(spokeId);
        Query q= new Query(spokeId, rpc, query);
        dest.deliverMessage(q);
}

    @Override
    public NetworkDescriptor describe() {
        return new NetworkDescriptor(networkId, spokes.length, hubs.length);
    }

}
