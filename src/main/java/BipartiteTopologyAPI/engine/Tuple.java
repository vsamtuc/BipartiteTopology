package BipartiteTopologyAPI.engine;

import BipartiteTopologyAPI.engine.thread.QueueEvent;
import BipartiteTopologyAPI.sites.NodeId;

import java.io.Serializable;

public class Tuple implements Serializable,QueueEvent {
    final public NodeId destination;

    final public Serializable tuple;


    public Tuple(NodeId spokeId, Serializable tuple){
        this.destination = spokeId;
        this.tuple = tuple;

    }



}
