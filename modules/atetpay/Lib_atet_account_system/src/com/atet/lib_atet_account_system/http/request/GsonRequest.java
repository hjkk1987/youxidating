package com.atet.lib_atet_account_system.http.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.atet.lib_atet_account_system.http.HttpParams;
import com.google.gson.Gson;


/**
 * 自定义GSON类型的Request
 * 
 * @author zhaominglai
 * @date 2014/7/22
 * 
 * */
public class GsonRequest<T> extends Request<T> {
	private Gson gson = new Gson();
	private Class<T> clazz;
	private Listener<T>  listener;
	private HttpParams mParams;
	
	//默认超时时间
	private int TIME_OUT_MS = 7000;
	
	public GsonRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
		//设置超时策略
		this.setRetryPolicy(new CustomRetryPolicy());
		// TODO Auto-generated constructor stub
	}


	public GsonRequest(int method,Class<T> clazz,Listener<T> listener,String url,ErrorListener errorListener){
		super(method, url, errorListener);
		//设置超时策略
		this.setRetryPolicy(new CustomRetryPolicy());
		this.clazz = clazz;
		this.listener = listener;	
	}
	
	public GsonRequest(Class<T> clazz,Listener<T> listener,String url,ErrorListener errorListener,HttpParams params)
	{
		super(Method.POST,url,errorListener);
		//设置超时策略
		this.setRetryPolicy(new CustomRetryPolicy());
		this.clazz = clazz;
		this.listener = listener;
		this.mParams = params;
	}

	/**
	 * 自定义Request必须重写的核心方法 ,用来处理服务器后台返回的response
	 * 
	 * @author zhaominglai
	 * */
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		
		try {
			//String jsonStr = new String(response.data,HttpHeaderParser.parseCharset(response.headers));
			String jsonStr = new String(response.data,"utf-8");
			System.out.println(Thread.currentThread() + " parseNetworkResponse"+jsonStr);
			Log.e("response", jsonStr);
			return Response.success(gson.fromJson(jsonStr, clazz), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		 listener.onResponse(response);
	}

	/**
	 * Volley默认不提供Post的参数设置，如果要向Request添加Post的参数，则需要复写这个方法
	 * */
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		// TODO Auto-generated method stub
		Map<String,String> map = new HashMap<String, String>();
		
		Gson gson = new Gson();
		
		String json = gson.toJson(mParams);
		
		//json = "{\"loginName\":\"frank09\",\"password\":\"c81f8381bb824d12a9d0e7ad06a15357\",\"deviceType\":1,\"userId\":0}";
		
		//java.net.URLEncoder.encode(json)
		
		/**ATET后台服务器规定的参数是一个json字符串，并非网络上常见的将各个参数分别存在
		 * map当中。
		 * 例如：后台需要两个参数  loginName和password,标准的post请求应当是两个键值对BasicNamePair login 和  password，然后放进map当中。
		 * 而ATET的后台规定要将login和password组织成一条json格式的字符串，再放到请求的参数当中，这个值得注意，特别是新加入公司的员工
		 * 
		 * */
		map.put("param",json);
		
		System.out.println("retry count:" + getRetryPolicy().getCurrentRetryCount() + " "+Thread.currentThread() +"  getParams" + json);
		Log.e("request", json);
		return map;
	}
	
	@Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
    }
	
	/*@Override
	public RetryPolicy getRetryPolicy() {
		// TODO Auto-generated method stub
		
		//RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_MS, 1, 1.0f);
		RetryPolicy policy = new ATETRetryPolicy(TIME_OUT_MS, 1, 1.0f);
		return policy;
	}*/


	@Override
	public byte[] getBody() throws AuthFailureError {
		// TODO Auto-generated method stub
		 Map<String, String> params = getParams();
	        if (params != null && params.size() > 0) {
	            return encodeParameters(params, getParamsEncoding());
	        }
	        return null;
	}
	
	/**
	 * 重写encodeParameters方法，为了防止volley自动将Post的Json字符串参数进行字符转义，导致后台接收不正常
	 * 
	 * @author zhaominglai
	 * @date 2014/8/11
	 * 
	 * */
	 private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
	        StringBuilder encodedParams = new StringBuilder();
	        
	        if (params.size() == 1)
	        {
	        	//URLEncoder.encode()会把其中的字符转义,如{会变成&73,这样会造成后台不能正确识别post的参数
	        	try {
		            for (Map.Entry<String, String> entry : params.entrySet()) {
		            	//URLEncoder.encode()
		              //  encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
		              //  encodedParams.append('=');
		                encodedParams.append(entry.getValue());
		            }
		            return encodedParams.toString().getBytes(paramsEncoding);
		        } catch (UnsupportedEncodingException uee) {
		            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
		        }
	        }
	        try {
	            for (Map.Entry<String, String> entry : params.entrySet()) {
	                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
	                encodedParams.append('=');
	                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
	                encodedParams.append('&');
	            }
	            return encodedParams.toString().getBytes(paramsEncoding);
	        } catch (UnsupportedEncodingException uee) {
	            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
	        }
	    }
	
	
	
}
