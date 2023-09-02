# aws-sdk-async-response-bytes

The test script [`findMinMem.sh`](https://github.com/rtyley/aws-sdk-async-response-bytes/blob/ab9307fee6d4965cce0cba60c439035a22cf2d98/findMinMem.sh)
repeatedly downloads a [**258 MB**](https://github.com/rtyley/aws-sdk-async-response-bytes/blob/eb84c15259f947e91c442eb0b8eda68b8cdbefbb/src/main/java/com/madgag/aws/sdk/async/responsebytes/KnownS3Object.java#L7)
object from S3 into memory, while varying the amount  of Java heap memory allocated with `-Xmx`,
to find the amount of memory necessary for the download to consistently succeed.

These are the resulting memory requirements found, in MB, for all possible permutations
of the fixes:

|           | Exclude A                                                                                                  | A1                                                                                                        | A1+A2                                                                                                     |
|-----------|------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| Exclude B | [1070](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058076293#summary-16439864723) | [934](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058092753#summary-16439895976) | [644](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058095645#summary-16439901557) |
| Include B | [787](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058093770#summary-16439898197)  | [657](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058097434#summary-16439905403) | [357](https://github.com/rtyley/aws-sdk-async-response-bytes/actions/runs/6058098624#summary-16439907698) |

From the results, it's clear that we need all 3 changes (A1, A2, & B) in order
to get the lowest memory usage.