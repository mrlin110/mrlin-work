package com.mrlin.thread;

import java.util.concurrent.*;

/**
 * @Description:
 * @Author: ljm
 * @Date: 2020/9/24 11:37
 * @Version: 1.0
 */
public class ArrayBlockingQueueMain2 {


    public static void main(String[] args) {
        BlockingQueue <Integer> blockingQueue = new ArrayBlockingQueue<>(3,true);
        ArrayBlockingQueueProducer2 arrayBlockingQueueProducer2 = new ArrayBlockingQueueProducer2(blockingQueue);
        ArrayBlockingQueueConsumer2 arrayBlockingQueueConsumer2 = new ArrayBlockingQueueConsumer2(blockingQueue);
        new Thread(arrayBlockingQueueProducer2).start();
        new Thread(arrayBlockingQueueConsumer2).start();
    }
}
