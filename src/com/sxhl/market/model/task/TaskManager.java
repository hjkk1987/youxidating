package com.sxhl.market.model.task;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;

/**
 * @author time：2012-8-13 上午10:48:51 description: 异步任务管理器
 */
public class TaskManager {
	Context context;

	/** 当前Activity异步任务列表 */
	private HashMap<Integer, BaseTask<?>> taskMap = new HashMap<Integer, BaseTask<?>>();

	public TaskManager(Context context) {
		this.context = context;
	}

	public <T> void startTask(AsynTaskListener<T> listener, Integer taskKey) {
		addTask(listener, taskKey).execute((Void) null);
	}

	public <T> BaseTask<T> addTask(AsynTaskListener<T> listener, Integer taskKey) {

		BaseTask<T> taskInMap = (BaseTask<T>) taskMap.get(taskKey);
		if (taskInMap != null
				&& taskInMap.getStatus() == android.os.AsyncTask.Status.RUNNING) {
			return taskInMap;
		}

		taskInMap = new BaseTask<T>(context, listener, taskKey);
		taskMap.put(taskKey, taskInMap);

		return taskInMap;
	}

	public void Exceute(Integer taskKey) {
		BaseTask<?> taskInMap = taskMap.get(taskKey);
		if (taskInMap != null
				&& taskInMap.getStatus() != android.os.AsyncTask.Status.RUNNING) {
			taskInMap.execute((Void) null);
		}
	}

	public void cancelTask(Integer taskKey) {
		BaseTask<?> taskInMap = taskMap.get(taskKey);
		if (taskInMap != null
				&& taskInMap.getStatus() == android.os.AsyncTask.Status.RUNNING) {
			taskInMap.cancel(true);
			taskMap.remove(taskKey);
		}
	}

	public void cancelAllTasks() {
		if (taskMap != null && !taskMap.isEmpty()) {
			// for (Integer taskKey : taskMap.keySet()) {
			// cancelTask(taskKey);
			// }
			Iterator keys = taskMap.keySet().iterator();

			while (keys.hasNext()) {
				int key = (Integer) keys.next();
				BaseTask<?> taskInMap = taskMap.get(key);
				if (taskInMap != null
						&& taskInMap.getStatus() == android.os.AsyncTask.Status.RUNNING) {
					taskInMap.cancel(true);
					keys.remove();
				}
			}
		}
	}
}
