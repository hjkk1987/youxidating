package com.sxhl.statistics.utils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.sxhl.market.app.BaseApplication;

import android.opengl.GLSurfaceView;

import android.util.Log;

public class DemoRenderer implements GLSurfaceView.Renderer {

	public void onSurfaceCreated(GL10 gl, EGLConfig config)

	{

		Log.d("SystemInfo", "GL_RENDERER = " + gl.glGetString(GL10.GL_RENDERER));
		Log.d("SystemInfo", "GL_RENDERER = " + gl.glGetString(GL10.GL_RENDERER));
		Log.d("SystemInfo", "GL_VENDOR = " + gl.glGetString(GL10.GL_VENDOR));
		Log.d("SystemInfo", "GL_VERSION = " + gl.glGetString(GL10.GL_VERSION));
		Log.i("SystemInfo",
				"GL_EXTENSIONS = " + gl.glGetString(GL10.GL_EXTENSIONS));
		BaseApplication.setGpuInfo(gl.glGetString(GL10.GL_RENDERER));
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}