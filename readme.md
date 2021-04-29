# 前言  
每个项目都会有一个代号，这个项目代号为**cat**[猫]。

**cat**是一个开源的游戏服务器框架，纯Java实现,目前有两个核心库组成, 分别是cat-orm与cat-net,只提供持久层与网络层的支持.
coral-orm封装了Mysql以及Redis的操作, 未来预计(其实懒得做)支持Mongo, 作为可拆卸的持久层支持,
coral-net基于Netty, Disruptor处理超高并发消息. 为了集成便捷, 基于Sring-context构建,


## cat的功能 
 1. 封装完善的coral-orm持久工程，所有pojo对象继承BasePo，实现了公共的保存修改，在系统启动时进行扫描注册，保存直接通过调用父类的save()方法即可。在业务逻辑层根据需要，注册异步或同步的持久化方式。开发人员不需要关心内部实现。持久层也基于redisson封装了对redis的操作，redis作为二级缓存时，数据以redis数据为准。否则以mysql数据为准。可根据需要配置。   
 2. 封装完善的coral-net网络工程，目前基于netty封装好了tcp，websocket, http三种服务，根据业务需要开启服务，也可以全部开启。网络层基于ProtoBuf构建, 版本可以根据需要进行升级, 也可以使用开发人员自己的一套网络协议，直接在业务逻辑层注册即可。   
 
cat提供的是网络通讯以及持久化的支持,使开发人员能做到开箱即用, 极大的方便了开发人员做业务开发, 在框架层支持了玩家线程安全, 无需考虑玩家线程的竞争,

还有其他功能，就不一一介绍了  
cat是一个强大的，灵活的的分布式架构，可以满足几乎所有游戏类型的需求。如果大家有什么好的意见或建议.  
请联系作者:  
QQ: 361600134
Wechat: huuzii   



