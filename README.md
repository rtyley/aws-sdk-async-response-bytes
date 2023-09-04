# aws-sdk-async-response-bytes

_Reducing memory consumption of the [AWS SDK for Java v2](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html) when loading S3 objects into memory_

This repo is for evaluation of different approaches to reducing memory consumption of the AWS SDK Java v2 [async](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/asynchronous.html)
method
[`AsyncResponseTransformer.toBytes()`](https://github.com/aws/aws-sdk-java-v2/blob/69f7191252c26b351f7fb1c5f031948dac43e4c9/core/sdk-core/src/main/java/software/amazon/awssdk/core/async/AsyncResponseTransformer.java#L204-L206).
The resulting PR is https://github.com/aws/aws-sdk-java-v2/pull/4355.

Approaches under consideration for reducing memory usage:

* Change A: Optimise byte storage, comes in two successive parts:
  * [**A1**](https://github.com/rtyley/aws-sdk-async-response-bytes/pull/8): Use the Content Length to initialise the `ByteArrayOutputStream` with **a byte array of the right size**.
  * [**A2**](https://github.com/rtyley/aws-sdk-async-response-bytes/pull/10): Use a simple fixed-size byte array in preference to a `ByteArrayOutputStream`
* Change [**B**](https://github.com/rtyley/aws-sdk-async-response-bytes/pull/12): Avoid performing a byte array copy when creating the `ResponseBytes` instance

Approaches A & B can be considered individually, but work much better when combined.

## Automated Memory-Consumption tests

The test script [`findMinMem.sh`](https://github.com/rtyley/aws-sdk-async-response-bytes/blob/ab9307fee6d4965cce0cba60c439035a22cf2d98/findMinMem.sh)
repeatedly downloads a [**258 MB**](https://github.com/rtyley/aws-sdk-async-response-bytes/blob/eb84c15259f947e91c442eb0b8eda68b8cdbefbb/src/main/java/com/madgag/aws/sdk/async/responsebytes/KnownS3Object.java#L7)
object from S3 into memory, while varying the amount of Java heap memory allocated with `-Xmx`,
to find the amount of memory necessary for the download to consistently succeed with the given
approach.

These are the resulting memory requirements found, in MB, for all possible permutations
of the fixes:

|           | Exclude A                                                                                                  | A1                                                                                                        | A1+A2                                                                                                     |
|-----------|------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| Exclude B | [1070](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058076293#summary-16439864723) | [934](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058092753#summary-16439895976) | [644](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058095645#summary-16439901557) |
| Include B | [787](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058093770#summary-16439898197)  | [657](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058097434#summary-16439905403) | [357](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058098624#summary-16439907698) |

From the results, it's clear that we need all 3 changes (A1, A2, & B) in order
to get the lowest memory usage.

### Test details

* JVM invocation: [forked](https://github.com/rtyley/aws-sdk-async-response-bytes/blob/3d8e86870a86e035189d6f88b4e41e42be72ca82/build.sbt#L1) (for each invocation of `GetObject`)
* Invocation repetition count for success: [20](https://github.com/rtyley/aws-sdk-async-response-bytes/blob/3d8e86870a86e035189d6f88b4e41e42be72ca82/build.sbt#L17)
* SdkAsyncHttpClient: [`AwsCrtAsyncHttpClient`](https://github.com/rtyley/aws-sdk-async-response-bytes/blob/3d8e86870a86e035189d6f88b4e41e42be72ca82/src/main/java/com/madgag/aws/sdk/async/responsebytes/Main.java#L39) ([the AWS CRT-based HTTP client](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/http-configuration-crt.html))
* Download integrity: [SHA-256 check against known hash](https://github.com/rtyley/aws-sdk-async-response-bytes/blob/main/src/main/java/com/madgag/aws/sdk/async/responsebytes/Main.java#L30)
