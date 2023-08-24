package com.madgag.aws.sdk.async.responsebytes.awssdk.services.s3;

import com.madgag.aws.sdk.async.responsebytes.awssdk.core.async.AsyncResponseTransformerAlternative;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class AsyncS3ResponseTransformer {

    public static AsyncResponseTransformer<GetObjectResponse, ResponseBytes<GetObjectResponse>> toBytes() {
        return AsyncResponseTransformerAlternative.toBytes(r -> r.contentLength().intValue());
    }
}
