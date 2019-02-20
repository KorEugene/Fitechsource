package com.fitechsource.test;

import java.util.*;

/**
 * Should be improved to reduce calculation time.
 *
 * 1. Change this file or create new one using parallel calculation mode.
 * 2. Do not use `executors`, only plain threads  (max threads count which should be created for calculations is com.fitechsource.test.TestConsts#MAX_THREADS)
 * 3. Try to provide simple solution, do not implement frameworks.
 * 4. Don't forget that calculation method can throw exception, process it right way.
 *   (Stop calculation process and print error message. Ignore already calculated intermediate results, user doesn't need it.)
 *
 *   Please attach code files to email - skhisamov@fitechsource.com
 */

public class ParallelTest {
    public static void main(String[] args) throws InterruptedException {
        Set<Double> res = Collections.synchronizedSet(new HashSet<>());
        List<Thread> threadList = new ArrayList<>();
        int index = 0;

        for (int i = 0; i < TestConsts.MAX_THREADS; i++) {
            Thread thread = new CalculatingThread(res, createListOfValuesInRange(TestConsts.N, TestConsts.MAX_THREADS, i));
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new CalculationException("Error during calculation waiting", e);
            }
        }

        System.out.println(res);
    }

    public static List<Integer> createListOfValuesInRange(int countOfIterations, int countOfThreads, int startPosition) {
        List<Integer> listOfValues = new ArrayList<>();

        for (int i = startPosition; i < countOfIterations; i += countOfThreads) {
            listOfValues.add(i);
        }
        return listOfValues;
    }
}

class CalculatingThread extends Thread {

    private Set<Double> res;
    private List<Integer> listOfValues;

    CalculatingThread(Set<Double> res, List<Integer> listOfValues) {
        this.res = res;
        this.listOfValues = listOfValues;
    }

    @Override
    public void run() {
        for (Integer value : listOfValues) {
            try {
                res.addAll(TestCalc.calculate(value));
            } catch (TestException e) {
                throw new CalculationException("Error during calculating", e);
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



