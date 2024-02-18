package bistro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


import bistro.engine.Tuple;
import bistro.operations.RemoteCallIdentifier;
import org.junit.jupiter.api.Test;

import bistro.annotations.InitOp;
import bistro.annotations.MergeOp;
import bistro.annotations.ProcessOp;
import bistro.annotations.QueryOp;
import bistro.annotations.RemoteOp;
import bistro.annotations.RemoteProxy;
import bistro.engine.thread.ThreadNetwork;
import bistro.engine.thread.ThreadNode;


import java.lang.reflect.Field;
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
        public void process(Integer[] val) {

            countStream += 1;
            for(int i=val.length -1; i>=0; i--) {
                sumStream += val[i];
            }
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

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



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

        for(int i=0;i<spokes.length; i++) {
            ThreadNode<TNodeIF, TQuerier> wrapper = tnet.getSpoke(i);
            assertTrue(spokes[i] == wrapper.getNode());
            assertSame(tnet, wrapper.getNetwork());

            assertTrue(wrapper.getNode() instanceof TNode);
            TNode node = (TNode)  wrapper.getNode();

            Integer[] arr = {i+1, i+2, i+3, i+4, i+5};

            tnet.sendTuple(tnet.getSpoke(i).getNodeId(), arr);
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(int i=0;i<hubs.length; i++) {
            assertEquals(1,spokes[i].countStream);
            if (i==0) {

                assertEquals(15, spokes[0].sumStream);
            }if(i==1){
                assertEquals(20, spokes[1].sumStream);
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

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for(int i=0;i<hubs.length; i++) {
            ThreadNode<TNodeIF, TQuerier> wrapper = tnet.getHub(i);
            assertTrue(hubs[i] == wrapper.getNode());
            assertSame(tnet, wrapper.getNetwork());
            assertEquals(100,hubs[i].get_field());
            System.out.println("Hub: "+ hubs[i]+" field: "+ hubs[i].get_field());
        }

    }































    @Test
    void testMessage() throws NoSuchFieldException, IllegalAccessException {
        var spokes = new TNode[] { new TNode(), new TNode() };
        var hubs = new TNode[] { new TNode(), new TNode() };
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = ThreadNetwork.create(spokes, hubs);

        var spoke0 = tnet.getSpoke(0);
        var spoke1 = tnet.getSpoke(1);
        var hub0 = tnet.getHub(0);
        var hub1 = tnet.getHub(1);
        System.out.println(tnet.getHub(0).messageQueue.size());

        tnet.send(spoke0.getNodeId(), hub0.getNodeId(), new RemoteCallIdentifier() ,"hello");

        System.out.println(tnet.getHub(0).messageQueue.size());
        Object obj= tnet.getHub(0).messageQueue.pop();

        Field field = obj.getClass().getDeclaredField("message");

        Object value= field.get(obj);
        System.out.println(value);
    }




    @Test
    void testDequeue() throws InterruptedException {

        var spokes = new TNode[] { new TNode(), new TNode() };
        var hubs = new TNode[] { new TNode(), new TNode() };
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = ThreadNetwork.create(spokes, hubs);
        var hub0 = tnet.getHub(0);
       var spoke0 = tnet.getSpoke(0);
        tnet.send(spoke0.getNodeId(), hub0.getNodeId(), new RemoteCallIdentifier() ,"hello");
        Object obj= hub0.messageQueue.takeLast();

    }

    @Test
    void testExecutor(){
        var spokes = new TNode[] { new TNode(), new TNode() };
        var hubs = new TNode[] { new TNode(), new TNode() };
        // <TNodeIF, TNodeIF, TQuerier>
        var tnet = ThreadNetwork.create(spokes, hubs);

            //maybe do this as tasks in a thread-pool
            for(int j=0; j<hubs.length;j++) {
                for(int k=0; k<spokes.length; k++){
                    for (int u=0; u<5; u++){
                        tnet.send(tnet.getHub(j).getNodeId(), tnet.getSpoke(k).getNodeId(), new RemoteCallIdentifier() ,"Hello from Hub:"+j+" to Spoke: "+k+" with message: "+u);
                        tnet.send(tnet.getSpoke(k).getNodeId(), tnet.getHub(j).getNodeId(), new RemoteCallIdentifier() ,"Hello from Spoke:"+k+" to Hub: "+j+" with message: "+u);
                    };

            }
        }
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}