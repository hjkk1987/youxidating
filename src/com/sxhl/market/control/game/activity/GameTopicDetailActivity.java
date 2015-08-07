package com.sxhl.market.control.game.activity;

import java.util.Iterator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.game.adapter.GameTopicDetailAdapter1;
import com.sxhl.market.control.game.adapter.GameTopicDetailAdapter1.ButtonClickInter;
import com.sxhl.market.control.game.adapter.GameTopicSecondAdapter;
import com.sxhl.market.control.manage.activity.MyGameActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.CollectionInfo;
import com.sxhl.market.model.entity.DownloadGameInfo;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.GameType;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.ScreenShootInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.DateUtil;
import com.sxhl.market.view.InnerPullDownView.OnPullDownListener;
import com.sxhl.market.view.PullDownView;
import com.sxhl.market.view.costom.GameTopicLvHead;

/**
 * 
 * @ClassName: GameTopicDetailActivity
 * @Description: TODO
 * @author
 * @date 2012-12-14 下午4:13:10
 */

public class GameTopicDetailActivity extends BaseActivity implements OnPullDownListener{
	public static final String GAME_TYPE = "gameType";
	private PullDownView mTopicPullDownView;// 创建精品游戏的PulldownView
	private FrameLayout layout;
	private ListView mGameTopic;
	private GameTopicLvHead mHeadView=null;
	//private GameTopicDetailAdapter1 mTopicDetailAdapter;
	private GameTopicSecondAdapter mTopicDetailAdapter;
	private TextView mTvTopicGameName;
	private TextView mTvTopicResume;
	private TextView mTvReadCountTime;
	private TextView mTextHeadTitle;
	private ImageButton mSearchButton;
	private Group<GameInfo> mGameInfos = new Group<GameInfo>();
	private GameType mGameType;
	private int position;
	// private View mHeaderView;
	private CollectionInfo mCollectionInfo;
	private int mPageSize = 10;
	private int mCurrentPage = 1;
	private boolean isTopicLoadFoot = false;// 是否是更多刷新
	private int mTopicSum=0;
	Handler handler;
	
	private SharedPreferences preferences ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_activity_topicdetail);
		preferences = getSharedPreferences("topic.count", MODE_PRIVATE);
		init();
		mTextHeadTitle.setText(getIdToString(R.string.game_game_special));
		Intent intent = getIntent();
		position = intent.getExtras().getInt("position");
		mGameType = (GameType) intent.getExtras().getSerializable(GAME_TYPE);
		// Toast.makeText(this,"gameType="+gameType,Toast.LENGTH_SHORT).show();
		//initLoadingView();
		if (mGameType != null) {
			mTvTopicGameName.setText(mGameType.getName());
			mTvTopicResume.setText(mGameType.getRemark());
			// mTvReadCountTime.setText(getUpdataTimeandReadTimes());
			// mGameTopic.addHeaderView(mHeaderView);
			setAdapter();
			//loadRemoteData();
			goBack();
		}

	}

	// 加载界面，正在加载界面，提示界面
	//private View mEmptyloading, mWhilingView, mPropmtView;
	// 加载提示信息,重新刷新
	private TextView mEmptyText, mTvClickbreak;
	private ProgressBar mEmptyProgressBar;

	/**
	 * @author fcs
	 * @Description: 初始化加载界面
	 * @date 2013-6-11 下午5:47:39
	 */
	private void initLoadingView() {
		// TODO Auto-generated method stub
		/*mEmptyloading = findViewById(R.id.game_layout_detail_load);
		mWhilingView = findViewById(R.id.common_layout_wiling);
		mPropmtView = findViewById(R.id.commom_layout_prompt);
		mEmptyText = (TextView) findViewById(R.id.emptyText);
		mTvClickbreak = (TextView) findViewById(R.id.common_tv_clickbreak);
		mTvClickbreak.setOnClickListener(mTvClickBreak);
		mEmptyProgressBar = (ProgressBar) findViewById(R.id.emptyProgress);*/
	}

	private void setAdapter() {
		mSearchButton.setOnClickListener(mSearchLintener);
		mTopicDetailAdapter = new GameTopicSecondAdapter(GameTopicDetailActivity.this);
		getDataBaseData(mGameType.getTypeId(),position);
		/*//addHeadView(mGameInfos);
		mGameTopic.setAdapter(mTopicDetailAdapter);
		mGameTopic.setOnItemClickListener(topicItemClickListener);
		// Toast.makeText(this,"gameInfos"+gameInfos.size(),Toast.LENGTH_SHORT).show();
*/		
	}

	private void init() {
		bindHeadRightButton(R.drawable.common_head_search);
		mTextHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
		// mHeaderView = LayoutInflater.from(GameTopicDetailActivity.this)
		// .inflate(R.layout.game_topic_detail_list_header, null);
		mTvTopicGameName = (TextView) findViewById(R.id.game_tv_topicname);
		mTvTopicResume = (TextView) findViewById(R.id.game_tv_topicresume);
		mTvReadCountTime = (TextView) findViewById(R.id.game_tv_readcount_time);
		mTvReadCountTime.setVisibility(View.GONE);
		layout = (FrameLayout) this.findViewById(R.id.game_lv_gameTopic);
		mTopicPullDownView = new PullDownView(this);
		layout.removeAllViews();
		layout.addView(mTopicPullDownView);
		mSearchButton = (ImageButton) findViewById(R.id.btnHeadRight);
	}
	
	/**
	 * 精品游戏item点击事件
	 */
	private OnItemClickListener topicItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			int cuPosition = position - 2;
			if (cuPosition >= 0 && position < mGameInfos.size()) {
				Bundle mBundle = new Bundle();
				mBundle.putSerializable(CommDetailActivity.KEY_GAMEINFO,
						mGameInfos.get(cuPosition));

				mBundle.putInt(CommDetailActivity.KEY_GAMEINFO_TYPEID,3);
				jumpToActivity(GameTopicDetailActivity.this, CommDetailActivity.class,
						mBundle, false);
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
		mHeadView = new GameTopicLvHead(GameTopicDetailActivity.this, headGameInfos);
		View view = mHeadView.getView();
		mGameTopic.addHeaderView(view, null, false);
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
				mHeadView = new GameTopicLvHead(GameTopicDetailActivity.this,
						headGameInfos);
				View view = mHeadView.getView();
				mGameTopic.addHeaderView(view, null, false);
				mHeadView.exeAutoScroll();
			} else {
				mHeadView.setGroup(headGameInfos);
			}
		}

	}
	
	@SuppressWarnings("unused")
	private String getUpdataTimeandReadTimes() {
		// String readTimes = "200";
		// mGameType.getReadTimes();
		String createTime = DateUtil.formatStandardDate(mGameType
				.getCreateTime());
		// String updataTimeandReadTimes = String.format(
		// getIdToString(R.string.game_read_count), readTimes)
		// + "   "
		// + createTime;
		return createTime;
	}

	OnClickListener mSearchLintener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(GameTopicDetailActivity.this,
					SearchActivity.class);
			startActivity(intent);
		}
	};

	/**
	 * 根据id删除数据库图片缓存文件
	 */
	private void deleteSCRDataBaseDataById(String gameId) {
		PersistentSynUtils.execDeleteData(ScreenShootInfo.class,
				"where gameId = '" + gameId + "'");
	}

	/**
	 * 
	 * @author fcs
	 * @Description:获取游戏详情
	 * @date 2013-6-12 上午9:54:45
	 */
	private void loadRemoteData() {
		taskManager.cancelTask(Constant.TASKKEY_GAME_TOPIC_LIST);
		taskManager.startTask(mGameListListener,
				Constant.TASKKEY_GAME_TOPIC_LIST);
	}
	
	private void loadNewGameRecommad(){
		taskManager.cancelTask(Constant.TASKKEY_GAME_TOPIC_LIST);
		taskManager.startTask(mNewGameRecommandListListener,
				Constant.TASKKEY_GAME_TOPIC_LIST);
	}
	
	private void loadRankingGame(){
		taskManager.cancelTask(Constant.TASKKEY_GAME_TOPIC_LIST);
		taskManager.startTask(mRankingGameListListener,
				Constant.TASKKEY_GAME_TOPIC_LIST);
	}

	AsynTaskListener<Group<GameInfo>> mRankingGameListListener = new AsynTaskListener<Group<GameInfo>>() {
		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			if(mGameInfos.size()==0){
				mTopicPullDownView.setLoadingView();
			}
			return true;
		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {
			// 请求游戏专题列表数据
			HttpReqParams params = new HttpReqParams();
			if (BaseApplication.getLoginUser() == null
					|| BaseApplication.getLoginUser().getUserId() == null) {
				params.setUserId("0");
			} else if (BaseApplication.getLoginUser() != null
					&& BaseApplication.getLoginUser().getUserId() != null) {
				params.setUserId(BaseApplication.getLoginUser().getUserId());
			}
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			params.setType(0);
			params.setPageSize(mPageSize);
			if(!isTopicLoadFoot){
				params.setCurrentPage(1);
			}
			else{
				params.setCurrentPage(mCurrentPage+1);
			}
			// return HttpApi.getList(UrlConstant.HTTP_GAMELIST_GAME_LIST,
			// GameInfo.class, params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_GAMELIST_GAME_LIST_RANKING1,
					UrlConstant.HTTP_GAMELIST_GAME_LIST_RANKING2,
					UrlConstant.HTTP_GAMELIST_GAME_LIST_RANKING3, GameInfo.class,
					params.toJsonParam());
		}

		@Override
		public void onResult(Integer taskKey,
				final TaskResult<Group<GameInfo>> result) {

			if (result.getCode() == TaskResult.OK) {
				if(result.getData()==null){
					mTopicPullDownView.notifyDidMore();
					return;
				}
				else if (!isTopicLoadFoot && result.getData().size()<0) {
					mTopicPullDownView.notifyDidMore();
					return;
				}
				mCurrentPage = (isTopicLoadFoot) ? ++mCurrentPage : 1;
				if(mCurrentPage==1){
					deleteDataBaseData(mGameType.getTypeId());
					for (GameInfo gameInfo : result.getData()) {
						// DebugTool.debug("Hot Game:"+gameInfo.toString());
						gameInfo.setTypeId(mGameType.getTypeId());
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
					if (mGameInfos.isEmpty()) {
						mGameInfos.addAll(result.getData());
						Editor editor = preferences.edit();
						editor.putInt("rankingcount", mGameInfos.size());
						editor.commit();
						// 初始化精品游戏
						initTopicGame();
					} else {
						mGameInfos.clear();
						mGameInfos.addAll(result.getData());
						Editor editor = preferences.edit();
						editor.putInt("rankingcount", mGameInfos.size());
						editor.commit();
						// 刷新头部
						updateHeadView(mGameInfos);
					}
					mTopicPullDownView.RefreshComplete();
				}
				else{
					mGameInfos.addAll(result.getData());
					for (GameInfo gameInfo : result.getData()) {
						gameInfo.setTypeId(mGameType.getTypeId());
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
					Editor editor = preferences.edit();
					editor.putInt("rankingcount", mGameInfos.size()+5);
					editor.commit();
				}
				mTopicPullDownView.notifyDidMore();
				// 获得数据总数
				mTopicSum = result.getTotalCount();
				// 通知适配器数据改变
				/*mHotGameAdapter.setGroup(mGameHotList);
				mHotGameAdapter.notifyDataSetChanged();*/
				initTopicGame();
				if (!isTopicLoadFoot &&  mGameTopic!= null) {
					mGameTopic.setSelectionFromTop(1, 0);
				}
				//mWhilingView.setVisibility(View.GONE);
				/*mGameInfos = null;
				mGameInfos = result.getData();
				updateHeadView(mGameInfos);
				mTopicDetailAdapter.setGroup(mGameInfos);
				mTopicDetailAdapter.notifyDataSetChanged();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						deleteDataBaseData(mGameType.getTypeId());

						for (GameInfo gameInfo : result.getData()) {
							gameInfo.setTypeId(mGameType.getTypeId());
							PersistentSynUtils.addModel(gameInfo);

							Group<ScreenShootInfo> screenShootInfos = gameInfo
									.getImgs();
							deleteSCRDataBaseDataById(gameInfo.getGameId());
							for (ScreenShootInfo ssInfo : screenShootInfos) {
								ssInfo.setGameId(gameInfo.getGameId());
								PersistentSynUtils.addModel(ssInfo);
							}

						}

					}
				}).start();*/

			}else {
				if (isTopicLoadFoot) {
					if (result.getCode() == TaskResult.OK
							&& (result.getData() == null || result.getData()
									.isEmpty())) {
						Toast.makeText(GameTopicDetailActivity.this,
								getString(R.string.game_no_more), 2 * 1000)
								.show();
					}
					mTopicPullDownView.notifyDidMore();
				} else {
					mTopicPullDownView.RefreshComplete();
				}

			}

			if (mTopicSum <= mGameInfos.size() + 5) {
				// 隐藏精品更多
				setPullDownViewHideFooter(mTopicPullDownView);
			} else {
				// 显示更多
				setPullDownViewShowFooter(mTopicPullDownView);
			}


			mTopicPullDownView.hideLoadingView();
			if (mGameInfos.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					mTopicPullDownView.showPromptLayout();
				} else {
					mTopicPullDownView.showLoadingView();
					mTopicPullDownView.setEmptyView();
				}
			}
			/*if (mGameInfos.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					showPropmtView();
				}
			}*/
		}
	};
	
	AsynTaskListener<Group<GameInfo>> mNewGameRecommandListListener = new AsynTaskListener<Group<GameInfo>>() {
		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			if(mGameInfos.size()==0){
				mTopicPullDownView.setLoadingView();
			}
			return true;
		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {
			// 请求游戏专题列表数据
			HttpReqParams params = new HttpReqParams();
			if (BaseApplication.getLoginUser() == null
					|| BaseApplication.getLoginUser().getUserId() == null) {
				params.setUserId("0");
			} else if (BaseApplication.getLoginUser() != null
					&& BaseApplication.getLoginUser().getUserId() != null) {
				params.setUserId(BaseApplication.getLoginUser().getUserId());
			}
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			/*params.setTypeId(mGameType.getTypeId());
			params.setPageSize(mPageSize);
			if(!isTopicLoadFoot){
				params.setCurrentPage(1);
			}
			else{
				params.setCurrentPage(mCurrentPage+1);
			}*/
			// return HttpApi.getList(UrlConstant.HTTP_GAMELIST_GAME_LIST,
			// GameInfo.class, params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_GAMELIST_GAME_LIST_NEW1,
					UrlConstant.HTTP_GAMELIST_GAME_LIST_NEW2,
					UrlConstant.HTTP_GAMELIST_GAME_LIST_NEW3, GameInfo.class,
					params.toJsonParam());
		}

		@Override
		public void onResult(Integer taskKey,
				final TaskResult<Group<GameInfo>> result) {

			if (result.getCode() == TaskResult.OK) {
				if(result.getData()==null){
					mTopicPullDownView.notifyDidMore();
					return;
				}
				else if (!isTopicLoadFoot && result.getData().size()<0) {
					mTopicPullDownView.notifyDidMore();
					return;
				}
				mCurrentPage = (isTopicLoadFoot) ? ++mCurrentPage : 1;
				if(mCurrentPage==1){
					deleteDataBaseData(mGameType.getTypeId());
					for (GameInfo gameInfo : result.getData()) {
						// DebugTool.debug("Hot Game:"+gameInfo.toString());
						gameInfo.setTypeId(mGameType.getTypeId());
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
					if (mGameInfos.isEmpty()) {
						mGameInfos.addAll(result.getData());
						Editor editor = preferences.edit();
						editor.putInt("newgamecount", mGameInfos.size());
						editor.commit();
						// 初始化精品游戏
						initTopicGame();
					} else {
						mGameInfos.clear();
						mGameInfos.addAll(result.getData());
						Editor editor = preferences.edit();
						editor.putInt("newgamecount", mGameInfos.size());
						editor.commit();
						// 刷新头部
						updateHeadView(mGameInfos);
					}
					mTopicPullDownView.RefreshComplete();
				}
				else{
					mGameInfos.addAll(result.getData());
					for (GameInfo gameInfo : result.getData()) {
						gameInfo.setTypeId(mGameType.getTypeId());
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
					Editor editor = preferences.edit();
					editor.putInt("newgamecount", mGameInfos.size()+5);
					editor.commit();
				}
				mTopicPullDownView.notifyDidMore();
				// 获得数据总数
				mTopicSum = result.getTotalCount();
				// 通知适配器数据改变
				/*mHotGameAdapter.setGroup(mGameHotList);
				mHotGameAdapter.notifyDataSetChanged();*/
				initTopicGame();
				if (!isTopicLoadFoot &&  mGameTopic!= null) {
					mGameTopic.setSelectionFromTop(1, 0);
				}
				//mWhilingView.setVisibility(View.GONE);
				/*mGameInfos = null;
				mGameInfos = result.getData();
				updateHeadView(mGameInfos);
				mTopicDetailAdapter.setGroup(mGameInfos);
				mTopicDetailAdapter.notifyDataSetChanged();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						deleteDataBaseData(mGameType.getTypeId());

						for (GameInfo gameInfo : result.getData()) {
							gameInfo.setTypeId(mGameType.getTypeId());
							PersistentSynUtils.addModel(gameInfo);

							Group<ScreenShootInfo> screenShootInfos = gameInfo
									.getImgs();
							deleteSCRDataBaseDataById(gameInfo.getGameId());
							for (ScreenShootInfo ssInfo : screenShootInfos) {
								ssInfo.setGameId(gameInfo.getGameId());
								PersistentSynUtils.addModel(ssInfo);
							}

						}

					}
				}).start();*/

			}else {
				if (isTopicLoadFoot) {
					if (result.getCode() == TaskResult.OK
							&& (result.getData() == null || result.getData()
									.isEmpty())) {
						Toast.makeText(GameTopicDetailActivity.this,
								getString(R.string.game_no_more), 2 * 1000)
								.show();
					}
					mTopicPullDownView.notifyDidMore();
				} else {
					mTopicPullDownView.RefreshComplete();
				}

			}

			if (mTopicSum <= mGameInfos.size() + 5) {
				// 隐藏精品更多
				setPullDownViewHideFooter(mTopicPullDownView);
			} else {
				// 显示更多
				setPullDownViewShowFooter(mTopicPullDownView);
			}


			mTopicPullDownView.hideLoadingView();
			if (mGameInfos.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					mTopicPullDownView.showPromptLayout();
				} else {
					mTopicPullDownView.showLoadingView();
					mTopicPullDownView.setEmptyView();
				}
			}
			/*if (mGameInfos.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					showPropmtView();
				}
			}*/
		}
	};
	
	AsynTaskListener<Group<GameInfo>> mGameListListener = new AsynTaskListener<Group<GameInfo>>() {
		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			if(mGameInfos.size()==0){
				mTopicPullDownView.setLoadingView();
			}
			return true;
		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {
			// 请求游戏专题列表数据
			HttpReqParams params = new HttpReqParams();
			if (BaseApplication.getLoginUser() == null
					|| BaseApplication.getLoginUser().getUserId() == null) {
				params.setUserId("0");
			} else if (BaseApplication.getLoginUser() != null
					&& BaseApplication.getLoginUser().getUserId() != null) {
				params.setUserId(BaseApplication.getLoginUser().getUserId());
			}
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			params.setTypeId(mGameType.getTypeId());
			params.setPageSize(mPageSize);
			if(!isTopicLoadFoot){
				params.setCurrentPage(1);
			}
			else{
				params.setCurrentPage(mCurrentPage+1);
			}
			Log.i("life", params.toString());
			// return HttpApi.getList(UrlConstant.HTTP_GAMELIST_GAME_LIST,
			// GameInfo.class, params.toJsonParam());
			return HttpApi.getList(UrlConstant.HTTP_GAMELIST_GAME_LIST1,
					UrlConstant.HTTP_GAMELIST_GAME_LIST2,
					UrlConstant.HTTP_GAMELIST_GAME_LIST3, GameInfo.class,
					params.toJsonParam());
		}

		@Override
		public void onResult(Integer taskKey,
				final TaskResult<Group<GameInfo>> result) {

			if (result.getCode() == TaskResult.OK) {
				if(result.getData()==null){
					mTopicPullDownView.notifyDidMore();
					return;
				}
				else if (!isTopicLoadFoot && result.getData().size()<0) {
					mTopicPullDownView.notifyDidMore();
					return;
				}
				mCurrentPage = (isTopicLoadFoot) ? ++mCurrentPage : 1;
				if(mCurrentPage==1){
					deleteDataBaseData(mGameType.getTypeId());
					for (GameInfo gameInfo : result.getData()) {
						// DebugTool.debug("Hot Game:"+gameInfo.toString());
						gameInfo.setTypeId(mGameType.getTypeId());
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
					if (mGameInfos.isEmpty()) {
						mGameInfos.addAll(result.getData());
						// 初始化精品游戏
						initTopicGame();
					} else {
						mGameInfos.clear();
						mGameInfos.addAll(result.getData());
						// 刷新头部
						updateHeadView(mGameInfos);
					}
					mTopicPullDownView.RefreshComplete();
				}
				else{
					mGameInfos.addAll(result.getData());
					for (GameInfo gameInfo : result.getData()) {
						gameInfo.setTypeId(mGameType.getTypeId());
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
				mTopicPullDownView.notifyDidMore();
				// 获得数据总数
				mTopicSum = result.getTotalCount();
				// 通知适配器数据改变
				/*mHotGameAdapter.setGroup(mGameHotList);
				mHotGameAdapter.notifyDataSetChanged();*/
				initTopicGame();
				if (!isTopicLoadFoot &&  mGameTopic!= null) {
					mGameTopic.setSelectionFromTop(1, 0);
				}
				//mWhilingView.setVisibility(View.GONE);
				/*mGameInfos = null;
				mGameInfos = result.getData();
				updateHeadView(mGameInfos);
				mTopicDetailAdapter.setGroup(mGameInfos);
				mTopicDetailAdapter.notifyDataSetChanged();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						deleteDataBaseData(mGameType.getTypeId());

						for (GameInfo gameInfo : result.getData()) {
							gameInfo.setTypeId(mGameType.getTypeId());
							PersistentSynUtils.addModel(gameInfo);

							Group<ScreenShootInfo> screenShootInfos = gameInfo
									.getImgs();
							deleteSCRDataBaseDataById(gameInfo.getGameId());
							for (ScreenShootInfo ssInfo : screenShootInfos) {
								ssInfo.setGameId(gameInfo.getGameId());
								PersistentSynUtils.addModel(ssInfo);
							}

						}

					}
				}).start();*/

			}else {
				if (isTopicLoadFoot) {
					if (result.getCode() == TaskResult.OK
							&& (result.getData() == null || result.getData()
									.isEmpty())) {
						Toast.makeText(GameTopicDetailActivity.this,
								getString(R.string.game_no_more), 2 * 1000)
								.show();
					}
					mTopicPullDownView.notifyDidMore();
				} else {
					mTopicPullDownView.RefreshComplete();
				}

			}

			if (mTopicSum <= mGameInfos.size() + 5) {
				// 隐藏精品更多
				setPullDownViewHideFooter(mTopicPullDownView);
			} else {
				// 显示更多
				setPullDownViewShowFooter(mTopicPullDownView);
			}


			mTopicPullDownView.hideLoadingView();
			if (mGameInfos.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					mTopicPullDownView.showPromptLayout();
				} else {
					mTopicPullDownView.showLoadingView();
					mTopicPullDownView.setEmptyView();
				}
			}
			/*if (mGameInfos.size() <= 0) {
				if (isNetWorkException(result.getException())) {
					showPropmtView();
				}
			}*/
		}
	};

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
	 * @author fcs
	 * @Description:显示空信息提示
	 * @date 2013-6-12 上午9:37:58
	 */
	private void showEmptyView() {
		/*mEmptyloading.setVisibility(View.VISIBLE);
		mWhilingView.setVisibility(View.VISIBLE);
		mPropmtView.setVisibility(View.GONE);
		mEmptyProgressBar.setVisibility(View.GONE);
		mEmptyText.setText(getString(R.string.common_no_data));*/
	}

	/**
	 * 
	 * @author fcs
	 * @Description:显示网络提示信息
	 * @date 2013-6-12 上午9:42:32
	 */
	private void showPropmtView() {
		/*mEmptyloading.setVisibility(View.VISIBLE);
		mWhilingView.setVisibility(View.GONE);
		mPropmtView.setVisibility(View.VISIBLE);*/
	}

	/**
	 * 
	 * @author fcs
	 * @Description:显示正在加载
	 * @date 2013-6-12 上午9:56:29
	 */
	private void showLoadingView() {
		/*mEmptyloading.setVisibility(View.VISIBLE);
		mWhilingView.setVisibility(View.VISIBLE);
		mPropmtView.setVisibility(View.GONE);*/
	}

	/**
	 * 删除数据库缓存文件
	 */
	public void deleteDataBaseData(String typeId) {
		PersistentSynUtils.execDeleteData(GameInfo.class, "where typeId='"
				+ typeId + "'");
	}

	// 查询数据库缓存文件
	public void getDataBaseData(String typeId,int orderNum) {
		mGameInfos = PersistentSynUtils.getModelList(GameInfo.class,
				" typeId='" + typeId + "'");
		if(mGameInfos.size()>0){
			mCurrentPage = (mGameInfos.size() - 1) / 10 + 1;
			if (isTimeToRefreshHotGame()) {
				mCurrentPage = 0;
			}
			initTopicGame();
			mTopicPullDownView.hideLoadingView();
		}
		else if(position==0){
			loadNewGameRecommad();
		}
		else if(position==1){
			loadRankingGame();
		}
		else{
			loadRemoteData();
		}
		
		/*if (mGameInfos.size() > 0) {
			mTopicDetailAdapter.setGroup(mGameInfos);
			mTopicDetailAdapter.notifyDataSetChanged();
			mEmptyloading.setVisibility(View.GONE);
		} else {
			mEmptyloading.setVisibility(View.VISIBLE);
		}*/
	}

	private void initTopicGame(){
		mTopicPullDownView.setOnPullDownListener(this);
		mTopicPullDownView.setTvClickBreakOnClick(mClickBreakListener);
		if(mGameTopic!=null){
			return ;
		}
		if(mGameInfos==null && mGameInfos.size() < 1){
			return ;
		}
		
		mGameTopic = mTopicPullDownView.getListView();
		addHeadView(mGameInfos);
		mTopicDetailAdapter.setGroup(mGameInfos);
		mGameTopic.setAdapter(mTopicDetailAdapter);
		mGameTopic.setOnItemClickListener(topicItemClickListener);
		setPullDownViewProperties(mTopicPullDownView);
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
	
	private OnClickListener mClickBreakListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mTopicPullDownView.setLoadingView();
			loadRemoteData();
		}
		
	};
	
	private boolean isTimeToRefreshHotGame() {
		// TODO Auto-generated method stub

		SharedPreferences sp = getSharedPreferences("timeToRefreshSP",
				MODE_PRIVATE);
		Long lastRefreshTime = sp.getLong("lastRefreshTime", 0l);
		Long now = System.currentTimeMillis();
		sp.edit().putLong("lastRefreshTime", now).commit();

		return now - lastRefreshTime > 24 * 60 * 60 * 1000;

	}
	
	ButtonClickInter mButtonClickInter = new ButtonClickInter() {

		@Override
		public void downLoadBtn(GameInfo gameInfo) {
			// Toast.makeText(GameTopicDetailActivity.this, "点击了下载按钮",
			// Toast.LENGTH_SHORT).show();
			DownloadGameInfo downloadGame = new DownloadGameInfo();
			downloadGame.setGameId(gameInfo.getGameId());
			downloadGame.setGameName(gameInfo.getGameName());
			downloadGame.setPackageName(gameInfo.getPackageName());
			downloadGame.setCpId(gameInfo.getCpId());
			downloadGame.setTypeId("3");
			PersistentSynUtils.addModel(downloadGame);
			MyGameActivity.addToMyGameList(getApplicationContext(), gameInfo);
		}

		@Override
		public void collectionBtn(GameInfo gameInfo) {
			Group<CollectionInfo> collectionInfos = PersistentSynUtils
					.getModelList(CollectionInfo.class,
							"gameId='" + gameInfo.getGameId() + "'");
			if (collectionInfos != null && collectionInfos.size() > 0) {
				Toast.makeText(GameTopicDetailActivity.this,
						getIdToString(R.string.game_already_savegame),
						Toast.LENGTH_SHORT).show();
			} else {
				// Group<ScreenShootInfo> imgs = gameInfo.getImgs();
				// StringBuffer sb = new StringBuffer();
				// for (ScreenShootInfo img : imgs ) {
				// sb.append(img.getPhotoUrl());
				// sb.append(",");
				// }
				// sb.deleteCharAt(sb.length()-1);
				// String str_imgs = sb.toString();

				mCollectionInfo = new CollectionInfo(gameInfo.getGameId(),
						gameInfo.getGameName(), gameInfo.getPackageName(),
						gameInfo.getMaxPhoto(), gameInfo.getMinPhoto(),
						gameInfo.getMiddlePhoto(), gameInfo.getStartLevel()
								+ "", gameInfo.getGameDownCount(),
						gameInfo.getGameSize() + "", gameInfo.getRemark(), "",
						gameInfo.getFile(), "", 3, gameInfo.getVersionCode());
				long id = PersistentSynUtils.addModel(mCollectionInfo);
				if (id > 0)
					Toast.makeText(GameTopicDetailActivity.this,
							getIdToString(R.string.game_savegame_sucess),
							Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 点击刷新事件
	 */
	private OnClickListener mTvClickBreak = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			loadRemoteData();
			showLoadingView();
		}
	};

	@Override
	public void onRefresh() {
		isTopicLoadFoot = false;
		loadRemoteData();
	}

	@Override
	public void onMore() {
		isTopicLoadFoot=true;
		loadRemoteData();
	}
}
