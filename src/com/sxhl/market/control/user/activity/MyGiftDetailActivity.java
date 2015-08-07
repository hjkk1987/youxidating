package com.sxhl.market.control.user.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.game.activity.CommDetailActivity;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.MyGiftInfo;

/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 此页面主要是显示用户已领取礼包的详情信息
 */
public class MyGiftDetailActivity extends BaseActivity implements
		OnClickListener {
	ImageView ivMyGiftIcon;
	Button btnDownGame;
	TextView tvGiftName;
	TextView tvDuringTime;
	TextView tvGiftRemark;
	TextView tvGiftCode;
	Button btnCopyGiftCode;
	TextView tvGiftUseMethod;
	MyGiftInfo myGiftInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mygift_detail);
		goBack();
		setHeadTitle(getIdToString(R.string.gift_mygift_detail));
		myGiftInfo = (MyGiftInfo) getIntent()
				.getSerializableExtra("myGiftInfo");
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		ivMyGiftIcon = (ImageView) findViewById(R.id.img_mygift_icon);
		btnDownGame = (Button) findViewById(R.id.btn_down_game);
		tvGiftName = (TextView) findViewById(R.id.tv_mygift_name);
		tvDuringTime = (TextView) findViewById(R.id.tv_mygift_during_time);
		tvGiftRemark = (TextView) findViewById(R.id.tv_mygift_remark);
		tvGiftCode = (TextView) findViewById(R.id.tv_mygift_code);
		btnCopyGiftCode = (Button) findViewById(R.id.btn_mygift_copy_code);
		tvGiftUseMethod = (TextView) findViewById(R.id.tv_mygift_usemethod);
		ivMyGiftIcon.setScaleType(ScaleType.FIT_XY);
		mImageFetcher.loadImage(myGiftInfo.getIcon(), ivMyGiftIcon, 8);

		tvGiftName.setText(myGiftInfo.getName());
		tvDuringTime.setText(getIdToString(R.string.gift_receive_time)
				+ myGiftInfo.getFormatReceiveTime());
		tvGiftRemark.setText(myGiftInfo.getContent());
		tvGiftCode.setText(myGiftInfo.getGiftCode());
		btnCopyGiftCode.setText(getIdToString(R.string.gift_copy_gift_code));
		tvGiftUseMethod.setText(myGiftInfo.getUseMethod());
		btnDownGame.setOnClickListener(this);
		btnCopyGiftCode.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_down_game:
			Intent in = new Intent(MyGiftDetailActivity.this,
					CommDetailActivity.class);
			GameInfo game = new GameInfo();
			game.setGameId(myGiftInfo.getGameId());
			in.putExtra(Constant.KEY_GAMEINFO, game);
			startActivity(in);
			break;

		case R.id.btn_mygift_copy_code:
			//复制礼包码
			String content = tvGiftCode.getText().toString();
			if (android.os.Build.VERSION.SDK_INT <= 11) {
				android.text.ClipboardManager cm = (android.text.ClipboardManager) this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText(content.trim());
			} else {
				android.content.ClipboardManager cm = (ClipboardManager) this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText(content.trim());
			}
			Toast.makeText(MyGiftDetailActivity.this,
					getIdToString(R.string.gift_copy_success), 2 * 1000).show();

			break;

		default:
			break;
		}

	}
}
