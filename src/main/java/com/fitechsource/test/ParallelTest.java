package com.fitechsource.test;

import java.util.*;

/**
 * Should be improved to reduce calculation time.
 * <p>
 * 1. Change this file or create new one using parallel calculation mode.
 * 2. Do not use `executors`, only plain threads  (max threads count which should be created for calculations is com.fitechsource.test.TestConsts#MAX_THREADS)
 * 3. Try to provide simple solution, do not implement frameworks.
 * 4. Don't forget that calculation method can throw exception, process it right way.
 * (Stop calculation process and print error message. Ignore already calculated intermediate results, user doesn't need it.)
 * <p>
 * Please attach code files to email - skhisamov@fitechsource.com
 */

public class ParallelTest {

    public static void main(String[] args) {
        Set<Double> res = calculateAsynchronously();
        System.out.println(res);
    }

    private static Set<Double> calculateAsynchronously() {
        Set<Double> res = Collections.synchronizedSet(new HashSet<>());
        List<Thread> threads = new ArrayList<>();

        createAndStartThreads(res, threads);
        waitThreadsExecuting(threads);
        return res;
    }

    private static void createAndStartThreads(Set<Double> res, List<Thread> threads) {
        for (int i = 0; i < TestConsts.MAX_THREADS; i++) {
            Thread thread = new CalculatingThread(res, createSequenceForCalculating(i));
            thread.setUncaughtExceptionHandler((t, e) -> processExceptionAndEndProgram(e));
            threads.add(thread);
            thread.start();
        }
    }

    private static void waitThreadsExecuting(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                for (Thread t : threads) {
                    t.interrupt();
                }
                processExceptionAndEndProgram(e);
            }
        }
    }

    private static void processExceptionAndEndProgram(Throwable throwable) {
        System.out.println("Calculation  unsuccessful, exception caught");
        throwable.printStackTrace();
        System.exit(7);

    }

    private static List<Integer> createSequenceForCalculating(int startPosition) {
        List<Integer> integers = new ArrayList<>();

        for (int i = startPosition; i < TestConsts.N; i += TestConsts.MAX_THREADS) {
            integers.add(i);
        }
        return integers;
    }
}

class CalculatingThread extends Thread {

    private Set<Double> res;
    private List<Integer> values;

    CalculatingThread(Set<Double> res, List<Integer> values) {
        this.res = res;
        this.values = values;
    }

    @Override
    public void run() {
        for (Integer value : values) {
            try {
                res.addAll(TestCalc.calculate(value));
            } catch (TestException e) {
                throw new CalculationException("Exception during calculation", e);
            }
        }
    }

}

class CalculationException extends RuntimeException {

    public CalculationException() {
    }

    public CalculationException(String message) {
        super(message);
    }

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
    }

}
