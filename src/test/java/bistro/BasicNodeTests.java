package bistro;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import bistro.annotations.InitOp;
import bistro.annotations.MergeOp;
import bistro.annotations.ProcessOp;
import bistro.annotations.QueryOp;
import bistro.annotations.Remote;
import bistro.annotations.RemoteProxy;
import bistro.interfaces.Network;
import bistro.interfaces.Node;
import bistro.operations.RemoteCallIdentifier;
import bistro.sites.NetworkDescriptor;
import bistro.sites.NodeId;
import bistro.sites.NodeType;


public class BasicNodeTests {
    
    @RemoteProxy
    public interface TNodeIF {

    }

    @RemoteProxy
    public interface TQuerier {
        
    }

    @RemoteProxy
    static public class TNode extends NodeInstance<TNodeIF, TQuerier> implements TNodeIF {
        public int countStream=0;
        public int sumStream=0;

        public TNode() { }

        @ProcessOp
        public void process(Integer val) {
            countStream += 1;
            sumStream += val;
        }

        @InitOp
        public void init() { }

        @MergeOp
        public void merge() { }

        @QueryOp
        public void query() { }
    }


    public static class TNetwork implements Network {
        NetworkDescriptor desc = new NetworkDescriptor(0, 1, 0);

        @Override
        public void send(NodeId source, NodeId destination, RemoteCallIdentifier rpc, Serializable message) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'send'");
        }

        @Override
        public void broadcast(NodeId source, Map<NodeId, RemoteCallIdentifier> rpcMap, Serializable message) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'broadcast'");
        }

        @Override
        public NetworkDescriptor describe() {
            return desc;
        }
    }

    
    @Test
    void testTNetwork() {
        TNetwork tnet = new TNetwork();
        assertEquals(0, tnet.describe().getNetworkId());
        assertEquals(1, tnet.describe().getNumberOfSpokes());
        assertEquals(0, tnet.describe().getNumberOfHubs());
    }


    @Test
    void testNodeWrapper() {
        TNetwork tnet = new TNetwork();
        TNode tnode = new TNode();
        var wrapper = new GenericWrapper(new NodeId(NodeType.SPOKE, 0), tnode, tnet);

        assertEquals(0, wrapper.nodeId.getNodeId());
        assertEquals(NodeType.SPOKE, wrapper.nodeId.getNodeType());
        assertSame(tnet, wrapper.network);
    }


    @Test
    void testSingleNode() {
        TNetwork tnet = new TNetwork();
        TNode tnode = new TNode();
        Node node = new GenericWrapper(new NodeId(NodeType.SPOKE, 0), tnode, tnet);

        /*
        for(int i=0;i<10; i++) {
            Integer[] tuple = new Integer[] {i};
            node.receiveTuple(tuple);
        }
        */

        IntStream
            .range(0,10)
            .mapToObj((i)->new Integer[]{i})
            .forEach(node::receiveTuple);

        assertEquals(10, tnode.countStream);
        assertEquals(45, tnode.sumStream);
    }    

}
