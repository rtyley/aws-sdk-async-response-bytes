package com.madgag.aws.sdk.async.responsebytes;

import com.google.common.hash.HashCode;

public record KnownS3Object(String bucket, String key, HashCode sha256) {
    public static KnownS3Object BIG = new KnownS3Object(
            "sagemaker-sample-files", "datasets/tabular/customer-churn/customer-churn-data.zip",
            HashCode.fromString("49e94915ca0de3368a0f715c088a38596c84f64a65f77d66c79c648f356c8008")
    );

    public static KnownS3Object SMALL = new KnownS3Object(
            "sagemaker-sample-files", "datasets/tabular/xgb-churn/test-dataset.csv",
            HashCode.fromString("1f395e2e95f746482989c9c943eabe0d9f6c69b505a05e067f6ae87e967aa0fc")
    );
}
