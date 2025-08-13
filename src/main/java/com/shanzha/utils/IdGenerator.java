package com.shanzha.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class IdGenerator {

    private final static Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    public static Long nextId()
    {
        return snowflake.nextId(); // 生成唯一ID
    }

}
