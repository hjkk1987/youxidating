package com.sxhl.market.model.net.http.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.Logtrace;
import com.sxhl.market.utils.NetUtil;

/**
 * @ClassName: DownloadThread
 * @Description: 下载线程
 * @author: Liuqin
 * @date 2012-12-10 上午11:06:53
 * 
 */
public class DownloadThread extends Thread {
	private static final String TAG = "DownloadThread";
	//网络连接状态下最多尝试次数
	private File saveFile;
	private String downUrl;
	private int block;
	/* 下载开始位置 */
	private int threadId = -1;
	private int downLength;
	private FileDownloader downloader;
	private volatile boolean isStopDownload = false;
	private volatile int downloadState=0;
	private Context context;
	private SharedPreferences preferences;
	
	private static final int MAX_RETRY_TIMES=5;
	private static final int MIN_FILE_SIZE=20*1024*1024;
	private static int BLOCK_NUM=20;
	private int blockSize;
	private int blockNum;
	private int currentBlockIndex=0;
	
//	private int iTest=0;

	public DownloadThread(Context context,FileDownloader downloader, String downUrl,
			File saveFile, int block, int downLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
		this.context=context;
		this.preferences=context.getSharedPreferences("marketApp", Context.MODE_PRIVATE);
		
		if(saveFile.length()>=MIN_FILE_SIZE){
		    if((saveFile.length() % BLOCK_NUM) == 0) {
		       blockSize=(int)saveFile.length() / BLOCK_NUM ; 
		       blockNum=BLOCK_NUM;
		    } else {
		       blockSize=(int)saveFile.length() / (BLOCK_NUM); 
		       blockNum=BLOCK_NUM;
		    }
		    currentBlockIndex=downLength/blockSize;
		    
//		    for (int i = 0; i < blockNum; i++) {
//		        Logtrace.warn(TAG, "start from percent:"+(int)(((long)blockSize*(i)*100)/block));
//		    }
		} else {
		    blockSize=(int)saveFile.length();
		    blockNum=1;
		    currentBlockIndex=0;
		    
		    Logtrace.warn(TAG, "start from percent:"+downLength*100/block);
		}
	}

	@Override
	public void run() {
		int retryTimes=0;
		downloadState=0;
		int lastBlockDownLen=0;
		byte[] buffer = new byte[1024*8];
		Logtrace.info(TAG, "***fileName:"+saveFile.getName()+" fileSize:"+saveFile.length()+" blockSize:"+blockSize);
		while(retryTimes++<MAX_RETRY_TIMES){
			if (downLength < block) {// 未下载完成
			    if (isStopDownload) {
			        return;
			    }
			    
			    Logtrace.info(TAG, "fileName:"+saveFile.getName()+" downLen:"+downLength+" block:"+block+" retryTimes:"+retryTimes);
			    
				RandomAccessFile threadfile=null;
				InputStream inStream=null;
				try {
				    boolean isWifiOpen=NetUtil.isWifiOpen(context);
			        if(!isWifiOpen && NetUtil.isNetworkAvailable(context, true)){
			            boolean result=preferences.getBoolean("wifi",false);
			            if(!result){
			                //已禁止非wifi环境下载
//			                Log.i("md", "disable down in not wifi");
			                Logtrace.info(TAG, "fileName:"+saveFile.getName()+" disable down in not wifi");
			                this.downloadState=-2;
			                return;
			            }
			        }

			        for (; currentBlockIndex < blockNum; currentBlockIndex++) {
			            int startPos;
			            int endPos;
			            if(blockNum==1){
			                startPos = downLength;// 开始位置
			            } else {
			                startPos = blockSize * currentBlockIndex + 0;// 开始位置
			            }
			            if(currentBlockIndex==blockNum-1){
			                endPos=(int)saveFile.length()-1;
			            } else {
			                endPos = blockSize * (currentBlockIndex+ 1) - 1;// 结束位置
			            }
			            if(endPos+1>saveFile.length()){
			                endPos=(int)saveFile.length()-1;
			            }
			            
			            int currentBlockSize=endPos-startPos+1;
			            
			            try{
			                if(threadfile!=null){
			                    threadfile.close();
			                    threadfile=null;
			                }
			                if(inStream!=null){
			                    inStream.close();
			                    inStream=null;
			                }
			            } catch(Exception e){
			                e.printStackTrace();
			            }
			            threadfile = new RandomAccessFile(this.saveFile, "rw");
			            threadfile.seek(startPos);

			            inStream = httpGetInputStream(downUrl,!NetUtil.isWifiOpen(context),startPos,endPos);
			            Logtrace.info(TAG, saveFile.getName()+ " start download from position: "+ startPos + " end position:"+endPos);
			            Logtrace.info(TAG, "currentBlockIndex:"+currentBlockIndex);

			            int offset = 0;
			            int blockDownLen=0;
			            while ((offset = inStream.read(buffer, 0, buffer.length)) != -1) {
			                if (isStopDownload) {
			                    // 暂停退出
			                    Logtrace.info(TAG, "fileName:"+saveFile.getName()+" download exit");
			                    
			                    try{
			                        if(threadfile!=null){
			                            threadfile.close();
			                            threadfile=null;
			                        }
			                        if(inStream!=null){
			                            inStream.close();
			                            inStream=null;
			                        }
			                    } catch(Exception e){
			                        e.printStackTrace();
			                    }
			                    return;
			                }

			                threadfile.write(buffer, 0, offset);
			                blockDownLen+=offset;
			                if(lastBlockDownLen<=0){
			                    downloader.update(this.threadId, blockDownLen, offset);
			                    if(blockNum==1){
			                        downLength+=offset;
			                    }
			                } else if(lastBlockDownLen>0 && blockDownLen>lastBlockDownLen){
			                    //重新下载该区段，下载位置已到达上次下载位置，更新下载进度
			                    downloader.update(this.threadId, 0, blockDownLen-lastBlockDownLen);
			                    Log.w(TAG, "redownload block:"+(blockDownLen-lastBlockDownLen)+ " blockDownLen"+blockDownLen+" currentBlock:"+currentBlockSize);
			                    lastBlockDownLen=0;
			                } else {
			                    //重新下载该区段，不更新下载进度
			                }
			                
//			                iTest++;
//			                if(iTest==1){
//			                    if(blockDownLen>=800*1024){
//			                        Log.w(TAG, "test blockDownLen:"+blockDownLen+" currentBlockSize:"+currentBlockSize);
//			                        break;
//			                    } else {
//			                        iTest=0;
//			                    }
//			                }

			                if(blockDownLen>=currentBlockSize){
			                    //该block下载完
			                    Logtrace.info(TAG, "fileName:"+saveFile.getName()+" downLength>=block,download complete");
			                    break;
			                }
			            } //end while
			            
			            if(blockDownLen<currentBlockSize){
			                //该区段没有下载完整，重新下载该段
			                Logtrace.info(TAG, "####"+saveFile.getName()+" blockDownLen<block"+",blockDownLen:"+blockDownLen+" **blockSize:"+blockSize);
			                if(blockNum>1){
			                    lastBlockDownLen=blockDownLen;
			                }
			                currentBlockIndex--;
			                continue;
			            }
			            
			            //保存进度到数据库
			            if(blockNum>1){
			                downLength+=blockDownLen;
			                downloader.updateProgressDB(threadId, downLength);
			            }
			            
			            try{
			                if(threadfile!=null){
			                    threadfile.close();
			                    threadfile=null;
			                }
			                if(inStream!=null){
			                    inStream.close();
			                    inStream=null;
			                }
			            } catch(Exception e){
			                e.printStackTrace();
			            }
			            
			            lastBlockDownLen=0;
			            if(downLength>=saveFile.length()){
			               //文件已下载完
			               break; 
			            }
			        } //end for
			        
			        if(downLength<saveFile.length()){
			            Logtrace.info(TAG, saveFile.getName()+" downLength<block"+",downLength:"+downLength+" **block:"+block);
			            if(blockNum==1){
			                currentBlockIndex=0;
			                continue;
			            }
			        }
					
					Logtrace.info(TAG, saveFile.getName()+" Thread " + this.threadId + " download finish"+" **downLength:"+downLength);
//					print("Thread " + this.threadId + " download finish");
					this.downloadState=1;
					return;
				} catch (FileNotFoundException e){
//					print("Thread " + this.threadId + ":" + e);
				    Logtrace.error(TAG, "fileName:"+saveFile.getName()+" error occur",e);
					e.printStackTrace();
					if(threadfile==null){
						this.downloadState=-1;
						return;
					} else {
						//可能是getInputStream返回的错误
					}
				} catch (Exception e) {
//					print("Thread " + this.threadId + ":" + e);
				    Logtrace.error(TAG, "fileName:"+saveFile.getName()+" error occur",e);
					e.printStackTrace();
				} finally {
					try{
						if(threadfile!=null){
							threadfile.close();
							threadfile=null;
						}
						if(inStream!=null){
							inStream.close();
							inStream=null;
						}
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			} else {
				//线程已经下载完成
				this.downloadState=1;
				return;
			}
			
			try {
			    int i;
			    for(i=0;i<10;i++){
			        if (isStopDownload) {
			            return;
			        }
			        
			        if(NetUtil.isNetworkAvailable(context, false)){
			            //网络已连接
			            break;
			        }
			        if(i<5){
			            //20秒等待网络连接
//			            Log.i("md", "sleep 4000 i="+i);
			            Thread.sleep(4000); 
			        } else {
			            //20秒后如果有网络正在连接中，再等待30秒，否则退出
			            if(NetUtil.isNetworkAvailable(context, true)){
//			                Log.i("md", "sleep 6000 i="+i);
			                //网络正在连接中,等待连接完成
			                Thread.sleep(6000); 
			            } else {
			                //网络已断开
//			                Log.i("md", "sleep end,no network i="+i);
			                this.downloadState=-2;
			                return;
			            }
			        }
			    }
			    if(i>=10){
			        break;
			    }
			} catch (Exception e) {
			    // TODO: handle exception
			    e.printStackTrace();
			}
		}
		this.downloadState=-2;
	}

	private static void print(String msg) {
		DebugTool.info(TAG,msg);
//		Log.i(TAG, msg);
	}

	/**
	 * 已经下载的内容大小
	 * 
	 * @return 如果返回值为-1,代表下载失败
	 */
	public long getDownLength() {
		return downLength;
	}

	/**
	 * 
	 * @Title: setStop
	 * @Description: 结束线程
	 * @param isStop
	 * @throws
	 */
	public void setStop(boolean isStop) {
		this.isStopDownload = isStop;
	}
	
	public int getDownloadState() throws FileNotFoundException,Exception {
		if(downloadState==-1){
			throw new FileNotFoundException();
		} else if(downloadState==-2){
			throw new Exception("Fail to download in Thread");
		}
		return downloadState;
	}
	
    public static InputStream httpGetInputStream(String url,boolean isNotWifi,int startPos,int endPos) throws Exception{
    	HttpResponse response = httpGetResponse(url,isNotWifi,startPos,endPos);
    	int statusCode=response.getStatusLine().getStatusCode();
    	if(statusCode==HttpStatus.SC_PARTIAL_CONTENT || statusCode==HttpStatus.SC_OK){
    		HttpEntity entity = response.getEntity();
    		if (entity != null) {
    			return response.getEntity().getContent();
    		} else {
    			throw new Exception("Error to getEntity()");
    		}
    	} else {
    		throw new Exception("status code is not 200 -- "+response.getStatusLine().getStatusCode());
    	}
    }
    
    public static HttpResponse httpGetResponse(String url,boolean isNotWifi,int startPos,int endPos) throws Exception{
    	HttpParams params = new BasicHttpParams();

    	HttpConnectionParams.setConnectionTimeout(params, 30000);
    	HttpConnectionParams.setSoTimeout(params, 30000);
    	HttpConnectionParams.setTcpNoDelay(params, true);

    	HttpClient httpclient = new DefaultHttpClient(params);
    	httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

    	if(isNotWifi){
    		String host = android.net.Proxy.getDefaultHost();
    		int port = android.net.Proxy.getDefaultPort();
    		DebugTool.info(TAG,"wifi not open:"+"host:"+host+" port:"+port);
    		if (host != null && port != -1) {
    			HttpHost httpHost=new HttpHost(host,port);
    			httpclient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
//    			DebugTool.debug(TAG, "host:"+host+" port:"+port);
    		}
    	} else {
    		DebugTool.info(TAG,"wifi open");
    	}

//    	DebugTool.info("uri encode:", NetUtil.getValidUri(url).toString());
    	HttpGet get = new HttpGet(NetUtil.getValidUri(url));
    	get.setHeader("User-Agent","Mozilla/5.0 ( compatible ) ");
    	get.setHeader("Charset", "UTF-8");
    	get.setHeader("Accept-Language", "zh-CN");
    	if(endPos>0 && endPos>=startPos){
    		get.setHeader("Range", "bytes=" + startPos + "-" + endPos);
    	}
    	get.setHeader("Accept","*/*");
    	get.setHeader("Referer", url);
    	get.setHeader("Connection", "close");	
    	return httpclient.execute(get); 
    }

    public static int httpGetFileLength(String url,boolean isNotWifi) throws Exception{
    	HttpResponse response = httpGetResponse(url, isNotWifi, 0, 0);
        int statusCode=response.getStatusLine().getStatusCode();
        if(statusCode!=HttpStatus.SC_PARTIAL_CONTENT && statusCode!=HttpStatus.SC_OK){
            throw new Exception("status code is not 200 -- "+response.getStatusLine().getStatusCode());
        }
    	Header[] headers=response.getHeaders("Content-Length");
    	return Integer.parseInt(headers[0].getValue());
    }

    public int getBlockNum() {
        return blockNum;
    }
}
