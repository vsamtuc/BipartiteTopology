package bistro.interfaces;

import java.io.Serializable;
import java.util.Map;

import bistro.operations.RemoteCallIdentifier;
import bistro.sites.NetworkDescriptor;
import bistro.sites.NodeId;

public interface Network extends Serializable {

    void send(NodeId source, NodeId destination, RemoteCallIdentifier rpc, Serializable message);

    void broadcast(NodeId source, Map<NodeId, RemoteCallIdentifier> rpcMap, Serializable message);

    NetworkDescriptor describe();

}
