package com.mrlin.thread;

import java.util.concurrent.BlockingQueue;

/**
 * @Description: 生产者
 * @Author: ljm
 * @Date: 2020/11/24 11:27
 * @Version: 1.0
 */
public class ArrayBlockingQueueProducer2 implements Runnable {

    private final BlockingQueue<Integer> blockingQueue;
    private static int element =0;

    //初始化堵塞队列
    public ArrayBlockingQueueProducer2(BlockingQueue<Integer> blockingQueue){
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            while (element<100){
                System.out.println("Producer: "+element);

                    blockingQueue.put(element++);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Producer end !");

    }
}
