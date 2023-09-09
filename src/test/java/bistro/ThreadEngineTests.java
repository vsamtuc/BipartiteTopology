package bistro;

import org.junit.jupiter.api.Test;

import bistro.annotations.InitOp;
import bistro.annotations.MergeOp;
import bistro.annotations.ProcessOp;
import bistro.annotations.QueryOp;
import bistro.annotations.RemoteProxy;
import bistro.engine.thread.ThreadNetwork;

public class ThreadEngineTests {
    

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
    void testCreateNetwork2by2() {
        var spokes = new TNode[] { new TNode(), new TNode() };
        var hubs = new TNode[] { new TNode(), new TNode() };
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = ThreadNetwork.create(spokes, hubs);
    }
}
