package com.sxhl.market.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ControllerService extends Service {
	private static final String TAG = "ControllerService";
	private ServiceThread mServiceThread;
	private Context mContext;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreate");
		mContext = this;
		mServiceThread = new ServiceThread();
		mServiceThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mServiceThread != null) {
			mServiceThread.terminate();
			mServiceThread = null;
		}
	}

	private class ServiceThread extends Thread implements Runnable {
		private volatile boolean mIsExecuting = true;
		private Process mProc = null;
		private long mLastTime = 0;
		private static final int INTERVAL = 5000;
		private OutputStreamWriter mOutStream;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			BufferedReader reader = null;
			String line;
			int i = 0;
			final int sdkVersion = android.os.Build.VERSION.SDK_INT;
			while (mIsExecuting) {
				try {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (sdkVersion >= 16) {
						// need root above 4.1.2
						mProc = Runtime.getRuntime().exec("su");
						mOutStream = new OutputStreamWriter(
								mProc.getOutputStream());
						mOutStream.write("logcat -c\n");
						mOutStream.flush();
						mOutStream.write("logcat WindowManager:D *:S");
						mOutStream.write("\n");
						mOutStream.flush();
					} else {
						Runtime.getRuntime().exec("logcat -c");
						mProc = Runtime.getRuntime()
								.exec(new String[] { "logcat",
										"WindowManager:D *:S" });
					}
					reader = new BufferedReader(new InputStreamReader(
							mProc.getInputStream(), "UTF-8"), 5 * 1024);

					while ((line = reader.readLine()) != null && mIsExecuting) {
						Log.i(TAG, "line:" + line);
						// if (line.indexOf("keycode=142 screenIsOn=true") > 0
						// && line.indexOf("down =false canceled = false")>0) {
						if (line.indexOf("keycode=142 screenIsOn=true") > 0) {
							// startMarketLand();
						}
						i++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Log.i(TAG, "controller monitor end:" + i);
		}

		public void terminate() {
			mIsExecuting = false;
			try {
				if (mProc != null) {
					mProc.destroy();
					// int exitValue=mProc.exitValue();
					// if(exitValue==0){
					// Log.i(TAG, "exit logcat");
					// }
					mProc = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
