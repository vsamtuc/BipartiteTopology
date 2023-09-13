package bistro.engine;

import java.io.Serializable;

import bistro.operations.RemoteCallIdentifier;
import bistro.sites.NodeId;

public class Message {
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
}