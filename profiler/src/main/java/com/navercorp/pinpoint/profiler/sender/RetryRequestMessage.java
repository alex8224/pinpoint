/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.sender;


import com.navercorp.pinpoint.rpc.FutureListener;
import com.navercorp.pinpoint.rpc.ResponseMessage;

/**
 * @author Woonduk Kang(emeroad)
 */
public class RetryRequestMessage<T> implements RequestMessage<T> {
    private final T message;
    private final int retryCount;


    RetryRequestMessage(T message, int retryCount) {
        this.message = message;
        this.retryCount = retryCount;
    }


    @Override
    public T getMessage() {
        return message;
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public FutureListener<ResponseMessage> getFutureListener() {
        return null;
    }
}