package BipartiteTopologyAPI.engine.thread;

import java.io.Serializable;
import java.util.concurrent.*;

import BipartiteTopologyAPI.BufferingWrapper;
import BipartiteTopologyAPI.GenericWrapper;
import BipartiteTopologyAPI.NodeInstance;
import BipartiteTopologyAPI.engine.Message;
import BipartiteTopologyAPI.engine.Query;
import BipartiteTopologyAPI.engine.Tuple;
import BipartiteTopologyAPI.sites.NodeId;

public class ThreadNode<RIfc, QIfc> extends BufferingWrapper {
    
    // Thread that receives messages to the node
    ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    // Used to store messages to be delivered to the node
    public BlockingDeque<QueueEvent> messageQueue;
    
    /**
     * Create a thread-based wrapper for a node instance.
     * 
     * @param nodeId    the node id of the wrapped node
     * @param node      the wrapped node
     * @param network   the network
     */

    public ThreadNode(NodeId nodeId, NodeInstance<RIfc, QIfc> node, ThreadNetwork network) {
        super(nodeId, node, network);
        messageQueue = new LinkedBlockingDeque<>();
        start();
    }

    /**
     * Deliver a message to this node
     * @param message  The message object to deliver.
     */
    public void deliverMessage(QueueEvent message) {
        try {
            messageQueue.putLast(message);
        } catch(InterruptedException ex) {
            String msg = "Interrupted at delivering message to node "+getNodeId().toString();
            throw new RuntimeException(msg, ex);
        }
    }

    public void HandleQueueEvent(QueueEvent q){
        if (q==null){
            return;
        }
        if(q instanceof Message){
                if(((Message) q).rpc== null){
                    System.out.println("Rpc Was Null");
                }
                Message m= ((Message) q);
                //System.out.println("Node id: "+ this.nodeId+ " and the message is: "+ m.getMessage());
            this.receiveMsg(m.source,m.rpc, m.message);
        } else if (q instanceof Tuple) {
            Tuple t= ((Tuple) q);
            Serializable[] args= {t.tuple};
            this.receiveTuple(args);

        }else if (q instanceof Query) {

            Query query= ((Query) q);
            //Serializable[] args= {query.Query};
            this.receiveQuery(query.id, (int[])query.mes);

        }
    }




    Runnable runnableTask = () -> {
        while(true) {
            try {
                QueueEvent queueEvent = messageQueue.takeFirst();
                HandleQueueEvent(queueEvent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

/**
 * Start a new thread to check and handle  messages.
 */

    public void start() {
        //CatchingRunnable catchingRunnable= new CatchingRunnable(runnableTask);


        executorService.execute(runnableTask);
    }

    /**
     * Stop the thread that checks and handles messages.
     */
    public void stop() {
       executorService.shutdownNow();
    }


/*

    public static class CatchingRunnable implements Runnable {

        private final Runnable delegate;

        public CatchingRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                delegate.run();
            } catch (RuntimeException e) {
                System.out.println(e.getMessage()); // Log, notify etc...
                throw e;
            }
        }
    }




*/



}
