package com.sxhl.market.control.user.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.user.adapter.GiftGameAdapter;
import com.sxhl.market.control.user.adapter.GiftPackageViewpagerAdapter;
import com.sxhl.market.control.user.adapter.MyGiftAdapter;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.GiftGameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGiftInfo;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskResult;

/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 礼包主页，包含礼包中心和我的礼包
 */
public class GiftPackageActivity extends BaseActivity implements
		OnCheckedChangeListener {

	ViewPager mviewPager;
	GiftGameAdapter myGitfGameAdapter;
	MyGiftAdapter mMyGiftAdpter;

	List<View> viewList = new ArrayList<View>();
	GiftPackageViewpagerAdapter mpagerAdapter;
	RadioButton rb_giftCenter, rb_myGift;
	ListView myGiftListView;

	ListView giftListView;
	Group<GiftGameInfo> giftGameInfos;
	Group<MyGiftInfo> myGiftInfos;
	ArrayList<Boolean> headLineStates;
	UserInfo user;
	LinearLayout linear_loadingProgress;
	ProgressBar giftProLoading;
	TextView tvProgress;
	LinearLayout linGiftList;
	LinearLayout linMyGiftList;
	TextView tvEmptyGift, tvEmptyMyGift;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gift_package);
		getSharedPreferences(Constant.SP_HAS_NEW_GIFT, MODE_PRIVATE).edit()
				.putBoolean(Constant.SP_VALUE_HAS_NEW_GIFT, false).commit();
		user = BaseApplication.getLoginUser();
		goBack();
		setHeadTitle(getIdToString(R.string.head_game_gift));
		initView();
		initData();
		initViewPager();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// 从数据库获取用户已领取礼包
		myGiftInfos = PersistentSynUtils.getModelList(MyGiftInfo.class,
				" userId='" + user.getUserId() + "'");

		Collections.sort(myGiftInfos, new Comparator<MyGiftInfo>() {

			@Override
			public int compare(MyGiftInfo lhs, MyGiftInfo rhs) {
				// TODO Auto-generated method stub
				return lhs.getReceiveTime() > rhs.getReceiveTime() ? -1 : 1;
			}
		});
		if (myGiftInfos == null || myGiftInfos.size() == 0) {
			tvEmptyMyGift.setVisibility(View.VISIBLE);
		} else {
			tvEmptyMyGift.setVisibility(View.GONE);
			initHeadTitleStates(myGiftInfos);
			mMyGiftAdpter.setHeadLineStates(headLineStates);
			mMyGiftAdpter.setGroup(myGiftInfos);
			mMyGiftAdpter.notifyDataSetChanged();
		}

		super.onResume();
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription初始化游戏礼包数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		Group<GiftGameInfo> allGiftGameInfos = PersistentSynUtils.getModelList(
				GiftGameInfo.class, " id>0");
		giftGameInfos = new Group<GiftGameInfo>();
		if (allGiftGameInfos != null && allGiftGameInfos.size() > 0) {
			for (GiftGameInfo giftgame : allGiftGameInfos) {
				if (giftgame.getGiftNum() > 0) {
					giftGameInfos.add(giftgame);
				}
			}
		}
		Collections.sort(giftGameInfos, new Comparator<GiftGameInfo>() {

			@Override
			public int compare(GiftGameInfo lhs, GiftGameInfo rhs) {
				// TODO Auto-generated method stub
				return lhs.getSortNum() > rhs.getSortNum() ? 1 : -1;
			}
		});

		myGiftInfos = PersistentSynUtils.getModelList(MyGiftInfo.class,
				" userId='" + user.getUserId() + "'");

		Collections.sort(myGiftInfos, new Comparator<MyGiftInfo>() {

			@Override
			public int compare(MyGiftInfo lhs, MyGiftInfo rhs) {
				// TODO Auto-generated method stub
				return lhs.getReceiveTime() > rhs.getReceiveTime() ? -1 : 1;
			}
		});

		initHeadTitleStates(myGiftInfos);

	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 初始化viewpager
	 */
	private void initViewPager() {
		// TODO Auto-generated method stub
		giftListView = new ListView(this);

		myGiftListView = new ListView(this);
		myGiftListView.setCacheColorHint(getResources().getColor(
				android.R.color.transparent));
		myGiftListView.setSelector(getResources().getDrawable(
				R.drawable.transparent));
		myGiftListView.setDivider(getResources().getDrawable(
				R.drawable.listview_divider));
		linGiftList.addView(tvEmptyGift);
		linGiftList.addView(giftListView);
		tvEmptyGift.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		tvEmptyGift.setGravity(Gravity.CENTER);
		tvEmptyGift.setVisibility(View.GONE);
		giftListView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		linMyGiftList.addView(tvEmptyMyGift);
		linMyGiftList.addView(myGiftListView);
		tvEmptyMyGift.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		tvEmptyMyGift.setGravity(Gravity.CENTER);
		tvEmptyMyGift.setVisibility(View.GONE);
		myGiftListView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mMyGiftAdpter = new MyGiftAdapter(this, headLineStates);
		myGiftListView.setAdapter(mMyGiftAdpter);
		if (myGiftInfos == null || myGiftInfos.size() == 0) {
			tvEmptyMyGift.setVisibility(View.VISIBLE);
		} else {
			tvEmptyMyGift.setVisibility(View.GONE);
			mMyGiftAdpter.setGroup(myGiftInfos);

		}
		myGitfGameAdapter = new GiftGameAdapter(this);
		giftListView.setAdapter(myGitfGameAdapter);
		if (giftGameInfos == null || giftGameInfos.size() == 0) {
			tvEmptyGift.setVisibility(View.GONE);
		} else {

			myGitfGameAdapter.setGroup(giftGameInfos);
		}

		viewList.add(linGiftList);
		viewList.add(linMyGiftList);
		mpagerAdapter = new GiftPackageViewpagerAdapter(viewList);
		mviewPager.setAdapter(mpagerAdapter);

		myGiftListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});
		// if (getIntent().getIntExtra(LoginActivity.GIFTINTENTKEY, 0) ==
		// LoginActivity.ExtraData) {
		loadMyGiftTask();
		// }
	}

	private void initView() {
		// TODO Auto-generated method stub
		linGiftList = new LinearLayout(this);
		linMyGiftList = new LinearLayout(this);
		tvEmptyGift = new TextView(this);
		tvEmptyMyGift = new TextView(this);
		tvEmptyGift.setText("亲，没有内容");
		tvEmptyMyGift.setText("亲，没有内容");
		linear_loadingProgress = (LinearLayout) findViewById(R.id.gift_linearlayout_progress);
		giftProLoading = (ProgressBar) findViewById(R.id.gift_proBar_loading);
		tvProgress = (TextView) findViewById(R.id.gift_tv_loading_tip);
		rb_giftCenter = (RadioButton) findViewById(R.id.gift_pkg_radioBtn_center);
		rb_myGift = (RadioButton) findViewById(R.id.gift_pkg_radioBtn_mygift);
		mviewPager = (ViewPager) findViewById(R.id.vPager_gift_package);
		rb_giftCenter.setOnCheckedChangeListener(this);
		rb_myGift.setOnCheckedChangeListener(this);
		mviewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if (arg0 == 0) {
					rb_giftCenter.setChecked(true);
				} else {
					rb_myGift.setChecked(true);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			if (buttonView.getId() == R.id.gift_pkg_radioBtn_center) {
				mviewPager.setCurrentItem(0);
			} else {
				mviewPager.setCurrentItem(1);
			}
		}

	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 加载用户已领取礼包数据
	 */
	private void loadMyGiftTask() {
		taskManager.cancelTask(Constant.TASKKEY_GIFTCENTER_MYGIFT);
		taskManager.startTask(myGiftRefreshAsynTaskListener,
				Constant.TASKKEY_GIFTCENTER_MYGIFT);
	}

	/**
	 * @param myGifts
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 将用户已领取礼包存到数据库
	 */
	private void saveTodatabase(final Group<MyGiftInfo> myGifts) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PersistentSynUtils.execDeleteData(MyGiftInfo.class,
						" where userId='" + user.getUserId() + "'");
				for (MyGiftInfo myGift : myGifts) {
					// if (myGift.getReceiveTime() != 0
					// && myGift.getUseMethod() != null
					// && !myGift.getUseMethod().equals("")) {
					myGift.setUserId(user.getUserId());
					PersistentSynUtils.addModel(myGift);
					// }
				}
			}
		}).start();
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 已领取礼包数据异步加载
	 */
	AsynTaskListener<Group<MyGiftInfo>> myGiftRefreshAsynTaskListener = new AsynTaskListener<Group<MyGiftInfo>>() {

		@Override
		public boolean preExecute(BaseTask<Group<MyGiftInfo>> task,
				Integer taskKey) {
			// TODO Auto-generated method stub
			if (user == null || user.getUserId() == null) {
				return false;
			}
			linear_loadingProgress.setVisibility(View.VISIBLE);
			mviewPager.setVisibility(View.GONE);
			return true;
		}

		@Override
		public void onResult(Integer taskKey,
				TaskResult<Group<MyGiftInfo>> result) {
			// TODO Auto-generated method stub
			linear_loadingProgress.setVisibility(View.GONE);
			mviewPager.setVisibility(View.VISIBLE);
			if (result.getCode() == 0 && result.getData() != null
					&& result.getData().size() > 0) {
				Group<MyGiftInfo> myGifts = result.getData();
				myGiftInfos = null;
				myGiftInfos = new Group<MyGiftInfo>();
				for (MyGiftInfo myGift : myGifts) {
					if (myGift.getReceiveTime() != 0
							&& myGift.getUseMethod() != null
							&& !myGift.getUseMethod().equals("")) {
						myGiftInfos.add(myGift);
					}
				}
				Collections.sort(myGiftInfos, new Comparator<MyGiftInfo>() {

					@Override
					public int compare(MyGiftInfo lhs, MyGiftInfo rhs) {
						// TODO Auto-generated method stub
						return lhs.getReceiveTime() > rhs.getReceiveTime() ? -1
								: 1;
					}
				});
				if (myGiftInfos == null || myGiftInfos.size() == 0) {
					tvEmptyMyGift.setVisibility(View.VISIBLE);
				} else {
					tvEmptyMyGift.setVisibility(View.GONE);
					initHeadTitleStates(myGiftInfos);
					mMyGiftAdpter.setGroup(myGiftInfos);
					mMyGiftAdpter.setHeadLineStates(headLineStates);
					mMyGiftAdpter.notifyDataSetChanged();
					saveTodatabase(myGiftInfos);
				}

			}
		}

		@Override
		public TaskResult<Group<MyGiftInfo>> doTaskInBackground(Integer taskKey) {
			// TODO Auto-generated method stub
			HttpReqParams param = new HttpReqParams();
			param.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			param.setUserId(user.getUserId());
			return HttpApi.getList(UrlConstant.HTTP_GET_MYGIFT1,
					UrlConstant.HTTP_GET_MYGIFT2, UrlConstant.HTTP_GET_MYGIFT3,
					MyGiftInfo.class, param.toJsonParam());
		}
	};

	/**
	 * @param myCurrentGiftInfos
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 按领取时间将已领取礼包排序
	 */
	private void initHeadTitleStates(Group<MyGiftInfo> myCurrentGiftInfos) {
		// TODO Auto-generated method stub
		headLineStates = null;
		headLineStates = new ArrayList<Boolean>();
		HashSet<String> set = new HashSet<String>();
		for (MyGiftInfo myGiftInfo : myCurrentGiftInfos) {
			if (!set.contains(myGiftInfo.getFormatReceiveTime())) {
				headLineStates.add(true);
			} else {
				headLineStates.add(false);
			}
			set.add(myGiftInfo.getFormatReceiveTime());
		}
	}

}
