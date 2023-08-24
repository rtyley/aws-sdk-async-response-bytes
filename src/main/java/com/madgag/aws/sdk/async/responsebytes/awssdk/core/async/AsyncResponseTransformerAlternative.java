package com.madgag.aws.sdk.async.responsebytes.awssdk.core.async;

import com.madgag.aws.sdk.async.responsebytes.awssdk.core.internal.async.ByteArrayAsyncResponseTransformerAlternative;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;

public class AsyncResponseTransformerAlternative {
    public static <ResponseT> AsyncResponseTransformer<ResponseT, ResponseBytes<ResponseT>> toBytes() {
        return new ByteArrayAsyncResponseTransformerAlternative<>();
    }
}
