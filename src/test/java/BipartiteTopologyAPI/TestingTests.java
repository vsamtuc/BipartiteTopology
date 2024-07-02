package BipartiteTopologyAPI;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TestingTests {

    @Test
    void failingtest() {
        assertTrue(true);
    }

    @Test
    void testTesting() {
        System.out.println("Hello world");
    }
    @Test
    void JohnThreadTesting() {

        ExecutorService executorService =
                Executors.newSingleThreadExecutor();
        executorService.execute(runnableTask1);
        executorService.execute(runnableTask2);
    }
    Runnable runnableTask1 = () -> {
        int i=0;
        while( i<5) {

                System.out.println("hi");
                i++;

        }
    };

    Runnable runnableTask2 = () -> {
        int i=0;
        while( i<5) {

            System.out.println("hiho");
            i++;

        }
    };

    @Test
    public void instancetest(){
        Integer temp=5;
        Integer[] temparr= new Integer[2];
        if(temp instanceof  Integer){
            System.out.println("hi1");

        }


    }
}

