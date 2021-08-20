package com.cat.net.terminal;

/**
 * @Description 服务器端服务接口
 * @author Jeremy
 */
public interface IServer {
	
  // 获取服务编号
  public String getServerId();

  // 启动服务
  public boolean startServer() throws Exception;

  // 关闭服务
  public void stopServer() throws Exception;

  // 服务是否运行
  public boolean isRunning();
  
}
