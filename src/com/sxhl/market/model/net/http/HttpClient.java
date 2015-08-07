package com.sxhl.market.model.net.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.IPPort;
import com.sxhl.market.model.exception.HttpRequestException;
import com.sxhl.market.model.exception.IllegalUrlException;
import com.sxhl.market.model.exception.NetWorkNotFoundException;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.DeviceTool;
import com.sxhl.market.utils.StringTool;
//import com.fpi.epma.product.common.http.HttpClient.GzipDecompressingEntity;
/*
 * 封装网络请求操作
 * **/
public class HttpClient {
	// 声明APACHE HttpClient实例
		private DefaultHttpClient httpClient = null;
		
		// 请求超时设置
		private static final int CONNECTION_TIMEOUT_MS = 10 * 1000;
		private static final int SOCKET_TIMEOUT_MS = 10 * 1000;
		
		// 在发生异常时自动重试次数
		private static final int AUTO_RETRY_TIMES = 2;
		
		// HTTP cookies 集合
		private HashMap<String, String> cookies = new HashMap<String, String>();
		
		// WEB服务器地址
		private static final String BASE_SERVER = IPPort.BASE_GAME_SERVER;
		
		private Context context = null;

		/**
		 * 构造HttpClient实例
		 */
		public HttpClient(Context context) {
			this.context = context;
			initHttpClient();
		}
		
		public DefaultHttpClient getHttpClient(){
			return httpClient;
		}
		
		/**
		 * 设置网络请求代理服务器. 
		 * <p>
		 * 当使用GPRS连接网络时, 需要设置代理为:
		 *   <code>
		 *       httpClient.setProxy("10.0.0.172", 80, "http");
		 *   </code>
		 * </p>
		 * 
		 * @param host 主机名或IP地址
		 * @param port 端口号
		 * @param scheme 协议(http or https)
		 */
		public void setProxy(String host, int port, String scheme) {
			HttpHost proxy = new HttpHost(host, port, scheme);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		
		/**
		 * 移除代理服务器.
		 */
		public void removeProxy() {
			httpClient.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
		
		/**
		 * 关闭网络连接.
		 */
		public void shutdown() {
			httpClient.getConnectionManager().shutdown();
		}
		
		/**
		 * 添加Cookie.
		 * 
		 * @param key
		 * @param value
		 */
		public void addCookie(String key, String value) {
			cookies.put(key, value);
		}
		
		/**
		 * 清空Cookie
		 */
		public void clearCookie() {
			cookies.clear();
		}
		
		public Response get(String url) throws IllegalUrlException,
				HttpRequestException, NetWorkNotFoundException {
			return get(url, null);
		}
		
		public Response get(String url, ArrayList<BasicNameValuePair> params)
				throws IllegalUrlException, HttpRequestException, NetWorkNotFoundException {
			if (params == null) {
				params = new ArrayList<BasicNameValuePair>();
			} 
			
//			params.add(new BasicNameValuePair ( "sessionId" ,
//					BaseApplication.m_loginUser.sessionId));
//			
			if (url.indexOf("?") < 0) {
				url += "?" + encodeParams(params);
			} else {
				url += "&" + encodeParams(params);
			}
			
			return request(url, params, HttpGet.METHOD_NAME);
		}
		
		public Response loadImage(String imageUrl) throws IllegalUrlException,
				HttpRequestException, NetWorkNotFoundException {
			return request(imageUrl, null, HttpGet.METHOD_NAME);
		}
		
		public Response post(String url) throws IllegalUrlException,
				HttpRequestException, NetWorkNotFoundException {
			
			return post(url, new ArrayList<BasicNameValuePair>());
		}
		
		public Response post(String url, ArrayList<BasicNameValuePair> params)
				throws IllegalUrlException, HttpRequestException, NetWorkNotFoundException {
			
			return post(url, params, null);
		}
		
		/**
		 * 上传照片.
		 * 
		 * @param url
		 * @param file
		 * @param params
		 * @return
		 * @throws IllegalUrlException
		 * @throws HttpRequestException
		 * @throws NetworkNotFoundException
		 * @throws FileNotFoundException
		 * @throws UnsupportedEncodingException 
		 */
		public Response upload(String url, File file, ArrayList<BasicNameValuePair> params) throws IllegalUrlException,
				HttpRequestException, NetWorkNotFoundException, FileNotFoundException, UnsupportedEncodingException {
			DebugTool.debug("uploading file to [" + url + "]...");
			
			setupNetworkProxy();
			
			DefaultHttpClient client = new DefaultHttpClient(); 
			HttpPost post = new HttpPost(createURI(url));
			
//			MultipartEntity multipartEntity = new MultipartEntity();
//			multipartEntity.addPart(url, new FileBody(file));
			if (params != null && !params.isEmpty()) {
				for (BasicNameValuePair param : params) {
//					multipartEntity.addPart(param.getName(), new StringBody(param.getValue()));
				}
			}
//			post.setEntity(multipartEntity);
			addCookieToHeader(post);
			
			Response result = null;
			HttpResponse httpResponse = null;
			
			try {
				httpResponse = client.execute(post);
				result = new Response(httpResponse);
			} catch (Exception e) {
				DebugTool.error(e.getMessage(), e);
				throw new HttpRequestException(e.getMessage(), e);
			}
			
			if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != 200) {
				DebugTool.error("http response from [" + url + "] is null...", null);
				throw new HttpRequestException("getting HttpResponse error: " + url);
			}
			
			return result;
		}
		
		public Response post(String url, ArrayList<BasicNameValuePair> params, File file) 
				throws IllegalUrlException, HttpRequestException, NetWorkNotFoundException {
			if (params == null) {
				params = new ArrayList<BasicNameValuePair>();
			}

			return request(url, params, HttpPost.METHOD_NAME);
		}
		
		/**
		 * 发送网络请求.
		 * 
		 * @param url 请求URL
		 * @param params 请求参数(可以为null)
		 * @param file 上传的文件(可以为null)
		 * @param getOrPost 请求方式: GET or POST
		 * @return
		 * @throws IllegalUrlException
		 * @throws HttpRequestException 
		 * @throws NetworkNotFoundException 
		 */
		private Response request(String url, ArrayList<BasicNameValuePair> params,
				String getOrPost) throws IllegalUrlException,
				HttpRequestException, NetWorkNotFoundException {
			DebugTool.debug("sending " + getOrPost + " request to [" + url + "]...");
			
			// 设置网络代理: CMNET or CMWAP.
			setupNetworkProxy();
			
			Response result = null;
			HttpResponse httpResponse = null;
			HttpUriRequest uriRequest = null;
			
			URI uri = createURI(url);
			
			// 创建 GET or POST uriRequest
			uriRequest = createUriRequest(getOrPost, uri, params);
			// 设置HTTP连接请求参数
			setupHTTPConnectionParams(uriRequest);
			
			try {
				httpResponse = httpClient.execute(uriRequest);
				result = new Response(httpResponse);
			} catch (Exception e) {
				DebugTool.error(e.getMessage(), e);
				throw new HttpRequestException(e.getMessage(), e);
			}
			
			if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != 200) {
				DebugTool.error("http response from [" + url + "] is null...", null);
				throw new HttpRequestException("getting HttpResponse error: " + url);
			}
			
			return result;
		}
		
		/**
		 * 初始化HttpClient基本配置
		 */
		private void initHttpClient() {
			// 初始化Http参数
			BasicHttpParams basicHttpParams = new BasicHttpParams();
			ConnManagerParams.setMaxTotalConnections(basicHttpParams, 10);
			HttpProtocolParams.setVersion(basicHttpParams, HttpVersion.HTTP_1_1);
			
			// 注册协议管理类型
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			
			// 创建带ThreadSafeClientConnManager的HttpClient实例
			ThreadSafeClientConnManager threadSafeClientConnManager = 
					new ThreadSafeClientConnManager(basicHttpParams, schemeRegistry);
			httpClient = new DefaultHttpClient(threadSafeClientConnManager, basicHttpParams);
			
			/*
			 * 设置请求与响应拦截器.
			 * 使用GZIP压缩和解压请求, 可以更好的节省电池的电量.
			 */
			httpClient.addRequestInterceptor(gzipReqInterceptor, 0);
			// Support GZIP
			httpClient.addResponseInterceptor(gzipResInterceptor);
		}
		
		/**
		 * 请求拦截器, 设置请求支持GZIP.
		 */
		private static HttpRequestInterceptor gzipReqInterceptor = new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				String gzipHead = "Accept-Encoding";
				if (!request.containsHeader(gzipHead)) {
					request.addHeader(gzipHead, "gzip");
				}
			}
		};
		
		/**
		 * 响应拦截器, 解码经过GZIP压缩的响应结果.
		 */
		private static HttpResponseInterceptor gzipResInterceptor = new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context)
					throws HttpException, IOException {
				HttpEntity entity = response.getEntity();
				Header header = entity.getContentEncoding();
				if (header != null) {
					HeaderElement[] codecs = header.getElements();
					for (int i = 0; i < codecs.length; i++) {
						if (codecs[i].getName().indexOf("gzip") >= 0) {
							response.setEntity(new GzipDecompressingEntity(response.getEntity()));
							return;
						}
					}
				}
			}
		};

		private void setupNetworkProxy() throws NetWorkNotFoundException {
			// 判断网络类型
			switch (DeviceTool.checkNetWorkType(context)) {
			case Constant.NETWORK_TYPE_NET:
				// CMNET连接, 移除代理
				removeProxy();
				break;
			case Constant.NETWORK_TYPE_WAP:
				// CMWAP连接, 设置代理
				setProxy("10.0.0.172", 80, "http");
				break;
			default:
				// 抛出无网络异常
				throw new NetWorkNotFoundException("无网络连接");
			}
		}

		private HttpUriRequest createUriRequest(String getOrPost, URI uri,
				ArrayList<BasicNameValuePair> params) throws HttpRequestException {
			HttpUriRequest uriRequest = null;
			
			if (HttpPost.METHOD_NAME.equalsIgnoreCase(getOrPost)) {
				HttpPost post = new HttpPost(uri);
				post.getParams().setBooleanParameter("http.protocol.expect-continue", false);
				try {
					HttpEntity requestEntity = null;
					if (params != null) {
						requestEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
					}
					post.setEntity(requestEntity);
				} catch (Exception e) {
					throw new HttpRequestException(e.getMessage());
				}
				uriRequest = post;
			} else {
				uriRequest = new HttpGet(uri);
			}
			
			return uriRequest;
		}

		private URI createURI(String url) throws IllegalUrlException {
			URI uri = null;
			try {
				if (!url.startsWith("http://")) {
					url = BASE_SERVER + url;
				}
				DebugTool.warn ( "调用的URL"+url );
				uri = new URI(url);
			} catch (URISyntaxException e) {
				DebugTool.error("URL格式不正确: " + url, e);
				throw new IllegalUrlException("URL格式不正确: " + url);
			}
			return uri;
		}

		/**
		 * 设置HTTP连接请求参数
		 */
		private void setupHTTPConnectionParams(HttpUriRequest uriRequest) {
			HttpConnectionParams.setConnectionTimeout(uriRequest.getParams(), CONNECTION_TIMEOUT_MS);
			HttpConnectionParams.setSoTimeout(uriRequest.getParams(), SOCKET_TIMEOUT_MS);
			
			httpClient.setHttpRequestRetryHandler(requestRetryHandler);
			
			uriRequest.addHeader("Accept-Encoding", "gzip, deflate");
			uriRequest.addHeader("Accept-Charset", "UTF-8,*;q=0.5");
			uriRequest.addHeader("Cache-Control", "no-cache");
			
			addCookieToHeader(uriRequest);
		}

		// 添加cookies
		private void addCookieToHeader(HttpUriRequest uriRequest) {
			if (!cookies.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, String> cookie : cookies.entrySet()) {
					sb.append(cookie.getKey()).append("=").append(cookie.getValue()).append(";");
				}
				if (!StringTool.isEmpty(sb.toString())) {
					uriRequest.addHeader("Cookie", sb.toString());
				}
			} else {
				uriRequest.removeHeaders("Cookie");
			}
		}
		
		private String encodeParams(ArrayList<BasicNameValuePair> params) throws HttpRequestException {
			if (params == null) {
				return "";
			}
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < params.size(); i++) {
				if (i != 0) {
					sb.append("&");
				}
				try {
					sb.append(URLEncoder.encode(params.get(i).getName(), "UTF-8"))
							.append("=").append(URLEncoder.encode(params.get(i).getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new HttpRequestException(e.getMessage(), e);
				}
			}
			
			return sb.toString();
		}
		
		/**
		 * 请求异常自动恢复处理策略
		 */
		private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				// Auto retry X times
				if (executionCount >= AUTO_RETRY_TIMES) {
					return false;
				}
				// Retry if the server dropped connection on us
				if (exception instanceof NoHttpResponseException) {
					return true;
				}
				// Do not retry on SSL handshake exception
				if (exception instanceof SSLHandshakeException) {
					return false;
				}
				
				return false;
			}
		};
		
		/**
		 * GZIP实体解压缩实现类
		 */
		private static class GzipDecompressingEntity extends HttpEntityWrapper {

			public GzipDecompressingEntity(final HttpEntity entity) {
				super(entity);
			}

			@Override
			public InputStream getContent() throws IOException, IllegalStateException {
				InputStream wrappedInputStream = wrappedEntity.getContent();
				return new GZIPInputStream(wrappedInputStream);
			}

			@Override
			public long getContentLength() {
				return -1;
			}
		}
		
		
}
