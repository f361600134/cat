package com.cat.orm.core.base;

/**
 * POJO类的接口, 实现此接口后, 默认支持orm层提供的全局通用的基础数据操作.
 * 对于POJO类, 一个表对应一个类, 避免重名
 * @author Jeremy
 */
public interface IBasePo {

	/**
	 * 一个对象的简单名字, class.getSimpleName()
	 * @return pojo的名字
	 */
	public String poName();
	
	/**
	 *	primary key, 对应数据库的主键,唯一主键.可以为null.
	 *	多主键情况下, 默认返回主键1
	 *	比如排行榜模块, 是复合索引.唯一主键就为null
	 * @return Object 返回Object类型的主键
	 */
	public Object key();
	
	/**
	 * 数据库主键的列名, 主键为null的情况下,列名也为null
	 * @return String类型的主键列
	 */
	public String keyColumn();
	
	/**
	 * 主键和索引列的值
	 * @return 主键与索引的值, 以数组的方式返回
	 */
	public Object[] keyAndIndexValues();
	
	/**
	 * 主键索引列, 对应数据库的索引,仅用于查询,删除,修改<P>
	 * 索引列  = 主键 + 索引列.<P>
	 *
	 * 比如,排行榜表rank{serverId, rankType, objectId, value1, value2}
	 * serverId,rankType,objectId为数据库组合唯一索引, 那么indexColumn就为这三个字段.
	 * key为indexColumn[0], 根据key查询, 默认根据第一索引serverId
	 * 删除数据, 修改数据默认所有所有索引定位数据<P>
	 *
	 * 比如，武将表hero{heroId, playerId, configId, level,...}
	 * heroId为数据库主键, playerId为索引
	 * 那么indexColumn就为heroId,playerId.
	 * key为indexColumn[0],即heroId, 根据key查询, 默认根据主键查询
	 * 若想根据索引查询, 使用自定义索引查询语句selectByIndex, 指定索引列与值
	 * 删除数据, 修改数据根据 主键+索引定位唯一的数据<P>
	 *
	 * 比如家族表family{familyId, name, serverId, createTime, ...}
	 * familyId为数据库主键, name,serverId是数据库唯一的组合索引
	 * 那么indexColumn就为 familyId,name,serverId
	 * key为indexColumn[0],即familyId,根据key查询,默认根据主键查询
	 * 若想根据索引查询, 使用自定义索引查询语句selectByIndex, 指定索引列与值
	 * 删除数据, 修改数据根据 主键+索引定位唯一的数据<P>
	 *
	 * @return 主键与索引的列,以数组的方式返回
	 */
	public String[] keyAndIndexColumn();
	
	/**
	 * 索引列的值
	 * @return 索引的值,以数组的方式返回
	 */
	public Object[] indexValues();
	
	/**
	 * 索引列
	 * @return 索引的列,以数组的方式返回
	 */
	public String[] indexColumn();
	
	/**
	 * 所有属性列
	 * @return 以数组的方式返回所有列名, 用于orm层拼装数据
	 */
	public String[] props();

	/**
	 * 所有属性列的值
	 * @return 以数组的方式返回所有列的值, 用于orm层拼装数据
	 */
	public Object[] propValues();
	
	/**
	 * 缓存二级ID
	 * 用于缓存的主键,  indexColumn()组合
	 * 直接用indexValues的组合生成最终的key
	 * 生成规则: key = indexVal1 +":"+ indexVal2 + ":"+...+ indexValn
	 * @return String类型的缓存唯一id
	 */
	public String cacheId();
	
	/**
	 * 存储前操作, 这个操作因为是异步执行, 会引发线程安全问题, 只能放在玩家线程去执行.
	 */
	public void beforeSave();
	
	/**
	 * 加载后操作
	 */
	public void afterLoad();
	
//	/**
//	 * 	当前对象实体
//	 * @return
//	 */
//	default public Class<?> clazz(){
//		return null;
//	}
	
}
