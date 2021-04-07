package com.cat.orm.core.db.process;

import java.util.Collection;
import java.util.List;

import com.cat.orm.core.base.IBasePo;

/**
 * 数据处理接口
 * @auth Jeremy
 * @date 2021年1月17日下午11:43:00
 */
public interface IDataProcess {
	
	/**
	 * 通过类对象查询所有数据信息
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> List<T> selectAll(Class<T> clazz); 
	
	/**
	 * 通过类对象, 查询复合条件的数据信息
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> T selectByPrimaryKey(Class<T> clazz, Object value);
	
	/**
	 * 查询玩家信息, 通过默认索引,这种方式的实现, 默认直接获取缓存的sql进行查询
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> List<T> selectByIndex(Class<T> clazz, Object[] objs);
	
	/**
	 * 查询玩家信息, 通过默认索引,这种方式直接获取缓存的sql进行查询
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> T selectOneByIndex(Class<T> clazz, Object[] objs);
	
	/**
	 * 查询玩家信息, 通过制定字段,这种方式只能通过sql组装进行查询
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> List<T> selectByIndex(Class<T> clazz, String[] props, Object[] objs);
	
	/**
	 * 添加玩家信息
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int insert(T po);
	
	/**
	 * 批量添加, 实现默认做数据分类
	 * @date 2020年6月30日
	 * @param basePos
	 */
	public <T extends IBasePo> void insertBatch(Collection<T> basePos);
	
	/**
	 * 添加数据信息
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int replace(T po);
	
	/**
	 * 修改玩家信息
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int update(T po);
	
	/**
	 * 批量修改玩家信息, 实现默认数据分类
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int updateBatch(Collection<T> pos);
	
	/**
	 * 添加玩家信息, 默认通过对象T的主键,删掉数据
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int delete(T po);
	
	/**
	 * 删除所有, 复合类对象信息, 全部删除
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> void deleteAll(Class<T> clazz);
	
	/**
	 * 批量删除, 批量删除指定集合内的数据信息
	 * @date 2020年6月30日
	 * @param basePos
	 */
	public <T extends IBasePo> void deleteBatch(List<T> basePos);
	
	/**
	 * 	通过指定sql查询. 虽说指定了sql语句,但需要通过clazz获取处理dao
	 * @date 2020年6月30日
	 * @param basePos
	 */
	public <T extends IBasePo> List<T> selectBySql(Class<T> clazz, String sql);

}
