package bistro.engine.thread;

import java.io.Serializable;

import bistro.interfaces.Mergeable;
import bistro.interfaces.Node;
import bistro.operations.RemoteCallIdentifier;
import bistro.sites.NodeId;

public class ThreadNode implements Node {

    @Override
    public void merge(Mergeable[] mergeables) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'merge'");
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void receiveQuery(long queryId, Serializable query) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveQuery'");
    }

    @Override
    public void receiveMsg(NodeId source, RemoteCallIdentifier rpc, Serializable message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveMsg'");
    }

    @Override
    public void receiveTuple(Serializable tuple) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveTuple'");
    }
    
}
