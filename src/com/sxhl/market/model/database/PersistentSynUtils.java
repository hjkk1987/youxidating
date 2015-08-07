package com.sxhl.market.model.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.sxhl.market.model.entity.AutoType;
import com.sxhl.market.model.entity.Group;

/** 
 * @ClassName: PersistentSynUtils 
 * @Description: 数据库同步操作
 * @author: Liuqin
 * @date 2013-3-1 下午2:09:23 
 *  
 */  
public class PersistentSynUtils {
	private static final Object lock = new Object();

	/**
	 * 增加新数据
	 * 
	 * @param model
	 */
	public static long addModel(BaseModel model) {
		synchronized (lock) {
			return PersistentUtils.addModel(model);
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param model
	 */
	public static void delete(BaseModel model) {
		synchronized (lock) {
			PersistentUtils.delete(model);
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param model
	 *            数据模型必须是BaseModel的子类；
	 */
	public static int update(BaseModel model) {
		synchronized (lock) {
			return PersistentUtils.update(model);
		}
	}

	/**
	 * @Title: execSQL
	 * @Description: TODO(执行sql原生语句)
	 * @param @param sql 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void execSQL(String sql) {
		synchronized (lock) {
			PersistentUtils.execSQL(sql);
		}
	}

	/**
	 * @Title: execSQL
	 * @Description: TODO(执行删除数据)
	 * @param @param model 设定文件
	 * @param @param where 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void execDeleteData(Class clazz, String where) {
		synchronized (lock) {
			PersistentUtils.execDeleteData(clazz, where);
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param model
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public static int update(BaseModel model, ContentValues values,
			String whereClause, String[] whereArgs) {
		synchronized (lock) {
			return PersistentUtils
					.update(model, values, whereClause, whereArgs);
		}
	}

	/**
	 * 查询数据表获取数据模型列表； 适用于通用模型IdNameModel，此模型不与具体的数据表关联；
	 * 
	 * @param tableName
	 *            数据库表名称
	 * @param conditon
	 *            查询条件
	 * @return 数据模型列表
	 */
	public static <T extends AutoType> Group<T> getModelList(
			Class<?> modelType, String tableName, String conditon) {
		synchronized (lock) {
			return PersistentUtils.getModelList(modelType, tableName, conditon);
		}

	}

	/**
	 * 查询符合条件的获取对象列表
	 * 
	 * @param <T>
	 *            返回数据类型 与modelType对应
	 * @param modelType
	 * @param sql
	 *            SQLit标准SQL语句
	 * @param selectionArgs
	 *            sql对应的参数
	 * @return
	 */
	public static <T extends AutoType> Group<T> getModelListBySQL(
			Class<?> modelType, String sql, String[] selectionArgs) {
		synchronized (lock) {
			return PersistentUtils.getModelListBySQL(modelType, sql,
					selectionArgs);
		}

	}

	/**
	 * 查询符合条件的获取对象列表
	 * 
	 * @param <T>
	 * @param modelType
	 *            模型类的Class
	 * @param conditon
	 *            SQL语句中where查询条件之后的条件
	 * @return
	 */
	public static <T extends AutoType> Group<T> getModelList(
			Class<?> modelType, String conditon) {
		synchronized (lock) {
			return PersistentUtils.getModelList(modelType, conditon);
		}
	}

	/**
	 * 将cursor中的当前记录按照modelType类型生成对应的对象实例
	 * 
	 * @param cursor
	 *            数据来源，主要取第一行数据
	 * @param modelType
	 *            要生成的对象类型
	 * @return
	 */
	public static Object getModel(Cursor cursor, Class<?> modelType) {
		synchronized (lock) {
			return PersistentUtils.getModel(cursor, modelType);
		}
	}

	/**
	 * 查询符合条件的记录数量
	 * 
	 * @param <T>
	 * @param modelType
	 *            模型类的Class
	 * @param conditon
	 *            SQL语句中where查询条件之后的条件
	 * @return
	 */
	public static <T extends AutoType> int getModelListCount(
			Class<?> modelType, String conditon) {
		synchronized (lock) {
			return PersistentUtils.getModelListCount(modelType, conditon);
		}

	}
	
    /**
     * 统计记录数
     * 
     * @param modelType
     *            模型类型Class
     * @param conditon
     *            查询条件，标准SQL语句中where 之后的条件。
     * @return
     */
    public static long getRowCount ( Class < ? > modelType , String conditon ) {
        synchronized (lock) {
            return PersistentUtils.getRowCount(modelType, conditon);
        }
    }	
}
