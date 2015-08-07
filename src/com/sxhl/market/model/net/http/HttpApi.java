package com.sxhl.market.model.net.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Configuration;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.model.entity.AutoType;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.exception.ServerLogicalException;
import com.sxhl.market.model.parsers.json.JsonArrayParser;
import com.sxhl.market.model.parsers.json.JsonObjectParser;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.DebugTool;

public class HttpApi {

	/**
	 * @author wsd
	 * @Description:此方法用于Http方法请求，返回数据中包含了一个列表 列表节点对应的字段为data
	 * @date 2012-12-5 下午3:49:48
	 */
	public static <T extends AutoType> TaskResult<Group<T>> getList(String url,
			Class<T> clazz, byte[] param) {
		TaskResult<Group<T>> taskResult = new TaskResult<Group<T>>();
		String m_string;
		try {
			Response response = getHttpPost1(url, param);
			m_string = response.asString();
			Log.e("response", m_string);
			if (null == m_string) {
				return null;
			}
			DebugTool.debug(Configuration.DEBUG_TAG, "返回的JOSN字符串：" + m_string);
			parseList(clazz, m_string, taskResult);
		} catch (Exception e) {
			taskResult.setException(e);
		}
		return taskResult;
	}

	public static <T extends AutoType> TaskResult<Group<T>> getList(
			String url1, String url2, String url3, Class<T> clazz, byte[] param) {
		TaskResult<Group<T>> taskResult = new TaskResult<Group<T>>();
		String m_string;
		try {
			Response response = getHttpPost1(url1, url2, url3, param);
			m_string = response.asString();
			Log.e("response", m_string);
			if (null == m_string) {
				return null;
			}
			DebugTool.debug(Configuration.DEBUG_TAG, "返回的JOSN字符串：" + m_string);
			parseList(clazz, m_string, taskResult);
		} catch (Exception e) {
			taskResult.setException(e);
		}
		return taskResult;
	}

	/**
	 * @author wsd
	 * @Description:此方法用于Http方法请求，返回单个对象 节点中无字段data
	 * @date 2012-12-5 下午3:49:48
	 */
	public static <T extends AutoType> TaskResult<T> getObject(String url1,
			String url2, String url3, Class<T> clazz, byte[] param) {
		TaskResult<T> taskResult = new TaskResult<T>();
		String m_string;
		Response response = getHttpPost1(url1, url2, url3, param);
		try {

			m_string = response.asString();
			Log.e("response", m_string);
			DebugTool.debug(Configuration.DEBUG_TAG, "返回的JOSN字符串：" + m_string);
			if (null == m_string) {
				return null;
			}
			parseObject(clazz, m_string, taskResult);
		} catch (Exception e) {
			taskResult.setException(e);
		}
		return taskResult;
	}

	public static <T extends AutoType> TaskResult<T> getObject(String url,
			Class<T> clazz, byte[] param) {
		TaskResult<T> taskResult = new TaskResult<T>();
		String m_string;
		try {

			Response response = getHttpPost1(url, param);
			m_string = response.asString();
			Log.e("response", m_string);
			DebugTool.debug(Configuration.DEBUG_TAG, "返回的JOSN字符串：" + m_string);
			if (null == m_string) {
				return null;
			}
			parseObject(clazz, m_string, taskResult);
		} catch (Exception e) {
			taskResult.setException(e);
		}
		return taskResult;
	}

	/**
	 * @author wsd
	 * @throws Exception
	 * @Description:向服务器上传一张图片 节点中无字段data
	 * @date 2012-12-5 下午3:49:48
	 */
	public static <T extends AutoType> TaskResult<T> uploadImage(String url1,
			String url2, String url3, File file, Class<T> clazz, Context context) {
		TaskResult<T> taskResult = null;

		try {
			taskResult = uploadImage(url1, file, clazz, context);
		} catch (Exception e) {
			try {
				taskResult = uploadImage(url2, file, clazz, context);
			} catch (Exception e2) {
				try {
					taskResult = uploadImage(url2, file, clazz, context);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					taskResult = new TaskResult<T>();
				}
			}

		}
		return taskResult;
	}

	public static <T extends AutoType> TaskResult<T> uploadImage(String url,
			File file, Class<T> clazz, Context context) throws Exception {
		TaskResult<T> taskResult = null;
		FileBody bin = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		// HttpConnectionParams.setSoTimeout(params, timeout)
		if (file != null) {
			bin = new FileBody(file);
		}

		// 请记住，这边传递汉字会出现乱码，解决方法如下,设置好编码格式就好
		// new StringBody("汉字",Charset.forName("UTF-8")));

		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("code", bin);
		reqEntity.addPart("data", bin);

		httppost.setEntity(reqEntity);

		System.out.println("执行: " + httppost.getRequestLine());

		HttpResponse response = httpclient.execute(httppost);
		System.out.println("statusCode is "
				+ response.getStatusLine().getStatusCode());
		HttpEntity resEntity = response.getEntity();
		System.out.println("----------------------------------------");
		System.out.println(response.getStatusLine());
		if (resEntity != null) {
			System.out.println("返回长度: " + resEntity.getContentLength());
			InputStream is = resEntity.getContent();
			StringBuilder str = new StringBuilder();
			byte[] bs = new byte[1024];
			while (true) {
				int i = is.read(bs);
				if (i < 0) {
					break;
				}
				str.append(new String(bs, 0, i, "utf-8"));
			}
			Log.e("response", "返回字符串" + str);
			Gson gson = new Gson();
			T t = gson.fromJson(str.toString(), clazz);
			taskResult = new TaskResult<T>();
			taskResult.setData(t);
		}

		return taskResult;
	}

	/**
	 * @author wsd
	 * @Description:发送HttpPost请求，此方法用于XPay系统
	 * @date 2013-5-24 下午3:19:19
	 */
	public static <T extends AutoType> TaskResult<T> httpPostter(String path,
			Class<T> clazz, byte[] param) {
		DebugTool.debug("请求服务器地址：" + path);
		TaskResult<T> taskResult = new TaskResult<T>();
		String m_string;
		try {
			Response response = getHttpPost1(path, param);
			m_string = response.asString();
			if (null == m_string) {
				return null;
			}
			Log.e("response", m_string);
			DebugTool.debug("服务器返回结果：" + m_string);
			Gson gson = new Gson();
			taskResult.setData((T) gson.fromJson(m_string, clazz));
			taskResult.setMsg(Constant.MESSAGE_LOGIN_SUCCESS);
			parseCode(taskResult, m_string);
			final int code = taskResult.getCode();
			DebugTool.debug(Configuration.DEBUG_TAG, "code=" + code);
			if (code != 0) {
				throw new ServerLogicalException(code);
			}
		} catch (Exception e) {
			taskResult.setCode(TaskResult.ERROR);
			taskResult.setException(e);
			taskResult.setMsg(Constant.MESSAGE_LOGIN_FAIL);
		}
		return taskResult;
	}

	public static <T extends AutoType> Response getHttpPost1(String path,
			byte[] param) throws MalformedURLException, IOException,
			ProtocolException {
		HttpPost post = new HttpPost(path);
		Response result = null;
		HttpResponse httpResponse = null;
		if (param == null) {
			param = new byte[] {};
		}

		ByteArrayEntity entity = new ByteArrayEntity(param);
		post.setEntity(entity);
		httpResponse = BaseApplication.getHttpClient().getHttpClient()
				.execute(post);
		result = new Response(httpResponse);
		return result;
	}

	public static <T extends AutoType> Response getHttpPost1(String path1,
			String path2, String path3, byte[] param) {
		Response result = null;
		try {
			Log.e("request", "第一次请求");

			result = getHttpPost1(path1, param);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			DebugTool.debug(
					"excption",
					e.getMessage() + " & " + e.getStackTrace() + " & "
							+ e.getLocalizedMessage() + " & " + e.toString()
							+ " & ");
			e.printStackTrace();
			Log.e("request", "第二次请求");
			try {
				result = getHttpPost1(path2, param);
			} catch (Exception e2) {
				e2.printStackTrace();
				Log.e("request", "第三次请求");
				// try {
				try {
					result = getHttpPost1(path3, param);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					return result;
				}
				// } catch (MalformedURLException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// } catch (ProtocolException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// } catch (IOException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
			}

		}

		return result;
	}

	/**
	 * 解析code
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-5-3 上午9:40:18
	 */
	private static <T extends AutoType> void parseCode(
			TaskResult<T> taskResult, String jsonData) throws IOException {
		// 解析JSON数据，首先要创建一个JsonReader对象
		@SuppressWarnings("resource")
		JsonReader reader = new JsonReader(new StringReader(jsonData));
		int code = 0;
		reader.beginObject();
		/*
		 * 返回的json格式{"data":XXX,"errorcode":"XXX","msg":"XXX"}
		 * 通过循环取得data，errorcode,msg,变量的值，放到taskResult对象中
		 */
		while (reader.hasNext()) {
			String tagName = reader.nextName();
			if (tagName.equals("code")) {
				code = reader.nextInt();
				// code==1 请求正确返回，code==2 请求异常
				taskResult.setCode(code);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
	}

	/**
	 * data 列表解析
	 * 
	 * @author
	 * @Description:
	 * @date 2013-5-22 下午2:52:21
	 * @param taskResult
	 *            TODO
	 */
	private static <T extends AutoType> void parseList(Class<T> clazz,
			String m_string, TaskResult<Group<T>> taskResult) throws Exception {
		JsonObjectParser<T> subParser = new JsonObjectParser<T>(clazz);
		JsonArrayParser arrParser = new JsonArrayParser(subParser);
		arrParser.parseJson(taskResult, m_string);
		final int code = taskResult.getCode();
		// 如果状态码等于1101 没有符合的数据的情况，我们当成成功来处理
		if (code == ServerLogicalException.CODE_GAME_NO_REQUIRED_DATA) {
			taskResult.setCode(TaskResult.OK);
		}
		if (code != 0) {
			throw new ServerLogicalException(code);
		}
	}

	/**
	 * 解析一个对象
	 * 
	 * @author
	 * @Description:
	 * @date 2013-5-22 下午3:00:35
	 * @param taskResult
	 *            TODO
	 */
	private static <T extends AutoType> void parseObject(Class<T> clazz,
			String m_string, TaskResult<T> taskResult) throws Exception {
		Gson gson = new Gson();
		taskResult.setData((T) gson.fromJson(m_string, clazz));
		parseCode(taskResult, m_string);
		final int code = taskResult.getCode();
		// 如果状态码等于1101 没有符合的数据的情况，我们当成成功来处理
		if (code == ServerLogicalException.CODE_GAME_NO_REQUIRED_DATA) {
			taskResult.setCode(TaskResult.OK);
		}
		if (code != 0) {
			throw new ServerLogicalException(code);
		}
	}

	public static String setImageToByteArray(String fileName) {

		byte[] image = null;
		StringBuilder sb = new StringBuilder();
		try {
			FileInputStream fs = new FileInputStream(fileName);
			int streamLength = fs.available();
			image = new byte[streamLength];
			fs.read(image, 0, streamLength);
			for (byte b : image) {
				sb.append(Integer.toBinaryString(b));
			}
			fs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String imgStr = "";
		// try {
		// imgStr = new String(image, "utf-8");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Log.e("hotgamehttp", sb.toString());
		return sb.toString();
	}
}
