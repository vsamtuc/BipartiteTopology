package BipartiteTopologyAPI;

import BipartiteTopologyAPI.engine.thread.ThreadNetwork;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreadImplementationTest {


    @Test
    void test(){

        var spokes = new NodeInstance[]{};
        var hubs = new ThreadEngineTests.TNode[] { new ThreadEngineTests.TNode(), new ThreadEngineTests.TNode() };
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = ThreadNetwork.create(spokes, hubs);

        assertEquals(spokes.length, tnet.numberOfSpokes());
        assertEquals(hubs.length, tnet.numberOfHubs());



    }
}
