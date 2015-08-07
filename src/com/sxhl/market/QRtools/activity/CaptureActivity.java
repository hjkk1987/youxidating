package com.sxhl.market.QRtools.activity;

import java.io.IOException;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.atet.api.pay.ui.InitActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.sxhl.market.R;
import com.sxhl.market.QRtools.activity.util.Res;
import com.sxhl.market.QRtools.camera.CameraManager;
import com.sxhl.market.QRtools.decoding.CaptureActivityHandler;
import com.sxhl.market.QRtools.decoding.InactivityTimer;
import com.sxhl.market.QRtools.view.ViewfinderView;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.game.activity.CommDetailActivity;
import com.sxhl.market.model.entity.GameInfo;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button cancelScanButton;
	private AlertDialog selectDialog;

	/** Called when the activity is first created. */
	// 测试商品
	// public static final String appid = "20000000000001200000";
	// public static final String appid = "4000003690";
	// public static final String appkey =
	// "MjEwRkM3QzczMUZFNTM2MDdBNUIzQ0VEMjQ5QkVCMzcwNjc3NTgyME1UTXlOREEwTmpZeU5UUTROVFU0T1RZeE5qTXJNVFEyTkRjek1UVXdOemM0TkRRNU9EWTFOelE1TXpBeU5EYzJNekl5TnpNeU5qUXdORGMz";

	// public static final String appid = "10000100000001100001";
	// public static final String appkey =
	// "ODlGMDFBQzQzQUJBNTg2QjBBM0M2MjZFNTQxMTczNTVBMTAwRjA2Q09USTJPRFkyTURFd01ERXpOekExTmpBME15c3hOekF6TURFd09EZ3lOVEF6TlRBNE5qSXpNRGMzT0RJMU9UQTFPVEEwTVRJNU5EUTRNRGs9";
	// public static final String notifyurl =
	// "http://192.168.0.140:8094/monizhuang/api?type=100";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(Res.layout(CaptureActivity.this, "aipay_scan_camera"));
		setHeadTitle(getIdToString(R.string.qrcode_scan_head_title));
		goBack();
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(Res.id(
				CaptureActivity.this, "viewfinder_view"));
		cancelScanButton = (Button) this.findViewById(Res.id(
				CaptureActivity.this, "btn_cancel_scan"));
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		cancelScanButton.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(Res.id(
				CaptureActivity.this, "preview_view"));
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

		// quit the scan view
		cancelScanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CaptureActivity.this.finish();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		// FIXME
		if (resultString.equals("")) {
			Toast.makeText(CaptureActivity.this, "Scan failed!",
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(Res.id(CaptureActivity.this,
					"decode_failed"));
		} else {

			try {
				// resultString = GzipHandler.uncompress(resultString);

				if (!TextUtils.isEmpty(resultString)) {
					if (resultString.contains("atetinterface/publishedGame_toGamePage_gameDownload.action?gameId=")) {
						showSelectLauncherDialog(resultString);
						return;
					}
					else if(resultString.startsWith("http")){
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						Uri content_url = Uri.parse(resultString);
						intent.setData(content_url);
						startActivity(intent);
						CaptureActivity.this.finish();
					}
					else{
						Intent in = new Intent(CaptureActivity.this,
								InitActivity.class);
						in.putExtra("result", resultString);
						startActivity(in);
						CaptureActivity.this.finish();
					}

				} else {
					Toast.makeText(CaptureActivity.this,
							getIdToString(R.string.qrcode_scan_failed),
							Toast.LENGTH_SHORT).show();
					handler.sendEmptyMessage(Res.id(CaptureActivity.this,
							"decode_failed"));
				}

			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(Res.id(CaptureActivity.this,
						"decode_failed"));
			}

		}
		// CaptureActivity.this.finish();
	}

	/**
	 * @param resultString
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 选择本程序或网页打开游戏详情
	 */
	private void showSelectLauncherDialog(final String resultString) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.qrcode_select_dialog, null);
		Button btn_detial = (Button) layout
				.findViewById(R.id.btn_qrcode_choose_detial);
		Button btn_website = (Button) layout
				.findViewById(R.id.btn_qrcode_choose_website);
		btn_website.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectDialog.dismiss();
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(resultString);
				intent.setData(content_url);

				startActivityForResult(intent, 1);
			}
		});
		btn_detial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectDialog.dismiss();
				String gameId = resultString.substring(
						resultString.lastIndexOf("=") + 1,
						resultString.length());
				Intent in = new Intent(CaptureActivity.this,
						CommDetailActivity.class);
				Bundle mBundle = new Bundle();
				GameInfo transGameInfo = new GameInfo();
				transGameInfo.setGameId(gameId);
				mBundle.putSerializable(CommDetailActivity.KEY_GAMEINFO,
						transGameInfo);
				mBundle.putInt(CommDetailActivity.KEY_GAMEINFO_TYPEID, 10);

				in.putExtras(mBundle);
				in.putExtra("gameInfoUrl", resultString);
				startActivity(in);
				CaptureActivity.this.finish();
			}
		});
		selectDialog = new AlertDialog.Builder(this).create();
		selectDialog.setCanceledOnTouchOutside(false);
		selectDialog.show();
		// checkVersionDialog.setCancelable(false);
		selectDialog.getWindow().setContentView(layout);

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					Res.raw(CaptureActivity.this, "beep"));
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// if(requestCode==1){
	// if(resultCode==RESULT_OK){
	// finish();
	// }
	// }
	// }
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 1) {
			this.finish();
		}
	};

}
