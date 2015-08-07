package com.sxhl.market.view.costom;

import java.util.ArrayList;
import java.util.List;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.game.activity.CommDetailActivity;
import com.sxhl.market.control.game.adapter.CommentAdapter;
import com.sxhl.market.control.game.adapter.GameZoneViewPagerAdapter;
import com.sxhl.market.control.user.activity.NewLoginAndRegisterActivity;
import com.sxhl.market.model.entity.Comment;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskManager;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.view.PullDownView;
import com.sxhl.market.view.InnerPullDownView.OnPullDownListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class GameDetailAndComments extends LinearLayout implements OnCheckedChangeListener,OnPullDownListener,OnPageChangeListener{

	private Context context;
	private GameInfo mGameInfo;

	private int mScreenH;
	private RadioGroup mRGroup;
	private RadioButton rbDetail,rbComment;
	private ViewPager myViewPager;
	private List<View> mContainerView = new ArrayList<View>();// 游戏详情和评论列表
	private GameZoneViewPagerAdapter mPagerAdapter;
	// 游戏详情视图
	private View mGameDetailContent;
	// 游戏详情
	private TextView tvDescription, gameDescriptionName;
	private TextView ratingBarHandleControl;
	private CustomGallery mGallery;
	// 游戏评论内容视图
	private View mCommContent; 
	private PullDownView mCommentpullDownView;// 创建评论PullDownView
	private ListView mCommentListView;
	private LinearLayout mCommentLayout;
	private EditText mEditText;
	private int mCurrentType=1;
	private boolean mIsLoadFoot = false;
	// 评论列表的adapter
	private CommentAdapter mCommentAdapter;
	
	private Group<Comment> mGroup = new Group<Comment>();
	protected TaskManager taskManager ;
	private UserInfo mUserInfo;
	private AlertDialog commentDialog;
	private EditText et_score;
	private RatingBar rat_score;
	private String mEditComment;
	private int mCurrentPage = 0;
	private int mCommSum;
	private int mPageSize = 10;
	
	public GameDetailAndComments(Context context){
		this(context,null);
	}
	public GameDetailAndComments(Context context, AttributeSet attrs){
		this(context,attrs,0);
	}
	public GameDetailAndComments(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		
		mScreenH = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
		taskManager = new TaskManager(context);
		mUserInfo = BaseApplication.getLoginUser();
		
		LayoutInflater.from(context).inflate(R.layout.game_detail_comment, this, true);
		mRGroup = (RadioGroup) findViewById(R.id.game_radioGroup_detailComm);
		rbDetail = (RadioButton) findViewById(R.id.game_radioButton_gameDetail);
		rbComment = (RadioButton)findViewById(R.id.game_radioButton_gameComm);
		myViewPager = (ViewPager)findViewById(R.id.game_viewpager_contentContainer);
		
		//游戏详情
		mGameDetailContent = LayoutInflater.from(context).inflate(
				R.layout.game_layout_detail, null); // 游戏详情布局
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
		
		//评论列表
		mCommContent = LayoutInflater.from(context).inflate(
				R.layout.layout_comm_list, null);
		mCommentpullDownView = new PullDownView(context);
		mCommentpullDownView.setOnPullDownListener(this);
		mCommentListView = mCommentpullDownView.getListView();
		mCommentListView.setSelector(R.drawable.comment_list_selector);
		mCommentLayout = (LinearLayout) mCommContent
				.findViewById(R.id.list_comment);
		mCommentLayout.addView(mCommentpullDownView);
		
		mContainerView.add(mGameDetailContent);
		mContainerView.add(mCommContent);
		mPagerAdapter = new GameZoneViewPagerAdapter(mContainerView);
		myViewPager.setAdapter(mPagerAdapter);
		myViewPager.setOnPageChangeListener(this);
		
		mEditText = (EditText) mCommContent.findViewById(R.id.etSend);
		mEditText.setLongClickable(false);
		mEditText.setFocusableInTouchMode(false);
		
		mCommentAdapter = new CommentAdapter(context);
		mCommentAdapter.setGroup(mGroup);
		mCommentListView.setAdapter(mCommentAdapter);
		setPullDownViewProperties(mCommentpullDownView);
		
		rbDetail.setOnCheckedChangeListener(this);
		rbComment.setOnCheckedChangeListener(this);
		mEditText.setOnClickListener(mSendCommentLintener);
		
		
		loadCommentRemoteData();
	}
	
	OnClickListener mSendCommentLintener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// CommentDialog.showDialog(CommDetailActivity.this);
			if(mGameInfo!=null){
				initUser();
			}
			// toScoreThread.start();
		}
	};
	

	/**
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 若用户未登录这跳到登录页面，如果已经登录则直接打开评分对话框
	 */
	private void initUser() {
		if (mUserInfo == null) {
			Toast.makeText(context,
					context.getResources().getString(R.string.gift_login_first), 2 * 1000).show();
			context.startActivity(new Intent(context,
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
	 * @discription 显示评分弹窗
	 */
	private void showScoreDialog() {
		// TODO Auto-generated method stub
		commentDialog = new AlertDialog.Builder(context).create();
		LayoutInflater inf = LayoutInflater.from(context);
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
	 * @discription 提交评论
	 */
	public void commenting() {
		// TODO Auto-generated method stub
		mEditComment = et_score.getText().toString();
		if ("".equals(mEditComment)) {
			Toast.makeText(context,
					context.getResources().getString(R.string.game_content_nobare),
					Toast.LENGTH_LONG).show();
			return;
		}
		replyComment();
		commentDialog.dismiss();
	}
	
	private void replyComment() {
		taskManager.cancelTask(Constant.TASKKEY_REPLY_COMMENT);
		if(mGameInfo!=null){
			taskManager.startTask(sendCommentListener,
					Constant.TASKKEY_REPLY_COMMENT);
		}
	}
	
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
				showToastMsg(context.getResources().getString(R.string.game_comm_reply_sucess));
				mIsLoadFoot = false;
				mCurrentPage = 0;
			} else {
				showToastMsg(context.getResources().getString(R.string.game_comment_fail));
			}
		}
	};
	
	/**
	 * 获取评论列表
	 * 
	 * @author fcs
	 * @Description:
	 * @date 2013-3-1 下午8:00:52
	 */
	private void loadCommentRemoteData() {
		taskManager.cancelTask(Constant.TASKKEY_COMMEN_LIST);
		if(mGameInfo!=null){
			taskManager.startTask(commentListener, Constant.TASKKEY_COMMEN_LIST);
		}
	}
	
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
					rbComment.setText(String.format(
							context.getResources().getString(R.string.game_comm_count), 0));
					return;
				}
				mCommSum = result.getTotalCount();
				rbComment.setText(String.format(
						context.getResources().getString(R.string.game_comm_count), mCommSum));
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
					rbComment.setText(String.format(
							context.getResources().getString(R.string.game_comm_count), 0));
					return;
				}
			}
			mCommentpullDownView.hideLoadingView();
		}

	};
	
	/**
	 * @author wsd
	 * @Description:显示提示信息
	 * @param tipsInfo
	 *            提示信息
	 * @date 2012-12-10 下午4:55:23
	 */
	protected void showToastMsg(String tipsInfo) {
		Toast.makeText(context, tipsInfo, Toast.LENGTH_SHORT).show();
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
	
	private void changeRadioButtonColor(RadioButton rb1, RadioButton rb2,
			int type) {
		switch (type) {
		case 1:
			rb1.setTextColor(context.getResources().getColor(R.color.color4));
			rb2.setTextColor(context.getResources().getColor(R.color.color5));
			rb1.setChecked(true);
			rb2.setChecked(false);
			break;
		case 2:
			rb2.setTextColor(context.getResources().getColor(R.color.color4));
			rb1.setTextColor(context.getResources().getColor(R.color.color5));
			rb2.setChecked(true);
			rb1.setChecked(false);
			break;
		}

	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.game_radioButton_gameDetail:
				if (mCurrentType != 1) {
					mCurrentType = 1;
					changeRadioButtonColor(rbDetail,
							rbComment, mCurrentType);
					myViewPager.setCurrentItem(0);
				}
				break;
			case R.id.game_radioButton_gameComm:
				if (mCurrentType != 2) {
					mCurrentType = 2;
					changeRadioButtonColor(rbComment,
							rbDetail, mCurrentType);
					myViewPager.setCurrentItem(1);
				}
				break;
			}
		}
	}
	@Override
	public void onRefresh() {
		mIsLoadFoot=false;
		loadCommentRemoteData();
	}
	@Override
	public void onMore() {
		mIsLoadFoot=true;
		loadCommentRemoteData();
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	@Override
	public void onPageSelected(int arg0) {
		mRGroup.clearCheck();
		mCurrentType = arg0 + 1;
		changeRadioButtonColor(rbDetail, rbComment,mCurrentType);
	}
	public TextView getTvDescription() {
		return tvDescription;
	}
	public void setTvDescription(TextView tvDescription) {
		this.tvDescription = tvDescription;
	}
	public TextView getGameDescriptionName() {
		return gameDescriptionName;
	}
	public void setGameDescriptionName(TextView gameDescriptionName) {
		this.gameDescriptionName = gameDescriptionName;
	}
	public CustomGallery getmGallery() {
		return mGallery;
	}
	public void setmGallery(CustomGallery mGallery) {
		this.mGallery = mGallery;
	}
	public ListView getmCommentListView() {
		return mCommentListView;
	}
	public void setmCommentListView(ListView mCommentListView) {
		this.mCommentListView = mCommentListView;
	}
	public EditText getmEditText() {
		return mEditText;
	}
	public void setmEditText(EditText mEditText) {
		this.mEditText = mEditText;
	}
	public boolean ismIsLoadFoot() {
		return mIsLoadFoot;
	}
	public void setmIsLoadFoot(boolean mIsLoadFoot) {
		this.mIsLoadFoot = mIsLoadFoot;
	}
	public GameInfo getmGameInfo() {
		return mGameInfo;
	}
	public void setmGameInfo(GameInfo mGameInfo) {
		this.mGameInfo = mGameInfo;
	}
}
