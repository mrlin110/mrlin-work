package com.mrlin.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description:
 * @Author: ljm
 * @Date: 2020/9/25 17:54
 * @Version: 1.0
 */
public class ConditionDemo {

    private ReentrantLock lock =new ReentrantLock();
    private Condition condition =lock.newCondition();

    public  void  method1(){
        lock.lock();
        try {
            System.out.println("条件不满足，开始await");
            condition.await();
            System.out.println("条件满足了，开始执行后续的任务");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public  void  method2(){
        lock.lock();
        try {
            System.out.println("准备唤醒其他线程");
            condition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionDemo conditionDemo =new ConditionDemo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {

                }
                conditionDemo.method2();
            }
        }).start();
        conditionDemo.method1();
    }
}
