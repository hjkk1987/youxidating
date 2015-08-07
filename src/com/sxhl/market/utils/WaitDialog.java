package com.sxhl.market.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/** 
 * @author  
 * time：2012-8-13 上午9:14:57 
 * description: 进度条
 */
public class WaitDialog extends Dialog {
	/*
	 * waitDialog
	 * @prama 
	 * */
	public WaitDialog(Context context,int theme)
	{
		super(context,theme);
	}
	
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
//		setContentView ( com.eacan.news_v4.R.layout.com_progressdialog );
	}

}
