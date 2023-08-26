package com.madgag.aws.sdk.async.responsebytes.awssdk.utils;

import java.nio.ByteBuffer;

public class BinaryUtilsAlternative {
    /**
     * This behaves identically to {@link software.amazon.awssdk.utils.BinaryUtils#copyBytesFrom(ByteBuffer)}, except
     * that the bytes are copied to the supplied destination array, at the supplied destination offset.
     */
    public static int copyBytes(ByteBuffer bb, byte[] dest, int destOffset) {
        if (bb == null) {
            return 0;
        }

        int remaining = bb.remaining();
        if (bb.hasArray()) {
            System.arraycopy(bb.array(), bb.arrayOffset() + bb.position(), dest, destOffset, remaining);
        } else {
            bb.asReadOnlyBuffer().get(dest, destOffset, remaining);
        }
        return remaining;
    }
}
