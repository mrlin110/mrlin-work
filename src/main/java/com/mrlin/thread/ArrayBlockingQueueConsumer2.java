package com.mrlin.thread;

import java.util.concurrent.BlockingQueue;

/**
 * @Description:
 * @Author: ljm
 * @Date: 2020/9/24 11:37
 * @Version: 1.0
 */
public class ArrayBlockingQueueConsumer2 implements  Runnable{

    private final BlockingQueue<Integer> blockingQueue;


    //初始化堵塞队列
    public ArrayBlockingQueueConsumer2(BlockingQueue<Integer> blockingQueue){
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
       try{
           while ( true){
               int value = blockingQueue.take();
               System.out.println("Consume: "+ value);
               if(value >=99){
                    break;
               }
           }

       }catch (InterruptedException e){
           e.printStackTrace();
       }
        System.out.println("Consume end");
    }
}
