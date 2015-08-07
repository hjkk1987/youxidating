package com.sxhl.market.control.user.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.control.user.activity.GiftListActivity;
import com.sxhl.market.model.entity.GiftInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGiftInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.DateUtil;
/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 礼包列表适配器
 */
@SuppressLint("NewApi")
public class GiftDetailAdapter extends BaseImgGroupAdapter<GiftInfo> {
	public static final int IMAGE_ROUND_RATIO = 8;
	Context context;
	LayoutInflater mInflater;
	Boolean[] foucoStates;
	AlertDialog receiveDialog;
	Handler handler;
	Boolean[] receiveStates;
	Group<MyGiftInfo> myGiftInfos;
	AlertDialog waitingDialog;

	public GiftDetailAdapter(Context context, Handler handler,
			Group<MyGiftInfo> myGiftInfos) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.handler = handler;
		this.myGiftInfos = myGiftInfos;
	}

	/**
	 * @param myGiftInfos
	 *  @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 设置已领取礼包数据
	 */
	public void setMyGiftInfos(Group<MyGiftInfo> myGiftInfos) {
		this.myGiftInfos = myGiftInfos;
	}

		
	/**
	 * @param foucoStates
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 设置当前选中的礼包状态
	 */
	public void setFoucoStates(Boolean[] foucoStates) {
		this.foucoStates = foucoStates;
	}

	/**
	 * @param receiveStates
	 *  @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 设置所有礼包的领取状态
	 */
	public void setReceiveStates(Boolean[] receiveStates) {
		this.receiveStates = receiveStates;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.gift_list_item, null);
			holder.tv_giftName = (TextView) convertView
					.findViewById(R.id.tv_gift_name);
			holder.tv_giftContent = (TextView) convertView
					.findViewById(R.id.tv_gift_content);
			holder.btn_receiveGift = (Button) convertView
					.findViewById(R.id.gift_item_recieve);
			holder.tv_duringTime = (TextView) convertView
					.findViewById(R.id.tv_during_time);
			holder.tv_giftUseMethod = (TextView) convertView
					.findViewById(R.id.tv_gift_use_method);
			holder.tv_giftCode = (TextView) convertView
					.findViewById(R.id.tv_gift_code);
			holder.lin_detail = (LinearLayout) convertView
					.findViewById(R.id.linear_detial);
			holder.lin_giftCode = (LinearLayout) convertView
					.findViewById(R.id.linear_gift_code);
			holder.lin_hintDetail = (LinearLayout) convertView
					.findViewById(R.id.lin_gift_hint);
			holder.tv_copyCode = (TextView) convertView
					.findViewById(R.id.tv_gift_copy_code);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		GiftInfo giftInfo = group.get(position);
		if (giftInfo != null) {
			holder.tv_giftName.setText(giftInfo.getName());
			holder.tv_giftContent.setText(giftInfo.getRemark());

			holder.tv_duringTime.setText(DateUtil.formatDate(giftInfo
					.getStartTime())
					+ "————"
					+ DateUtil.formatDate(giftInfo.getEndTime()));
			if (giftInfo.getUseMethod() != null) {

				holder.tv_giftUseMethod.setText(giftInfo.getUseMethod());
			}

			if (foucoStates == null || foucoStates.length <= position) {
				if (position == 0) {
					holder.lin_detail.setVisibility(View.VISIBLE);
					holder.lin_hintDetail.setVisibility(View.GONE);
				} else {
					holder.lin_detail.setVisibility(View.GONE);
					holder.lin_hintDetail.setVisibility(View.VISIBLE);
				}

			} else if (foucoStates[position]) {
				holder.lin_detail.setVisibility(View.VISIBLE);
				holder.lin_hintDetail.setVisibility(View.GONE);
			} else {
				holder.lin_detail.setVisibility(View.GONE);
				holder.lin_hintDetail.setVisibility(View.VISIBLE);
			}

			if (receiveStates[position]) {
				holder.btn_receiveGift
						.setText(R.string.gift_state_has_received);
				holder.btn_receiveGift
						.setBackgroundResource(R.drawable.gift_button_gray);
				holder.btn_receiveGift.setEnabled(false);
				holder.lin_giftCode.setVisibility(View.VISIBLE);
				holder.tv_giftCode.setText(getGiftCodeIfHasReceived(giftInfo));
				holder.tv_copyCode
						.setOnClickListener(new onItemCopyTextClickLinten());
				holder.tv_copyCode.setTag(getGiftCodeIfHasReceived(giftInfo));
			} else {
				holder.btn_receiveGift
						.setOnClickListener(new onItemButtonClickListen());
				holder.lin_giftCode.setVisibility(View.GONE);
			}
			holder.btn_receiveGift.setTag(position);
		}

		return convertView;
	}

	/**
	 * @param giftInfo
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription  获取已领取礼包的礼包码
	 */
	private String getGiftCodeIfHasReceived(GiftInfo giftInfo) {
		// TODO Auto-generated method stub
		for (MyGiftInfo myGiftInfo : myGiftInfos) {
			if (giftInfo.getGiftPackageid().equals(
					myGiftInfo.getGiftPackageid())) {
				return myGiftInfo.getGiftCode();
			}
		}
		return "";
	}

	class ViewHolder {
		TextView tv_giftName;
		TextView tv_giftContent;
		Button btn_receiveGift;
		TextView tv_duringTime;
		TextView tv_giftUseMethod;
		TextView tv_giftCode;
		TextView tv_copyCode;
		LinearLayout lin_detail;
		LinearLayout lin_giftCode;
		LinearLayout lin_hintDetail;
	}

	class onItemCopyTextClickLinten implements OnClickListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String giftCode = (String) v.getTag();

			if (android.os.Build.VERSION.SDK_INT <= 11) {
				android.text.ClipboardManager cm = (android.text.ClipboardManager) context
						.getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText(giftCode.trim());
			} else {
				android.content.ClipboardManager cm = (ClipboardManager) context
						.getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText(giftCode.trim());
			}
			Toast.makeText(context,
					context.getText(R.string.gift_copy_success), 1 * 2000)
					.show();

		}

	}

	class onItemButtonClickListen implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final int position = (Integer) v.getTag();
			final GiftInfo giftInfo = group.get(position);
			LayoutInflater inflater = LayoutInflater.from(context);
			View layout = inflater.inflate(R.layout.gift_rec_dialog, null);
			waitingDialog = new AlertDialog.Builder(context).create();
			waitingDialog.show();
			waitingDialog.getWindow().setContentView(layout);
			waitingDialog.setCancelable(false);

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					HttpReqParams params = new HttpReqParams();
					// params.set
					params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
					params.setUserId(BaseApplication.getLoginUser().getUserId());
					params.setGiftPackageid(giftInfo.getGiftPackageid());

					TaskResult<MyGiftInfo> result = HttpApi.getObject(
							UrlConstant.HTTP_RECEIVE_GIFT1,
							UrlConstant.HTTP_RECEIVE_GIFT2,
							UrlConstant.HTTP_RECEIVE_GIFT3, MyGiftInfo.class,
							params.toJsonParam());
					if (result == null) {
						waitingDialog.dismiss();
						Message msg_fail = new Message();
						msg_fail.what = position;
						msg_fail.obj = GiftListActivity.HANDLER_RECEIVE_FAIL;
						handler.sendMessage(msg_fail);
					} else {
						waitingDialog.dismiss();
						if (result.getCode() == 0) {
							MyGiftInfo myGiftInfo = result.getData();
							Message msg_success = new Message();
							msg_success.what = position;
							msg_success.obj = myGiftInfo.getGiftCode();
							handler.sendMessage(msg_success);
						} else if (result.getCode() == 1801) {
							Message msg_fail = new Message();
							msg_fail.what = position;
							msg_fail.obj = GiftListActivity.HANDLER_NO_LEFT;
							handler.sendMessage(msg_fail);
						} else if (result.getCode() == 1802) {
							Message msg_fail = new Message();
							msg_fail.what = position;
							msg_fail.obj = GiftListActivity.HANDLER_HAD_RECEIVED;
							handler.sendMessage(msg_fail);
						} else if (result.getCode() == 1803) {
							Message msg_fail = new Message();
							msg_fail.what = position;
							msg_fail.obj = GiftListActivity.HANDLER_HAS_OVERDUE;
							handler.sendMessage(msg_fail);
						} else if (result.getCode() == 1804) {
							Message msg_fail = new Message();
							msg_fail.what = position;
							msg_fail.obj = GiftListActivity.HANDLER_NOT_AT_TIME;
							handler.sendMessage(msg_fail);
						} else {
							Message msg_fail = new Message();
							msg_fail.what = position;
							msg_fail.obj = GiftListActivity.HANDLER_RECEIVE_FAIL;
							handler.sendMessage(msg_fail);
						}
					}

				}
			}).start();

			// LayoutInflater inflater = LayoutInflater.from(context);
			// View layout = inflater.inflate(R.layout.dia_receiver_gift, null);
			// receiveDialog = new AlertDialog.Builder(context).create();
			// receiveDialog.show();
			// receiveDialog.getWindow().setContentView(layout);

		}
	}

}
