package com.sxhl.market.control.game.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.game.adapter.SearchResultAdapter;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskManager;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.StringTool;
import com.sxhl.market.view.InnerPullDownView.OnPullDownListener;
import com.sxhl.market.view.PullDownView;

public class SearchResultActivity extends BaseActivity implements
		OnPullDownListener {
	private static final int PAGE_SIZE = 10;
	// 返回按钮
	private ImageButton mImgBtnBack;
	// 搜索按钮
	private ImageButton mImgBtnSearch;
	// 输入框中的删除按钮
	private ImageButton mImgBtnDeleteText;
	// private ImageButton searchOp;
	// 关键词输入框
	private EditText mEtSearch;
	// 结果提示文字
	private TextView mTvTip;
	private PullDownView mPullDownView;
	private ListView mlistview;

	private TaskManager mTaskManager;
	private Group<GameInfo> mGameInfos;
	private SearchResultAdapter mAdapter;
	private String mKeyword = "360";
	private int mCurrentPage = 1;
	private int mTotalCount = 0;
	private int mActState = Constant.ACTIVITY_STATE_INVISIBLE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_activity_search_result);
		initViews();
		initListview();
		mKeyword = getIntentKeywords();
		loadSearchData();
	}

	private String getIntentKeywords() {
		Intent intent = getIntent();
		String words = null;
		if (intent != null) {
			words = intent.getStringExtra("keywords");
		}
		if (!StringTool.isEmpty(words)) {
			mEtSearch.setText(words);
			mEtSearch.setSelection(words.length());
			return words;
		}
		return null;
	}

	private void initViews() {
		mTaskManager = new TaskManager(getApplicationContext());
		mGameInfos = new Group<GameInfo>();
		mEtSearch = (EditText) findViewById(R.id.game_et_resultsearch);
		mImgBtnBack = (ImageButton) findViewById(R.id.game_imgBtn_sRBack);
		mImgBtnSearch = (ImageButton) findViewById(R.id.game_imgBtn_search);
		mImgBtnDeleteText = (ImageButton) findViewById(R.id.game_imgBtn_DeleteText);
		// searchOp=(ImageButton)findViewById(R.id.searchOp);
		mTvTip = (TextView) findViewById(R.id.game_tv_Tip);
		mTvTip.setText(getIdToString(R.string.game_whilingsearch));

		mImgBtnBack.setOnClickListener(onButtonClickListenner);
		mImgBtnSearch.setOnClickListener(onButtonClickListenner);
		mImgBtnDeleteText.setOnClickListener(onButtonClickListenner);
	}

	private void initListview() {
		// pullDownView=(PullDownView)findViewById(R.id.pullDownView);
		mPullDownView = new PullDownView(this);
		mPullDownView.setOnPullDownListener(this);

		LinearLayout container = (LinearLayout) findViewById(R.id.game_liearLayout_container);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		container.addView(mPullDownView, params);

		mAdapter = new SearchResultAdapter(this);
		mAdapter.setGroup(mGameInfos);
		mlistview = mPullDownView.getListView();
		mlistview.setDivider(null);
		mlistview.setDividerHeight(0);
		// listview.setDivider(getResources().getDrawable(R.drawable.listview_divider));
		mlistview.setSelector(R.drawable.listview_selector);
		mlistview.setAdapter(mAdapter);
		mlistview.setOnItemClickListener(onItemClickListnner);
		mlistview.setOnTouchListener(onTouchListenner);
		setPullDownViewProperties(mPullDownView);
	}

	private void setPullDownViewProperties(PullDownView mPullDownView) {
		mPullDownView.enableAutoFetchMore(true, 1);
		mPullDownView.setHideFooter();// 隐藏 并禁用尾部
		mPullDownView.setHideHeader();// 隐藏并且禁用头部刷新
	}

	private void loadSearchData() {
		mTaskManager.cancelAllTasks();
		mPullDownView.setHideFooter();
		mPullDownView.setLoadingView("");
		mPullDownView.showLoadingView();
		mGameInfos.clear();
		mAdapter.notifyDataSetChanged();
		mTvTip.setText(getIdToString(R.string.game_whilingsearch));
		mTotalCount = 0;
		mCurrentPage = 1;
		mTaskManager.startTask(asynTaskListenner, 0);
	}

	private OnClickListener onButtonClickListenner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if (id == R.id.game_imgBtn_sRBack) {
				finish();
			} else if (id == R.id.game_imgBtn_search) {
				String words = mEtSearch.getText().toString();
				if (!StringTool.isEmpty(words)) {
					shutdownKeyboardIfOpened();
					mKeyword = words;
					loadSearchData();
				} else {
					Toast.makeText(SearchResultActivity.this,
							getString(R.string.game_search_no_null),
							Toast.LENGTH_SHORT).show();
				}
			} else if (id == R.id.game_imgBtn_DeleteText) {
				mEtSearch.setText("");
			}

		}
	};

	private void shutdownKeyboardIfOpened() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			// 如果开启
			imm.hideSoftInputFromWindow(mEtSearch.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
			// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
			// InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private OnItemClickListener onItemClickListnner = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			try {
				if (mGameInfos.size() <= 0 || mGameInfos.size() < position) {
					return;
				}
				Bundle mBundle = new Bundle();
				mBundle.putSerializable(CommDetailActivity.KEY_GAMEINFO,
						mGameInfos.get(position - 1));
				jumpToActivity(
						SearchResultActivity.this.getApplicationContext(),
						CommDetailActivity.class, mBundle, false);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};

	AsynTaskListener<Group<GameInfo>> asynTaskListenner = new AsynTaskListener<Group<GameInfo>>() {
		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			return true;
		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {
			HttpReqParams params = new HttpReqParams();
			params.setSearchStr(mKeyword);
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			// return HttpApi.getList(UrlConstant.HTTP_GAME_SEARCH,
			// GameInfo.class, params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_GAME_SEARCH1,
					UrlConstant.HTTP_GAME_SEARCH2,
					UrlConstant.HTTP_GAME_SEARCH3, GameInfo.class,
					params.toJsonParam());

		}

		@Override
		public void onResult(Integer taskKey, TaskResult<Group<GameInfo>> result) {
			onRequestHandle(result);
		}
	};

	private void onRequestHandle(TaskResult<Group<GameInfo>> result) {
		if (mActState == Constant.ACTIVITY_STATE_DESTROY) {
			return;
		}
		mPullDownView.hideLoadingView();
		boolean ret = false;
		Group<GameInfo> gameInfos = null;
		if (result != null
				&& (result.getCode() == TaskResult.OK || result.getCode() == TaskResult.NULL_DATA)) {
			ret = true;
			gameInfos = result.getData();
			if (gameInfos != null) {
				SearchResultActivity.this.mGameInfos.addAll(gameInfos);
				mAdapter.notifyDataSetChanged();
			}
		}
		if (mCurrentPage == 1) {
			if (ret) {
				// mTotalCount = result.getTotalCount();
				mTotalCount = 0;
				if (result.getData() != null && result.getData().size() > 0) {
					mTotalCount = result.getData().size();
				}

				mTvTip.setText(String.format(
						getIdToString(R.string.game_search_result_count),
						mTotalCount));
			} else {
				mTvTip.setText(R.string.game_conn_fail);
			}
		} else {
			if (!ret && mActState == Constant.ACTIVITY_STATE_VISIBLE) {
				showToastMsg(getIdToString(R.string.game_conn_fail));
			}

		}

		mPullDownView.notifyDidMore();
		if (mTotalCount > 0) {
			if (SearchResultActivity.this.mGameInfos.size() < mTotalCount) {
				mCurrentPage++;
				mPullDownView.setShowFooter();
			} else {
				mPullDownView.setHideFooter();
			}
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMore() {
		// TODO Auto-generated method stub
		mTaskManager.cancelAllTasks();
		mTaskManager.startTask(asynTaskListenner, 0);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mActState = Constant.ACTIVITY_STATE_VISIBLE;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mActState = Constant.ACTIVITY_STATE_INVISIBLE;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mActState = Constant.ACTIVITY_STATE_DESTROY;
		mTaskManager.cancelAllTasks();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mEtSearch.clearFocus();
			shutdownKeyboardIfOpened();
		}
		return super.onTouchEvent(event);
	}

	private OnTouchListener onTouchListenner = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			mEtSearch.clearFocus();
			shutdownKeyboardIfOpened();
			return false;
		}
	};
}
