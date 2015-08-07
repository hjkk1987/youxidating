package com.sxhl.market.control.game.activity;

import java.util.Calendar;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.SearchKeyword;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.StringTool;
import com.sxhl.market.view.costom.KeywordsFlow;

public class SearchActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "SearchActivity";
	private static final int HANDLER_GET_KEYWORD_LIST_OK = 1;
	private static final int HANDLER_GET_KEYWORD_LIST_FAIL = 2;
	private static final String PREF_NAME = "search";
	private static final String KEY_LASH_REFRESH_SEARCH = "last_search";

	/*
	 * public volatile String[] keywords = { "神庙大逃亡", "鳄鱼小顽皮", "捕鱼达人", "水果忍者",
	 * "祖玛", "会说话的汤姆猫", "超音速飞行", "水果鳄鱼", "疯狂喷气机", "愤怒的火柴人", "汤姆猫2", "正宗水果忍者",
	 * "武士2：复仇", "星球大战", "致命空袭", "切西瓜", "都市赛车5", "忘仙", "帝王三国", "时空猎人", "怪物联盟" };
	 */

	public volatile String[] mKeywords;
	private SharedPreferences mSp;
	private KeywordsFlow mKeywordsFlow;
	private Button mChangeBtn;
	private Button mSearchBtn;
	private EditText mSearchEditText;
	private GestureDetector mGestureDetector;
	private boolean mIsToast = false;
	private int mLastKeywordsIndex = -1;
	private volatile int mActState = Constant.ACTIVITY_STATE_INVISIBLE;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_activity_search);

		mKeywords = this.getResources().getStringArray(R.array.keywords);

		getCacheData();
		initViews();
		setHeadTitle(getIdToString(R.string.game_search));
		goBack();
		getSearchKeywordList();
	}

	private void initViews() {
		mSearchEditText = (EditText) findViewById(R.id.game_et_search);
		mSearchBtn = (Button) findViewById(R.id.game_btn_search);
		mSearchBtn.setOnClickListener(mSearchClickListenner);
		mChangeBtn = (Button) findViewById(R.id.game_btn_change);
		mChangeBtn.setOnClickListener(this);
		mKeywordsFlow = (KeywordsFlow) findViewById(R.id.frameLayout1);
		mKeywordsFlow.setDuration(800l);
		mKeywordsFlow.setOnItemClickListener(this);
		// 添加
		feedKeywordsFlow(mKeywordsFlow, mKeywords);
		mKeywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		mGestureDetector = new GestureDetector(new DefaultGestureDetector());
		mSp = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	private void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			mLastKeywordsIndex++;
			if (mLastKeywordsIndex > arr.length - 1) {
				mLastKeywordsIndex = 0;
			}
			String tmp = arr[mLastKeywordsIndex];
			keywordsFlow.feedKeyword(tmp);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mChangeBtn) {
			if (!mIsToast) {
				mIsToast = true;
				// Toast.makeText(SearchActivity.this, "左右滑动试试！",
				// Toast.LENGTH_SHORT).show();
			}
			keywordAnimation();
		} else if (v instanceof TextView) {
			String keyword = ((TextView) v).getText().toString();
			mSearchEditText.setText(keyword);
			if (!StringTool.isEmpty(keyword)) {
				mSearchEditText.setSelection(keyword.length());
				launchSearchResult(keyword);
			}
		}
	}

	private void keywordAnimation() {
		int type = new Random().nextInt();
		if (type % 2 == 0) {
			keywordAnimationIn();
		} else {
			keywordAnimationOut();
		}
	}

	private void keywordAnimationIn() {
		mKeywordsFlow.rubKeywords();
		// keywordsFlow.rubAllViews();
		feedKeywordsFlow(mKeywordsFlow, mKeywords);
		mKeywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
	}

	private void keywordAnimationOut() {
		mKeywordsFlow.rubKeywords();
		// keywordsFlow.rubAllViews();
		feedKeywordsFlow(mKeywordsFlow, mKeywords);
		mKeywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
	}

	private class DefaultGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			final int FLING_MIN_DISTANCE = 100;// X或者y轴上移动的距离(像素)
			final int FLING_MIN_VELOCITY = 200;// x或者y轴上的移动速度(像素/秒)
			if ((e1.getX() - e2.getX()) > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				// 向左滑动
				keywordAnimationOut();
			} else if ((e2.getX() - e1.getX()) > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				// 向右滑动
				keywordAnimationIn();
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// searchEditText.clearFocus();
			shutdownKeyboardIfOpened(mSearchEditText);
		}
		return mGestureDetector.onTouchEvent(event);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (mActState == Constant.ACTIVITY_STATE_DESTROY) {
				return;
			}
			if (msg.what == SearchActivity.HANDLER_GET_KEYWORD_LIST_OK) {
				keywordAnimation();
				// Toast.makeText(SearchActivity.this, "列表已更新！",
				// Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(SearchActivity.this, "列表更新失败！",
				// Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void getSearchKeywordList() {
		long lastRefreshRawTime = mSp.getLong(KEY_LASH_REFRESH_SEARCH, 0);
		if (!isOutOfDate(lastRefreshRawTime)) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpReqParams params = new HttpReqParams();
				params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
//				TaskResult<Group<SearchKeyword>> result = HttpApi.getList(
//						UrlConstant.HTTP_GAME_SEARCH_KEYWORDS,
//						SearchKeyword.class, params.toJsonParam());
				 TaskResult<Group<SearchKeyword>> result = HttpApi.getList(
				 UrlConstant.HTTP_GAME_SEARCH_KEYWORDS1,
				 UrlConstant.HTTP_GAME_SEARCH_KEYWORDS2,
				 UrlConstant.HTTP_GAME_SEARCH_KEYWORDS3,
				 SearchKeyword.class, params.toJsonParam());
				String[] words = null;
				int count = 0;
				Group<SearchKeyword> keywordGroup = result.getData();
				if (keywordGroup != null && keywordGroup.size() >= 10) {
					PersistentSynUtils.execDeleteData(SearchKeyword.class,
							" where id>0");
					int len = keywordGroup.size();
					words = new String[len];
					for (int i = 0; i < len; i++) {
						words[i] = keywordGroup.get(i).getKeyword();
						if (!StringTool.isEmpty(words[i])) {
							PersistentSynUtils.addModel(keywordGroup.get(i));
							count++;
						}
					}
				}
				if (mActState == Constant.ACTIVITY_STATE_DESTROY) {
					return;
				}
				if (count >= 10 && words != null && words.length >= 10) {
					SearchActivity.this.mKeywords = words;
					mHandler.sendEmptyMessage(SearchActivity.HANDLER_GET_KEYWORD_LIST_OK);
					mSp.edit()
							.putLong(KEY_LASH_REFRESH_SEARCH,
									System.currentTimeMillis()).commit();
				} else {
					DebugTool.error("error to get keywords list", null);
					// handler.sendEmptyMessage(SearchActivity.HANDLER_GET_KEYWORD_LIST_FAIL);
				}
			}
		}).start();

		return;
	}

	private boolean isOutOfDate(long time) {
		if (time <= 0) {
			return true;
		}
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int day = calendar.get(Calendar.DAY_OF_YEAR);

		calendar.setTimeInMillis(time);
		int lastYear = calendar.get(Calendar.YEAR);
		int lastDay = calendar.get(Calendar.DAY_OF_YEAR);

		// DebugTool.error(TAG, "cur year:"+year+" cur day:"+day, null);
		// DebugTool.error(TAG, "last year:"+lastYear+" last day:"+lastDay,
		// null);
		if (lastYear == year && lastDay == day) {
			return false;
		}
		return true;
	}

	private void getCacheData() {
		Group<SearchKeyword> keywords = PersistentSynUtils.getModelList(
				SearchKeyword.class, " id>0");
		if (keywords != null && keywords.size() > 0) {
			int len = keywords.size();
			String[] words = new String[len];
			for (int i = 0; i < len; i++) {
				words[i] = keywords.get(i).getKeyword();
			}
			if (words != null && words.length >= 5) {
				this.mKeywords = words;
			}
		}
	}

	private OnClickListener mSearchClickListenner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onSearchBtnClick();
		}
	};

	private void onSearchBtnClick() {
		String searchText = mSearchEditText.getText().toString();
		if (StringTool.isEmpty(searchText)) {
			Toast.makeText(this, getString(R.string.game_search_no_null),
					Toast.LENGTH_SHORT).show();
			return;
		}
		DebugTool.debug(TAG, "search words:" + searchText);
		launchSearchResult(searchText);
	}

	/**
	 * @Title: launchSearchResult
	 * @Description: 加载搜索结果页
	 * @param words
	 *            搜索的关键词
	 * @throws
	 */
	private void launchSearchResult(String words) {
		Intent intent = new Intent(this, SearchResultActivity.class);
		intent.putExtra("keywords", words);
		startActivity(intent);
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
	}

	private void shutdownKeyboardIfOpened(EditText editText) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			// 如果开启
			imm.hideSoftInputFromWindow(editText.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
			// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
			// InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}