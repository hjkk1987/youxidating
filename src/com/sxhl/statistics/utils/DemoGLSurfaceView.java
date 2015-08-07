package com.sxhl.statistics.utils;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class DemoGLSurfaceView extends GLSurfaceView {  
  
    DemoRenderer mRenderer;  
    public DemoGLSurfaceView(Context context) {  
        super(context);  
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);  
        mRenderer = new DemoRenderer();  
        setRenderer(mRenderer);  
    }  
}  