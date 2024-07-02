package BipartiteTopologyAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import BipartiteTopologyAPI.annotations.InitOp;
import BipartiteTopologyAPI.annotations.MergeOp;
import BipartiteTopologyAPI.annotations.ProcessOp;
import BipartiteTopologyAPI.annotations.QueryOp;
import BipartiteTopologyAPI.annotations.RemoteOp;
import BipartiteTopologyAPI.annotations.RemoteProxy;
import BipartiteTopologyAPI.engine.serial.SerialNetwork;
import BipartiteTopologyAPI.engine.serial.SerialNode;

public class SerialEngineTests {


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
        public int field_set_times=0;

        public TNode() { }

        @Override
        public void set_field(Integer val) { 
            field_set_times ++;
            field = val; 
        }

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


    static TNode[] create_tnode_array(int tnodes) {
        TNode[] nodes = new TNode[tnodes];
        for(int i=0; i<tnodes; i++) nodes[i] = new TNode();
        return nodes;
    }


    @ParameterizedTest
    @CsvSource({"1,1", "2,2", "2,1", "4,2", "1,3", "1,0"})
    void testCreateNetworkNbyN(int nspokes, int nhubs) {
        var spokes = create_tnode_array(nspokes);
        var hubs = create_tnode_array(nhubs);
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = SerialNetwork.create(spokes, hubs);

        assertEquals(spokes.length, tnet.numberOfSpokes());
        assertEquals(hubs.length, tnet.numberOfHubs());

        var desc = tnet.describe();
        assertEquals(spokes.length, desc.getNumberOfSpokes());
        assertEquals(hubs.length, desc.getNumberOfHubs());

        for(int i=0;i<spokes.length; i++) {
            SerialNode<TNodeIF, TQuerier> wrapper = tnet.getSpoke(i);
            assertTrue(spokes[i] == wrapper.getNode());
            assertSame(tnet, wrapper.getNetwork()); 

            assertTrue(wrapper.getNode() instanceof TNode);
            TNode node = (TNode)  wrapper.getNode();

            assertEquals(42,  node.get_field());

            for(int j=0; j<hubs.length;j++) {
                node.getProxy(j).set_field(100);
            }
        }

        tnet.deliverMessages();

        for(int i=0;i<hubs.length; i++) {
            final SerialNode<TNodeIF, TQuerier> wrapper = tnet.getHub(i);
            final TNode node = (TNode) wrapper.getNode();
            assertTrue(hubs[i] == node);
            assertSame(tnet, wrapper.getNetwork()); 
            assertEquals(100, node.get_field());
            assertEquals(tnet.numberOfSpokes(), node.field_set_times);        
        }
    }
        
}
