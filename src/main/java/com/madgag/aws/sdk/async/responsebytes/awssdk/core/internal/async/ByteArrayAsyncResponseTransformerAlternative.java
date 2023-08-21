/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.madgag.aws.sdk.async.responsebytes.awssdk.core.internal.async;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.utils.BinaryUtils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static software.amazon.awssdk.utils.FunctionalUtils.invokeSafely;

/**
 * Implementation of {@link AsyncResponseTransformer} that dumps content into a byte array and supports further
 * conversions into types, like strings.
 *
 * This can be created with static methods on {@link AsyncResponseTransformer}.
 *
 * @param <ResponseT> Pojo response type.
 * @see AsyncResponseTransformer#toBytes()
 */
@SdkInternalApi
public final class ByteArrayAsyncResponseTransformerAlternative<ResponseT> implements
        AsyncResponseTransformer<ResponseT, ResponseBytes<ResponseT>> {

    private final Optional<Function<ResponseT, Integer>> knownSize;
    private volatile CompletableFuture<byte[]> cf;
    private volatile ResponseT response;

    public ByteArrayAsyncResponseTransformerAlternative(Optional<Function<ResponseT, Integer>> knownSize) {
        this.knownSize = knownSize;
    }

    @Override
    public CompletableFuture<ResponseBytes<ResponseT>> prepare() {
        cf = new CompletableFuture<>();
        return cf.thenApply(arr -> ResponseBytes.fromByteArrayUnsafe(response, arr));
    }

    @Override
    public void onResponse(ResponseT response) {
        this.response = response;
    }

    @Override
    public void onStream(SdkPublisher<ByteBuffer> publisher) {
        ByteArrayOutputStream baos =
                knownSize.map(f -> new ByteArrayOutputStream(f.apply(response))).orElse(new ByteArrayOutputStream());
        publisher.subscribe(new BaosSubscriber(cf, baos));
    }

    @Override
    public void exceptionOccurred(Throwable throwable) {
        cf.completeExceptionally(throwable);
    }

    static class BaosSubscriber implements Subscriber<ByteBuffer> {
        private final CompletableFuture<byte[]> resultFuture;

        private ByteArrayOutputStream baos;

        private Subscription subscription;

        BaosSubscriber(CompletableFuture<byte[]> resultFuture, ByteArrayOutputStream baos) {
            this.resultFuture = resultFuture;
            this.baos = baos;
        }

        @Override
        public void onSubscribe(Subscription s) {
            if (this.subscription != null) {
                s.cancel();
                return;
            }
            this.subscription = s;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer byteBuffer) {
            invokeSafely(() -> baos.write(BinaryUtils.copyBytesFrom(byteBuffer)));
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            baos = null;
            resultFuture.completeExceptionally(throwable);
        }

        @Override
        public void onComplete() {
            resultFuture.complete(baos.toByteArray());
        }
    }
}
