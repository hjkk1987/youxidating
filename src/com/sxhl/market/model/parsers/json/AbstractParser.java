package com.sxhl.market.model.parsers.json;

import java.io.IOException;
import java.io.StringReader;

import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.sxhl.market.model.entity.AutoType;
import com.sxhl.market.model.task.TaskResult;

/**
 * @author wsd
 * @Description:Gson解析的抽象基类，用来负责对信息的封装
 * @date 2012-12-5 上午9:48:33
 */
public abstract class AbstractParser<T extends AutoType> {
	/**
	 * @author wsd
	 * @Description:负责解析具体的字符串
	 * @param taskResult
	 *            存放解析后的结果
	 * @param jsonData
	 *            要解析的数据
	 * @throws IOException
	 * @date 2012-12-5 上午9:50:03
	 */
	public void parseJson(TaskResult taskResult, String jsonData)
			throws IOException {
		// 解析JSON数据，首先要创建一个JsonReader对象
		JsonReader reader = new JsonReader(new StringReader(jsonData));
		reader.beginObject();
		/*
		 * 返回的json格式{"data":XXX,"errorcode":"XXX","msg":"XXX"}
		 * 通过循环取得data，errorcode,msg,变量的值，放到taskResult对象中
		 */
		while (reader.hasNext()) {
			String tagName = reader.nextName();
			if (tagName.equals("data")) {
				/*
				 * data对象可能是一个数组或对象，所以具体解析交给子类去做此处parserInner（JsonReader
				 * reader）方法是一个模板方法将解析好的数据放到taksResult对象的Data中
				 */
				taskResult.setData(parserInner(reader));
			} else if (tagName.equals("code")) {
				int a = reader.nextInt();
				taskResult.setCode(a);
			} else if (tagName.equals("total")) {
				taskResult.setTotalCount(reader.nextInt());
			} else if (tagName.equals("startTime")) {
				taskResult.setStartTime(reader.nextLong());
			} else if (tagName.equals("endTime")) {
				taskResult.setEndTime(reader.nextLong());
			} else if (tagName.equals("createTime")) {
				taskResult.setCreateTime(reader.nextLong());
			}else if (tagName.equals("updateTime")) {
				taskResult.setUpdateTime(reader.nextLong());
			}else if (tagName.equals("lastUpdateTime")) {
				taskResult.setLastUpdateTime(reader.nextLong());
			}else if (tagName.equals("currentTime")) {
				taskResult.setCurrentTime(reader.nextLong());
			}
		}
		reader.endObject();
	}

	/**
	 * @author wsd
	 * @Description:模板方法，子类实现此方法用来处理具体的解析逻辑
	 * @date 2012-12-5 上午9:47:10
	 * @return T
	 */
	public abstract T parserInner(JsonReader reader);
}
