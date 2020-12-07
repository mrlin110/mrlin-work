package com.mrlin.thread;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: ljm
 * @Date: 2020/9/24 11:37
 * @Version: 1.0
 */
public class ArrayListDeom {


    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<Integer>();
       //ArrayBlockingQueue <Integer> queue = new ArrayBlockingQueue<Integer>(500);
        for (int i = 0;i<499;i++){
                list.add(i);
        }

        Consumer1 c1= new Consumer1(list ,"c-1" );
        Consumer1 c2= new Consumer1(list ,"c-2" );
        Consumer1 c3= new Consumer1(list ,"c-3" );
        //线程池管理
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute( c1);
        service.execute( c2);
        service.execute( c3);
    }
}

//消费者
 class Consumer1 implements Runnable{
     private  ArrayList<Integer> list ;
     private String name ;
    public Consumer1(ArrayList<Integer> list,String name){
        this.list =list;
        this.name =name ;
    }
    public void run(){
         for(int i =0 ;i<list.size();i++){
             System. out .println("当前线程:"+name+" 消费:" +list.get(i));
         }
        }
    }
