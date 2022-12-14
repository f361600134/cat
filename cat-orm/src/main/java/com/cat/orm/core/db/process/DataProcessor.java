package com.cat.orm.core.db.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cat.orm.core.base.BasePo;
import com.cat.orm.core.base.IBasePo;
import com.cat.orm.core.db.dao.CommonDao;
import com.cat.orm.core.db.dao.IDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class DataProcessor implements IDataProcess {

	private static final Logger log = LoggerFactory.getLogger(DataProcessor.class);

	/**
	 * 由DataProcessor代理的Dao, 只能从DataProcessor内获取 可以由spring管理, 通过注解去获取 key: class
	 * name, 类名 value: dao 存储dao
	 */
	private Map<String, IDao<BasePo>> commonDaoMap;

	@SuppressWarnings("unchecked")
	public <T extends IBasePo> IDao<T> getCommonDao(String name) {
		if (name == null || name.equals("")) {
			throw new NullPointerException("name is can not be null:" + name);
		}
		return (IDao<T>) commonDaoMap.get(name);
	}

	public <T extends IBasePo> IDao<T> getCommonDao(Class<T> clazz) {
		if (clazz == null) {
			throw new NullPointerException("clazz is can not be null:" + clazz);
		}
		String name = clazz.getSimpleName().toLowerCase();
		return getCommonDao(name);
	}

	public <T extends IBasePo> IDao<T> getCommonDao(T clazz) {
		if (clazz == null) {
			throw new NullPointerException("clazz is can not be null:" + clazz);
		}
		return getCommonDao(clazz.poName().toLowerCase());
	}

	/**
	 * 构造方法
	 * 
	 * @param basePoMap
	 * @param jdbcTemplate
	 */
	public DataProcessor(Collection<Class<BasePo>> basePos, JdbcTemplate jdbcTemplate) {
		this.commonDaoMap = new HashMap<>();
		for (Class<BasePo> basePo : basePos) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			IDao<BasePo> dao = new CommonDao(basePo, jdbcTemplate);
			commonDaoMap.put(basePo.getSimpleName().toLowerCase(), dao);// 注意,通过spring注入进来的Map, key默认全部小写
		}
	}

	/**
	 * 查询信息
	 * 
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> List<T> selectAll(Class<T> clazz) {
		IDao<T> dao = getCommonDao(clazz);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the clazz:" + clazz);
		}
		return (List<T>) dao.selectAll();
	}

	/**
	 * 查询信息
	 * 
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> T selectByPrimaryKey(Class<T> clazz, Object value) {
		IDao<T> dao = getCommonDao(clazz);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the clazz:" + clazz);
		}
		return dao.selectByKey(value);
	}

	/**
	 * 查询玩家信息, 通过默认索引,这种方式直接获取缓存的sql进行查询
	 * 
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> List<T> selectByIndex(Class<T> clazz, Object[] objs) {
		IDao<T> dao = getCommonDao(clazz);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the clazz:" + clazz);
		}
		return (List<T>) dao.selectByIndex(objs);
	}

	/**
	 * 查询玩家信息, 通过默认索引,这种方式直接获取缓存的sql进行查询
	 * 
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> T selectOneByIndex(Class<T> clazz, Object[] objs) {
		List<T> ret = this.selectByIndex(clazz, objs);
		if (ret == null || ret.size() <= 0) {
			return null;
		}
		return ret.get(0);
	}

	/**
	 * 查询玩家信息, 通过制定字段,这种方式只能通过sql组装进行查询
	 * 
	 * @date 2020年6月30日
	 * @param clazz
	 * @return
	 */
	public <T extends IBasePo> List<T> selectByIndex(Class<T> clazz, String[] props, Object[] objs) {
		IDao<T> dao = getCommonDao(clazz);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the props:" + props);
		}
		return (List<T>) dao.selectByIndex(props, objs);
	}

	/**
	 * 添加玩家信息
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int insert(T po) {
		IDao<T> dao = getCommonDao(po);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the Pojo:" + po);
		}
		return dao.insert(po);
	}

	/**
	 * 批量添加
	 * 
	 * @date 2020年6月30日
	 * @param basePos
	 */
	public <T extends IBasePo> void insertBatch(Collection<T> basePos) {
		Map<String, List<T>> map = splitData(basePos);
		IDao<T> dao = null;
		for (String name : map.keySet()) {
			dao = getCommonDao(name);
			dao.insertBatch(map.get(name));
		}
		map = null;
	}

	/**
	 * 添加玩家信息
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int replace(T po) {
		IDao<T> dao = getCommonDao(po);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the Pojo:" + po);
		}
		return dao.replace(po);
	}

	/**
	 * 添加玩家信息
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int update(T po) {
		IDao<T> dao = getCommonDao(po);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the Pojo:" + po);
		}
		return dao.update(po);
	}

	/**
	 * 批量修改玩家信息
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int updateBatch(Collection<T> pos) {
		pos.forEach(po -> {
			getCommonDao(po).update(po);
		});
		return 0;
	}

	/**
	 * 添加玩家信息
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int delete(T po) {
		IDao<T> dao = getCommonDao(po);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the Pojo:" + po);
		}
		return dao.delete(po);
	}

	/**
	 * 删除所有
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> void deleteAll(Class<T> clazz) {
		IDao<T> dao = getCommonDao(clazz);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the clazz:" + clazz);
		}
		dao.deleteAll();
	}

	/**
	 * 批量删除
	 * 
	 * @date 2020年6月30日
	 * @param basePos
	 */
	public <T extends IBasePo> void deleteBatch(List<T> basePos) {
		Map<String, List<T>> map = splitData(basePos);
		IDao<T> dao = null;
		for (String name : map.keySet()) {
			dao = getCommonDao(name);
			dao.deleteBatch(map.get(name));
		}
		map = null;
	}

	/**
	 * 通过指定sql查询. 虽说指定了sql语句,但需要通过clazz获取处理dao
	 * 
	 * @date 2020年6月30日
	 * @param basePos
	 */
	public <T extends IBasePo> List<T> selectBySql(Class<T> clazz, String sql) {
		IDao<T> dao = getCommonDao(clazz);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the clazz:" + clazz);
		}
		return (List<T>) dao.selectBySql(sql);
	}

	/**
	 * 数据分类
	 * 
	 * @date 2020年8月5日
	 * @param basePos
	 * @return
	 */
	protected <T extends IBasePo> Map<String, List<T>> splitData(Collection<T> basePos) {
		// 数据分类
		Map<String, List<T>> map = new HashMap<String, List<T>>();
		for (T po : basePos) {
			String name = po.poName().toLowerCase();
			List<T> list = map.get(name);
			if (list == null) {
				list = new ArrayList<T>();
				map.put(name, list);
			}
			list.add(po);
		}
		return map;
	}

}
