package com.sxhl.market.model.net.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sxhl.market.model.exception.HttpResponseException;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.StreamTool;
import com.sxhl.market.utils.StringTool;
/**
 * @author wsd
 * @Description:封装网络请求结果
 * @date 2012-12-4 下午3:22:17
 */
public class Response {
	private HttpResponse response = null;

	/**
	 * 构造Response实例.
	 * 
	 * @param response
	 */
	public Response(HttpResponse response) {
		this.response = response;
	}
	
	/**
	 * 将请求结果转换为输入流.
	 * 
	 * @return
	 * @throws HttpResponseException
	 */
	public InputStream asStream() throws HttpResponseException {
		try {
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				return entity.getContent();
			}
			return null;
		} catch (IllegalStateException e) {
			throw new HttpResponseException(e.getMessage());
		} catch (IOException e) {
			throw new HttpResponseException(e.getMessage());
		}
	}
	
	/**
	 * 将请求结果转换为字符串.
	 * 
	 * @return
	 * @throws HttpResponseException
	 */
	public String asString () throws HttpResponseException {
		InputStream is = null;
		String result=null;
		try {
			final HttpEntity entity = response.getEntity ();
			is = entity.getContent ();
			result = StreamTool.convertStreamToString ( is );
			if ( is == null || StringTool.isEmpty ( result ) ) {
				throw new HttpResponseException ( "服务器无响应" );
			}
			return result;
		}
		catch ( IllegalStateException e ) {
			throw new HttpResponseException ( e.getMessage () );
		}
		catch ( IOException e ) {
			throw new HttpResponseException ( e.getMessage () );
		}
		finally {
			StreamTool.closeInputStream ( is );
		}
	}

	
	/**
	 * 将请求结果转换为JSONObject.
	 * 
	 * @return
	 * @throws HttpResponseException
	 */
	public JSONObject asJSONObject() throws HttpResponseException {
		String result = asString();
		try {
			return new JSONObject(result);
		} catch (JSONException jsonException) {
			DebugTool.error("JSON格式有误: " + result, jsonException);
			throw new HttpResponseException(jsonException.getMessage());
		}
	}
	
	/**
	 * 将请求结果转换为JSONArray.
	 * 
	 * @return
	 * @throws HttpResponseException
	 */
	public JSONArray asJSONArray() throws HttpResponseException {
		String result = asString();
		try {
			return new JSONArray(asString());
		} catch (JSONException jsonException) {
			DebugTool.error("JSON格式有误: " + result, jsonException);
			throw new HttpResponseException(jsonException.getMessage());
		}
	}
	
	public HttpEntity getEntity() throws HttpResponseException {
		try {
			HttpEntity entity = response.getEntity();
			
			if (entity == null) {
				throw new HttpResponseException("获取数据失败");
			}
			
			return entity;
		} catch (Exception e) {
			throw new HttpResponseException(e.getMessage());
		}
	}
	
}