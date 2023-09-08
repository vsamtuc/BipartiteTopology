package bistro;

import bistro.annotations.ProcessOp;
import bistro.annotations.RemoteProxy;

public class BasicNodeTests {
    
    @RemoteProxy
    static public class TNode {
        public int countStream=0;
        public int sumStream=0;

        public TNode() { }

        @ProcessOp
        public void process(Integer val) {
            countStream += 1;
            sumStream += val;
        }
    }


    

}
