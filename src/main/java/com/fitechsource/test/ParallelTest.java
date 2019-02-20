package com.fitechsource.test;

import java.util.*;

public class ParallelTest {
    public static void main(String[] args) throws TestException {
        Set<Double> res = Collections.synchronizedSet(new HashSet<>());





        System.out.println(res);

    }

}


