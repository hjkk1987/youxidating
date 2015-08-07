package com.sxhl.market.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.MainTabActivity;
import com.sxhl.market.model.entity.AdInfo;
import com.sxhl.market.model.entity.BillInfo;
import com.sxhl.market.model.entity.ChosenGameInfo;
import com.sxhl.market.model.entity.CollectionInfo;
import com.sxhl.market.model.entity.Comment;
import com.sxhl.market.model.entity.CountLevelInfo;
import com.sxhl.market.model.entity.DownloadGameInfo;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.GameType;
import com.sxhl.market.model.entity.GiftGameInfo;
import com.sxhl.market.model.entity.GiftInfo;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.model.entity.MyGiftInfo;
import com.sxhl.market.model.entity.PostComment;
import com.sxhl.market.model.entity.PostEntity;
import com.sxhl.market.model.entity.ScreenShootInfo;
import com.sxhl.market.model.entity.SearchKeyword;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.statistics.model.CollectGameInfo;
import com.sxhl.statistics.model.GameOnlineInfo;
import com.sxhl.statistics.model.InitInfo;

/**
 * @ClassName: DBHelper.java
 * @Description: 数据库帮助类
 * @author 吴绍东
 * @date 2012-12-12 下午12:48:48
 */
public class DBHelper extends SQLiteOpenHelper {

	public static final String NAME = "box.db"; // DB name

	public DBHelper(Context context) {

		super(context, NAME, null, Constant.DATABASE_VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {

		super(context, name, factory, version);
	}

	/**
	 * 用户第一次使用软件时调用的操作，用于获取数据库创建语句（SW）,然后创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		DebugTool.info("onCreate");
		db.execSQL(BaseModelTool.getCreateTableSql(Comment.class));
		db.execSQL(BaseModelTool.getCreateTableSql(GameType.class));
		db.execSQL(BaseModelTool.getCreateTableSql(GameInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(PostComment.class));
		db.execSQL(BaseModelTool.getCreateTableSql(PostEntity.class));
		db.execSQL(BaseModelTool.getCreateTableSql(UserInfo.class));
		// db.execSQL(BaseModelTool.getCreateTableSql(MyGameInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSqlNoSuper(MyGameInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(SearchKeyword.class));
		db.execSQL(BaseModelTool.getCreateTableSql(CollectionInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(BillInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(ChosenGameInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(ScreenShootInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(AdInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(CountLevelInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(CollectGameInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(InitInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(GameOnlineInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(DownloadGameInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(GiftGameInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(MyGiftInfo.class));
		db.execSQL(BaseModelTool.getCreateTableSql(GiftInfo.class));

	}

	/**
	 * 数据库更新
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(BaseModelTool.getDropTableSql(Comment.class));
		db.execSQL(BaseModelTool.getDropTableSql(GameType.class));
		db.execSQL(BaseModelTool.getDropTableSql(GameInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(PostComment.class));
		db.execSQL(BaseModelTool.getDropTableSql(PostEntity.class));
		db.execSQL(BaseModelTool.getDropTableSql(UserInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(MyGameInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(SearchKeyword.class));
		db.execSQL(BaseModelTool.getDropTableSql(CollectionInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(BillInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(ChosenGameInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(ScreenShootInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(AdInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(CountLevelInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(CollectGameInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(InitInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(GameOnlineInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(DownloadGameInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(GiftGameInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(MyGiftInfo.class));
		db.execSQL(BaseModelTool.getDropTableSql(GiftInfo.class));
		BaseApplication.m_appContext
				.getSharedPreferences(MainTabActivity.SP_GIFT_MINTIME,
						Context.MODE_PRIVATE).edit()
				.clear().commit();

		onCreate(db);
	}

}
