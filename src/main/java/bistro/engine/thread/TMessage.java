package bistro.engine.thread;

import java.io.Serializable;

import bistro.operations.RemoteCallIdentifier;
import bistro.sites.NodeId;

public class TMessage {
    final public NodeId source;
    final public NodeId destination;
    final public RemoteCallIdentifier rpc;
    final public Serializable message;

    public TMessage(NodeId source, NodeId destination, RemoteCallIdentifier rpc, Serializable message) {
        this.source = source;
        this.destination = destination;
        this.rpc = rpc;
        this.message = message;
    }
}