package com.atena.redis;

import com.atena.exceptions.exception.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;

public abstract class RedisCacheTemplate<T> {

  protected static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

  public abstract String generateKey(String param);

  public String generateKeyForScan(String param) {
    return generateKey(param) + ":*";
  }

  public abstract long expirationTime();

  public abstract Class<T> classDef();

  private String scanKeys(String match, Jedis jedis) {
    final var params = new ScanParams().match(match);
    final var listKeys = jedis.scan(ScanParams.SCAN_POINTER_START, params).getResult();

    if (listKeys.isEmpty()) {
      return null;
    }

    return listKeys.getFirst();
  }

  public void saveWithExpiration(T object, String param) {
    try (final Jedis jedis = RedisConnector.getRedis()) {
      final String json = OBJ_MAPPER.writeValueAsString(object);
      jedis.setex(generateKey(param), expirationTime(), json);
    } catch (Exception e) {
      throw new BadRequestException(e);
    }
  }

  public void save(T object, String param) {
    try (final Jedis jedis = RedisConnector.getRedis()) {
      final String json = OBJ_MAPPER.writeValueAsString(object);
      jedis.set(generateKey(param), json);
    } catch (Exception e) {
      throw new BadRequestException(e);
    }
  }

  public T load(String match) {
    try (final Jedis jedis = RedisConnector.getRedis()) {
      final String key = scanKeys(generateKeyForScan(match), jedis);

      if (key == null) {
        return null;
      }

      final String json = jedis.get(key);
      return json != null ? OBJ_MAPPER.readValue(json, classDef()) : null;
    } catch (Exception e) {
      throw new BadRequestException(e);
    }
  }

  public void delete(String match) {
    try (final Jedis jedis = RedisConnector.getRedis()) {
      final String key = generateKey(match);
      jedis.del(key);
    } catch (Exception e) {
      throw new BadRequestException(e);
    }
  }
}
