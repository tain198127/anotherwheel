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
package com.danebrown.entity;


import org.joda.time.DateTime;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dane on 2017/7/4.
 * This class is a context object. invoke by IDistributedTransaction
 * when dubbo invoke a new method, this object will add in to the context.
 */
public class TransactionKey {
    private volatile AtomicReference<DateTime> createTime;
    private volatile AtomicReference<DateTime> lastInvokeTime;
    private volatile AtomicInteger currentCount;

    /**
     * get the first time invoke with the key
     *
     * @return
     */
    public DateTime getCreateTime() {
        if (createTime == null)
            return null;
        return createTime.get();
    }

    /**
     * set the create time, and just set once.
     * when set more than once, the real val is not work
     *
     * @param createTime
     */
    public void setCreateTime(DateTime createTime) {
        if (this.createTime == null)
            this.createTime = new AtomicReference<>();
        this.createTime.set(createTime);
    }

    /**
     * get the last invoke
     *
     * @return
     */
    public DateTime getLastInvokeTime() {
        if (lastInvokeTime == null)
            return null;
        return lastInvokeTime.get();
    }

    /**
     * set the time is now
     */
    public void setLastInvokeTime() {
        this.lastInvokeTime.set(DateTime.now());

    }

    /**
     * set the last time of invoke
     *
     * @param lastInvokeTime
     */
    public void setLastInvokeTime(DateTime lastInvokeTime) {
        if (this.lastInvokeTime == null)
            this.lastInvokeTime = new AtomicReference<>();
        this.lastInvokeTime.set(lastInvokeTime);

    }

    /**
     * get how much time be invoked
     *
     * @return
     */
    public int getCurrentCount() {
        return currentCount.get();
    }

    /**
     * increment one time
     */
    public void incrementCurrentCount() {
        currentCount.incrementAndGet();
    }

    @Override
    public String toString() {
        return "TransactionKey{" +
                "createTime=" + createTime.get().toString() +
                ", lastInvokeTime=" + lastInvokeTime.get().toString() +
                ", currentCount=" + currentCount.get() +
                '}';
    }
}
