package com.shanzha.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

/**
 * @author zhoubin
 * @date 2024/9/5
 */
public class JsonUtil {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        // 支持 Java 8 时间类型
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 支持 Long、BigDecimal 等转 String（防止 JS 精度丢失）
        jsonMapper.registerModule(bigIntToStrsimpleModule());
    }

    public static JsonNode toNode(String str) {
        try {
            return jsonMapper.readTree(str);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static <T> T toObj(String str, Class<T> clz) {
        try {
            return jsonMapper.readValue(str, clz);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static <T> String toStr(T t) {
        try {
            return jsonMapper.writeValueAsString(t);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 序列换成json时,将所有的long变成string
     * 因为js中得数字类型不能包含所有的java long值
     */
    public static com.fasterxml.jackson.databind.Module bigIntToStrsimpleModule() {
        SimpleModule module = new SimpleModule();

        // Long 类型及其原始类型
        JsonSerializer<Long> longSerializer = stringSerializer(String::valueOf);
        module.addSerializer(Long.class, longSerializer);
        module.addSerializer(Long.TYPE, longSerializer);
        module.addSerializer(Long[].class, arraySerializer(String::valueOf));
        module.addSerializer(
            long[].class,
            new JsonSerializer<long[]>() {
                @Override
                public void serialize(long[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    if (value == null) {
                        gen.writeNull();
                        return;
                    }
                    gen.writeStartArray();
                    for (long l : value) {
                        gen.writeString(String.valueOf(l));
                    }
                    gen.writeEndArray();
                }
            }
        );

        // BigDecimal
        JsonSerializer<BigDecimal> bigDecimalSerializer = stringSerializer(BigDecimal::toString);
        module.addSerializer(BigDecimal.class, bigDecimalSerializer);
        module.addSerializer(BigDecimal[].class, arraySerializer(BigDecimal::toString));

        // BigInteger
        JsonSerializer<BigInteger> bigIntegerSerializer = stringSerializer(BigInteger::toString);
        module.addSerializer(BigInteger.class, bigIntegerSerializer);
        module.addSerializer(BigInteger[].class, arraySerializer(BigInteger::toString));

        return module;
    }

    private static <T> JsonSerializer<T> stringSerializer(Function<T, String> func) {
        return new JsonSerializer<T>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                } else {
                    gen.writeString(func.apply(value));
                }
            }
        };
    }

    private static <T> JsonSerializer<T[]> arraySerializer(Function<T, String> func) {
        return new JsonSerializer<T[]>() {
            @Override
            public void serialize(T[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                    return;
                }
                gen.writeStartArray();
                for (T t : value) {
                    if (t == null) {
                        gen.writeNull();
                    } else {
                        gen.writeString(func.apply(t));
                    }
                }
                gen.writeEndArray();
            }
        };
    }
}
