package bistro.engine;

import bistro.engine.thread.QueueEvent;
import bistro.operations.RemoteCallIdentifier;
import bistro.sites.NodeId;

import java.io.Serializable;

public class Query  implements Serializable, QueueEvent{
    final public NodeId source;

    static long generateId=0;

    final public RemoteCallIdentifier rpc;
    final public Serializable Query;

    final public long id=generateId();


    public Query(NodeId source, RemoteCallIdentifier rpc, Serializable Query) {
        this.source = source;
        this.rpc = rpc;
        this.Query = Query;
    }



    static synchronized public long generateId(){
        return generateId++;
    }

}
