package com.sxhl.market.view;

import org.w3c.dom.Text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.view.InnerPullDownView.OnPullDownListener;

/**
 * 下拉刷新控件 真正实现下拉刷新的是这个控件， ScrollOverListView只是提供触摸的事件等
 * 
 * @author 孔德升
 */
public class PullDownView extends FrameLayout {
	private ProgressBar mEmptyProgress;
	private TextView mEmptyText;
	private InnerPullDownView mInnerPullDownView;
	private View emptyLoading = null, mLoadingView;
	private TextView mTvClickBreak;
	// 正在加载提示，和网络连接失败提示
	private View mWilingView, mPromptView;

	public PullDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	public PullDownView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mInnerPullDownView = (InnerPullDownView) LayoutInflater.from(context)
				.inflate(R.layout.layout_gama_pulldown, null);
		emptyLoading = LayoutInflater.from(context).inflate(
				R.layout.empty_loading, null);
		mWilingView = emptyLoading.findViewById(R.id.common_layout_wiling);
		mEmptyText = (TextView) emptyLoading.findViewById(R.id.emptyText);
		mLoadingView = emptyLoading.findViewById(R.id.common_layout_loading);
		mEmptyProgress = (ProgressBar) emptyLoading
				.findViewById(R.id.emptyProgress);
		mPromptView = emptyLoading.findViewById(R.id.commom_layout_prompt);
		mTvClickBreak = (TextView) emptyLoading
				.findViewById(R.id.common_tv_clickbreak);
		this.addView(mInnerPullDownView);
		this.addView(emptyLoading);
	}

	public void setBackgroudColor(int id) {
		mInnerPullDownView.setBackgroundColor(id);
		emptyLoading.setBackgroundColor(id);
	}

	public void showLoadingView() {
		emptyLoading.setVisibility(ViewGroup.VISIBLE);
	}

	public void hideLoadingView() {
		emptyLoading.setVisibility(ViewGroup.GONE);
	}

	public void setEmptyView() {
		mWilingView.setVisibility(View.VISIBLE);
		mEmptyProgress.setVisibility(ViewGroup.GONE);
		mEmptyText.setText(getResources().getString(R.string.common_no_data));
	}

	public void setLoadingView() {
		mWilingView.setVisibility(View.VISIBLE);
		mPromptView.setVisibility(View.GONE);
		mEmptyProgress.setVisibility(ViewGroup.VISIBLE);
		mEmptyText.setText(getResources()
				.getString(R.string.common_whilingLoad));
	}

	public void setEmptyView(String content) {
		mWilingView.setVisibility(View.VISIBLE);
		mPromptView.setVisibility(View.GONE);
		mEmptyProgress.setVisibility(ViewGroup.GONE);
		mEmptyText.setText(content);
	}

	public void setEmptyView(String content, int colorId) {
		mWilingView.setVisibility(View.VISIBLE);
		mPromptView.setVisibility(View.GONE);
		mEmptyProgress.setVisibility(ViewGroup.VISIBLE);
		mEmptyText.setTextColor(colorId);
		mEmptyText.setText(content);

	}

	public void setLoadingView(String content) {
		mWilingView.setVisibility(View.VISIBLE);
		mPromptView.setVisibility(View.GONE);
		mEmptyProgress.setVisibility(ViewGroup.VISIBLE);
		mEmptyText.setText(content);
	}

	/**
	 * 通知已经获取完更多了，要放在Adapter.notifyDataSetChanged后面
	 * 当你执行完更多任务之后，调用这个notyfyDidMore() 才会隐藏加载圈等操作
	 */
	public void notifyDidMore() {
		mInnerPullDownView.notifyDidMore();
	}

	/**
	 * 刷新完毕 关闭头部滚动条
	 */
	public void RefreshComplete() {
		mInnerPullDownView.RefreshComplete();
	}

	/**
	 * 设置监听器
	 * 
	 * @param listener
	 */
	public void setOnPullDownListener(OnPullDownListener listener) {
		mInnerPullDownView.setOnPullDownListener(listener);
	}

	/**
	 * 获取内嵌的listview
	 * 
	 * @return ScrollOverListView
	 */
	public ListView getListView() {
		return mInnerPullDownView.getListView();
	}

	/**
	 * 是否开启自动获取更多 自动获取更多，将会隐藏footer，并在到达底部的时候自动刷新
	 * 
	 * @param index
	 *            倒数第几个触发
	 */
	public void enableAutoFetchMore(boolean enable, int index) {
		mInnerPullDownView.enableAutoFetchMore(enable, index);
	}

	/**
	 * 隐藏头部 禁用下拉更新
	 */
	public void setHideHeader() {
		mInnerPullDownView.setHideHeader();
	}

	/**
	 * 显示头部 使用下拉更新
	 */
	public void setShowHeader() {
		mInnerPullDownView.setShowHeader();
	}

	/**
	 * 隐藏底部 禁用上拉更多
	 */
	public void setHideFooter() {
		mInnerPullDownView.setHideFooter();
	}

	/**
	 * 显示底部 使用上拉更多
	 */
	public void setShowFooter() {
		mInnerPullDownView.setShowFooter();
	}

	/**
	 * 
	 * @author fcs
	 * @Description:设置背景颜色
	 * @date 2013-6-5 上午10:40:51
	 */
	public void setBackgroundColor(int color) {
		setBackgroudColor(color);
		mInnerPullDownView.setBackgroundColor(color);
		emptyLoading.setBackgroundColor(color);
	}

	/**
	 * 
	 * @author fcs
	 * @Description:显示网络失败提示信息
	 * @date 2013-6-7 下午5:03:19
	 */
	public void showPromptLayout() {
		emptyLoading.setVisibility(ViewGroup.VISIBLE);
		mWilingView.setVisibility(View.GONE);
		mPromptView.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 * @author fcs
	 * @Description:设置文字点击刷新点击事件
	 * @date 2013-6-7 下午5:08:01
	 */
	public void setTvClickBreakOnClick(OnClickListener listener) {
		mTvClickBreak.setOnClickListener(listener);
	}

	/**
	 * @author fcs
	 * @Description:设置加载view背景颜色
	 * @date 2013-6-20 上午10:22:48
	 */
	public void setTvClickBreakColor(int color) {
		mTvClickBreak.setTextColor(color);
	}
}
