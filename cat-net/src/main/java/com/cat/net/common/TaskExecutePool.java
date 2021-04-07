package com.cat.net.common;

import org.springframework.scheduling.annotation.EnableAsync;

//@Configuration
//@EnableAsync
public class TaskExecutePool {

//	@Autowired
//	private TaskThreadPoolConfig config;

//	/**
//	 * 启动时加载的线程池, 使用注解实现异步的类, 只能通过其他内调用才会开启异步.
//	 * 
//	 * @return
//	 * @return Executor
//	 * @date 2018年12月27日下午4:40:12
//	 */
//	@Bean
//	public Executor taskAsyncPool() {
////		int cpuNums = Runtime.getRuntime().availableProcessors();
////		int nThreads = (cpuNums * 2);
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(config.getCorePoolSize());
//		executor.setMaxPoolSize(config.getMaximumPoolSize());
//		executor.setQueueCapacity(config.getQueueCapacity());
//		executor.setKeepAliveSeconds(config.getKeepAliveTime());
//		executor.setThreadNamePrefix("Executor-Service-");
//		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
//		executor.initialize();
//		return executor;
//	}

}
