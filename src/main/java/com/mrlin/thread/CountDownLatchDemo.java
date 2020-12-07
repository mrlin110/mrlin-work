package com.mrlin.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: ljm
 * @Date: 2020/9/25 16:45
 * @Version: 1.0
 */
public class CountDownLatchDemo {


    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch latch =new CountDownLatch(1);
        final CountDownLatch end =new CountDownLatch(5);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(int i = 1 ;i<=5 ;i++){
                final int index = i;
                Runnable runnable =  new Runnable(){
                    @Override
                    public void run() {
                        System.out.println("NO."+index+"等待发令枪");
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep((long)Math.random()*10000);
                            System.out.println("NO."+index+"结束比赛");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            end.countDown();
                        }
                    }
                };
                executorService.submit(runnable);
        }

        Thread.sleep(5000);
        System.out.println("比赛开始");
        latch.countDown();
        end.await();
        System.out.println("比赛结束");
        executorService.shutdown();
    }

}
