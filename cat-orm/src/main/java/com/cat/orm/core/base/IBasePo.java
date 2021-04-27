package com.cat.orm.core.base;

public interface IBasePo {
	
	public String poName();
	
	/**
	 * primary key, 对应数据库的主键,唯一主键.可以为null.
	 * 比如排行榜模块, 是复合主键.生成的唯一主键就为空
	 * @return
	 */
	public Object key();
	
	/**
	 * 数据库主键的列名
	 * @return
	 */
	public String keyColumn();
	
	/**
	 * 缓存二级ID
	 * 用于缓存的主键,  indexColumn()组合
	 * 直接用indexValues的组合生成最终的key
	 * 生成规则: key = indexVal1 +":"+ indexVal2 + ":"+...+ indexValn
	 * @return
	 */
	public String cacheId();
	
	/**
	 * 索引列的值
	 * @return
	 */
	public Object[] indexValues();
	
	/**
	 * 索引列, 对应数据库的索引,仅用于查询,删除
	 * 索引列  = 主键 + 索引列.
	 * 比如,排行榜表rank{serverId, rankType, playerId, value1, value2}
	 * serverId,rankType,playerId为复合主键.那么index就为这三个字段.删除数据就需要使用复合主键作为字段
	 * @return
	 */
	public String[] indexColumn();
	
	/**
	 * 所有属性列
	 * @return
	 */
	public String[] props();

	/**
	 * 所有属性列的值
	 * @return
	 */
	public Object[] propValues();
	
	/**
	 * 存储前操作, 这个操作因为是异步执行, 会引发线程安全问题, 只能放在玩家线程去执行.
	 */
	public void beforeSave();
	
	/**
	 * 加载后操作
	 */
	public void afterLoad();
	
	/**
	 * 	当前对象实体
	 * @return
	 */
	default public Class<?> clazz(){
		return null;
	}
	
}
