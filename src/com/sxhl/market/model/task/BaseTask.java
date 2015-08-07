package com.sxhl.market.model.task;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.os.AsyncTask;

import com.sxhl.market.app.BaseApplication;

/** 
 * 异步任务实例类
 * @author  
 * time：2012-8-13 上午10:29:42 
 */
public class BaseTask<T> extends AsyncTask<Void, Object, TaskResult<T>> implements
		Observer {
	/** 任务ID */
	protected Integer taskKey = -1;
	/** 任务依附的context */
	protected Context context = null;
	/** 任务提交监听器 */
	protected AsynTaskListener<T> taskListener = null;
	 

	/**
	 * 构建异步任务实例.
	 * 
	 * @param activity 任务依附的Activity实例
	 * @param taskPreListener 任务提交监听器实例
	 * @param taskPostListener 任务返回结果监听器实例
	 * @param taskKey 任务ID
	 */
	public BaseTask(Context context, AsynTaskListener<T> taskListener, Integer taskKey) {
		this.context = context;
		this.taskListener = taskListener;
		this.taskKey = taskKey;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// 任务提交前准备
		if (!taskListener.preExecute(this, taskKey)) {
			// 如果返回false,则取消此任务
			this.cancel(false);
		}
	}

	@Override
	protected void onPostExecute(TaskResult<T> result) {
		super.onPostExecute(result);
			if(result!=null && result.getCode() != TaskResult.OK){
//				BaseApplication.handleException(result.getException());
//				return;
		}

		// 处理返回结果
		taskListener.onResult(taskKey,result);
	}

	@Override
	public void update(Observable observable, Object data) {
		if (taskKey == (Integer) data) {
			if (this.getStatus() == AsyncTask.Status.RUNNING) {
				this.cancel(true);
			}
		}
	}

	@Override
	protected TaskResult<T> doInBackground(Void... params) {
		return taskListener.doTaskInBackground(taskKey);
	}
}
