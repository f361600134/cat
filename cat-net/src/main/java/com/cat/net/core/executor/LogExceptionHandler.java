package com.cat.net.core.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.ExceptionHandler;

/**
 * DisruptorExecutor异常处理,异常记录日志 .
 * 
 * @date 2015年9月26日 下午1:15:24
 *
 */
public class LogExceptionHandler implements ExceptionHandler<Object> {

  private static final Logger LOG = LoggerFactory.getLogger(LogExceptionHandler.class);

  @Override
  public void handleEventException(final Throwable ex, final long sequence, final Object event) {
    LOG.error("Exception processing: " + sequence + " " + event, ex);
  }

  @Override
  public void handleOnStartException(final Throwable ex) {
    LOG.error("Exception during onStart()", ex);
  }

  @Override
  public void handleOnShutdownException(final Throwable ex) {
    LOG.error("Exception during onShutdown()", ex);
  }

}
