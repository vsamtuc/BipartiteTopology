package BipartiteTopologyAPI.engine;

import java.io.Serializable;

import BipartiteTopologyAPI.engine.thread.QueueEvent;
import BipartiteTopologyAPI.operations.RemoteCallIdentifier;
import BipartiteTopologyAPI.sites.NodeId;

public class Message implements Serializable, QueueEvent {


    final public NodeId source;
    final public NodeId destination;
    final public RemoteCallIdentifier rpc;
    final public Serializable message;

    public Message(NodeId source, NodeId destination, RemoteCallIdentifier rpc, Serializable message) {
        this.source = source;
        this.destination = destination;
        this.rpc = rpc;
        this.message = message;
    }

    public  Serializable getMessage() {
        return message;
    }
}