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

import com.danebrown.def.Define;
import com.danebrown.entity.TransactionKey;
import com.danebrown.inf.IDistributedTransaction;

/**
 * Created by dane on 2017/7/4.
 */
public class DefaultDistributedTransaction implements IDistributedTransaction {
    @Override
    public TransactionKey getCurrentTransKey() {
        TransactionKey key = null;
        Object transKey = com.alibaba.dubbo.rpc.RpcContext.getContext().get(Define.TRANSACTION_KEY);

        if (transKey != null && transKey instanceof TransactionKey) {
            key = (TransactionKey) transKey;

        } else if (transKey == null) {
            key = new TransactionKey();
            com.alibaba.dubbo.rpc.RpcContext.getContext().set(Define.TRANSACTION_KEY, key);
        }
        return key;
    }
}
