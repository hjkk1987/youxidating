package com.sxhl.market.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class DialogUtil {
	private static DialogUtil INSTANCE;
	private ProgressDialog proDialog;
	private AlertDialog alertDialog;
	private Toast toast;
	
	public static DialogUtil getInstanse(){
		if(INSTANCE==null){
			INSTANCE=new DialogUtil();
		}
		return INSTANCE;
	}
	
	public void showProgressDialog(Context context,String title,String content,boolean isCancelable,final OnCancelListener listener){
		dismiss();
		proDialog=new ProgressDialog(context);
		
		proDialog.setTitle(title);
		proDialog.setMessage(content);
		proDialog.setCancelable(isCancelable);
		proDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if(listener!=null){
					listener.onCancel(dialog);
				}
			}
		});
		proDialog.show();
	}
	
	public void showMessageDialog(Context context,String title,String content,boolean isCancelable,final OnCancelListener listener,final OnClickListener closeListener){
		dismiss();
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(content);
		builder.setCancelable(isCancelable);
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if(listener!=null){
					listener.onCancel(dialog);
				}
			}
		});
		builder.setNegativeButton("关闭", closeListener);
		alertDialog=builder.create();
		alertDialog.show();
	}
	
	public void showMessageDialog(Context context,String title,String content,boolean isCancelable,final OnCancelListener listener){
	    showMessageDialog(context, title, content, isCancelable, listener, null);
	}
	
	public void showToast(Context context,String content,boolean isClear){
	    dismiss();
	    if(isClear && toast!=null){
	        toast.cancel();
	    }
	    toast=Toast.makeText(context, content, Toast.LENGTH_SHORT);
	    toast.show();
	}
	
	public void dismiss(){
		if(proDialog!=null && proDialog.isShowing()){
			proDialog.dismiss();
			proDialog=null;
		}
		if(alertDialog!=null && alertDialog.isShowing()){
			alertDialog.dismiss();
			alertDialog=null;
		}
		if(toast!=null){
		    toast.cancel();
		}
	}

}
