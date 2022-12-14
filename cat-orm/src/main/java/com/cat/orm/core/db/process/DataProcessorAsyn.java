package com.cat.orm.core.db.process;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cat.orm.core.base.BasePo;
import com.cat.orm.core.base.IBasePo;
import com.cat.orm.core.db.dao.IDao;
import com.cat.orm.core.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 对于查询模块需要立即返回数据信息,所以不需要异步处理 对于修改模块, 无需立即返回结果, 所以异步方式处理
 *
 * 直接把任务丢进executor中, 会产生一个问题, 就是 定时任务线程池, 通过此种方式, 仅仅只是实现了异步入库, 所有入队列的数据1分钟后保存
 * 但实际上, 我们想做到的效果是, 对于需要保存的数据, 定时批量保存, 也就是说线程池1分钟从队列中拿出一批数据进行存储,
 * 
 * 所以最终, 选择通过队列的方式实现.
 *
 * @note 使用外部的队列,跟使用 ScheduledExecutorService内部的阻塞队列毫无区别.
 *
 * @author Jeremy
 * @date 2020年7月31日
 *
 */
public class DataProcessorAsyn extends DataProcessor {

	private static final Logger log = LoggerFactory.getLogger(DataProcessorAsyn.class);

	/**
	 * 这里的线程池,目前使用单线程处理, 如果压力过大, 后期可以个改成基于actor的多线程定时处理.
	 */
	private ScheduledExecutorService executor;
	private Queue<Task> queue;

	public DataProcessorAsyn(Collection<Class<BasePo>> basePos, JdbcTemplate jdbcTemplate) {
		super(basePos, jdbcTemplate);
		this.queue = new ConcurrentLinkedQueue<Task>();
		this.executor = Executors.newSingleThreadScheduledExecutor();
		this.executor.scheduleAtFixedRate(() -> {
			Task task = queue.poll();
			while (task != null) {
				try {
					task.execute();
				} catch (Exception e) {
					log.info("DataProcessorAsyn error, e:{}", e);
				}
				task = queue.poll();
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	/**
	 * 加入执行队列
	 * 
	 * @param <T>
	 * @param e
	 * @return
	 * @throws Exception
	 */
	public void execute(Task e) {
		 this.queue.add(e);
	}

	// public void execute(Runnable run) {
	// this.executor.scheduleAtFixedRate(run, 1, 1, TimeUnit.MINUTES);
	// }

	/**
	 * 添加玩家信息
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int insert(T po) {
		IDao<T> dao = super.getCommonDao(po);
		if (dao == null) {
			throw new NullPointerException("Can not found dao by the Pojo:" + po);
		}
		this.execute(() -> {
			dao.insert(po);
		});
		return 0;
	}

	/**
	 * 批量添加
	 * 
	 * @date 2020年6月30日
	 * @param basePos
	 */
	public <T extends IBasePo> void insertBatch(List<T> basePos) {
		Map<String, List<T>> map = splitData(basePos);
		for (String name : map.keySet()) {
			IDao<T> dao = super.getCommonDao(name);
			if (dao == null) {
				throw new NullPointerException("Can not find dao by the name:" + name);
			}
			Collection<T> list = map.get(name);
			this.execute(() -> {
				dao.insertBatch(list);
			});
		}
		map = null;
	}

	/**
	 * 添加信息
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int replace(T po) {
		IDao<T> dao = super.getCommonDao(po);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the po:" + po);
		}
		this.execute(() -> {
			dao.replace(po);
		});
		return 0;
	}

	/**
	 * 添加玩家信息
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> int update(T po) {
		IDao<T> dao = super.getCommonDao(po);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the po:" + po);
		}
		this.execute(() -> {
			dao.update(po);
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
		IDao<T> dao = super.getCommonDao(po);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the po:" + po);
		}
		this.execute(() -> {
			dao.delete(po);
		});
		return 0;
	}

	/**
	 * 删除所有
	 * 
	 * @date 2020年6月30日
	 * @param po
	 */
	public <T extends IBasePo> void deleteAll(Class<T> clazz) {
		IDao<T> dao = super.getCommonDao(clazz);
		if (dao == null) {
			throw new NullPointerException("Can not find dao by the clazz:" + clazz);
		}
		this.execute(() -> {
			dao.deleteAll();
		});
	}

	/**
	 * 批量删除
	 * 
	 * @date 2020年6月30日
	 * @param basePos
	 */
	public <T extends IBasePo> void deleteBatch(List<T> basePos) {
		Map<String, List<T>> map = super.splitData(basePos);
		for (String name : map.keySet()) {
			IDao<T> dao = super.getCommonDao(name);
			if (dao == null) {
				throw new NullPointerException("Can not find dao by the name:" + name);
			}
			Collection<T> list = map.get(name);
			this.execute(() -> {
				dao.insertBatch(list);
			});
		}
		map = null;
	}

	@Override
	public <T extends IBasePo> int updateBatch(Collection<T> pos) {
		pos.forEach(po -> {
			this.execute(() -> {
				super.update(po);
			});
		});
		return 0;
	}

}
