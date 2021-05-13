package com.cat.orm.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 * @author Jeremy
 * @date 2020年6月29日
 */
@Configuration
public class OrmConfig {
	
	@Value("${spring.redis.host}")
	private String redisHost;
	@Value("${spring.redis.port}")
	private int redisPort;
	@Value("${spring.redis.password}")
	private String redisPassword;
	@Value("${spring.redis.timeout}")
	private long timeout;
	@Value("${spring.redis.database}")
	private int database;
	@Value("${spring.redis.lettuce.pool.max-active}") // 最大连接数
	private int maxActive;
	@Value("${spring.redis.lettuce.pool.max-idle}") // 最大空闲 数
	private int maxIdle;
	@Value("${spring.redis.lettuce.pool.min-idle}") // 最小空闲 数
	private int minIdle;
	@Value("${spring.redis.lettuce.pool.max-wait}") // 连接池等待时间
	private long maxWait;
	@Value("${spring.redis.lettuce.pool.shutdown.timeout}") // 关闭超时
	private long shutdownTimeout;
	
	public String getRedisHost() {
		return redisHost;
	}
	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}
	public int getRedisPort() {
		return redisPort;
	}
	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}
	public String getRedisPassword() {
		return redisPassword;
	}
	public void setRedisPassword(String redisPassword) {
		this.redisPassword = redisPassword;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public int getDatabase() {
		return database;
	}
	public void setDatabase(int database) {
		this.database = database;
	}
	public int getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	public long getMaxWait() {
		return maxWait;
	}
	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}
	public long getShutdownTimeout() {
		return shutdownTimeout;
	}
	public void setShutdownTimeout(long shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}
	
//	//是否开启缓存
//	private boolean enable;
//	//缓存时长,单位(minute),超过这个时长会移除缓存
//	private int duration;
//	//模块缓存数量,按照模块缓存
//	private int maximumSize;
//	//初始容量
//	private int initialSize;
//	//并发等级
//	private int concurrencyLevel;
//	
//	public boolean isEnable() {
//		return enable;
//	}
//	public void setEnable(boolean enable) {
//		this.enable = enable;
//	}
//	
//	public int getDuration() {
//		return duration;
//	}
//	public void setDuration(int duration) {
//		this.duration = duration;
//	}
//	public int getMaximumSize() {
//		return maximumSize;
//	}
//	public void setMaximumSize(int maximumSize) {
//		this.maximumSize = maximumSize;
//	}
//	public int getInitialSize() {
//		return initialSize;
//	}
//	public void setInitialSize(int initialSize) {
//		this.initialSize = initialSize;
//	}
//	public int getConcurrencyLevel() {
//		return concurrencyLevel;
//	}
//	public void setConcurrencyLevel(int concurrencyLevel) {
//		this.concurrencyLevel = concurrencyLevel;
//	}
	
	
	
}
