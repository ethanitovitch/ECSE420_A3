package ca.mcgill.ecse420.a3;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FineGrainedTest {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FineGrainedSet<Integer> fineGrainedSet = new FineGrainedSet<>();
        Boolean[] ins = new Boolean[10];
        int tests = 0;
        Random rn = new Random();

        // for each number between 1 and 10 add that element to the list
        // if the randomly assigned boolean "in" is true
        for (int i = 0; i < 10; i++) {
            Boolean in = (rn.nextInt(2) == 0) ? true : false;
            ins[i] = in;
            if (in) {
                tests += 1;
                fineGrainedSet.add(i);
            }
        }

        boolean result = true;

        // for each element
        for (int i = 0; i < 10; i++) {
            int finalI = i;

            // try and see if it is contained
            Future<Boolean> is_contained = exec.submit(() -> fineGrainedSet.contains(finalI));

            // if it was added then is_contained should be true otherwise false
            if (ins[i] != is_contained.get()) {
                result = false;
            }
        }
        if (result) {
            System.out.println("Completed " + tests + " Lookups Successfully");
        } else {
            System.out.println("Failed");
        }
        exec.shutdown();
    }
}
