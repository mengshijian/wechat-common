package com.cootf.wechat.support;

import com.cootf.wechat.api.TokenAPI;
import com.cootf.wechat.bean.token.Token;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author : mengsj
 * 基于redis的普通access_token管理器，实现token自动刷新功能
 */
public class JedisTokenManager {

  private static final Logger logger = LoggerFactory.getLogger(JedisTokenManager.class);

  private static ScheduledExecutorService scheduledExecutorService;

  private static Map<String, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();

  private static JedisPool pool = new JedisPool();

  private static int poolSize = 2;

  private static boolean daemon = Boolean.TRUE;

  /**
   * 初始化 scheduledExecutorService
   */
  private static void initScheduledExecutorService() {
    logger.info("daemon:{},poolSize:{}", daemon, poolSize);
    scheduledExecutorService = Executors.newScheduledThreadPool(poolSize, arg0 -> {

      Thread thread = Executors.defaultThreadFactory().newThread(arg0);
      //设置守护线程
      thread.setDaemon(daemon);
      return thread;
    });
  }

  /**
   *设置redis连接池
   * @param pool
   */
  public static void setPool(JedisPool pool) {
    JedisTokenManager.pool = pool;
  }

  /**
   * 设置线程池
   *
   * @param poolSize poolSize
   */
  public static void setPoolSize(int poolSize) {
    JedisTokenManager.poolSize = poolSize;
  }

  /**
   * 设置线程方式
   *
   * @param daemon daemon
   */
  public static void setDaemon(boolean daemon) {
    JedisTokenManager.daemon = daemon;
  }

  /**
   * 初始化token 刷新，每118分钟刷新一次。
   *
   * @param appid appid
   * @param secret secret
   */
  public static void init(final String appid, final String secret) {
    init(appid, secret, 0, 60 * 118);
  }

  /**
   * 初始化token 刷新，每118分钟刷新一次。
   *
   * @param appid appid
   * @param secret secret
   * @param initialDelay 首次执行延迟（秒）
   * @param delay 执行间隔（秒）
   */
  public static void init(final String appid, final String secret, int initialDelay,
      int delay) {
    if (scheduledExecutorService == null) {
      initScheduledExecutorService();
    }
    if (futureMap.containsKey(appid)) {
      futureMap.get(appid).cancel(true);
    }
    //立即执行一次
    if (initialDelay == 0) {
      doRun(appid, secret,delay-60);
    }
    ScheduledFuture<?> scheduledFuture = scheduledExecutorService
        .scheduleWithFixedDelay(new Runnable() {
          @Override
          public void run() {
            doRun(appid, secret,delay-60);
          }
        }, initialDelay == 0 ? delay : initialDelay, delay, TimeUnit.SECONDS);
    futureMap.put(appid, scheduledFuture);
    logger.info("appid:{}", appid);
  }

  private static void doRun(final String appid, final String secret,int expire) {
    try {
      Jedis jedis = pool.getResource();
      Token token = TokenAPI.token(appid, secret);
      jedis.setex(appid,expire,token.getAccessToken());
      logger.info("ACCESS_TOKEN refurbish with appid:{}", appid);
    } catch (Exception e) {
      logger.error("ACCESS_TOKEN refurbish error with appid:{}", appid);
      logger.error("", e);
    }
  }

  /**
   * 取消 token 刷新
   */
  public static void destroyed() {
    scheduledExecutorService.shutdownNow();
    logger.info("destroyed");
  }

  /**
   * 取消刷新
   *
   * @param appid appid
   */
  public static void destroyed(String appid) {
    if (futureMap.containsKey(appid)) {
      futureMap.get(appid).cancel(true);
      logger.info("destroyed appid:{}", appid);
    }
  }

  /**
   * 获取 access_token
   *
   * @param appid appid
   * @return token
   */
  public static String getToken(String appid) {
    return pool.getResource().get(appid);
  }
}
