package com.sxhl.market.control.game.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.game.adapter.CommentAdapter;
import com.sxhl.market.control.game.adapter.GameZoneViewPagerAdapter;
import com.sxhl.market.control.game.adapter.HotGameGridAdapter;
import com.sxhl.market.control.user.activity.ChangeLoginActivity;
import com.sxhl.market.control.user.activity.NewLoginAndRegisterActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.CollectionInfo;
import com.sxhl.market.model.entity.Comment;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.ScreenShootInfo;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.net.http.download.BtnDownListenner;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.view.InnerPullDownView.OnPullDownListener;
import com.sxhl.market.view.MyDialog;
import com.sxhl.market.view.PullDownView;
import com.sxhl.market.view.costom.CustomGallery;
import com.sxhl.statistics.model.CollectGameInfo;
import com.sxhl.statistics.utils.GameCollectHelper;

@SuppressWarnings("deprecation")
public class CommDetailActivity extends BaseActivity implements
		OnCheckedChangeListener, OnPullDownListener, OnPageChangeListener {
	public static final String KEY_GAMEINFO = "KEY_GAMEINFO";
	public static final String KEY_GAMEINFO_TYPEID = "KEY_GAMEINFO_TYPEID";
	private int mTypeId = 2;
	private CustomGallery mGallery;
	private CollectionInfo mCollectionInfo;
	// 热门游戏视图
	private GridView mHotGridView;
	// 史诗推荐中间模块用来显示 游戏详情和评论切换的那块布局
	private ViewPager mViewPagerContentContainer;
	// 游戏详情视图
	private View mGameDetailContent;
	// 游戏评论内容视图
	private View mCommContent;
	// 评论列表的adapter
	private CommentAdapter mCommentAdapter;
	// 评论编辑框
	private EditText mEditText;
	// 发送评论按钮
	// private Button mSendCommet;
	// 收藏按钮
	private Button mGameCollection;
	// 下载按钮
	private Button mBtnGameDown;
	private RadioButton mRadioBtnGameDetail;
	private PullDownView mCommentpullDownView;// 创建评论PullDownView
	private ListView mCommentListView;
	private int mCurrentPage = 0;
	private LinearLayout mCommentLayout;
	// 返回的游戏对象
	private GameInfo mGameInfo;
	// 包含有游戏详细信息的GameInfo
	private GameInfo mDetailGameInfo;
	private Group<Comment> mGroup = new Group<Comment>();
	private UserInfo mUserInfo;
	private BtnDownListenner mDownloadBtnListenner;
	private HotGameGridAdapter mHotGameadapter;
	private String mEditComment;
	private MyDialog mDialog;
	private RadioButton mRadionButtonGameComm;
	private SharedPreferences mSharedPreferences;
	private int mPageSize = 10;
	private int mCurrentType = 1;
	private boolean mFlag = false;
	private int mCommSum;
	private GameZoneViewPagerAdapter mPagerAdapter;
	private List<View> mContainerView = new ArrayList<View>();// 游戏详情和评论列表
	private boolean mIsLoadFoot = false;
	// 游戏简介中小图标
	private ImageView mIvIconMin;
	// 游戏名称
	private TextView tvGameName;
	// 下载次数
	private TextView tvGameDownCounts;
	// 游戏大小
	private TextView mtvGameSize;
	// 游戏星级水平
	private TextView tvStarLvel,ratingBarHandleControl;
	// 星级
	private RatingBar ratingBarGameMarke;
	// 游戏详情
	private TextView tvDescription;
	private AlertDialog commentDialog;
	// 详细信息名称
	private TextView gameDescriptionName;
	private RadioGroup mRadioGroup;
	private int mScreenH;
	private GralleryAdapter mGralleryAdapter;
	private Group<ScreenShootInfo> mGameImgList;
	private View loadingView;
	private View detialView;
	private EditText et_score;
	private RatingBar rat_score;
	private String gameDetialUrl;
	private AlertDialog dialog_gotoWeb;
	private TextView tv_refresh;
	private View lin_loading;
	private View will_view;
	private boolean hasInitDetail = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_activity_comm_detail);
		mScreenH = getWindowManager().getDefaultDisplay().getHeight();
		Intent intent = getIntent();
		mGameInfo = (GameInfo) intent
				.getSerializableExtra(Constant.KEY_GAMEINFO);

		mTypeId = intent.getIntExtra(KEY_GAMEINFO_TYPEID, 2);
		if (mTypeId == 1) {
			setHeadTitle(getIdToString(R.string.game_epic_recommend));
		} else if (mTypeId == 2) {
			setHeadTitle(getIdToString(R.string.game_best_game));
		} else if (mTypeId == 10) {
			setHeadTitle(getIdToString(R.string.game_game_detail));
			gameDetialUrl = intent.getStringExtra("gameInfoUrl");
			if (gameDetialUrl == null || gameDetialUrl.equals("")) {
				gameDetialUrl = "www.baidu.com";
			}
		} else {
			setHeadTitle(getIdToString(R.string.game_topic_game));
		}

		mCommentAdapter = new CommentAdapter(CommDetailActivity.this);
		initGameData();
		initAdapter();
		getGameDetailFromDatabase();
		loadGameRelativeData();
		goBack();
		// initDownloadBtn();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mUserInfo = BaseApplication.getLoginUser();
		/*
		 * InputMethodManager imm = (InputMethodManager)
		 * getSystemService(Context.INPUT_METHOD_SERVICE);
		 * imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
		 */

		if (mDownloadBtnListenner != null) {
			// 有时解压对话框没能及时关闭,在这里把它关闭
			mDownloadBtnListenner.closeUnzipDialogIfFinish();
		}
	}

	/*
	 * 初始化下载按钮监听事件
	 */
	private void initDownloadBtn() {
		mDownloadBtnListenner = new BtnDownListenner(this);
		mDownloadBtnListenner.listen(mBtnGameDown, mDetailGameInfo);
	}

	private void initGameData() {
		bindHeadRightButton(R.drawable.common_head_search);
		mViewPagerContentContainer = (ViewPager) findViewById(R.id.game_viewpager_contentContainer);
		
		mCommentpullDownView = new PullDownView(this);
		mCommentpullDownView.setOnPullDownListener(this);
		mCommentListView = mCommentpullDownView.getListView();
		mCommentListView.setSelector(R.drawable.comment_list_selector);
		mGameDetailContent = LayoutInflater.from(this).inflate(
				R.layout.game_layout_detail, null); // 游戏详情布局

		mCommContent = LayoutInflater.from(this).inflate(
				R.layout.layout_comm_list, null);
		mCommentLayout = (LinearLayout) mCommContent
				.findViewById(R.id.list_comment);
		mCommentLayout.addView(mCommentpullDownView);
		/**
		 * 添加评论列表和游戏详情
		 */
		mContainerView.add(mGameDetailContent);
		mContainerView.add(mCommContent);
		mPagerAdapter = new GameZoneViewPagerAdapter(mContainerView);
		mViewPagerContentContainer.setAdapter(mPagerAdapter);
		mViewPagerContentContainer.setOnPageChangeListener(this);
		initView();
		initGameDetailView();
		initCommentView();
		// initListeren();
		// showData();
	}

	/**
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 通过gameid从数据库获取对应的游戏数据
	 */
	private void getGameDetailFromDatabase() {
		// TODO Auto-generated method stub
		Group<GameInfo> games = PersistentSynUtils.getModelList(GameInfo.class,
				" gameId = '" + mGameInfo.getGameId() + "'");
		if (games != null && games.size() > 0) {
			mDetailGameInfo = games.get(0);
			mGameImgList = PersistentSynUtils.getModelList(
					ScreenShootInfo.class,
					" gameId = '" + mGameInfo.getGameId() + "'");
			mDetailGameInfo.setImgs(mGameImgList);
			showData();
			initDownloadBtn();
			initListeren();
			collectGame();
			loadingView.setVisibility(View.GONE);
			detialView.setVisibility(View.VISIBLE);
			hasInitDetail = true;
		}
	}

	/**
	 * 初始化要显示的数据
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-5-23 下午5:24:27
	 */
	private void showData() {
		// TODO Auto-generated method stub
		mImageFetcher.setImageSize(110, 110);
		mImageFetcher.loadImage(mDetailGameInfo.getMinPhoto(), mIvIconMin, 10);
		tvGameName.setText(mDetailGameInfo.getGameName());
		tvGameDownCounts.setText(mDetailGameInfo.getGameDownCount()
				+ getIdToString(R.string.game_down_num));
		// mtvGameSize.setText(String.valueOf(StringTool.StringToFloat(mGameInfo
		// .getGameSize())) + getIdToString(R.string.game_MB));
		mtvGameSize.setText(mDetailGameInfo.getGameSize() / (1024 * 1024)
				+ getIdToString(R.string.game_MB));
		tvStarLvel.setText((int) mDetailGameInfo.getStartLevel()
				+ getIdToString(R.string.game_share));
		ratingBarGameMarke.setMax(5);
		Log.i("hotgamehttp", mDetailGameInfo.getStartLevel() + "");
		ratingBarGameMarke.setProgress((int) mDetailGameInfo.getStartLevel());
		gameDescriptionName.setText(mDetailGameInfo.getGameName());

		if (mDetailGameInfo.getRemark() != null) {
			tvDescription.setText("		" + mDetailGameInfo.getRemark());
		} else {
			tvDescription.setText("");
		}
		if(mDetailGameInfo.getControllability()<=3){
			ratingBarHandleControl.setText("不建议");
		}
		else if(mDetailGameInfo.getControllability()==4){
			ratingBarHandleControl.setText("一般");
		}
		else if(mDetailGameInfo.getControllability()==5){
			ratingBarHandleControl.setText("适合");
		}
		/*ratingBarHandleControl.setMax(5);
		ratingBarHandleControl.setProgress(mDetailGameInfo.getControllability());*/
		mGralleryAdapter = new GralleryAdapter();
		mGralleryAdapter.setGroup(null);
		mGallery.setAdapter(mGralleryAdapter);

		Group<ScreenShootInfo> scrGroup = mDetailGameInfo.getImgs();
		if (scrGroup != null) {
			mGameImgList = mDetailGameInfo.getImgs();
			String[] str = new String[mGameImgList.size()];
			for (int i = 0; i < mGameImgList.size(); i++) {
				str[i] = mGameImgList.get(i).getPhotoUrl();
			}
			mGralleryAdapter.dataChange(str);
		}
		Group<CollectionInfo> collectionInfos = null;
		try {
			collectionInfos = PersistentSynUtils.getModelList(
					CollectionInfo.class,
					"gameid='" + mDetailGameInfo.getGameId() + "'");
		} catch (Exception e) {
			mFlag = false;
			mGameCollection
					.setText(getIdToString(R.string.game_manage_savegame));
		}
		if (collectionInfos != null && collectionInfos.size() > 0) {
			mGameCollection
					.setText(getIdToString(R.string.game_already_savegame));
			mFlag = true;
		}
	}

	/**
	 * 初始化评论View
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-5-23 下午5:17:48
	 */
	private void initCommentView() {
		// TODO Auto-generated method stub
		mEditText = (EditText) mCommContent.findViewById(R.id.etSend);
		mEditText.setLongClickable(false);
		mEditText.setFocusableInTouchMode(false);
		// mSendCommet = (Button) mCommContent.findViewById(R.id.imgSend);
	}

	/**
	 * 初始化事件监听
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-5-23 下午5:16:14
	 */
	private void initListeren() {
		// TODO Auto-generated method stub
		mRadioBtnGameDetail.setOnCheckedChangeListener(this);
		mRadionButtonGameComm.setOnCheckedChangeListener(this);
		mGameCollection.setOnClickListener(collectionListener);
		// 史诗推荐中间模块用来显示 游戏详情和评论切换的那块布局 将另外的内容评论列表视图添加进来
		// mSendCommet.setOnClickListener(mSendCommentLintener);
		mEditText.setOnClickListener(mSendCommentLintener);
	}

	private void initView() {
		loadingView = findViewById(R.id.detial_loading_body);
		detialView = findViewById(R.id.detail_all);
		lin_loading = findViewById(R.id.commom_layout_prompt);
		will_view = findViewById(R.id.common_layout_wiling);
		tv_refresh = (TextView) findViewById(R.id.common_tv_clickbreak);
		tv_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub.
				lin_loading.setVisibility(View.GONE);
				loadingView.setVisibility(View.VISIBLE);
				detialView.setVisibility(View.GONE);
				will_view.setVisibility(View.VISIBLE);
				loadGameRelativeData();
			}
		});
		mRadioGroup = (RadioGroup) findViewById(R.id.game_radioGroup_detailComm);
		mRadionButtonGameComm = (RadioButton) findViewById(R.id.game_radioButton_gameComm);
		mRadioBtnGameDetail = (RadioButton) findViewById(R.id.game_radioButton_gameDetail);
		mGameCollection = (Button) findViewById(R.id.game_btn_detailCollection);
		mBtnGameDown = (Button) findViewById(R.id.game_btn_detailGameDown);
		mHotGridView = (GridView) findViewById(R.id.game_gridview_recomLick);

		mIvIconMin = (ImageView) findViewById(R.id.game_iv_iconMin);
		tvGameName = (TextView) findViewById(R.id.game_tv_detailGameName);
		tvGameDownCounts = (TextView) findViewById(R.id.game_tv_detailDownCounts);
		mtvGameSize = (TextView) findViewById(R.id.game_tv_detailGameSize);
		tvStarLvel = (TextView) findViewById(R.id.game_tv_detailStarLevel);
		ratingBarGameMarke = (RatingBar) findViewById(R.id.game_ratingBar_detailGameMarke);
		loadingView.setVisibility(View.VISIBLE);
		detialView.setVisibility(View.GONE);
	}

	/**
	 * 初始化游戏详情界面
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-5-23 下午4:52:36
	 */
	private void initGameDetailView() {
		tvDescription = (TextView) mGameDetailContent
				.findViewById(R.id.game_tv_gameDescription);
		gameDescriptionName = (TextView) mGameDetailContent
				.findViewById(R.id.game_tv_detail_gameDetailName);
		ratingBarHandleControl = (TextView)mGameDetailContent
				.findViewById(R.id.game_tv_detail_gameStartLevel);
		mGallery = (CustomGallery) mGameDetailContent
				.findViewById(R.id.game_gallery_gameDetailImg);
		mGallery.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, mScreenH / 4));
		mGallery.setSpacing(30);
		mGallery.setPadding(34, 0, 34, 0);
		mGallery.setGravity(Gravity.CENTER);
	}

	private void initAdapter() {
		mHotGameadapter = new HotGameGridAdapter(CommDetailActivity.this);

		mHotGridView.setAdapter(mHotGameadapter);

		mHotGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				finish();
				GameInfo gameInfo = (GameInfo) arg0.getItemAtPosition(arg2);
				Intent intent = new Intent(CommDetailActivity.this,
						CommDetailActivity.class);
				intent.putExtra(Constant.KEY_GAMEINFO, gameInfo);
				startActivity(intent);
			}
		});

		// 评论适配器
		mCommentAdapter.setGroup(mGroup);
		mCommentListView.setAdapter(mCommentAdapter);
		setPullDownViewProperties(mCommentpullDownView);
		// lvComment.setAdapter(commentAdapter);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.game_radioButton_gameDetail:
				if (mCurrentType != 1) {
					mCurrentType = 1;
					changeRadioButtonColor(mRadioBtnGameDetail,
							mRadionButtonGameComm, mCurrentType);
					mViewPagerContentContainer.setCurrentItem(0);
				}
				break;
			case R.id.game_radioButton_gameComm:
				if (mCurrentType != 2) {
					mCurrentType = 2;
					changeRadioButtonColor(mRadionButtonGameComm,
							mRadioBtnGameDetail, mCurrentType);
					mViewPagerContentContainer.setCurrentItem(1);
				}
				break;
			}
		}
	}

	private void changeRadioButtonColor(RadioButton rb1, RadioButton rb2,
			int type) {
		switch (type) {
		case 1:
			rb1.setTextColor(getResources().getColor(R.color.color4));
			rb2.setTextColor(getResources().getColor(R.color.color5));
			rb1.setChecked(true);
			rb2.setChecked(false);
			break;
		case 2:
			rb2.setTextColor(getResources().getColor(R.color.color4));
			rb1.setTextColor(getResources().getColor(R.color.color5));
			rb2.setChecked(true);
			rb1.setChecked(false);
			break;
		}

	}

	/**
	 * @return
	 * @Title: getHotGame
	 * @Description: 获取热门游戏列表
	 * @param @return
	 * @return ArrayList<GameInfo>
	 * @throws
	 */
	public void loadGameRelativeData() {
		taskManager.cancelTask(Constant.TASKKEY_GAME_DETAIL);
		taskManager.startTask(gameDetailTaskListener,
				Constant.TASKKEY_GAME_DETAIL);

		taskManager.cancelTask(Constant.TASKKEY_HOTGAME_LIST);
		taskManager.startTask(gameHotListListener,
				Constant.TASKKEY_HOTGAME_LIST);
		loadCommentRemoteData();
	}

	/**
	 * 获取评论列表
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-3-1 下午8:00:52
	 */
	private void loadCommentRemoteData() {
		taskManager.cancelTask(Constant.TASKKEY_COMMEN_LIST);
		taskManager.startTask(commentListener, Constant.TASKKEY_COMMEN_LIST);
	}

	private void replyComment() {
		taskManager.cancelTask(Constant.TASKKEY_REPLY_COMMENT);
		taskManager.startTask(sendCommentListener,
				Constant.TASKKEY_REPLY_COMMENT);
	}

	/**
	 * 用于获取热门游戏
	 */
	AsynTaskListener<Group<GameInfo>> gameHotListListener = new AsynTaskListener<Group<GameInfo>>() {
		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			return true;
		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {

			HttpReqParams params = new HttpReqParams();
			// params.setTypeId(Integer.parseInt(mGameInfo.getTypeId()));
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			params.setGameId(mGameInfo.getGameId());
			// params.setGameId("20140815141914446190");
			// params.setPageSize(mPageSize);
			// if (!isHotLoadFoot) {
			// params.setCurrentPage(1);
			// } else {
			// params.setCurrentPage(mHotCurrentpage + 1);
			// }
			// return HttpApi.getList(UrlConstant.HTTP_HOT_GAME_LIST,
			// GameInfo.class, params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_HOT_GAME_LIST1,
					UrlConstant.HTTP_HOT_GAME_LIST2,
					UrlConstant.HTTP_HOT_GAME_LIST3, GameInfo.class,
					params.toJsonParam());
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<Group<GameInfo>> result) {
			if (result.getCode() == TaskResult.OK && result.getData() != null
					&& result.getData().size() > 0) {
				// for (GameInfo gameInfo : result.getData()) {
				// PersistentSynUtils.addModel(gameInfo);
				// }

				Group<GameInfo> gameInfos = result.getData();

				if (gameInfos.size() > 5) {
					for (int i = 5; i < result.getData().size(); i++) {
						gameInfos.remove(i);
					}
				}
				mHotGameadapter.setGroup(gameInfos);
				mHotGameadapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * 用于获取评论表，并且将从服务器端获取的数据添加到本地数据库中
	 */
	AsynTaskListener<Group<Comment>> commentListener = new AsynTaskListener<Group<Comment>>() {
		@Override
		public boolean preExecute(BaseTask<Group<Comment>> task, Integer taskKey) {
			return true;
		}

		@Override
		public TaskResult<Group<Comment>> doTaskInBackground(Integer taskKey) {
			HttpReqParams params = new HttpReqParams();
			params.setGameId(mGameInfo.getGameId());
			// params.setGameId("20140815141914446190");
			params.setPageSize(mPageSize);
			if (mIsLoadFoot) {
				params.setCurrentPage(mCurrentPage + 1);
			} else {
				params.setCurrentPage(1);
			}
			// return HttpApi.getList(UrlConstant.HTTP_GAME_GETCOMMENT,
			// Comment.class, params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_GAME_GETCOMMENT1,
					UrlConstant.HTTP_GAME_GETCOMMENT2,
					UrlConstant.HTTP_GAME_GETCOMMENT3, Comment.class,
					params.toJsonParam());

		}

		@Override
		public void onResult(Integer taskKey, TaskResult<Group<Comment>> result) {
			if (result.getCode() == 0) {
				if (!mIsLoadFoot) {
					mCurrentPage = 1;
				} else {
					mCurrentPage++;
				}
				if (result.getData() == null || result.getData().size() <= 0) {
					mCommentpullDownView.setEmptyView(getResources().getString(
							R.string.game_no_comment));
					mRadionButtonGameComm.setText(String.format(
							getIdToString(R.string.game_comm_count), 0));
					return;
				}
				mCommSum = result.getTotalCount();
				mRadionButtonGameComm.setText(String.format(
						getIdToString(R.string.game_comm_count), mCommSum));
				mCommentLayout.setGravity(Gravity.NO_GRAVITY);
				if (mCurrentPage == 1) {
					mCommentpullDownView.RefreshComplete();
					mGroup.clear();
				}
				// 将结果绑定到适配器，并调用更新列表
				mGroup.addAll(result.getData());
				mCommentpullDownView.notifyDidMore();
				mCommentAdapter.setGroup(mGroup);
				mCommentAdapter.notifyDataSetChanged();
				if (mCommSum <= mGroup.size()) {
					mCommentpullDownView.setHideFooter();
				} else {
					mCommentpullDownView.setShowFooter();
				}
			
			}else {
				if (result.getData() == null || result.getData().size() <= 0) {
					mCommentpullDownView.setEmptyView(getResources().getString(
							R.string.game_no_comment));
					mRadionButtonGameComm.setText(String.format(
							getIdToString(R.string.game_comm_count), 0));
					return;
				}
			}
			mCommentpullDownView.hideLoadingView();
		}

	};

	/**
	 * 更新评论至服务器
	 */
	AsynTaskListener<Comment> sendCommentListener = new AsynTaskListener<Comment>() {
		@Override
		public boolean preExecute(BaseTask<Comment> task, Integer taskKey) {
			return true;
		}

		public TaskResult<Comment> doTaskInBackground(Integer taskKey) {
			if (mUserInfo != null && mUserInfo.getUserId() != null) {
				HttpReqParams params = new HttpReqParams();
				params.setGameId(mGameInfo.getGameId());
				// params.setGameId("20140815141914446190");
				params.setUserId(mUserInfo.getUserId());
				params.setAvator(mUserInfo.getAvator());
				params.setNickName(mUserInfo.getNickName());
				params.setCommContent(mEditComment);
				// return HttpApi.getObject(UrlConstant.HTTP_GAME_UPCOMMENT,
				// Comment.class, params.toJsonParam());
				return HttpApi.getObject(UrlConstant.HTTP_GAME_UPCOMMENT1,
						UrlConstant.HTTP_GAME_UPCOMMENT2,
						UrlConstant.HTTP_GAME_UPCOMMENT3, Comment.class,
						params.toJsonParam());
			}
			return null;
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<Comment> result) {
			if (result != null && result.getCode() == TaskResult.OK) {
				mEditText.setText("");
				loadCommentRemoteData();
				showToastMsg(getIdToString(R.string.game_comm_reply_sucess));
				mIsLoadFoot = false;
				mCurrentPage = 0;
			} else {
				showToastMsg(getIdToString(R.string.game_comment_fail));
			}
		}
	};

	/**
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 若用户未登录这跳到登录页面，如果已经登录则直接打开评分对话框
	 */
	private void initUser() {
		if (mUserInfo == null) {
			Toast.makeText(CommDetailActivity.this,
					getIdToString(R.string.gift_login_first), 2 * 1000).show();
			startActivity(new Intent(CommDetailActivity.this,
					NewLoginAndRegisterActivity.class));
			return;
		} else {
			mUserInfo = BaseApplication.getLoginUser();
		}
		showScoreDialog();

	}

	/**
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 提交评论
	 */
	public void commenting() {
		// TODO Auto-generated method stub
		mEditComment = et_score.getText().toString();
		if ("".equals(mEditComment)) {
			Toast.makeText(CommDetailActivity.this,
					getIdToString(R.string.game_content_nobare),
					Toast.LENGTH_LONG).show();
			return;
		}
		replyComment();
		commentDialog.dismiss();
	}

	OnClickListener mSendCommentLintener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// CommentDialog.showDialog(CommDetailActivity.this);

			initUser();
			// toScoreThread.start();
		}
	};

	private void showDialog() {
		mDialog = new MyDialog(CommDetailActivity.this, R.style.MyDialog);
		mDialog.show();
	}

	private OnClickListener collectionListener = new OnClickListener() {
		int count = 0;

		@Override
		public void onClick(View v) {
			count++;
			mSharedPreferences = getSharedPreferences("collectionState",
					Context.MODE_APPEND);
			Editor editor = mSharedPreferences.edit();
			editor.putInt("collection", count);
			editor.commit();
			int clickCount = mSharedPreferences.getInt("collection", 0);

			// if (mGameInfo.getImgs()!=null&&mGameInfo.getImgs().size()>0) {
			// }else {
			// }

			if (mFlag == false && clickCount == 1) {
				// Group<ScreenShootInfo> imgs = mGameInfo.getImgs();
				// StringBuffer sb = new StringBuffer();
				// for (ScreenShootInfo img : imgs ) {
				// sb.append(img.getPhotoUrl());
				// sb.append(",");
				// }
				// sb.deleteCharAt(sb.length()-1);
				// String str_imgs = sb.toString();

				mCollectionInfo = new CollectionInfo(
						mDetailGameInfo.getGameId(),
						mDetailGameInfo.getGameName(),
						mDetailGameInfo.getPackageName(),
						mDetailGameInfo.getMaxPhoto(),
						mDetailGameInfo.getMinPhoto(),
						mDetailGameInfo.getMiddlePhoto(),
						mDetailGameInfo.getStartLevel() + "",
						mDetailGameInfo.getGameDownCount(),
						mDetailGameInfo.getGameSize() + "",
						mDetailGameInfo.getRemark(), "",
						mDetailGameInfo.getFile(), "", mTypeId,
						mDetailGameInfo.getVersionCode() / 1.0);

				long id = PersistentSynUtils.addModel(mCollectionInfo);
				if (id > 0)
					Toast.makeText(CommDetailActivity.this,
							getIdToString(R.string.game_savegame_sucess),
							Toast.LENGTH_LONG).show();
				mGameCollection
						.setText(getIdToString(R.string.game_already_savegame));

			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDownloadBtnListenner != null) {
			mDownloadBtnListenner.recycle();
		}
		taskManager.cancelTask(Constant.TASKKEY_GAME_DETAIL);
		taskManager.cancelTask(Constant.TASKKEY_HOTGAME_LIST);
		taskManager.cancelTask(Constant.TASKKEY_COMMEN_LIST);
		taskManager.cancelTask(Constant.TASKKEY_REPLY_COMMENT);

	}

	private void setPullDownViewProperties(PullDownView mPullDownView) {
		// 加载数据 本类使用
		// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
		mPullDownView.enableAutoFetchMore(true, 1);
		mPullDownView.setHideFooter();// 隐藏 并禁用尾部
		// mPullDownView.setShowFooter();// 显示并启用自动获取更多
		// mPullDownView.setHideHeader();// 隐藏并且禁用头部刷新
		mPullDownView.setShowHeader();// 显示并且可以使用头部刷新
	}

	public void onRefresh() {
		mIsLoadFoot = false;
		loadCommentRemoteData();
	}

	public void onMore() {
		mIsLoadFoot = true;
		loadCommentRemoteData();
	}

	private class GralleryAdapter extends BaseAdapter {
		private String[] list;

		public GralleryAdapter() {
			// TODO Auto-generated constructor stub
			super();
		}

		private void setGroup(String[] data) {

			if (data != null) {
				// list = new String[data.length];
				// for (int i = 0; i < list.length; i++) {
				//
				// list[i] = data[list.length - i - 1];
				//
				// }
				list = data;
			} else {
				list = new String[0];
			}
		}

		private void dataChange(String[] data) {
			setGroup(data);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = new ImageView(CommDetailActivity.this);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					Gallery.LayoutParams.MATCH_PARENT,
					Gallery.LayoutParams.MATCH_PARENT));
			imageView.setBackgroundResource(R.drawable.middlephoto);
			mImageFetcher.setImageSize(600, 340);
			mImageFetcher.loadImage(list[position], imageView, 8);
			imageView.setScaleType(ScaleType.FIT_XY);
			return imageView;
		}

	}

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
		changeRadioButtonColor(mRadioBtnGameDetail, mRadionButtonGameComm,
				mCurrentType);
	}

	/**
	 * 
	 * @author fcs
	 * @Description:解析图片字符串
	 * @date 2013-6-4 上午11:50:44
	 */
	// private String[] parseImageLists(String imageList) {
	// if (imageList == null) {
	// return null;
	// }
	// return imageList.split(",");
	// }
	/**
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 统计游戏点击量
	 */
	private void collectGame() {
		// TODO Auto-generated method stub
		CollectGameInfo collectGame = new CollectGameInfo();
		if (BaseApplication.m_loginUser != null
				&& BaseApplication.m_loginUser.getUserId() != null) {
			collectGame.setUserId(Integer
					.parseInt(BaseApplication.m_loginUser
							.getUserId()));
		} else {
			collectGame.setUserId(0);
		}
		collectGame.setGameId(mDetailGameInfo.getGameId());
		collectGame.setGameName(mDetailGameInfo.getGameName());
		collectGame
				.setPackageName(mDetailGameInfo.getPackageName());

		collectGame.setCopyRight(1);
		collectGame.setCpId(mDetailGameInfo.getCpId());

		collectGame.setDownCount(0);
		collectGame.setRecordTime(System.currentTimeMillis());
		if (mTypeId == 1) {
			collectGame.setAdClick(1);
			collectGame.setClickCount(0);
			collectGame
					.setGameType(getIdToString(R.string.game_epic_recommend));
			GameCollectHelper.addGameCollectInfo(collectGame, 2);
		} else if (mTypeId == 2) {
			collectGame.setAdClick(0);
			collectGame.setClickCount(1);
			collectGame
					.setGameType(getIdToString(R.string.game_best_game));
			GameCollectHelper.addGameCollectInfo(collectGame, 1);
		} else {
			collectGame.setAdClick(0);
			collectGame.setClickCount(1);
			collectGame
					.setGameType(getIdToString(R.string.game_game_special));
			GameCollectHelper.addGameCollectInfo(collectGame, 1);
		}
	}
	/**
	 * 游戏详情异步加载
	 */
	AsynTaskListener<Group<GameInfo>> gameDetailTaskListener = new AsynTaskListener<Group<GameInfo>>() {

		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			// TODO Auto-generated method stub
			return true;
		}

		public void onResult(Integer taskKey, TaskResult<Group<GameInfo>> result) {
			loadingView.setVisibility(View.GONE);
			detialView.setVisibility(View.VISIBLE);
			
			

			if (result != null && result.getCode() == TaskResult.OK) {
				if (result.getData() != null) {
					mDetailGameInfo = result.getData().get(0);
					mDetailGameInfo.setTypeId(mTypeId + "");
					showData();
					if (!hasInitDetail) {
						initDownloadBtn();
						initListeren();
						collectGame();
						hasInitDetail = true;
					}

					

				}

			}
			if (mDetailGameInfo == null && !hasInitDetail) {
				if (isNetWorkException(result.getException())) {
					lin_loading.setVisibility(View.VISIBLE);
					loadingView.setVisibility(View.VISIBLE);
					detialView.setVisibility(View.GONE);
					will_view.setVisibility(View.GONE);
				} else {
					if (mTypeId == 10) {
						showNoThisGameDialog();
					}
				}
			}
		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {
			// TODO Auto-generated method stub
			if (mGameInfo != null) {
				HttpReqParams params = new HttpReqParams();
				// params.setGameId(mGameInfo.getGameId());
				params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
				params.setGameId(mGameInfo.getGameId());
				byte[] requestStr = params.toJsonParam();

				// return HttpApi.getList(UrlConstant.HTTP_GAME_DETAIL,
				// GameInfo.class, params.toJsonParam());
				return HttpApi.getList(UrlConstant.HTTP_GAME_DETAIL1,
						UrlConstant.HTTP_GAME_DETAIL2,
						UrlConstant.HTTP_GAME_DETAIL3, GameInfo.class,
						requestStr);

			}
			return null;
		}

	};

	/**
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 如果扫描二维码进入游戏详情时服务器找不到该gameid的游戏这弹窗提示
	 */
	protected void showNoThisGameDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.qrcode_select_dialog, null);
		Button btn_detial = (Button) layout
				.findViewById(R.id.btn_qrcode_choose_detial);
		Button btn_website = (Button) layout
				.findViewById(R.id.btn_qrcode_choose_website);
		TextView tv_headLine = (TextView) layout
				.findViewById(R.id.dia_headline);
		TextView tv_content = (TextView) layout.findViewById(R.id.dia_content);
		tv_headLine.setText(getIdToString(R.string.game_not_found_dia_title));
		tv_content.setText(getIdToString(R.string.game_not_found_dia_content));
		btn_detial
				.setText(getIdToString(R.string.game_not_found_dia_btn_cancle));
		btn_website
				.setText(getIdToString(R.string.game_not_found_dia_btn_sure));
		btn_detial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog_gotoWeb.dismiss();
				CommDetailActivity.this.finish();
			}
		});
		btn_website.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog_gotoWeb.dismiss();

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(gameDetialUrl);
				intent.setData(content_url);
				startActivityForResult(intent, 2);
			}
		});
		dialog_gotoWeb = new AlertDialog.Builder(this).create();
		dialog_gotoWeb.setCanceledOnTouchOutside(false);
		dialog_gotoWeb.setCancelable(false);
		dialog_gotoWeb.show();
		// checkVersionDialog.setCancelable(false);
		dialog_gotoWeb.getWindow().setContentView(layout);
	}

	/**
	 * @param score
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 提交用户评分
	 */
	private void toScore(final int score) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpReqParams params = new HttpReqParams();
				params.setGameId(mGameInfo.getGameId());
				params.setScore(score);
				params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
				if (BaseApplication.m_loginUser != null
						&& BaseApplication.m_loginUser.getUserId() != null) {
					params.setUserId(BaseApplication.m_loginUser.getUserId());
				} else if (BaseApplication.m_loginUser == null
						|| BaseApplication.m_loginUser.getUserId() == null) {
					params.setUserId("0");
				}
				try {
					// Response response = HttpApi.getHttpPost1(
					// UrlConstant.HTTP_GAME_GIVE_SCORE,
					// params.toJsonParam());
					// Response response =
					HttpApi.getHttpPost1(UrlConstant.HTTP_GAME_GIVE_SCORE1,
							UrlConstant.HTTP_GAME_GIVE_SCORE2,
							UrlConstant.HTTP_GAME_GIVE_SCORE3,
							params.toJsonParam());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 显示评分弹窗
	 */
	private void showScoreDialog() {
		// TODO Auto-generated method stub
		commentDialog = new AlertDialog.Builder(this).create();
		LayoutInflater inf = LayoutInflater.from(this);
		View linear = inf.inflate(R.layout.commentdialog, null);
		et_score = (EditText) linear.findViewById(R.id.edit_score);

		rat_score = (RatingBar) linear.findViewById(R.id.ratingBar_score);
		rat_score.setMax(5);
		rat_score.setNumStars(5);
		rat_score.setProgress(5);
		final Button btn_score = (Button) linear.findViewById(R.id.btn_score);
		btn_score.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toScore(rat_score.getProgress());
				// initUser();
				commenting();
			}
		});

		commentDialog.setCanceledOnTouchOutside(false);
		commentDialog.show();
		// checkVersionDialog.setCancelable(false);
		commentDialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		commentDialog.getWindow().setContentView(linear);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 2) {
			this.finish();
		}
	}
}
