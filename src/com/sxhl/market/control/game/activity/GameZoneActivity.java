package com.sxhl.market.control.game.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseExitActivity;
import com.sxhl.market.control.game.adapter.GameTopicAdapter;
import com.sxhl.market.control.game.adapter.GameZoneViewPagerAdapter;
import com.sxhl.market.control.game.adapter.HotGameAdapter;
import com.sxhl.market.control.game.adapter.RecommendAdapter;
import com.sxhl.market.control.game.adapter.RecommendAdapter.RecommedButtonInferface;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.AdInfo;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.GameType;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.ScreenShootInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.preferences.Preferences;
import com.sxhl.market.view.InnerPullDownView.OnPullDownListener;
import com.sxhl.market.view.PullDownView;
import com.sxhl.market.view.costom.HotGameLvHead;
import com.sxhl.market.view.costom.HotGameLvHead.OnHotGameListener;

//import android.annotation.SuppressLint;
/**
 * @ClassName: GameZoneActivity
 * @Description:游戏列表
 * @author
 * @date 2012-12-4 下午9:02:30
 */
public class GameZoneActivity extends BaseExitActivity implements
		OnCheckedChangeListener, OnPullDownListener, OnPageChangeListener {
	private RadioButton mRadioButtonRecommend, mRadioButtonBest,
			mRadioButtonSpec;
	private ListView mRecommendListView;// 史诗listview
	private ListView mHotListView;// 精品listview
	private ListView mTopicListView;// 专题列表游戏ListView
	private RadioGroup mRadioGroup;
	private RecommendAdapter mRecommendAdapter;// 创建Adapter
	private HotGameAdapter mHotGameAdapter;// 热门游戏适配器
	private GameTopicAdapter mGameTopicAdapter;// 专题适配器
	private PullDownView mRecommendPullDownView;// 创建诗史推荐PulldownView
	private PullDownView mHotPullDownView;// 创建精品游戏的PulldownView
	private Group<GameInfo> mGameRecList = new Group<GameInfo>();// 史诗推荐列表
	private Group<GameInfo> mGameHotList = new Group<GameInfo>();// 精品游戏列表
	private Group<GameInfo> mGameHotHandle = new Group<GameInfo>();// 精品手柄游戏列表
	private Group<GameInfo> mGameHotNormal = new Group<GameInfo>();// 精品普通游戏列表
	private Group<GameType> mGameTypeList = new Group<GameType>();// 专题列表
	private Group<AdInfo> mAdList = new Group<AdInfo>();// 广告列表
	private int mCurrentType = 1;// 记录当前选中的哪个选项卡 1：史诗推荐 2：精品游戏 3：游戏专题
	private int mPageSize = 10;
	private int mRecmCurrentPage = 0;
	private int mHotCurrentpage = 0;
	private HotGameLvHead mHeadView = null;
	private ViewPager mViewPagerZone;
	private GameZoneViewPagerAdapter mViewPagerAdapter;
	private List<View> mViews;
	private boolean isHotLoadFoot = false;// 是否是更多刷新
	private boolean isRecmLoadFoot = false;// 史诗推荐更多刷新
	private SharedPreferences mSharedPreferences;
	private int mHotSum = 0;// 精品游戏数
	private int mRecSum = 0; // 史诗游戏数
	private View mTipcLoadingView, mTicpView;// 专题加载提示信息布局，专题layout
	private TextView mTvClickBreak;// 点击刷新
	private View mWilingView, mPromptView;// 正在加载View 和提示网络不稳定view
	private ProgressBar mEmptyProgressBar;
	private TextView mTvEmpty;
	public static final int HOT_TYPE = 2;// 热门游戏id
	public static final int RECOMMEND_TYPE = 1;// 史诗推荐id
	private int mScrrenWidth;
	private boolean firstToTopic = true;
	private int lflag=1;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mScrrenWidth = getWindowManager().getDefaultDisplay().getWidth();
		setContentView(R.layout.game_activity_zone);
		mSharedPreferences = getSharedPreferences("isFirstIn", MODE_APPEND);
		// PreferenceManager
		// .getDefaultSharedPreferences(this);
		initViews();
		initAdapter();
	}

	/**
	 * 设置是否显示进度圈值
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-5-24 下午2:50:14
	 */

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * @Title: init
	 * @Description: 加载控件
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */

	private void initViews() {
		bindHeadRightButton(R.drawable.common_head_search);
		// 初始化诗史推荐的视图
		mRecommendPullDownView = new PullDownView(this);
		mRecommendPullDownView.setOnPullDownListener(this);
		mRecommendListView = mRecommendPullDownView.getListView();
		mRecommendPullDownView.setTvClickBreakOnClick(mClickBreakListener);
		// 去掉选中效果
		mRecommendListView.setSelector(R.color.list_item_selector);
		// 初始化精品游戏的视图
		mHotPullDownView = new PullDownView(this);
		initTopicView();
		mViewPagerZone = (ViewPager) findViewById(R.id.game_viewpager_zone);
		mViews = new ArrayList<View>();
		mViews.add(mRecommendPullDownView);
		mViews.add(mHotPullDownView);
		mViews.add(mTicpView);
		mViewPagerZone.setOnPageChangeListener(this);
		mRadioGroup = (RadioGroup) findViewById(R.id.game_radioGroup_container);
		mRadioButtonRecommend = (RadioButton) findViewById(R.id.game_radioButton_recommend);
		mRadioButtonBest = (RadioButton) findViewById(R.id.game_radioButton_best);
		mRadioButtonSpec = (RadioButton) findViewById(R.id.game_radioButton_spec);
		mRadioButtonRecommend.setOnCheckedChangeListener(this);
		mRadioButtonBest.setOnCheckedChangeListener(this);
		mRadioButtonSpec.setOnCheckedChangeListener(this);
	}

	/**
	 * 
	 * @author fcs
	 * @Description:初始化化精品游戏
	 * @date 2013-6-1 上午10:45:41
	 */
	private void initHotGame() {
		mHotPullDownView.setOnPullDownListener(this);
		mHotPullDownView.setTvClickBreakOnClick(mClickBreakListener);
		if (mHotListView != null) {
			// mGameHotList.clear();
			return;
		}
		if (mGameHotList == null || mGameHotList.size() < 1) {
			return;
		}
		
		mHotListView = mHotPullDownView.getListView();
		addHeadView(mGameHotList);
		mGameHotHandle.clear();
		mGameHotNormal.clear();
		for(GameInfo mgameInfo:mGameHotList){
			Log.i("life", "handtype:"+mgameInfo.getHandleType());
			if(mgameInfo.getHandleType()>0){
				mGameHotHandle.add(mgameInfo);
			}
			else{
				mGameHotNormal.add(mgameInfo);
			}
		}
		//mHotGameAdapter.setGroup(mGameHotList);
		mHotGameAdapter.setGroup(mGameHotHandle);
		mHotListView.setAdapter(mHotGameAdapter);
		mHotListView.setOnItemClickListener(hotItemClickListener);
		setPullDownViewProperties(mHotPullDownView);
		
		if(mHeadView!=null){
			mHeadView.getRbHandle().setText(getResources().getString(R.string.game_handle, mGameHotHandle.size()));
			mHeadView.getRbNormal().setText(getResources().getString(R.string.game_normal, mGameHotNormal.size()));
			mHeadView.setOnHotGameListener(new OnHotGameListener() {
				
				@Override
				public void onHotGame(int currentType) {
					if(currentType==1){
						lflag=1;
						mHotGameAdapter.setGroup(mGameHotHandle);
					}
					else if(currentType==2){
						lflag=2;
						mHotGameAdapter.setGroup(mGameHotNormal);
					}
				}
			});
		}
		
	}

	/**
	 * @author fcs
	 * @Description:初始化专题控件
	 * @date 2013-6-12 上午10:37:23
	 */
	private void initTopicView() {
		mTicpView = LayoutInflater.from(this).inflate(R.layout.common_listview,
				null);
		// 游戏专题列表ListView
		mTopicListView = (ListView) mTicpView.findViewById(R.id.topicListView);
		mTipcLoadingView = mTicpView.findViewById(R.id.topicLoadView);
		mWilingView = mTicpView.findViewById(R.id.common_layout_wiling);
		mPromptView = mTicpView.findViewById(R.id.commom_layout_prompt);
		mTvClickBreak = (TextView) mTicpView
				.findViewById(R.id.common_tv_clickbreak);
		mEmptyProgressBar = (ProgressBar) mTicpView
				.findViewById(R.id.emptyProgress);
		mTvEmpty = (TextView) mTicpView.findViewById(R.id.emptyText);
		mTvClickBreak.setOnClickListener(mClickBreakListener);
		mTopicListView.setOnItemClickListener(mToticListItem);
	}

	private OnItemClickListener mToticListItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Bundle mBundle = new Bundle();
			mBundle.putInt("position", position);
			mBundle.putSerializable(GameTopicDetailActivity.GAME_TYPE,
					mGameTypeList.get(position));
			/*jumpToActivity(GameZoneActivity.this,
					GameTopicDetailActivity.class, mBundle, false);*/
			Intent intent = new Intent(GameZoneActivity.this,GameTopicDetailActivity.class);
			intent.putExtras(mBundle);
			startActivityForResult(intent, 1000);
			startAnimate(GameZoneActivity.this);
		}
	};

	/**
	 * @author fcs
	 * @Description:显示空信息提示
	 * @date 2013-6-12 上午10:42:35
	 */
	private void showTopicEmptyTV() {
		mTipcLoadingView.setVisibility(View.VISIBLE);
		mWilingView.setVisibility(View.VISIBLE);
		mPromptView.setVisibility(View.GONE);
		mEmptyProgressBar.setVisibility(View.GONE);
		mTvEmpty.setText(getString(R.string.common_no_data));
	}

	/**
	 * @author fcs
	 * @Description:初始化精品数据库
	 * @date 2013-6-1 上午10:34:33
	 */
	private void initHotGameDB() {
		// 获取以后才初始化
		mGameHotList = getDataBaseData(2);
		if (mGameHotList.size() > 0) {
			mHotCurrentpage = (mGameHotList.size() - 1) / 10 + 1;
			if (isTimeToRefreshHotGame()) {
				mHotCurrentpage = 0;
			}
			initHotGame();
			mHotPullDownView.hideLoadingView();
		}
	}

	private boolean isTimeToRefreshHotGame() {
		// TODO Auto-generated method stub

		SharedPreferences sp = getSharedPreferences("timeToRefreshSP",
				MODE_PRIVATE);
		Long lastRefreshTime = sp.getLong("lastRefreshTime", 0l);
		Long now = System.currentTimeMillis();
		sp.edit().putLong("lastRefreshTime", now).commit();

		return now - lastRefreshTime > 24 * 60 * 60 * 1000;

	}

	/**
	 * @author ydc
	 * @Description:初始化广告数据库
	 * @date 2013-6-1 上午10:33:56
	 */
	private void initAdDB() {
		// 获取以后才初始化
		mAdList = PersistentSynUtils.getModelList(AdInfo.class, " id>0 ");
		if (mAdList.size() > 0) {
			mRecommendPullDownView.hideLoadingView();
		}
	}

	/**
	 * 
	 * @author fcs
	 * @Description:初始化专题
	 * @date 2013-6-1 上午10:35:39
	 */
	private void initTopicDB() {
		mGameTypeList = PersistentSynUtils.getModelList(GameType.class,
				" id >0 ");
		if (mGameTypeList.size() > 0) {
			Collections.sort(mGameTypeList, new Comparator<GameType>() {

	            public int compare(GameType arg0, GameType arg1) {

	                return arg1.getOrderNum().compareTo(arg0.getOrderNum());

	            }

	        });
			//mGameTypeList.add(0, addGameType());
			mTipcLoadingView.setVisibility(View.GONE);
		}

	}
	
	/**
	 * 
	 * @author fcs
	 * @Description:初始化适配器
	 * @date 2013-6-1 上午10:36:55
	 */
	private void initAdapter() {
		mViewPagerAdapter = new GameZoneViewPagerAdapter(mViews);
		mViewPagerZone.setAdapter(mViewPagerAdapter);
		// 初始化三个适配器
		mRecommendAdapter = new RecommendAdapter(this, recommedButtonInferface);
		mRecommendAdapter.setScrrenW(mScrrenWidth);
		// initRecommendDB();
		initAdDB();
		mRecommendAdapter.setGroup(mAdList);
		mRecommendListView.setAdapter(mRecommendAdapter);
		setPullDownViewProperties(mRecommendPullDownView);
		if (mGameRecList.isEmpty()) {
			setPullDownViewHideFooter(mRecommendPullDownView);
		}

		mHotGameAdapter = new HotGameAdapter(this);
		initHotGameDB();

		mGameTopicAdapter = new GameTopicAdapter(this);
		initTopicDB();
		mGameTopicAdapter.setGroup(mGameTypeList);
		mTopicListView.setAdapter(mGameTopicAdapter);

		changeTab();
	}

	/**
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-6-1 上午10:48:29
	 */
	private void setPullDownViewProperties(PullDownView mPullDownView) {
		// 加载数据 本类使用
		// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
		mPullDownView.enableAutoFetchMore(true, 1);
		// mPullDownView.setShowFooter();// 隐藏 并禁用尾部
		mPullDownView.setShowHeader();// 显示并且可以使用头部刷新
	}

	/**
	 * 
	 * @author fcs
	 * @Description:隐藏更多
	 * @date 2013-6-1 上午10:25:22
	 */
	private void setPullDownViewHideFooter(PullDownView mPullDownView) {
		mPullDownView.setHideFooter();
	}

	/**
	 * 
	 * @author fcs
	 * @Description:显示更多
	 * @date 2013-6-1 上午10:25:22
	 */
	private void setPullDownViewShowFooter(PullDownView mPullDownView) {
		mPullDownView.setShowFooter();
	}

	/**
	 * 选中项..
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.game_radioButton_recommend:
				if (mCurrentType != 1) {
					mViewPagerZone.setCurrentItem(0);
					mCurrentType = 1;
					changeTab();
				}
				break;
			case R.id.game_radioButton_best:
				if (mCurrentType != 2) {
					mViewPagerZone.setCurrentItem(1);
					mCurrentType = 2;
					changeTab();
				}
				break;
			case R.id.game_radioButton_spec:

				if (mCurrentType != 3) {
					mViewPagerZone.setCurrentItem(2);
					mCurrentType = 3;
					changeTab();
				}
				break;
			}
		}
	}

	/**
	 * 设置RadioButton变换的颜色
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-5-20 下午2:42:53
	 */
	private void setRdnColorChange(RadioButton rb1, RadioButton rd2,
			RadioButton rd3, int type) {
		switch (type) {
		case 1:
			rb1.setTextColor(getResources().getColor(R.color.color25));
			rb1.setBackgroundResource(R.drawable.btn_game_zone_left_sel);
			rd2.setTextColor(getResources().getColor(R.color.color26));
			rd2.setBackgroundResource(R.drawable.btn_game_zone_left_unsel);
			rd3.setTextColor(getResources().getColor(R.color.color26));
			rd3.setBackgroundResource(R.drawable.btn_game_zone_left_unsel);
			break;
		case 2:
			rb1.setTextColor(getResources().getColor(R.color.color26));
			rb1.setBackgroundResource(R.drawable.btn_game_zone_left_unsel);
			rd2.setTextColor(getResources().getColor(R.color.color25));
			rd2.setBackgroundResource(R.drawable.btn_game_zone_left_sel);
			rd3.setTextColor(getResources().getColor(R.color.color26));
			rd3.setBackgroundResource(R.drawable.btn_game_zone_left_unsel);
			break;
		case 3:
			rb1.setTextColor(getResources().getColor(R.color.color26));
			rb1.setBackgroundResource(R.drawable.btn_game_zone_left_unsel);
			rd2.setTextColor(getResources().getColor(R.color.color26));
			rd2.setBackgroundResource(R.drawable.btn_game_zone_left_unsel);
			rd3.setTextColor(getResources().getColor(R.color.color25));
			rd3.setBackgroundResource(R.drawable.btn_game_zone_left_sel);
			break;
		}
	}

	/***
	 * 
	 * 
	 * @author fcs
	 * @Description:根据点击类型，移除当前布局，添加点击的布局
	 * @date 2013-1-29 下午2:28:20
	 */
	private void changeTab() {
		mRecmCurrentPage = 0;
		// mHotCurrentpage = 0;
		setRdnColorChange(mRadioButtonRecommend, mRadioButtonBest,
				mRadioButtonSpec, mCurrentType);
		if (mCurrentType == 1) {
			if (Preferences.getGameRecommendState(mSharedPreferences) == 0) {
				loadGameListRemoteData();
				Preferences.setGameRecommendState(mSharedPreferences.edit(), 1);
			} else {
				if (mAdList == null || mAdList.size() == 0) {
					loadGameListRemoteData();
				}
			}
		} else if (mCurrentType == 2) {
			initHotGame();
			if (Preferences.getGameHotState(mSharedPreferences) == 0) {
				loadGameHotListRemoteData();
				Preferences.setGameHotState(mSharedPreferences.edit(), 1);
			} else {
				if (mGameHotList == null || mGameHotList.size() == 0) {
					loadGameHotListRemoteData();
				}
			}
		} else if (mCurrentType == 3) {
			if (firstToTopic) {
				loadGameTopicRemoteData();
				firstToTopic = false;
			} else {
				if (mGameTypeList == null || mGameTypeList.size() == 0) {
					loadGameTopicRemoteData();
				}
			}
		}
	}

	/**
	 * 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete()
	 */
	public void onRefresh() {
		if (mCurrentType == 1) {
			loadGameListRemoteData();
			isRecmLoadFoot = false;
		} else if(mCurrentType==2){
			isHotLoadFoot = false;
			loadGameHotListRemoteData();
		}
	}

	/**
	 * 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore()
	 */
	public void onMore() {
		if (mCurrentType == 1) {
			isRecmLoadFoot = true;
			loadGameListRemoteData();
		} else if(mCurrentType==2){
			isHotLoadFoot = true;
			loadGameHotListRemoteData();
		}
	}

	/**
	 * 精品游戏item点击事件
	 */
	private OnItemClickListener hotItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.i("life", "pos:"+position);
			int cuPosition = position - 2;
			if(lflag==1){
				if (cuPosition >= 0 && cuPosition < mGameHotHandle.size()) {
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(CommDetailActivity.KEY_GAMEINFO,
							mGameHotHandle.get(cuPosition));

					mBundle.putInt(CommDetailActivity.KEY_GAMEINFO_TYPEID,
							mCurrentType);
					jumpToActivity(GameZoneActivity.this, CommDetailActivity.class,
							mBundle, false);
				}
			}
			else if(lflag==2){
				if (cuPosition >= 0 && cuPosition < mGameHotNormal.size()) {
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(CommDetailActivity.KEY_GAMEINFO,
							mGameHotNormal.get(cuPosition));

					mBundle.putInt(CommDetailActivity.KEY_GAMEINFO_TYPEID,
							mCurrentType);
					jumpToActivity(GameZoneActivity.this, CommDetailActivity.class,
							mBundle, false);
				}
			}
			

		}
	};

	/**
	 * @Title: addHeadView
	 * @Description: 增加头布局文件
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void addHeadView(Group<GameInfo> gameInfos) {
		if(gameInfos.size()<5){
			return ;
		}
		Group<GameInfo> headGameInfos = new Group<GameInfo>();
		if (gameInfos.size() == 0) {
			headGameInfos.add(new GameInfo());
		}
		Iterator<GameInfo> sListIterator = gameInfos.iterator();
		int i = 0;
		while (sListIterator.hasNext()) {
			GameInfo gameInfo = sListIterator.next();
			i++;
			headGameInfos.add(gameInfo);
			sListIterator.remove();
			if (i > 4) {
				break;
			}
		}
		if (headGameInfos.size() < 5) {
			return;
		}
		mHeadView = new HotGameLvHead(GameZoneActivity.this, headGameInfos);
		View view = mHeadView.getView();
		mHotListView.addHeaderView(view, null, false);
		mHeadView.exeAutoScroll();
	}

	/**
	 * @Title: updateHeadView
	 * @Description: 更新精品游戏头部
	 * @param gameInfos
	 * @throws
	 */
	private void updateHeadView(Group<GameInfo> gameInfos) {
		if (mHeadView == null) {
			return;
		}
		if(gameInfos.size()<5){
			return ;
		}
		Group<GameInfo> headGameInfos = new Group<GameInfo>();
		Iterator<GameInfo> sListIterator = gameInfos.iterator();
		int i = 0;
		while (sListIterator.hasNext()) {
			GameInfo gameInfo = sListIterator.next();
			i++;
			headGameInfos.add(gameInfo);
			sListIterator.remove();
			if (i > 4) {
				break;
			}
		}
		if (headGameInfos.size() >= 5) {
			if (mHeadView == null) {
				mHeadView = new HotGameLvHead(GameZoneActivity.this,
						headGameInfos);
				View view = mHeadView.getView();
				mHotListView.addHeaderView(view, null, false);
				mHeadView.exeAutoScroll();
			} else {
				mHeadView.setGroup(headGameInfos);
			}
		}

	}

	/**
	 * 删除数据库缓存文件
	 */

	public void deleteDataBaseData(int typeId) {
		PersistentSynUtils.execDeleteData(GameInfo.class, "where typeId="
				+ typeId);
	}

	/**
	 * 删除 查询游戏类型缓存文件
	 */
	public void deleteGameTypeData() {
		PersistentSynUtils.execDeleteData(GameType.class, " where id>0");
	}

	/**
	 * 删除数据库广告缓存文件
	 */
	public void deleteAdData() {
		PersistentSynUtils.execDeleteData(AdInfo.class, " where id>0");
	}

	/**
	 * @Title: getGameTypeData
	 * @Description: 查询数据库缓存文件
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private Group<GameInfo> getDataBaseData(int typeId) {
		return PersistentSynUtils.getModelList(GameInfo.class, " typeId="
				+ typeId);
	}

	/**
	 * 根据type删除数据库图片缓存文件
	 */
	private void deleteSCRDataBaseData(String typeId) {
		PersistentSynUtils.execDeleteData(ScreenShootInfo.class,
				"where gameTypeId = '" + typeId + "'");
	}

	/**
	 * 根据id删除数据库图片缓存文件
	 */
	private void deleteSCRDataBaseDataById(String gameId) {
		PersistentSynUtils.execDeleteData(ScreenShootInfo.class,
				"where gameId = '" + gameId + "'");
	}

	/**
	 * 获取史诗列表
	 * 
	 * @Title: loadRemoteData
	 * @Description: 通过AsyncTask获取远程数据
	 * @param
	 * @return void
	 * @throws
	 */
	private void loadGameListRemoteData() {
		// 取消所有任务
		taskManager.cancelTask(Constant.TASKKEY_RECOMMED_LIST);
		// 启动新任务
		taskManager.startTask(gemeListTaskListener,
				Constant.TASKKEY_RECOMMED_LIST);

	}

	/**
	 * @author fcs
	 * @Description:获取精品游戏列表
	 * @date 2013-2-25 下午8:30:20
	 */
	private void loadGameHotListRemoteData() {
		taskManager.cancelTask(Constant.TASKKEY_HOTGAME_LIST);
		taskManager.startTask(gameHotListTaskListener,
				Constant.TASKKEY_HOTGAME_LIST);
	}

	/**
	 * 
	 * @author 当需要时才启动任务
	 * @Description:游戏专题
	 * @date 2013-1-31 下午6:50:56
	 */
	private void loadGameTopicRemoteData() {
		taskManager.cancelTask(Constant.TASKKEY_GAME_TOPIC_LIST);
		taskManager.startTask(gameTopicAsynTaskListener,
				Constant.TASKKEY_GAME_TOPIC_LIST);
	}

	/**
	 * @param group
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription  根据一大图两小图顺序排序后的广告数据
	 */
	private Group<AdInfo> getSortedAd(Group<AdInfo> group) {
		// TODO Auto-generated method stub
		Group<AdInfo> bigImgAd = new Group<AdInfo>();
		Group<AdInfo> midImgAd = new Group<AdInfo>();
		Group<AdInfo> showAdList = new Group<AdInfo>();
		for (AdInfo adInfo : group) {
			if (adInfo.getSizeType().equals(
					getIdToString(R.string.ad_game_img_type_big))) {
				bigImgAd.add(adInfo);
			} else if (adInfo.getSizeType().equals(
					getIdToString(R.string.ad_game_img_type_small))) {
				midImgAd.add(adInfo);
			}
		}
		if (bigImgAd.size() * 2 > midImgAd.size()) {
			for (int i = 0; i < midImgAd.size(); i += 2) {
				if (i + 1 < midImgAd.size()) {
					showAdList.add(bigImgAd.get(i / 2));
					showAdList.add(midImgAd.get(i));
					showAdList.add(midImgAd.get(i + 1));
				}

			}
		} else {
			for (int i = 0; i < bigImgAd.size(); i++) {
				showAdList.add(bigImgAd.get(i));
				showAdList.add(midImgAd.get(i * 2));
				showAdList.add(midImgAd.get(i * 2 + 1));
			}
		}
		return showAdList;
	}

	/**
	 * 精品游戏异步加载
	 */
	AsynTaskListener<Group<GameInfo>> gameHotListTaskListener = new AsynTaskListener<Group<GameInfo>>() {

		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			// TODO Auto-generated method stub
			if (mGameHotList.size() == 0) {
				mHotPullDownView.setLoadingView();
			}
			return true;
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<Group<GameInfo>> result) {
			if (result.getCode() == TaskResult.OK && result.getData() != null
					&& !result.getData().isEmpty()) {

				if (!isHotLoadFoot && result.getData().size() < 1) {
					return;
				}
				mHotCurrentpage = (isHotLoadFoot) ? ++mHotCurrentpage : 1;
				// 如果有数据
				if (mHotCurrentpage == 1) {
					// 清空缓存在数据库中的数据
					deleteDataBaseData(2);

					for (GameInfo gameInfo : result.getData()) {
						// DebugTool.debug("Hot Game:"+gameInfo.toString());
						gameInfo.setTypeId("2");
						// 服务器获取到得数据插入数据库
						PersistentSynUtils.addModel(gameInfo);
						Group<ScreenShootInfo> screenShootInfos = gameInfo
								.getImgs();
						deleteSCRDataBaseDataById(gameInfo.getGameId());
						for (ScreenShootInfo ssInfo : screenShootInfos) {
							ssInfo.setGameId(gameInfo.getGameId());
							PersistentSynUtils.addModel(ssInfo);
						}

					}
					if (mGameHotList.isEmpty()) {
						mGameHotList.addAll(result.getData());
						// 初始化精品游戏
						initHotGame();
					} else {
						mGameHotList.clear();
						mGameHotList.addAll(result.getData());
						// 刷新头部
						updateHeadView(mGameHotList);
						mGameHotHandle.clear();
						mGameHotNormal.clear();
						for (GameInfo gameInfo : mGameHotList) {
							if(gameInfo.getHandleType()>0){
								mGameHotHandle.add(gameInfo);
							}
							else{
								mGameHotNormal.add(gameInfo);
							}
						}
						if(lflag==1){
							mHeadView.getRbHandle().setText(getResources().getString(R.string.game_handle, mGameHotHandle.size()));
							mHeadView.getRbNormal().setText(getResources().getString(R.string.game_normal, mGameHotNormal.size()));
							mHotGameAdapter.setGroup(mGameHotHandle);
						}
						else{
							mHeadView.getRbHandle().setText(getResources().getString(R.string.game_handle, mGameHotHandle.size()));
							mHeadView.getRbNormal().setText(getResources().getString(R.string.game_normal, mGameHotNormal.size()));
							mHotGameAdapter.setGroup(mGameHotNormal);
						}
					}
					mHotPullDownView.RefreshComplete();
				} else {
					// 从网络获取的数据添加至集合中
					//mGameHotList.addAll(result.getData());
					for (GameInfo gameInfo : result.getData()) {
						if(mGameHotList.contains(gameInfo)){
							continue;
						}
						else{
							mGameHotList.add(gameInfo);
							if(gameInfo.getHandleType()>0){
								mGameHotHandle.add(gameInfo);
							}
							else{
								mGameHotNormal.add(gameInfo);
							}
							gameInfo.setTypeId("2");
							// 服务器获取到得数据插入数据库
							PersistentSynUtils.addModel(gameInfo);
							Group<ScreenShootInfo> screenShootInfos = gameInfo
									.getImgs();
							for (ScreenShootInfo ssInfo : screenShootInfos) {
								ssInfo.setGameId(gameInfo.getGameId());
								ssInfo.setGameTypeId(gameInfo.getTypeId());
								PersistentSynUtils.addModel(ssInfo); 
							}
						}
					}
					
					if(lflag==1){
						mHeadView.getRbHandle().setText(getResources().getString(R.string.game_handle, mGameHotHandle.size()));
						mHeadView.getRbNormal().setText(getResources().getString(R.string.game_normal, mGameHotNormal.size()));
						//mHotGameAdapter.setGroup(mGameHotHandle);
					}
					else{
						mHeadView.getRbHandle().setText(getResources().getString(R.string.game_handle, mGameHotHandle.size()));
						mHeadView.getRbNormal().setText(getResources().getString(R.string.game_normal, mGameHotNormal.size()));
						//mHotGameAdapter.setGroup(mGameHotNormal);
					}
					
				}
				mHotPullDownView.notifyDidMore();
				// 获得数据总数
				mHotSum = result.getTotalCount();
				// 通知适配器数据改变
				/*mHotGameAdapter.setGroup(mGameHotList);
				mHotGameAdapter.notifyDataSetChanged();*/
				initHotGame();
				if (!isHotLoadFoot && mHotListView != null) {
					mHotListView.setSelectionFromTop(1, 0);
				}

			} else {
				if (isHotLoadFoot) {
					if (result.getCode() == TaskResult.OK
							&& (result.getData() == null || result.getData()
									.isEmpty())) {
						Toast.makeText(GameZoneActivity.this,
								getString(R.string.game_no_more), 2 * 1000)
								.show();
					}
					mHotPullDownView.notifyDidMore();
				} else {
					mHotPullDownView.RefreshComplete();
				}

			}

			if (mHotSum <= mGameHotList.size() + 5) {
				// 隐藏精品更多
				setPullDownViewHideFooter(mHotPullDownView);
			} else {
				// 显示更多
				setPullDownViewShowFooter(mHotPullDownView);
			}
			
			/*if (mHotSum <= mGameHotNormal.size() + 5) {
				// 隐藏精品更多
				setPullDownViewHideFooter(mHotPullDownView);
			} else {
				// 显示更多
				setPullDownViewShowFooter(mHotPullDownView);
			}*/


			mHotPullDownView.hideLoadingView();
			if (mGameHotList.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					mHotPullDownView.showPromptLayout();
				} else {
					mHotPullDownView.showLoadingView();
					mHotPullDownView.setEmptyView();
				}
			}

		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {
			// TODO Auto-generated method stub
			HttpReqParams params = new HttpReqParams();
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			params.setPageSize(mPageSize);
			if (!isHotLoadFoot) {
				params.setCurrentPage(1);
			} else {
				params.setCurrentPage(mHotCurrentpage + 1);
			}
			// params.setUserId("0");
			// return HttpApi.getList(UrlConstant.HTTP_HOTGAME_LIST,
			// GameInfo.class, params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_HOTGAME_LIST1,
					UrlConstant.HTTP_HOTGAME_LIST2,
					UrlConstant.HTTP_HOTGAME_LIST3, GameInfo.class,
					params.toJsonParam());
		}

	};

	/**
	 * 调用AsynTaskListener 史诗在网络下载完后肯定回调onResult方法
	 */
	AsynTaskListener<Group<AdInfo>> gemeListTaskListener = new AsynTaskListener<Group<AdInfo>>() {

		@Override
		public boolean preExecute(BaseTask<Group<AdInfo>> task, Integer taskKey) {
			if (mAdList.size() == 0) {
				mRecommendPullDownView.setLoadingView();
			}
			return true;
		}

		/**
		 * 获取服务器数据，将返回
		 */
		@Override
		public TaskResult<Group<AdInfo>> doTaskInBackground(Integer taskKey) {
			HttpReqParams params = new HttpReqParams();
			// params.setDeviceId("20140723134324300179");
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			params.setModelId("A1A0A0000");

			// return HttpApi.getList(UrlConstant.HTTP_AD_LIST, AdInfo.class,
			// params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_AD_LIST1,
					UrlConstant.HTTP_AD_LIST2, UrlConstant.HTTP_AD_LIST3,
					AdInfo.class, params.toJsonParam());
		}

		/**
		 * 从服务器数据传入次方法
		 * 
		 * @author
		 * @Description:
		 * @date 2013-1-31 下午7:45:47
		 */
		@Override
		public void onResult(Integer taskKey, TaskResult<Group<AdInfo>> result) {

			if (result.getCode() == TaskResult.OK && result.getData() != null
					&& !result.getData().isEmpty()) {
				mRecmCurrentPage = (isRecmLoadFoot) ? ++mRecmCurrentPage : 1;
				Group<AdInfo> adData = getSortedAd(result.getData());
				// 如果有数据
				if (mRecmCurrentPage == 1) {

					deleteAdData();
					for (AdInfo adInfo : adData) {
						// adInfo.setTypeId("3");
						PersistentSynUtils.addModel(adInfo);
					}
					mAdList.clear();
					mRecommendPullDownView.RefreshComplete();
				}
				// 获取数据总数
				mRecSum = result.getTotalCount();
				// 从网络获取的数据添加至集合中
				// mGameRecList.addAll(result.getData());
				mAdList.addAll(adData);
				mRecommendPullDownView.notifyDidMore();
				// 通知适配器数据改变
				mRecommendAdapter.setGroup(mAdList);
				mRecommendAdapter.notifyDataSetChanged();
				// 隐藏更多
				if (mRecSum <= mAdList.size()) {
					setPullDownViewHideFooter(mRecommendPullDownView);
				} else {
					setPullDownViewShowFooter(mRecommendPullDownView);

				}

			} else {
				if (isRecmLoadFoot) {
					mRecommendPullDownView.notifyDidMore();
				} else {
					mRecommendPullDownView.RefreshComplete();
				}
			}

			mRecommendPullDownView.hideLoadingView();
			if (mAdList.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					mRecommendPullDownView.showPromptLayout();
				} else {
					mRecommendPullDownView.setEmptyView();
				}
			}
		}
	};

	/**
	 * 游戏专题异步加载事件监听器 在网络下载完后肯定回调onResult方法
	 */
	AsynTaskListener<Group<GameType>> gameTopicAsynTaskListener = new AsynTaskListener<Group<GameType>>() {

		@Override
		public boolean preExecute(BaseTask<Group<GameType>> task,
				Integer taskKey) {
			if (mGameTypeList.size() == 0) {
				mTipcLoadingView.setVisibility(View.VISIBLE);
			}
			return true;
		}

		@Override
		public TaskResult<Group<GameType>> doTaskInBackground(Integer taskKey) {
			HttpReqParams params = new HttpReqParams();
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			if (BaseApplication.getLoginUser() == null
					|| BaseApplication.getLoginUser().getUserId() == null) {
				params.setUserId("0");
			} else if (BaseApplication.getLoginUser() != null
					&& BaseApplication.getLoginUser().getUserId() != null) {
				params.setUserId(BaseApplication.getLoginUser().getUserId());
			}
			//
			// return HttpApi.getList(UrlConstant.HTTP_GAMETYPE_LIST,
			// GameType.class, params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_GAMETYPE_LIST1,
					UrlConstant.HTTP_GAMETYPE_LIST2,
					UrlConstant.HTTP_GAMETYPE_LIST3, GameType.class,
					params.toJsonParam());
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<Group<GameType>> result) {

			if (result.getCode() == TaskResult.OK && result.getData() != null
					&& result.getData().size() > 0) {
				deleteGameTypeData();
				mGameTypeList = result.getData();
				Collections.sort(mGameTypeList, new Comparator<GameType>() {

		            public int compare(GameType arg0, GameType arg1) {

		                return arg1.getOrderNum().compareTo(arg0.getOrderNum());

		            }

		        });
				Iterator<GameType> sListIterator = mGameTypeList.iterator();
				while (sListIterator.hasNext()) {
					GameType gameType = sListIterator.next();
					// typeId=1,2分别为诗史推荐和精品游戏 这两个类别不应该在游戏专题的列表里显示
					// if (gameType.getTypeId() == 1 || gameType.getTypeId() ==
					// 2) {
					// sListIterator.remove();
					// continue;
					// }
					PersistentSynUtils.addModel(gameType);
				}
				//mGameTypeList.add(0, addGameType());
				mGameTopicAdapter.setGroup(mGameTypeList);
				mGameTopicAdapter.notifyDataSetChanged();
				mTipcLoadingView.setVisibility(View.GONE);
			}
			if (mGameTypeList.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					mWilingView.setVisibility(View.GONE);
					mPromptView.setVisibility(View.VISIBLE);
				} else {
					showTopicEmptyTV();
				}
			}
		}
	};

	/**
	 * 史诗推荐点击事件...
	 * 
	 */
	private RecommedButtonInferface recommedButtonInferface = new RecommedButtonInferface() {
		@Override
		public void bigImageOnClick(AdInfo object) {
			Intent intent = new Intent(GameZoneActivity.this,
					CommDetailActivity.class);
			Bundle mBundle = new Bundle();
			GameInfo transGameInfo = new GameInfo();
			transGameInfo.setGameId(object.getGameId());
			mBundle.putSerializable(CommDetailActivity.KEY_GAMEINFO,
					transGameInfo);
			mBundle.putInt(CommDetailActivity.KEY_GAMEINFO_TYPEID, mCurrentType);
			intent.putExtras(mBundle);
			startActivity(intent);
		}
	};

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		mRadioGroup.clearCheck();
		mCurrentType = arg0 + 1;
		changeTab();
	}

	private OnClickListener mClickBreakListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 1 史诗推荐 2精品游戏 3游戏专题
			if (mCurrentType == 1) {
				mRecommendPullDownView.setLoadingView();
				loadGameListRemoteData();
			} else if (mCurrentType == 2) {
				mHotPullDownView.setLoadingView();
				loadGameHotListRemoteData();
			} else {
				mTipcLoadingView.setVisibility(View.VISIBLE);
				mWilingView.setVisibility(View.VISIBLE);
				mPromptView.setVisibility(View.GONE);
				loadGameTopicRemoteData();
			}
		}
	};
	
	/*private GameType addGameType(){
		GameType newGameRecommandType = new GameType();
		newGameRecommandType.setTypeId("-1");
		newGameRecommandType.setIcon("");
		newGameRecommandType.setName("新游推荐");
		newGameRecommandType.setRemark("新游推荐频道为广大玩家提供海量最新游戏，一大波新游来袭");
		newGameRecommandType.setOrderNum(0);
		newGameRecommandType.setGames(0);
		return newGameRecommandType;
	}*/
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1000){
			initTopicDB();
			mGameTopicAdapter.setGroup(mGameTypeList);
			mTopicListView.setAdapter(mGameTopicAdapter);
		}
	};
}
