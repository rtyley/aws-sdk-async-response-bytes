package com.madgag.aws.sdk.async.responsebytes.awssdk.core.async;

import com.madgag.aws.sdk.async.responsebytes.awssdk.core.internal.async.ByteArrayAsyncResponseTransformerAlternative;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;

import java.util.Optional;
import java.util.function.Function;

public class AsyncResponseTransformerAlternative {
    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseBytes<ResponseT>> toBytes() {
        return new ByteArrayAsyncResponseTransformerAlternative<>(Optional.empty());
    }

    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseBytes<ResponseT>> toBytes(Function<ResponseT, Integer> f) {
        return new ByteArrayAsyncResponseTransformerAlternative<>(Optional.of(f));
    }
}
