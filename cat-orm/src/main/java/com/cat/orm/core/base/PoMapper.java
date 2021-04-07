package com.cat.orm.core.base;

import com.cat.orm.core.annotation.PO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库POJO, SQL映射
 * 默认缓存单表操作,批量操作数据动态生成
 * 此方法仅只能生成单表操作的数据, 不能生成批量操作数据,
 */
public class PoMapper<T extends BasePo> {
	private static final Logger log = LoggerFactory.getLogger(PoMapper.class);

	//	真正的class类
	private Class<T> realCls;
	private String tbName;
	private String selectAll;
	private String selectByIndex;
	private String selectByKey;
	private String deleteAll;
	private String delete;
	private String update;
	private String insert;
	private String replace;
	
	/**
	 * 默认生成sql缓存
	 * @date 2020年6月22日
	 * @param cls
	 */
	public PoMapper(Class<T> cls) {
		this.realCls = cls;
		try {
			initSql();
		} catch (Exception e) {
			log.error("BasePo对象反射出错:{}", cls, e);
		}
	}
	
	private void initSql() throws InstantiationException, IllegalAccessException {
		PO po = realCls.getAnnotation(PO.class);
		if (po == null) {
			log.error("BasePo未找到PO注解, className:{}", realCls.getName());
			return;
		}
		this.tbName = po.name();
		BasePo ins = this.realCls.newInstance();
		this.selectAll = SQLGenerator.selectAll(tbName);
		this.deleteAll = SQLGenerator.deleteAll(tbName);
		this.selectByIndex = SQLGenerator.select(tbName, ins.indexColumn());
		this.selectByKey = SQLGenerator.select(tbName, ins.keyColumn());
		this.delete = SQLGenerator.delete(tbName, ins.indexColumn());
		this.update = SQLGenerator.update(tbName, ins.props(), ins.indexColumn());
		this.insert = SQLGenerator.insert(tbName, ins.props());
		this.replace = SQLGenerator.replace(tbName, ins.props());
	}

	public Class<T>getRealCls() {
		return realCls;
	}

	public String getTbName() {
		return tbName;
	}

	public String getSelectAll() {
		return selectAll;
	}

	public String getSelectByIndex() {
		return selectByIndex;
	}

	public String getSelectByKey() {
		return selectByKey;
	}

	public String getDeleteAll() {
		return deleteAll;
	}

	public String getDelete() {
		return delete;
	}

	public String getUpdate() {
		return update;
	}

	public String getInsert() {
		return insert;
	}

	public String getReplace() {
		return replace;
	}
	
}
