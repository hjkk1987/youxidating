package com.sxhl.market.utils;

import java.io.IOException;
import java.io.StringReader;

import com.google.gson.stream.JsonReader;
import com.sxhl.market.model.task.TaskResult;

/**
 * @author wsd
 * @Description:gson解析辅助工具
 * @date 2012-12-4 下午4:52:03
 */
public class JsonParserTool {
	/**
	 * 将网络数据中的errorcode和msg信息封装到TaskResult对象中
	 * 返回数据部分的字符串
	 * @author wsd
	 * @Description:
	 * @date 2012-12-4 下午5:15:53
	 */
	public static String parseArrayJson(TaskResult taskResult, String jsonData) {
		// 解析JSON数据，首先要创建一个JsonReader对象
		JsonReader reader = new JsonReader(new StringReader(jsonData));
		String data = null;
		try {
			reader.beginObject();
			// 循环读取JSON数组中的对象
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equals("data")) {
					reader.peek();
					
				} else if (tagName.equals("errcode")) {
					int code = reader.nextInt();
					if (code == 1) {
						taskResult.setCode(TaskResult.OK);
					} else if (code == 2) {
						taskResult.setCode(TaskResult.ERROR);
					}
				} else if (tagName.equals("msg")) {
					taskResult.setMsg(reader.nextString());
				}
			}
			reader.endObject();
		} catch (IOException e) {
			taskResult.setException(e);
		}
		return data;
	}
}
