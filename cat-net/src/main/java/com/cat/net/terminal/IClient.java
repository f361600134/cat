package com.cat.net.terminal;

/**
 * 客户端接口, 用于客户端
 * @author Jeremy
 */
public interface IClient {
	
  /**
   * 获取客户端编号编号
   * @return
   */
  public String getClientId();

  /**
   * 启动客户端连接
   * @return
   * @throws Exception
   */
  public boolean connect() throws Exception;
  
  /**
   * 友好的断开连接,不可以接收请求,把当前请求完成后,断开连接.
   */
  public void disConnect() throws Exception;

  /**
   * 立刻关闭客户端连接
   * @throws Exception
   */
  public void disConnectNow() throws Exception;
  
  /**
   * 尝试重连
   * @throws Exception
   */
  public void tryConnect() throws Exception;
  
  /**
   * 检查连接
   * @throws Exception
   */
  public void checkConnect() throws Exception;
  
  /**
   * 客户端是否运行
   * @return
   */
  public boolean isRunning();

}
