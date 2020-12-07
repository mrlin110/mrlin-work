package com.mrlin.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: ljm
 * @Date: 2020/9/27 10:58
 * @Version: 1.0
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier =new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " 完成最后任务");
            }
        });
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0;i<5;i++){
            service.execute(new Demo2(cyclicBarrier));
        }
    }
}

//消费者
class Demo2 implements Runnable{
    private CyclicBarrier cyclicBarrier;

    public Demo2(CyclicBarrier cyclicBarrier){
      this.cyclicBarrier = cyclicBarrier;
    }
    public void run(){

        try {
            Thread.sleep(1000);
            System.out.println("线程名"+Thread.currentThread().getName()+"开始等待其他线程准备");
            cyclicBarrier.await();
            System.out.println("线程名"+Thread.currentThread().getName()+"开始运行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
