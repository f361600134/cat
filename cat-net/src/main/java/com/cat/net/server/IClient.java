package com.cat.net.server;

/**
 * @Description 服务器服务
 */
public interface IClient {
	
  // 获取服务编号
  public String getServerId();

  // 启动服务
  public boolean startServer() throws Exception;

  // 关闭服务
  public void stopServer() throws Exception;

  // 服务是否运行
  public boolean isRunning();
  
}
