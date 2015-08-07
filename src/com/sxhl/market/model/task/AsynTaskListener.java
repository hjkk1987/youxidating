package com.sxhl.market.model.task;
/** 
 * @author  
 * time：2012-8-13 上午10:25:59 
 * description: 异步监听接口
 * @prama T
 */
public interface AsynTaskListener<T> {
	/**
	 * 提交任务前先添加到任务观察器管理中.
	 * 
	 * @param task 实现了Observer接口的任务实例
	 * @param taskKey 任务标识
	 * @return 正常情况下返回true, 如果返回false, 应该终止任务继续执行.
	 */
	public boolean preExecute(BaseTask<T> task, Integer taskKey);
	
	/**
	 * 任务返回后将要执行的操作.
	 * 
	 * @param result
	 */
	public void onResult(Integer taskKey, TaskResult<T> result);
	 
 
	/**
	 * 后台执行任务
	 * @param taskKey
	 * @return
	 */
	public TaskResult<T> doTaskInBackground(Integer taskKey);

}
