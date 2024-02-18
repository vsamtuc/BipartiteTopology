package bistro.engine.thread;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.concurrent.*;

import bistro.GenericWrapper;
import bistro.NodeInstance;
import bistro.engine.Message;
import bistro.engine.Query;
import bistro.engine.Tuple;
import bistro.sites.NodeId;

public class ThreadNode<RIfc, QIfc> extends GenericWrapper {
    
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
        executorService.execute(runnableTask);
    }

    /**
     * Stop the thread that checks and handles messages.
     */
    public void stop() {
       executorService.shutdownNow();
    }


}
