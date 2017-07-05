/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by dane on 2017/7/4.
 */
public class MainTest {
    volatile ConcurrentSkipListSet<ClassLoader> set = new ConcurrentSkipListSet<>();
    static CuratorFramework cf = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    private static final String LOCK_PATH = "/curator_lock_path";
    private static final String COUNTER_PATH = "/curator_counter_path";
    private static final String BARRIER_PATH = "/curator_barrier_path";
    private static final String DOUBLE_BARRIER_PATH = "/curator_double_barrier_path";
    private static DistributedBarrier barrier = new DistributedBarrier(cf, BARRIER_PATH);
    private static int qty = 4;
    private static final String DOUBLE_BARRIER_MULTI_PATH = "/curator_double_barrier_multi_path";
    private static Logger logger = LogManager.getLogger(MainTest.class);

    private static DistributedDoubleBarrier doubleBarrier = new DistributedDoubleBarrier(cf, DOUBLE_BARRIER_PATH, qty);

    @Test
    public void doubleBarrierMultiTest() throws InterruptedException, ExecutionException {
        cf.start();
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        CountDownLatch countDownLatch = new CountDownLatch(qty + 3);

        ExecutorService es = new ThreadPoolExecutor(3, 10, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10));
        LinkedBlockingQueue<Future<String>> futuresResult = new LinkedBlockingQueue<>();
        CompletionService<String> cs = new ExecutorCompletionService<String>(es, futuresResult);

        for (int i = 0; i < qty + 3; i++) {
            //final DistributedDoubleBarrier multi_barrier = new DistributedDoubleBarrier(cf, DOUBLE_BARRIER_MULTI_PATH,qty);
            cs.submit(() -> {
                String threadCtx = String.format("current thread:[%d:%s]", Thread.currentThread().getId(), Thread.currentThread().getName());
                System.out.println("enter" + threadCtx);
                Random rdm = new Random();
                int rdmInt = rdm.nextInt(1000);
                Thread.sleep(rdmInt);
                //multi_barrier.leave();
                System.out.println("leave" + threadCtx + ":" + rdmInt);
                countDownLatch.countDown();
                return threadCtx;
            });


        }

        countDownLatch.await();
        futuresResult.stream().forEach(f -> {
            try {
                System.out.println(f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        es.awaitTermination(3, TimeUnit.SECONDS);
        es.shutdown();
        while (!es.isShutdown()) {
            Thread.sleep(100);
        }
        cf.close();
    }
    @Test
    public void doubleBarrierPathTest() throws Exception {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        cf.start();
        doubleBarrier.enter();
        System.out.println("enter:" + pid + ";time:" + DateTime.now());
        Thread.sleep(5000);
        doubleBarrier.leave();
        System.out.println("leave:" + pid + ";time:" + DateTime.now());
        cf.close();
    }

    @Test
    public void distribute_account() throws Exception {

        String pid = ManagementFactory.getRuntimeMXBean().getName();
        cf.start();
        DistributedAtomicInteger integer = new DistributedAtomicInteger(cf, COUNTER_PATH, new RetryNTimes(3, 1000));
        barrier.setBarrier();
        AtomicValue<Integer> val = integer.increment();
        if (val.succeeded() && val.postValue() == 2) {
            barrier.removeBarrier();
        } else {
            barrier.waitOnBarrier();
        }

        System.out.println(integer.get().preValue() + ":" + integer.get().postValue() + "" + integer.get());
        System.out.println(pid + ":" + DateTime.now());
        cf.close();

    }

    @Test
    public void zkTest() throws Exception {

        cf.start();
        InterProcessMutex mutex = new InterProcessMutex(cf, LOCK_PATH);
        mutex.acquire();

        System.out.println(DateTime.now());
        System.in.read();
        mutex.release();
    }
}
