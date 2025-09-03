package com.atena.redis;

import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisConnector {

  private RedisConnector() {}

  private static final Object KEY = new Object();
  private static final int MAX_RETRIES = 5;
  private static final Logger LOGGER = LoggerFactory.getLogger(RedisConnector.class);

  private static JedisPool jedisPool;

  public static JedisPool getJedisPool() {

    if (jedisPool != null) {
      return jedisPool;
    }

    synchronized (KEY) {
      if (jedisPool != null) {
        return jedisPool;
      }

      try {
        initRedis();
        return jedisPool;
      } catch (JedisConnectionException ignored) {
        return retryConnection();
      }
    }
  }

  public static Jedis getRedis() {
    final var pool = getJedisPool();
    return pool.getResource();
  }

  private static JedisPool retryConnection() {
    LOGGER.info("Retrying redis connection");
    for (int count = 0; count <= MAX_RETRIES; count++) {
      try {
        LOGGER.info("Starting attempt {}", count);
        TimeUnit.MILLISECONDS.sleep(200);
        initRedis();
        LOGGER.info("Attempt {} succeeded", count);
        return jedisPool;
      } catch (JedisConnectionException e) {
        LOGGER.info("Attempt {} failed", count);
        if (count == MAX_RETRIES) {
          throw e;
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    return jedisPool;
  }

  private static void initRedis() {
    LOGGER.info("Initializing JedisPool");

    final var config = ConfigProvider.getConfig();

    final int redisPort = config.getValue("REDIS_PORT", Integer.class);
    final String redisUrl = config.getValue("REDIS_URL", String.class);
    final String redisPassword =
        config.getOptionalValue("REDIS_PASSWORD", String.class).orElse(null);
    final String redisUser = config.getOptionalValue("REDIS_USER", String.class).orElse("default");

    try {
      jedisPool = new JedisPool(buildPoolConfig(), redisUrl, redisPort, redisUser, redisPassword);
      LOGGER.info("Connected to redis with success");
    } catch (Exception e) {
      LOGGER.error("Failed to init redis: ", e);
    }
  }

  private static JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);

    poolConfig.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");

    return poolConfig;
  }
}
