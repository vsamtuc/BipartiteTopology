package BipartiteTopologyAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import BipartiteTopologyAPI.annotations.InitOp;
import BipartiteTopologyAPI.annotations.MergeOp;
import BipartiteTopologyAPI.annotations.ProcessOp;
import BipartiteTopologyAPI.annotations.QueryOp;
import BipartiteTopologyAPI.annotations.RemoteProxy;

public class NodeClassTests {
    
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


    @Test
    void testNodeClassUnique() {
        NodeClass tnc = NodeClass.forClass(TNode.class);

        // Calling forNode again should yield same object
        assertSame(tnc, NodeClass.forClass(TNode.class));
    }

    @Test
    void testNodeClassData() throws NoSuchMethodException {
        NodeClass tnc = NodeClass.forClass(TNode.class);

        // Check that we are analysing TNode correctly
        assertSame(TNode.class, tnc.getWrappedClass());
        assertSame(TNodeIF.class, tnc.getProxiedInterface());
        
        assertEquals(TNode.class.getMethod("init"), tnc.getInitMethod());
        assertEquals(TNode.class.getMethod("query"), tnc.getQueryMethod());
        assertEquals(TNode.class.getMethod("merge"), tnc.getMergeMethod());
        assertEquals(TNode.class.getMethod("process", Integer.class), tnc.getProcessMethod());
        
     
    }


}
