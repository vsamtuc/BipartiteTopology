package bistro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import bistro.annotations.InitOp;
import bistro.annotations.MergeOp;
import bistro.annotations.ProcessOp;
import bistro.annotations.QueryOp;
import bistro.annotations.RemoteOp;
import bistro.annotations.RemoteProxy;
import bistro.engine.thread.ThreadNetwork;
import bistro.engine.thread.ThreadNode;
import bistro.interfaces.Network;

public class ThreadEngineTests {
    

    @RemoteProxy
    public interface TNodeIF {
        @RemoteOp
        void set_field(Integer val);
    }

    @RemoteProxy
    public interface TQuerier {
        
    }

    @RemoteProxy
    static public class TNode extends NodeInstance<TNodeIF, TQuerier> implements TNodeIF {
        public int countStream=0;
        public int sumStream=0;
        public int field=0;

        public TNode() { }

        @Override
        public void set_field(Integer val) { field = val; }

        public int get_field() { return field; }

        @ProcessOp
        public void process(Integer val) {
            countStream += 1;
            sumStream += val;
        }

        @InitOp
        public void init() { field = 42; }
        @MergeOp
        public void merge() { }
        @QueryOp
        public void query() { }
    }


    @Test
    void testCreateNetwork2by2() {
        var spokes = new TNode[] { new TNode(), new TNode() };
        var hubs = new TNode[] { new TNode(), new TNode() };
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = ThreadNetwork.create(spokes, hubs);

        assertEquals(spokes.length, tnet.numberOfSpokes());
        assertEquals(hubs.length, tnet.numberOfHubs());

        var desc = tnet.describe();
        assertEquals(spokes.length, desc.getNumberOfSpokes());
        assertEquals(hubs.length, desc.getNumberOfHubs());

        for(int i=0;i<spokes.length; i++) {
            ThreadNode<TNodeIF, TQuerier> wrapper = tnet.getSpoke(i);
            assertTrue(spokes[i] == wrapper.getNode());
            assertSame(tnet, wrapper.getNetwork()); 

            assertTrue(wrapper.getNode() instanceof TNode);
            TNode node = (TNode)  wrapper.getNode();

            assertEquals(42,  node.get_field());

            for(int j=0; j<hubs.length;j++) {
                node.getProxy(j).set_field(100);
            }
        }

        for(int i=0;i<hubs.length; i++) {
            ThreadNode<TNodeIF, TQuerier> wrapper = tnet.getHub(i);
            assertTrue(hubs[i] == wrapper.getNode());
            assertSame(tnet, wrapper.getNetwork()); 
        }
    }
}
