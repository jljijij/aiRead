package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotifyMsgTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static NotifyMsg getNotifyMsgSample1() {
        return new NotifyMsg()
            .id(1L)
            .relatedId("relatedId1")
            .relatedInfo("relatedInfo1")
            .operateUserId(1L)
            .operateUserName("operateUserName1")
            .operateUserPhoto("operateUserPhoto1")
            .type(1)
            .msg("msg1")
            .state(1);
    }

    public static NotifyMsg getNotifyMsgSample2() {
        return new NotifyMsg()
            .id(2L)
            .relatedId("relatedId2")
            .relatedInfo("relatedInfo2")
            .operateUserId(2L)
            .operateUserName("operateUserName2")
            .operateUserPhoto("operateUserPhoto2")
            .type(2)
            .msg("msg2")
            .state(2);
    }

    public static NotifyMsg getNotifyMsgRandomSampleGenerator() {
        return new NotifyMsg()
            .id(longCount.incrementAndGet())
            .relatedId(UUID.randomUUID().toString())
            .relatedInfo(UUID.randomUUID().toString())
            .operateUserId(longCount.incrementAndGet())
            .operateUserName(UUID.randomUUID().toString())
            .operateUserPhoto(UUID.randomUUID().toString())
            .type(intCount.incrementAndGet())
            .msg(UUID.randomUUID().toString())
            .state(intCount.incrementAndGet());
    }
}
