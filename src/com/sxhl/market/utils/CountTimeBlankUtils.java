package com.sxhl.market.utils;

import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
public class CountTimeBlankUtils {
	private CountTimeBlankUtils(){}
	/**
	 * 是否超出指定间隔时间
	 * @author fcs
	 * @Description:model 模块名；id 标识存入的key; blankTime规定的间隔时间
	 * @date 2013-2-28 下午3:46:28
	 */
	public static boolean isArrivalTime(Context context,String model,int id,long blankTime){
		//获取model的SharedPreference
		SharedPreferences sp=context.getSharedPreferences(model,Context.MODE_PRIVATE);
		Editor editor=sp.edit();
		//获取该id的value
		long values=sp.getLong(id+"",-1);
		//如果不存在
		if(values==-1){
			//则添加
			editor.putLong(id+"",System.currentTimeMillis());
			editor.commit();
			return true;
		}
		//如果存在则计算时间间隔
		long currentTime=System.currentTimeMillis();
		//时间间隔大于等于指定的时间间隔
		if(currentTime-values>=blankTime){
			editor.putLong(id+"",System.currentTimeMillis());
			editor.commit();
			return true;
		}
		return false;
	}
	/**
	 * 清理SharedPreference
	 * @author fcs
	 * @Description:
	 * @date 2013-2-28 下午3:46:08
	 */
	public static void clearSharedPreferences(Context context,String model,long blankTime){
		SharedPreferences sp=context.getSharedPreferences(model,Context.MODE_PRIVATE);
		Map<String,?> valueMap=sp.getAll();
		long currentTime=System.currentTimeMillis();
		long value=0;
		if(valueMap!=null){
			Set<String> keys=valueMap.keySet();
			for(String key:keys){
				System.out.println("key="+key);
				value=sp.getLong(key,-1);
				System.out.println("value="+value);
				//如果时间超出了指定的间隔时间，则移除
				if(currentTime-value>=blankTime){
					sp.edit().remove(key);
					sp.edit().commit();
					continue;
				}
				if(value<0){
					sp.edit().remove(key);
					sp.edit().commit();
				}
			}
		}
	}

}
