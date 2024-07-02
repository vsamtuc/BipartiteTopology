package BipartiteTopologyAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


import BipartiteTopologyAPI.operations.RemoteCallIdentifier;
import org.junit.jupiter.api.Test;

import BipartiteTopologyAPI.annotations.InitOp;
import BipartiteTopologyAPI.annotations.MergeOp;
import BipartiteTopologyAPI.annotations.ProcessOp;
import BipartiteTopologyAPI.annotations.QueryOp;
import BipartiteTopologyAPI.annotations.RemoteOp;
import BipartiteTopologyAPI.annotations.RemoteProxy;
import BipartiteTopologyAPI.engine.thread.ThreadNetwork;
import BipartiteTopologyAPI.engine.thread.ThreadNode;


import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ThreadEngineTests {
    

    @RemoteProxy
    public interface TNodeIF {
        @RemoteOp
        void set_field(Integer val);
    }

    @RemoteProxy
    public interface TQuerier {
        @RemoteOp
        void sendQueryResponse(String response);
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
        public void query( long queryId, int qT, int[] arr) {
            System.out.println(arr[0] + " " + arr[1] + " " + arr[2]);
            getQuerier().sendQueryResponse("Hello from TNode");


        }
    }



    @Test
    void testSimpleQuery(){



        var spokes = new TNode[] { new TNode(), new TNode() };
        var hubs = new TNode[] { new TNode(), new TNode() };
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = ThreadNetwork.create(spokes, hubs);

        assertEquals(spokes.length, tnet.numberOfSpokes());
        assertEquals(hubs.length, tnet.numberOfHubs());

        var desc = tnet.describe();
        assertEquals(spokes.length, desc.getNumberOfSpokes());
        assertEquals(hubs.length, desc.getNumberOfHubs());
        int[] arr={0,1,2};
        //Object[] obj= {arr};
        tnet.sendQuery(tnet.getSpoke(0).getNodeId(), new RemoteCallIdentifier(), arr );
        System.out.println("ddd");


    }


    @Test
    void testSimpleProcess() {
        var spokes = new TNode[] { new TNode(), new TNode() };
        var hubs = new TNode[] { new TNode(), new TNode() };
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = ThreadNetwork.create(spokes, hubs);

        assertEquals(spokes.length, tnet.numberOfSpokes());
        assertEquals(hubs.length, tnet.numberOfHubs());

        var desc = tnet.describe();
        assertEquals(spokes.length, desc.getNumberOfSpokes());
        assertEquals(hubs.length, desc.getNumberOfHubs());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for(int i=0;i<spokes.length; i++) {
            ThreadNode<TNodeIF, TQuerier> wrapper = tnet.getSpoke(i);
            assertTrue(spokes[i] == wrapper.getNode());
            assertSame(tnet, wrapper.getNetwork());

            assertTrue(wrapper.getNode() instanceof TNode);
            TNode node = (TNode)  wrapper.getNode();

            Integer arr = i+1;

            tnet.sendTuple(tnet.getSpoke(i).getNodeId(),  arr);
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(int i=0;i<hubs.length; i++) {
            assertEquals(1,spokes[i].countStream);
            if (i==0) {

                assertEquals(1, spokes[0].sumStream);
            }if(i==1){
                assertEquals(2, spokes[1].sumStream);
            }
            System.out.println("Spoke "+i +" Times Accessed: "+spokes[i].countStream+" Sum: "+spokes[i].sumStream);
        }

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
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }

        for (int i = 0; i < spokes.length; i++) {
            ThreadNode<TNodeIF, TQuerier> wrapper = tnet.getSpoke(i);
            assertTrue(spokes[i] == wrapper.getNode());
            assertSame(tnet, wrapper.getNetwork());

            assertTrue(wrapper.getNode() instanceof TNode);
            TNode node = (TNode) wrapper.getNode();

            assertEquals(42, node.get_field());

            for (int j = 0; j < hubs.length; j++) {
                node.getProxy(j).set_field(100);
            }
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HashMap temp= tnet.getSpoke(0).getProxyMap();

        if(temp.containsValue(null)){
            System.out.print("Value is null");

        }else if(temp.containsValue("null")){
            System.out.print("Value is 'null'");
        }else if(temp.get(0) instanceof Proxy){
            System.out.print("Value is of type GenericProxy");
        }

    for(int i=0;i<hubs.length; i++) {
        tnet.getSpoke(i).stop();
        tnet.getHub(i).stop();
    }
    }





void testBroadcast(){
    var spokes = new TNode[] { new TNode(), new TNode() };
    var hubs = new TNode[] { new TNode(), new TNode() };
    // <TNodeIF, TNodeIF, TQuerier>
    var tnet = ThreadNetwork.create(spokes, hubs);

    assertEquals(spokes.length, tnet.numberOfSpokes());
    assertEquals(hubs.length, tnet.numberOfHubs());

    var desc = tnet.describe();
    assertEquals(spokes.length, desc.getNumberOfSpokes());
    assertEquals(hubs.length, desc.getNumberOfHubs());



    for (int i = 0; i < spokes.length; i++) {
        ThreadNode<TNodeIF, TQuerier> wrapper = tnet.getSpoke(i);
        assertTrue(spokes[i] == wrapper.getNode());
        assertSame(tnet, wrapper.getNetwork());

        assertTrue(wrapper.getNode() instanceof TNode);
        TNode node = (TNode) wrapper.getNode();

        assertEquals(42, node.get_field());

        for (int j = 0; j < hubs.length; j++) {
            node.getProxy(j).set_field(100);
        }
    }


}































}