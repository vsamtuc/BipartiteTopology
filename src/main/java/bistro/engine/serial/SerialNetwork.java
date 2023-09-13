package bistro.engine.serial;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;

import bistro.NodeInstance;
import bistro.engine.Message;
import bistro.engine.serial.SerialNetwork;
import bistro.engine.serial.SerialNode;
import bistro.interfaces.Network;
import bistro.operations.RemoteCallIdentifier;
import bistro.sites.NetworkDescriptor;
import bistro.sites.NodeId;
import bistro.sites.NodeType;

public class SerialNetwork<SpokeIfc, HubIfc, QueryIfc> implements Network {

    int networkId = 0;
    SerialNode<HubIfc, QueryIfc> spokes[];  // List of spoke nodes
    SerialNode<SpokeIfc, QueryIfc> hubs[];  // List of hub nodes
    Queue<Message> messageQueue;

    /**
     * Initialize the network object. 
     * 
     * This is a protected constructor. In order to create a network object, use the
     * static method 'create'.
     * 
     * @param nspokes
     * @param nhubs
     */
    protected SerialNetwork(int networkId, int nspokes, int nhubs) {
        this.networkId = networkId;
        spokes = new SerialNode[nspokes];
        hubs = new SerialNode[nhubs];
        messageQueue = new ArrayDeque<>();
    }

    /**
     * Create and return a bistro SerialNetwork object, with the given id and nodes.
     * 
     * @param networkId   The id of the network.
     * @param spokes      Array of spoke nodes, these are instances of NodeInstance<HubIfc, QueryIfc>.
     * @param hubs        Array of hub nodes, these are instances of   NodeInstance<SpokeIfc, QueryIfc>
     * @return            The fully initialized SerialNetwork object.
     */
    public static <SpokeIfc, HubIfc, QueryIfc> SerialNetwork<SpokeIfc, HubIfc, QueryIfc> 
        create(
            int networkId,
            NodeInstance<HubIfc, QueryIfc>[] spokes, 
            NodeInstance<SpokeIfc, QueryIfc>[] hubs)
    {
        // Create the SerialNetwork object
        SerialNetwork<SpokeIfc, HubIfc, QueryIfc> tnet = new SerialNetwork<>(networkId, spokes.length, hubs.length);

        // Create the spoke wrappers
        for(int i=0; i<spokes.length; i++) {
            NodeId nid = new NodeId(NodeType.SPOKE, i);
            var node = new SerialNode<HubIfc, QueryIfc>(nid, spokes[i], tnet);
            tnet.spokes[i] = node;
        }

        // Create the hub wrappers
        for(int i=0; i<spokes.length; i++) {
            NodeId nid = new NodeId(NodeType.HUB, i);
            var node = new SerialNode<SpokeIfc, QueryIfc>(nid, hubs[i], tnet);
            tnet.hubs[i] = node;
        }

        return tnet;
    }


    /**
     * Create and return a bistro SerialNetwork object, with the given nodes.
     * 
     * The network id is initialized to 0.
     * 
     * @param spokes      Array of spoke nodes, these are instances of NodeInstance<HubIfc, QueryIfc>.
     * @param hubs        Array of hub nodes, these are instances of   NodeInstance<SpokeIfc, QueryIfc>
     * @return            The fully initialized SerialNetwork object.
     */
    public static <SpokeIfc, HubIfc, QueryIfc> SerialNetwork<SpokeIfc, HubIfc, QueryIfc> 
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

    public SerialNode<HubIfc, QueryIfc> getSpoke(int i) { return spokes[i]; }
    public SerialNode<SpokeIfc, QueryIfc> getHub(int i) { return hubs[i]; }

    public SerialNode<?, QueryIfc> getNode(NodeId nodeId) {
        var array = switch(nodeId.getNodeType()) {
            case HUB -> hubs;
            case SPOKE -> spokes;
        };
        var pos = nodeId.getNodeId();
        if(pos<0 || pos>=array.length)
            throw new RuntimeException("Node "+nodeId.toString()+" illegal (bad id)");
        return array[pos];        
    }

    @Override
    public void send(NodeId source, NodeId destination, RemoteCallIdentifier rpc, Serializable message) {
        SerialNode<?,?> dest = getNode(destination);
        Message msg = new Message(source, destination, rpc, message);
        dest.deliverMessage(msg);
    }

    @Override
    public NetworkDescriptor describe() {
        return new NetworkDescriptor(networkId, spokes.length, hubs.length);
    }
    

}
