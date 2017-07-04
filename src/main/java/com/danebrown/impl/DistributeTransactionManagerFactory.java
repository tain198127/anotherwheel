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
package com.danebrown.impl;

import com.danebrown.inf.IDistributedTransactionBuilder;
import com.danebrown.inf.IDistributedTransactionManager;

/**
 * Created by dane on 2017/7/3.
 */
public class DistributeTransactionManagerFactory {
    private static IDistributedTransactionManager innerinstance;

    public static IDistributedTransactionManager newInstance() {
        if (innerinstance == null) {
            innerinstance = new DefaultDistributeTransactionManager();
        }
        return innerinstance;
    }

    public IDistributedTransactionBuilder builder() {
        return new DefaultDistributedTransactionBuilder();
    }
}
