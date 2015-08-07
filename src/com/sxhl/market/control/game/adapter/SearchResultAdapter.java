package com.sxhl.market.control.game.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.control.manage.activity.MyGameActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.utils.AppUtil;
import com.sxhl.market.utils.StringTool;
public class SearchResultAdapter extends BaseImgGroupAdapter<GameInfo> {
	private Context context;

	public SearchResultAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		setImageSize(110, 110);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.search_listview_item, null);
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.gameIcon);
			viewHolder.gameName = (TextView) convertView
					.findViewById(R.id.gameName);
//			TextPaint tp=viewHolder.gameName.getPaint();
//			tp.setFakeBoldText(true);
			viewHolder.starLevelRatingBar = (RatingBar) convertView
					.findViewById(R.id.startLevelRatingBar);
			viewHolder.starLevelRatingBar.setMax(5);
			viewHolder.starLevelTextview = (TextView) convertView
					.findViewById(R.id.startLevelTV);
			viewHolder.downTimes = (TextView) convertView
					.findViewById(R.id.downTimes);
			viewHolder.size = (TextView) convertView
					.findViewById(R.id.gameSize);
//			viewHolder.description = (TextView) convertView
//					.findViewById(R.id.description);
			viewHolder.searchOp=(ImageButton)convertView.findViewById(R.id.searchOp);
			viewHolder.searchTip=(TextView)convertView.findViewById(R.id.searchOpTv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		GameInfo gameInfo = group.get(position);
		viewHolder.gameName.setText(gameInfo.getGameName());
//		viewHolder.starLevelRatingBar.setRating(Integer.valueOf(gameInfo.getStarLevel())); 
//		if(gameInfo.getStartLevel()!=0){
			viewHolder.starLevelRatingBar.setProgress((int)(double)gameInfo.getStartLevel());
//		}else{
//			viewHolder.starLevelRatingBar.setProgress(5);
//		}
		viewHolder.starLevelTextview.setText((int)gameInfo.getStartLevel()+context.getString(R.string.game_share));
		viewHolder.downTimes.setText(gameInfo.getGameDownCount()+context.getString(R.string.game_down_num));
		viewHolder.size.setText(gameInfo.getGameSize()/(1024*1024)+context.getString(R.string.game_MB));
//		viewHolder.description.setText(gameInfo.getDescription());
//		bindImg(gameInfo.getIconMin(), viewHolder.icon);
		bindRoundImg(gameInfo.getMinPhoto(), viewHolder.icon, 10);
		
		viewHolder.searchOp.setFocusable(false);
		initSearchOp(viewHolder.searchOp, viewHolder.searchTip, gameInfo);
		
		return convertView;
	}
	
	private void initSearchOp(ImageButton searchOp,TextView searchTip,GameInfo gameInfo){
		searchOp.setFocusable(false);
		
		Integer ret=0;
//		int installVersionCode=-1;
		MyGameInfo myGameInfo=null;
		int state=Constant.GAME_STATE_NOT_DOWNLOAD;
		try {
		    Group<MyGameInfo> infos = PersistentSynUtils.getModelList(MyGameInfo.class, " packageName='" + gameInfo.getPackageName() + "'");
		    if (infos != null && infos.size() > 0) {
		        myGameInfo = infos.get(0);
		        state=myGameInfo.getState() & 0xff;
		    }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

//		Group<MyGameInfo> infos = PersistentSynUtils.getModelList(MyGameInfo.class, " gameId='" + gameInfo.getGameId() + "'");
//        if (infos != null && infos.size() > 0) {
//            installVersionCode=0;
//        }
//		int installVersionCode=AppUtil.getInstalledAppVersionCode(context,gameInfo.getPackageName());
//		if(installVersionCode==-1){
        if(state==Constant.GAME_STATE_NOT_DOWNLOAD || state==Constant.GAME_STATE_DOWNLOAD_ERROR){
		    //下载
			searchTip.setText(R.string.down_btn_not_download);
			ret=Constant.GAME_STATE_NOT_DOWNLOAD;
			searchOp.setImageResource(R.drawable.search_result_btn_op);
//		} else if(installVersionCode<gameInfo.getVersion()){
//		    //升级
//			searchTip.setText(R.string.down_btn_update);
//			ret=Constant.GAME_STATE_UPDATE;
//			searchOp.setImageResource(R.drawable.search_result_btn_op);
		} else if(state==Constant.GAME_STATE_INSTALLED){
		    //运行
			searchTip.setText(R.string.down_btn_installed);
			ret=Constant.GAME_STATE_INSTALLED;
			searchOp.setImageResource(R.drawable.search_result_btn_run);
		}
		searchOp.setTag(R.id.searchOpTv,ret);
		searchOp.setTag(R.id.searchOp, gameInfo);
		searchOp.setOnClickListener(onSearchOpClickListenner);
	}
	
	private OnClickListener onSearchOpClickListenner=new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try{
				GameInfo gameInfo=(GameInfo)v.getTag(R.id.searchOp);
				Integer op=(Integer)v.getTag(R.id.searchOpTv);
//				if(op==Constant.GAME_STATE_NOT_DOWNLOAD || op==Constant.GAME_STATE_UPDATE){
				if(op==Constant.GAME_STATE_NOT_DOWNLOAD){
					MyGameActivity.addToMyGameList(context,gameInfo);
				} else if(op==Constant.GAME_STATE_INSTALLED){
					AppUtil.startAppByPkgName(context, gameInfo.getPackageName());
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	

	private class ViewHolder {
		// 图标
		private ImageView icon = null;
		// 游戏名称
		private TextView gameName = null;
		// 星星等级
		private RatingBar starLevelRatingBar = null;
		// 数字等级
		private TextView starLevelTextview = null;
		// 下载次数
		private TextView downTimes = null;
		// 大小
		private TextView size = null;
		// 简介
		private TextView description = null;
		//下载，运行按钮
		private ImageButton searchOp=null;
		//下载，运行提示文字
		private TextView searchTip=null;
	}
}
