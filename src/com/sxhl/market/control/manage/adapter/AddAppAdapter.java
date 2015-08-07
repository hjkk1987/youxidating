package com.sxhl.market.control.manage.adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.model.entity.AutoType;
import com.sxhl.market.model.entity.MyGameInfo;

public class AddAppAdapter extends BaseImgGroupAdapter<AutoType>{
    public static final int IMAGE_ROUND_RATIO=8;
	private Context mContext;
	private LayoutInflater mInflater;
	private PackageManager mPackageManager;
	private List<ResolveInfo> mShareAppInfos;
	
	public AddAppAdapter(Context context) {
        // TODO Auto-generated constructor stub
	    super(context);
	    this.mContext = context;
		mInflater = LayoutInflater.from(context);
		mPackageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
		return mShareAppInfos==null?0:mShareAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
		return mShareAppInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
			convertView = mInflater.inflate(R.layout.manage_add_app_gv_layout, null);
			holder.ivIcon=(ImageView)convertView.findViewById(R.id.manage_iv_icon);
			holder.tvName=(TextView)convertView.findViewById(R.id.manage_tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
        }
		holder.tvName.setText(mShareAppInfos.get(position).loadLabel(mPackageManager));
		
		ResolveInfo appInfo=mShareAppInfos.get(position);
		try {
		    bindRoundImg(appInfo.activityInfo.name, holder.ivIcon, IMAGE_ROUND_RATIO,
		            mContext, appInfo.activityInfo.packageName,
		            appInfo.activityInfo.name, null);
        } catch (Exception e) {
            // TODO: handle exception
            holder.ivIcon.setImageDrawable(mShareAppInfos.get(position).loadIcon(mPackageManager));
        }
        return convertView;
    }
    
    public void updateDataSet(List<ResolveInfo> shareAppInfos){
		mShareAppInfos=shareAppInfos;
		notifyDataSetChanged();
	}
    
    private class ViewHolder {
		//应用图标
		private ImageView ivIcon;
		//应用名称
		private TextView tvName;
	}

}
