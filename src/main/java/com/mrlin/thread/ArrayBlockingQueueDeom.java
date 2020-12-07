package com.mrlin.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: ljm
 * @Date: 2020/9/24 11:37
 * @Version: 1.0
 */
public class ArrayBlockingQueueDeom {


    public static void main(String[] args) {
        long startTime =  System.currentTimeMillis();
       ArrayBlockingQueue <Integer> queue = new ArrayBlockingQueue<Integer>(100);
        for (int i = 0;i<100;i++){
            try {
                queue.put(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final CountDownLatch c = new CountDownLatch(3);
        Consumer c1= new Consumer(queue ,"c1",c);
        Consumer c2= new Consumer(queue ,"c2",c);
        Consumer c3= new Consumer(queue ,"c3",c);
        //线程池管理
       // ExecutorService service = Executors.newFixedThreadPool(50);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute( c1);
        service.execute( c1);
        service.execute( c1);
        service.execute( c2);
        service.execute( c2);
        service.execute( c2);
        service.execute( c2);
        service.execute( c3);
//        for (int t =0 ;t<100;t++){
//            service.submit(c3);
//        }
        try {
            c.await();
            service.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        System.out.println("=============================一共运行了："+usedTime+"秒===================");
    }
}

//消费者
 class Consumer implements Runnable{
     private  ArrayBlockingQueue<Integer> queue ;
     private String name ;
    private CountDownLatch countDownLatch;
    public Consumer(ArrayBlockingQueue<Integer> queue,String name,CountDownLatch countDownLatch){
        this.queue =queue;
        this.name =name ;
        this.countDownLatch = countDownLatch;
    }
    public void run(){
        while (true ){
            try {
                if(queue.size()==0){
                    countDownLatch.countDown();
                    break;
                }
                System. out .println("当前线程:"+Thread.currentThread().getName()+" 消费:" +queue.take());
            } catch (InterruptedException e1 ) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                Thread. sleep(1000);
            } catch (InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}