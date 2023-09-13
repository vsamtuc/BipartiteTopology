package bistro.engine.thread;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import bistro.GenericWrapper;
import bistro.NodeInstance;
import bistro.engine.Message;
import bistro.sites.NodeId;

public class ThreadNode<RIfc, QIfc> extends GenericWrapper {
    
    // Thread that receives messages to the node
    Thread nodeThread = null;

    // Used to store messages to be delivered to the node
    BlockingDeque<Message> messageQueue;
    
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
    }

    /**
     * Deliver a message to this node
     * @param message  The message object to deliver.
     */
    public void deliverMessage(Message message) {
        try {
            messageQueue.putLast(message);
        } catch(InterruptedException ex) {
            String msg = "Interrupted at delivering message to node "+getNodeId().toString();
            throw new RuntimeException(msg, ex);
        }
    }
}
