package com.fosss.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author: fosss
 * Date: 2023/10/1
 * Time: 14:44
 * Description:
 */
public class BlockingQueueTest {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        new Producer(queue).start();
        new Consumer(queue).start();
        new Consumer(queue).start();
        new Consumer(queue).start();
    }
}

/**
 * 消费者
 */
class Consumer extends Thread {
    BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                queue.take();
                System.out.println(currentThread().getName() + "消费：" + queue.size());
                sleep(new Random().nextInt(1000));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 生产者
 */
class Producer extends Thread {
    BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                queue.put(i);
                System.out.println(currentThread().getName() + "生产：" + queue.size());
                sleep(20);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}