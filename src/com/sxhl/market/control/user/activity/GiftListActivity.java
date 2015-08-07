package com.sxhl.market.control.user.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.game.activity.CommDetailActivity;
import com.sxhl.market.control.user.adapter.GiftDetailAdapter;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.GiftGameInfo;
import com.sxhl.market.model.entity.GiftInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGiftInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.TaskResult;

/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 礼包列表页面，用户领取和查看某游戏下的所有礼包
 */
public class GiftListActivity extends BaseActivity {
	public static final int HANDLER_RECEIVE_SUCCESS = 1;
	public static final String HANDLER_RECEIVE_FAIL = "RECEIVE_FAIL";
	public static final String HANDLER_HAD_RECEIVED = "HAD_RECEIVED";
	public static final String HANDLER_NO_LEFT = "NO_LEFT";
	public static final String HANDLER_HAS_OVERDUE = "HAS_OVERDUE";
	public static final String HANDLER_NOT_AT_TIME = "NOT_AT_TIME";

	private ImageView iv_giftMaxPhoto, iv_giftMinPhoto;
	private TextView tv_giftGameName, tv_giftNum;
	private Button btn_down;
	private GiftGameInfo myGiftGameInfo;
	private GiftDetailAdapter myGiftDetailAdapter;
	private Group<GiftInfo> giftInfos;
	private ListView giftListView;
	private Handler handler;
	private AlertDialog receiveDialog;
	private Boolean[] foucusStates;
	private Boolean[] receiveStates;
	private Group<MyGiftInfo> myGiftInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gift_list);
		goBack();

		myGiftGameInfo = (GiftGameInfo) getIntent().getSerializableExtra(
				"giftGameInfo");
		setHeadTitle(myGiftGameInfo.getGameName()
				+ getIdToString(R.string.gift));
		inithandler();
		initView();
		initData();
		initAdapter();

	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 初始化礼包数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		giftInfos = PersistentSynUtils.getModelList(GiftInfo.class, " gameid='"
				+ myGiftGameInfo.getGameid() + "'");
		foucusStates = new Boolean[giftInfos.size()];
		initReceiveStates(giftInfos);
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 初始化礼包adapter
	 */
	private void initAdapter() {
		// TODO Auto-generated method stub
		myGiftDetailAdapter = new GiftDetailAdapter(this, handler, myGiftInfos);
		myGiftDetailAdapter.setGroup(giftInfos);
		myGiftDetailAdapter.setReceiveStates(receiveStates);
		giftListView.setAdapter(myGiftDetailAdapter);

		giftListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				for (int i = 0; i < giftInfos.size(); i++) {
					if (i == position) {
						foucusStates[i] = true;
					} else {
						foucusStates[i] = false;
					}

				}
				myGiftDetailAdapter.setFoucoStates(foucusStates);
				myGiftDetailAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 初始化控件和监听事件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		iv_giftMaxPhoto = (ImageView) findViewById(R.id.iv_gift_maxPhoto);
		iv_giftMinPhoto = (ImageView) findViewById(R.id.iv_gift_minPhoto);
		iv_giftMinPhoto.setScaleType(ScaleType.FIT_XY);
		iv_giftMaxPhoto.setScaleType(ScaleType.FIT_XY);
		tv_giftGameName = (TextView) findViewById(R.id.tv_gift_gameName);
		tv_giftNum = (TextView) findViewById(R.id.tv_gift_nums);
		btn_down = (Button) findViewById(R.id.btn_down_game);
		btn_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(GiftListActivity.this,
						CommDetailActivity.class);
				GameInfo game = new GameInfo();
				game.setGameId(myGiftGameInfo.getGameid());
				in.putExtra(Constant.KEY_GAMEINFO, game);
				startActivity(in);
			}
		});

		mImageFetcher.loadImage(myGiftGameInfo.getMaxPhoto(), iv_giftMaxPhoto,
				10);
		mImageFetcher.loadImage(myGiftGameInfo.getMinPhoto(), iv_giftMinPhoto,
				10);
		tv_giftGameName.setText(myGiftGameInfo.getGameName());
		tv_giftNum.setText("共有" + myGiftGameInfo.getGiftNum() + "个礼包");

		giftListView = (ListView) findViewById(R.id.giftListView);

	}

	/**
	 * @param giftInfos
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 初始化所有礼包的领取状态
	 */
	private void initReceiveStates(Group<GiftInfo> giftInfos) {
		receiveStates = new Boolean[giftInfos.size()];
		for (int i = 0; i < giftInfos.size(); i++) {
			GiftInfo giftInfo = giftInfos.get(i);
			receiveStates[i] = isInMyGift(giftInfo);
		}

	}

	/**
	 * @author yindangchao
	 * @param giftInfo
	 * @date 2015/3/14 15:20
	 * @return 礼包是否已被领取
	 */
	private Boolean isInMyGift(GiftInfo giftInfo) {
		myGiftInfos = PersistentSynUtils.getModelList(MyGiftInfo.class,
				" userId='" + BaseApplication.getLoginUser().getUserId() + "'");
		for (MyGiftInfo myGiftInfo : myGiftInfos) {
			if (giftInfo.getGiftPackageid().equals(
					myGiftInfo.getGiftPackageid())) {
				return true;
			}
		}
		return false;

	}

	/**
	 * @author yindangchao
	 * @param giftInfo
	 * @date 2015/3/14 15:20
	 * @return 将礼包数据转换为已领取篱笆数据
	 */
	private MyGiftInfo giftToMyGift(GiftInfo giftInfo) {

		MyGiftInfo myGiftInfo = new MyGiftInfo();
		myGiftInfo.setGameId(myGiftGameInfo.getGameid());
		myGiftInfo.setUserId(BaseApplication.getLoginUser().getUserId());
		myGiftInfo.setGiftPackageid(giftInfo.getGiftPackageid());
		myGiftInfo.setContent(giftInfo.getRemark());
		myGiftInfo.setUseMethod(giftInfo.getUseMethod());
		myGiftInfo.setIcon(myGiftGameInfo.getMinPhoto());
		myGiftInfo.setReceiveTime(System.currentTimeMillis());
		myGiftInfo.setName(giftInfo.getName());
		return myGiftInfo;

	}


	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 初始化handler，处理领取礼包的结果
	 */
	private void inithandler() {
		// TODO Auto-generated method stub
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				final int position = msg.what;
				final String giftCode = (String) msg.obj;
				// if (position == HANDLER_RECEIVE_FAIL) {
				LayoutInflater inflater = LayoutInflater
						.from(GiftListActivity.this);
				View layout = inflater
						.inflate(R.layout.dia_receiver_gift, null);
				LinearLayout lin_reveive_fail = (LinearLayout) layout
						.findViewById(R.id.linear_dia_btns_fail);
				LinearLayout lin_reveive_success = (LinearLayout) layout
						.findViewById(R.id.linear_dia_btns_success);
				TextView tv_result = (TextView) layout
						.findViewById(R.id.tv_result_receive);
				TextView tv_hintOrGiftCode = (TextView) layout
						.findViewById(R.id.tv_hint_result);
				final TextView tv_giftCode = (TextView) layout
						.findViewById(R.id.tv_dia_gift_code);
				TextView tv_copyCode = (TextView) layout
						.findViewById(R.id.tv_dia_copy_gift_code);

				if (giftCode.equals(HANDLER_RECEIVE_FAIL)) {
					lin_reveive_fail.setVisibility(View.VISIBLE);
					lin_reveive_success.setVisibility(View.GONE);
					tv_result.setText(R.string.gift_receive_failed);
					tv_hintOrGiftCode.setText(R.string.gift_check_network);
					tv_giftCode.setVisibility(View.GONE);
					tv_copyCode.setVisibility(View.GONE);
					Button btn_failTryAgain = (Button) layout
							.findViewById(R.id.dia_btn_try_again);
					Button btn_failClose = (Button) layout
							.findViewById(R.id.dia_fail_btn_close);
					btn_failTryAgain.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							receiveDialog.dismiss();
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									HttpReqParams params = new HttpReqParams();
									// params.set
									params.setDeviceId(BaseApplication.deviceInfo
											.getDeviceId());
									params.setUserId(BaseApplication
											.getLoginUser().getUserId());
									params.setGiftPackageid(giftInfos.get(
											position).getGiftPackageid());
									try {
										TaskResult<MyGiftInfo> result = HttpApi
												.getObject(
														UrlConstant.HTTP_RECEIVE_GIFT1,
														UrlConstant.HTTP_RECEIVE_GIFT2,
														UrlConstant.HTTP_RECEIVE_GIFT3,
														MyGiftInfo.class,
														params.toJsonParam());

										if (result != null
												&& result.getCode() == 0) {
											MyGiftInfo myGiftInfo = result
													.getData();
											Message msg_success = new Message();
											msg_success.what = position;
											msg_success.obj = myGiftInfo
													.getGiftCode();
											handler.sendMessage(msg_success);
										} else {
											Message msg_fail = new Message();
											msg_fail.what = position;
											msg_fail.obj = GiftListActivity.HANDLER_RECEIVE_FAIL;
											handler.sendMessage(msg_fail);
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										Message msg_fail = new Message();
										msg_fail.what = position;
										msg_fail.obj = GiftListActivity.HANDLER_RECEIVE_FAIL;
										handler.sendMessage(msg_fail);
									}
								}
							}).start();

						}
					});

					btn_failClose.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							receiveDialog.dismiss();
						}
					});
				} else {

					lin_reveive_fail.setVisibility(View.GONE);
					lin_reveive_success.setVisibility(View.VISIBLE);
					Button btn_successClose = (Button) layout
							.findViewById(R.id.dia_suc_btn_close);
					if (giftCode.equals(HANDLER_HAD_RECEIVED)) {
						tv_result
								.setText(getIdToString(R.string.gift_receive_failed));
						tv_hintOrGiftCode
								.setText(getIdToString(R.string.gift_user_had_received));
						tv_giftCode.setVisibility(View.GONE);
						tv_copyCode.setVisibility(View.GONE);
					} else if (giftCode.equals(HANDLER_NO_LEFT)) {
						tv_result
								.setText(getIdToString(R.string.gift_receive_failed));
						tv_hintOrGiftCode
								.setText(getIdToString(R.string.gift_no_left));
						tv_giftCode.setVisibility(View.GONE);
						tv_copyCode.setVisibility(View.GONE);
					} else if (giftCode.equals(HANDLER_HAS_OVERDUE)) {
						tv_result
								.setText(getIdToString(R.string.gift_receive_failed));
						tv_hintOrGiftCode
								.setText(getIdToString(R.string.gift_has_overdue));
						tv_giftCode.setVisibility(View.GONE);
						tv_copyCode.setVisibility(View.GONE);
					} else if (giftCode.equals(HANDLER_NOT_AT_TIME)) {
						tv_result
								.setText(getIdToString(R.string.gift_receive_failed));
						tv_hintOrGiftCode
								.setText(getIdToString(R.string.gift_not_at_time));
						tv_giftCode.setVisibility(View.GONE);
						tv_copyCode.setVisibility(View.GONE);
					} else {
						final MyGiftInfo myGiftInfo = giftToMyGift(giftInfos
								.get(position));
						myGiftInfo.setGiftCode(giftCode);
						myGiftInfos.add(myGiftInfo);
						receiveStates[position] = true;
						myGiftDetailAdapter.setMyGiftInfos(myGiftInfos);
						myGiftDetailAdapter.setReceiveStates(receiveStates);
						myGiftDetailAdapter.notifyDataSetChanged();
						tv_result
								.setText(getIdToString(R.string.gift_receive_success));
						tv_hintOrGiftCode
								.setText(getIdToString(R.string.gift_giftcode));
						tv_giftCode.setVisibility(View.VISIBLE);
						tv_copyCode.setVisibility(View.VISIBLE);
						tv_giftCode.setText(giftCode);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								PersistentSynUtils.addModel(myGiftInfo);

							}
						}).start();
					}

					tv_copyCode.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							copyText(tv_giftCode.getText().toString());
							Toast.makeText(GiftListActivity.this,
									getIdToString(R.string.gift_copy_success),
									2 * 1000).show();
						}
					});

					btn_successClose.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							receiveDialog.dismiss();
						}
					});

				}

				receiveDialog = new AlertDialog.Builder(GiftListActivity.this)
						.create();
				receiveDialog.show();
				receiveDialog.getWindow().setContentView(layout);
				// }
				super.handleMessage(msg);
			}
		};
	}

	/**
	 * @param content
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 将字符串复制到剪切板
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void copyText(String content) {
		if (android.os.Build.VERSION.SDK_INT <= 11) {
			android.text.ClipboardManager cm = (android.text.ClipboardManager) this
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cm.setText(content.trim());
		} else {
			android.content.ClipboardManager cm = (ClipboardManager) this
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cm.setText(content.trim());
		}
	}
}
