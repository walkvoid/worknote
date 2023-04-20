package com.github.walkvoid.worknote.threadpool;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/4/20
 * @desc
 */
public class ThreadPoolTest1 {


    @Test
    public void test1() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(()-> System.out.println("task exec!"));




    }
}
