package com.github.walkvoid.worknote.concurrent;

import org.junit.Test;

/**
 * @author jiangjunqing
 * @version 1.0.0
 * @date 2023/2/7
 * @desc
 */
public class VolatileTest {
    //static int i = 0;
    //static int j = 0;

    volatile static int i = 0;
    volatile static int j = 0;

    public static class MyRunnable implements Runnable {
        @Override
        //public synchronized void run() {
        public void run() {
            i++;
            j++;
        }
    }

    public static class MyRunnable2 implements Runnable {
        @Override
        //public synchronized void run() {
        public void run() {
            System.out.println("thread name:"+Thread.currentThread().getName()+" i:" + i + ";j:"+j);
        }
    }

    @Test
    public void test1() throws InterruptedException {
        Thread thread1 = new Thread(new MyRunnable());
        Thread thread2 = new Thread(new MyRunnable2());


        thread1.start();
        thread2.start();

        //new CountDownLatch(1).await()
    }
}
