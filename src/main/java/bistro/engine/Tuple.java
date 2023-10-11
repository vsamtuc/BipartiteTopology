package bistro.engine;

import bistro.engine.thread.QueueEvent;
import bistro.sites.NodeId;

import java.io.Serializable;

public class Tuple implements Serializable,QueueEvent {
    final public NodeId destination;

    final public Serializable[] tuple;


    public Tuple(NodeId spokeId, Serializable[] tuple){
        this.destination = spokeId;
        this.tuple = tuple;

    }



}
