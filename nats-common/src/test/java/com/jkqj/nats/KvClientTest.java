package com.jkqj.nats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ContextConfiguration
@Execution(ExecutionMode.SAME_THREAD)
public class KvClientTest {
    @Autowired
    private KvClient client;

    @SneakyThrows
    @Test
    public void addBucketTest() {
        var kv = client.addBucket("my_mini_kv");
        assertTrue(kv.put("key1", "value1".getBytes(StandardCharsets.UTF_8)));
        var value1 = kv.get("key1");
        assertEquals("value1", value1.getString().orElseThrow());

        assertTrue(kv.delete("key1"));
        assertTrue(kv.get("key1").getString().isEmpty());

        assertTrue(kv.put("key2", "value2"));
        assertEquals("value2", kv.get("key2").getString().orElseThrow());

        assertTrue(kv.put("key2", "value21"));
        assertEquals("value21", kv.get("key2").getString().orElseThrow());

        assertTrue(kv.put("key3", 123));
        assertEquals(123, kv.get("key3").getLong().orElseThrow());

    }

    @SneakyThrows
    @Test
    public void valuesTest() {
        var bucket = client.addBucket("my_values_kv");
        assertTrue(bucket.put("int1", Integer.MIN_VALUE));
        assertEquals(Integer.MIN_VALUE, bucket.getInt("int1").orElseThrow());
        assertTrue(bucket.put("int2", Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, bucket.getInt("int2").orElseThrow());
        assertTrue(bucket.put("byte1", Byte.MIN_VALUE));
        assertEquals(Byte.MIN_VALUE, bucket.getByte("byte1").orElseThrow());
        assertTrue(bucket.put("byte2", Byte.MAX_VALUE));
        assertEquals(Byte.MAX_VALUE, bucket.getByte("byte2").orElseThrow());
        assertTrue(bucket.put("long1", Long.MIN_VALUE));
        assertEquals(Long.MIN_VALUE, bucket.getLong("long1").orElseThrow());
        assertTrue(bucket.put("long2", Long.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, bucket.getLong("long2").orElseThrow());
        assertTrue(bucket.put("float1", Float.MIN_VALUE));
        assertEquals(Float.MIN_VALUE, bucket.getFloat("float1").orElseThrow());
        assertTrue(bucket.put("float2", Float.MAX_VALUE));
        assertEquals(Float.MAX_VALUE, bucket.getFloat("float2").orElseThrow());
        assertTrue(bucket.put("short1", Short.MIN_VALUE));
        assertEquals(Short.MIN_VALUE, bucket.getShort("short1").orElseThrow());
        assertTrue(bucket.put("short2", Short.MAX_VALUE));
        assertEquals(Short.MAX_VALUE, bucket.getShort("short2").orElseThrow());
        assertTrue(bucket.put("bytes1", "bytes".getBytes(StandardCharsets.UTF_8)));
        assertEquals(0, Arrays.compare("bytes".getBytes(StandardCharsets.UTF_8), bucket.getBytes("bytes1")));
        assertTrue(bucket.put("double1", Double.MIN_VALUE));
        assertEquals(Double.MIN_VALUE, bucket.getDouble("double1").orElseThrow());
        assertTrue(bucket.put("double2", Double.MAX_VALUE));
        assertEquals(Double.MAX_VALUE, bucket.getDouble("double2").orElseThrow());
        assertTrue(bucket.put("string1", "hello, 中概"));
        assertEquals("hello, 中概", bucket.getString("string1").orElseThrow());
        assertTrue(bucket.put("boolean1", true));
        assertEquals(true, bucket.getBoolean("boolean1").orElseThrow());
        assertTrue(bucket.put("boolean2", false));
        assertEquals(false, bucket.getBoolean("boolean2").orElseThrow());
        assertTrue(bucket.put("time1", LocalTime.MIN));
        assertEquals(LocalTime.MIN, bucket.getTime("time1").orElseThrow());
        assertTrue(bucket.put("time2", LocalTime.MAX));
        assertEquals(LocalTime.MAX, bucket.getTime("time2").orElseThrow());
        var past = LocalDateTime.now().plusYears(-1000);
        assertTrue(bucket.put("date1", past));
        assertEquals(past.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                bucket.getDateTime("date1").orElseThrow().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        var future = LocalDateTime.now().plusYears(1000);
        assertTrue(bucket.put("date2", future));
        assertEquals(future.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                bucket.getDateTime("date2").orElseThrow().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        Map<String, ?> map1 = Map.of("name", "Kafka", "birthday", LocalDateTime.now());
        assertTrue(bucket.putMap("map1", map1));
        assertEquals(map1.get("name"), bucket.getMap("map1").orElseThrow().get("name"));
        var birthday = bucket.getMap("map1").orElseThrow().get("birthday");
        log.debug("birthday {}", birthday);
        assertEquals(map1.get("birthday"), birthday);

        var obj1 = new MyObj("karak", 12, LocalDateTime.now());
        assertTrue(bucket.putObject("obj1", obj1));
        assertEquals(obj1.getName(), bucket.getObject("obj1", MyObj.class).orElseThrow().getName());
        assertEquals(obj1.getAge(), bucket.getObject("obj1", MyObj.class).orElseThrow().getAge());
        assertEquals(obj1.getBirthday().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                bucket.getObject("obj1", MyObj.class).orElseThrow().getBirthday().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        assertTrue(bucket.put("version", "v1"));
        var verVal = bucket.get("version");
        var vsn = verVal.getVersion();
        assertTrue(bucket.cas("version", "v2".getBytes(StandardCharsets.UTF_8), vsn));
        assertEquals("v2", bucket.getString("version").orElseThrow());
        assertFalse(bucket.cas("version", "v3".getBytes(StandardCharsets.UTF_8), vsn));
        assertEquals("v2", bucket.getString("version").orElseThrow());
    }

    @SneakyThrows
    @Test
    public void watchTest() {
        var bucket = client.addBucket("watch_bucket_test");
        CountDownLatch latch = new CountDownLatch(2);
        assertTrue(bucket.watch("test.*", value -> {
            log.debug("got value {}", value.getString().orElse(null));
            assertEquals("hello", value.getString().orElseThrow());
            latch.countDown();
        }));

        bucket.put("test.str", "hello");
        bucket.put("test.str", "hello");
        latch.await();
    }

    @SneakyThrows
    @Test
    public void persistenceTest() {
        var bucket = client.addBucket("my_persistence", true);
        var optional = bucket.get("mystr").getString();
        if (optional.isEmpty()) {
            log.debug("empty put it");
            bucket.put("mystr", "hello file");
        } else {
            log.debug("not empty");
            var value = optional.orElseThrow();
            log.debug(value);
            assertEquals("hello file", value);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyObj {
        String name;
        int age;
        LocalDateTime birthday;
    }
}
