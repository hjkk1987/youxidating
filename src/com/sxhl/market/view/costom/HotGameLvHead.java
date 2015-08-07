package com.sxhl.market.view.costom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.game.activity.CommDetailActivity;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.StringTool;
import com.sxhl.market.utils.asynCache.ImageFetcher;

/**
 * @ClassName: HotGameLvHead
 * @Description: 精品游戏头部滚动图片区域
 * @author: Liuqin
 * @date 2012-12-13 下午3:31:22
 * 
 */

public class HotGameLvHead implements OnCheckedChangeListener{
	private static final String TAG = "HOT_GAME_HEAD";
	private static final int ACTUAL_ITEM_COUNT = 5;
	private static final int VIEW_PAGER_COUNT = 24 * 3600;
	private static final int FIRST_COUNT = 300;
	private static final int IMAGE_ROUND_RATIO = 8;
	private Context context = null;
	// android-support-v4中的滑动组件
	private ViewPager viewPager;
	// 滑动的图片集合
	private ImageView[] imageViews;
	//private TextView tvTitle;
	// 图片标题正文的那些点
	private List<View> dots;
	// 当前图片的索引号
	private View layout = null;
	private boolean isAutoScrolling = false;

	private Group<GameInfo> gameInfos;
	private MyPagerAdapter myPagerAdapter = null;
	private boolean isDragging = false;
	private int currentPossition = FIRST_COUNT;
	
	private RadioGroup mGroup;
	private RadioButton rbHandle,rbNormal;	
	private int mCurrentType = 1;
	private OnHotGameListener onHotGameListener;
	//
	// //图片获取器
	private ImageFetcher mImageFetcher;
	private ScheduledExecutorService scheduledExecutorService;

	public HotGameLvHead(Context context, Group<GameInfo> gameInfos) {
		this.context = context;
		mImageFetcher = ((BaseActivity) context).getmImageFetcher();

		if (gameInfos != null && gameInfos.size() >= ACTUAL_ITEM_COUNT) {
			this.gameInfos = gameInfos;
		} else {
			this.gameInfos = null;
		}
	}

	/**
	 * @Title: getView
	 * @Description: 获取一个headerView
	 * @return
	 * @throws
	 */
	public View getView() {
		layout = LayoutInflater.from(this.context).inflate(
				R.layout.lv_head_hot_game, null);
		imageViews = new ImageView[ACTUAL_ITEM_COUNT];
		initImageViews();

		// 初始化几个点
		dots = new ArrayList<View>();
		dots.add(layout.findViewById(R.id.v_dot0));
		dots.add(layout.findViewById(R.id.v_dot1));
		dots.add(layout.findViewById(R.id.v_dot2));
		dots.add(layout.findViewById(R.id.v_dot3));
		dots.add(layout.findViewById(R.id.v_dot4));

		/*tvTitle = (TextView) layout.findViewById(R.id.tv_title);
		if (gameInfos != null && !gameInfos.isEmpty()
				&& gameInfos.get(0).getRemark() != null) {
			// DebugTool.info(TAG,
			// "\u3000\u3000"+gameInfos.get(0).getBriedDesc().trim());
			tvTitle.setText("\u3000\u3000"
					+ gameInfos.get(0).getRemark().trim());
		} else {
			tvTitle.setText("");
		}*/

		viewPager = (ViewPager) layout.findViewById(R.id.vp);
		// 设置填充ViewPager页面的适配器
		myPagerAdapter = new MyPagerAdapter();
		viewPager.setAdapter(myPagerAdapter);
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

		if (imageViews != null && imageViews.length > 0) {
			viewPager.setCurrentItem(currentPossition);
		}
		
		mGroup = (RadioGroup)layout.findViewById(R.id.game_radioGroup_detailComm);
		rbHandle = (RadioButton)layout.findViewById(R.id.game_radioButton_gameDetail);
		rbHandle.setOnCheckedChangeListener(this);
		rbNormal = (RadioButton)layout.findViewById(R.id.game_radioButton_gameComm);
		rbNormal.setOnCheckedChangeListener(this);
		return layout;
	}

	public void setGroup(Group<GameInfo> gameInfos) {
		if (gameInfos == null || gameInfos.size() < ACTUAL_ITEM_COUNT) {
			return;
		}
		this.gameInfos = gameInfos;
		initImageViews();
		/*int cp = currentPossition % imageViews.length;
		if (this.gameInfos.get(cp).getRemark() != null) {
			// DebugTool.info(TAG,
			// "\u3000\u3000"+gameInfos.get(0).getBriedDesc().trim());
			tvTitle.setText("\u3000\u3000"
					+ this.gameInfos.get(cp).getRemark().trim());
		} else {
			tvTitle.setText("\u3000\u3000");
		}*/
	}

	private void initImageViews() {
		// 初始化图片列表
		for (int i = 0; i < ACTUAL_ITEM_COUNT; i++) {
			if (imageViews[i] == null) {
				ImageView imageView = new ImageView(context);
				imageView.setOnTouchListener(onTouchListener);
				// imageView.setBackgroundResource(R.drawable.maxphoto); 
				imageView.setScaleType(ScaleType.FIT_XY);
				imageViews[i] = imageView;
				final int index = i;
				imageViews[i].setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// Toast.makeText(context,
						// HotGameLvHead.this.gameInfos.get(index).getDescription(),
						// Toast.LENGTH_SHORT).show();
						GameInfo gameInfo = (GameInfo) gameInfos.get(index);
						Intent intent = new Intent(context,
								CommDetailActivity.class);
						Bundle mBundle = new Bundle();
						mBundle.putSerializable(
								CommDetailActivity.KEY_GAMEINFO, gameInfo);
						mBundle.putInt(CommDetailActivity.KEY_GAMEINFO_TYPEID,
								2);
						intent.putExtras(mBundle);
						context.startActivity(intent);
					}
				});
			}

			GameInfo gameInfo = gameInfos.get(i);
			mImageFetcher.setImageSize(700, 260);
			if (gameInfo != null && !StringTool.isEmpty(gameInfo.getMaxPhoto())) {
				// 加载图片2
				// mImageFetcher.loadImage(gameInfo.getMaxPhoto(),
				// imageViews[i]);
				mImageFetcher.loadImage(gameInfo.getMaxPhoto(), imageViews[i],
						IMAGE_ROUND_RATIO);
			} else {
				imageViews[i].setImageResource(R.drawable.maxphoto);
			}
		}
	}

	/**
	 * @Title: exeAutoScroll
	 * @Description: 执行图片自动滚动
	 * @throws
	 */
	public void exeAutoScroll() {
		if (!isAutoScrolling && gameInfos != null) {
			scheduledExecutorService = Executors
					.newSingleThreadScheduledExecutor();
			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 6,
					6, TimeUnit.SECONDS);
			isAutoScrolling = true;
			DebugTool.debug(TAG, "exeAutoScroll");
		}
	}

	/**
	 * @Title: stopAutoScroll
	 * @Description: 停止图片自动滚动
	 * @throws
	 */
	public void stopAutoScroll() {
		scheduledExecutorService.shutdown();
		isAutoScrolling = false;
	}

	public boolean isAutoScrolling() {
		return isAutoScrolling;
	}

	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// DebugTool.debug(TAG,"change Image:"+currentPossition+" relative:"+(currentPossition%imageViews.length));
			if (++currentPossition >= VIEW_PAGER_COUNT) {
				currentPossition = FIRST_COUNT;
			}
			viewPager.setCurrentItem(currentPossition);// 切换当前显示的图片
		};
	};

	private class ScrollTask implements Runnable {
		public void run() {
			if (!isDragging) {
				synchronized (viewPager) {
					// DebugTool.debug(TAG,"ScrollTask");
					handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
				}
			}
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			int currentItem = position % imageViews.length;
			/*if (gameInfos != null && gameInfos.size() > currentItem
					&& gameInfos.get(currentItem).getRemark() != null) {
				// DebugTool.info(TAG,
				// "\u3000\u3000"+gameInfos.get(0).getBriedDesc().trim());
				tvTitle.setText("\u3000\u3000"
						+ gameInfos.get(currentItem).getRemark().trim());
			} else {
				tvTitle.setText("\u3000\u3000");
			}*/

			dots.get(currentItem).setBackgroundResource(R.drawable.dot_focused);
			currentPossition = position;
			setNotFocusDots(currentItem);
		}

		public void onPageScrollStateChanged(int arg0) {
			DebugTool.debug("state changed:" + arg0);
			if (arg0 == ViewPager.SCROLL_STATE_DRAGGING) {
				// 用户正在滑动
				isDragging = true;
			} else if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
				isDragging = false;
			}
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// DebugTool.debug("page scrool:"+arg0+ " - "+arg1+" - "+arg2);
		}
	}

	private void setNotFocusDots(int except) {
		for (int i = 0; i < ACTUAL_ITEM_COUNT; i++) {
			if (i != except) {
				dots.get(i).setBackgroundResource(R.drawable.dot_normal);
			}
		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			// return Integer.MAX_VALUE;
			return VIEW_PAGER_COUNT;
		}

		@Override
		public void destroyItem(View collection, int position, Object arg2) {
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			// DebugTool.info(TAG,"instantiateItem - position:"+position);
			try {
				DebugTool.debug(TAG, "collection:" + collection.toString());
				((ViewPager) collection)
						.addView(imageViews[(position % imageViews.length)]);
			} catch (Exception e) {
			}
			return imageViews[(position % imageViews.length)];
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	/**
	 * 
	 */
	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				isDragging = true;
				break;
			case MotionEvent.ACTION_UP:
				isDragging = false;
				break;
			default:
				break;
			}
			return false;
		}
	};

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.game_radioButton_gameDetail:
				if (mCurrentType != 1) {
					mCurrentType = 1;
				}
				break;
			case R.id.game_radioButton_gameComm:
				if (mCurrentType != 2) {
					mCurrentType = 2;
					
				}
				break;
			}
			
			if(onHotGameListener!=null){
				onHotGameListener.onHotGame(mCurrentType);
			}
		}
	}
	
	public interface OnHotGameListener{
		public void onHotGame(int currentType);
	}

	public void setOnHotGameListener(OnHotGameListener onHotGameListener) {
		this.onHotGameListener = onHotGameListener;
	}

	public RadioButton getRbHandle() {
		return rbHandle;
	}

	public RadioButton getRbNormal() {
		return rbNormal;
	}
	
}
