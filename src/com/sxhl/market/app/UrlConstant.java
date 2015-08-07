package com.sxhl.market.app;

/**
 * @author wsd
 * @Description:
 * @date 2012-12-4 下午2:44:53
 */
public class UrlConstant {
	/** 获取deviceid接口 **/
	public static final String HTTP_GET_DEVICE_ID = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/deviceid.do";
	public static final String HTTP_GET_DEVICE_ID1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/deviceid.do";
	public static final String HTTP_GET_DEVICE_ID2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/deviceid.do";
	public static final String HTTP_GET_DEVICE_ID3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/deviceid.do";

	/********************************* 游戏相关接口 *********************************************/
	/** 精品游戏接口 **/
	public static final String HTTP_HOTGAME_LIST = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/wglist.do";
	public static final String HTTP_HOTGAME_LIST1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/wglist.do";
	public static final String HTTP_HOTGAME_LIST2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/wglist.do";
	public static final String HTTP_HOTGAME_LIST3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/wglist.do";
	/** 广告接口 **/
	public static final String HTTP_AD_LIST = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/adsbymodel.do";
	public static final String HTTP_AD_LIST1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/adsbymodel.do";
	public static final String HTTP_AD_LIST2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/adsbymodel.do";
	public static final String HTTP_AD_LIST3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/adsbymodel.do";

	/** 热门游戏接口 **/
	public static final String HTTP_HOT_GAME_LIST = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/hotgamelist.do";
	public static final String HTTP_HOT_GAME_LIST1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/hotgamelist.do";
	public static final String HTTP_HOT_GAME_LIST2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/hotgamelist.do";
	public static final String HTTP_HOT_GAME_LIST3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/hotgamelist.do";

	/** 专题列表接口 **/
	public static final String HTTP_GAMETYPE_LIST = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/typelist.do";
	public static final String HTTP_GAMETYPE_LIST1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/typelist.do";
	public static final String HTTP_GAMETYPE_LIST2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/typelist.do";
	public static final String HTTP_GAMETYPE_LIST3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/typelist.do";

	/** 专题游戏列表接口 **/
	public static final String HTTP_GAMELIST_GAME_LIST = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/glist.do";
	public static final String HTTP_GAMELIST_GAME_LIST1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/glist.do";
	public static final String HTTP_GAMELIST_GAME_LIST2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/glist.do";
	public static final String HTTP_GAMELIST_GAME_LIST3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/glist.do";
	
	public static final String HTTP_GAMELIST_GAME_LIST_NEW = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/newrecommend.do";
	public static final String HTTP_GAMELIST_GAME_LIST_NEW1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/newrecommend.do";
	public static final String HTTP_GAMELIST_GAME_LIST_NEW2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/newrecommend.do";
	public static final String HTTP_GAMELIST_GAME_LIST_NEW3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/newrecommend.do";
	
	public static final String HTTP_GAMELIST_GAME_LIST_RANKING = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/topdown.do";
	public static final String HTTP_GAMELIST_GAME_LIST_RANKING1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/topdown.do";
	public static final String HTTP_GAMELIST_GAME_LIST_RANKING2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/topdown.do";
	public static final String HTTP_GAMELIST_GAME_LIST_RANKING3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/topdown.do";

	/** 游戏详细信息接口 **/
	public static final String HTTP_GAME_DETAIL = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/gdetail.do";
	public static final String HTTP_GAME_DETAIL1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/gdetail.do";
	public static final String HTTP_GAME_DETAIL2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/gdetail.do";
	public static final String HTTP_GAME_DETAIL3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/gdetail.do";

	/** 上传游戏评论接口 **/
	public static final String HTTP_GAME_UPCOMMENT = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/uploadgamecomm.do";
	public static final String HTTP_GAME_UPCOMMENT1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/uploadgamecomm.do";
	public static final String HTTP_GAME_UPCOMMENT2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/uploadgamecomm.do";
	public static final String HTTP_GAME_UPCOMMENT3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/uploadgamecomm.do";

	/** 获取游戏评论接口 **/
	public static final String HTTP_GAME_GETCOMMENT = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/gamecommlist.do";
	public static final String HTTP_GAME_GETCOMMENT1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/gamecommlist.do";
	public static final String HTTP_GAME_GETCOMMENT2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/gamecommlist.do";
	public static final String HTTP_GAME_GETCOMMENT3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/gamecommlist.do";

	/** 更新游戏评论头像接口 **/
	public static final String HTTP_CHANGE_COMMENT_ICON = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/updateuserinfo.do";
	public static final String HTTP_CHANGE_COMMENT_ICON1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/updateuserinfo.do";
	public static final String HTTP_CHANGE_COMMENT_ICON2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/updateuserinfo.do";
	public static final String HTTP_CHANGE_COMMENT_ICON3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/updateuserinfo.do";

	/** 获取游戏星级和下载次数接口 **/
	public static final String HTTP_GAME_GETCOUNT_AND_STAR = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/gcountlevel.do";
	public static final String HTTP_GAME_GETCOUNT_AND_STAR1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/gcountlevel.do";
	public static final String HTTP_GAME_GETCOUNT_AND_STAR2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/gcountlevel.do";
	public static final String HTTP_GAME_GETCOUNT_AND_STAR3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/gcountlevel.do";

	/** 获取版本号 **/
	public static final String HTTP_GAME_GET_VERSION = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/queryversion.do";
	public static final String HTTP_GAME_GET_VERSION1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/queryversion.do";
	public static final String HTTP_GAME_GET_VERSION2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/queryversion.do";
	public static final String HTTP_GAME_GET_VERSION3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/queryversion.do";

	/** 游戏搜索列表接口 **/
	public static final String HTTP_GAME_SEARCH = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/searchgame.do";
	public static final String HTTP_GAME_SEARCH1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/searchgame.do";
	public static final String HTTP_GAME_SEARCH2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/searchgame.do";
	public static final String HTTP_GAME_SEARCH3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/searchgame.do";

	/** 下载数自增 */
	public static final String HTTP_GAME_DOWNLOAD_COUNT = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/dcount.do";
	public static final String HTTP_GAME_DOWNLOAD_COUNT1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/dcount.do";
	public static final String HTTP_GAME_DOWNLOAD_COUNT2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/dcount.do";
	public static final String HTTP_GAME_DOWNLOAD_COUNT3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/dcount.do";

	/** 服务器时间接口 */

	public static final String HTTP_GET_SEVER_TIME = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/time.do";
	public static final String HTTP_GET_SEVER_TIME1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/time.do";
	public static final String HTTP_GET_SEVER_TIME2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/time.do";
	public static final String HTTP_GET_SEVER_TIME3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/time.do";

	/** 游戏评分 **/
	public static final String HTTP_GAME_GIVE_SCORE = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/starscore.do";
	public static final String HTTP_GAME_GIVE_SCORE1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/starscore.do";
	public static final String HTTP_GAME_GIVE_SCORE2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/starscore.do";
	public static final String HTTP_GAME_GIVE_SCORE3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/starscore.do";

	/** 关键字列表 **/
	public static final String HTTP_GAME_SEARCH_KEYWORDS = IPPort.BASE_GAMEBOX_SEVER
			+ "/atetinterface/keywordlist.do";
	public static final String HTTP_GAME_SEARCH_KEYWORDS1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/keywordlist.do";
	public static final String HTTP_GAME_SEARCH_KEYWORDS2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/keywordlist.do";
	public static final String HTTP_GAME_SEARCH_KEYWORDS3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/keywordlist.do";

	/** 根据包名获取游戏版本接口 */
	public static final String HTTP_GET_GAMEINFO_INSTALLED1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/gameversion.do";
	public static final String HTTP_GET_GAMEINFO_INSTALLED2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/gameversion.do";
	public static final String HTTP_GET_GAMEINFO_INSTALLED3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/gameversion.do";

	/************************ 用户相关接口 ******************************/

	/** 修改用户资料接口 */
	public static final String HTTP_USER_MODIFY1 = IPPort.BASE_USER_SERVER1
			+ "/myuser/update.do";
	public static final String HTTP_USER_MODIFY2 = IPPort.BASE_USER_SERVER2
			+ "/myuser/update.do";
	public static final String HTTP_USER_MODIFY3 = IPPort.BASE_USER_SERVER3
			+ "/myuser/update.do";

	/** 上传图片 */

	public static final String HTTP_COMMON_PUPLOAD = IPPort.BASE_USER_SERVER3
			+ "/myuser/picUpload.do";
	public static final String HTTP_COMMON_PUPLOAD1 = IPPort.BASE_USER_SERVER1
			+ "/myuser/picUpload.do";
	public static final String HTTP_COMMON_PUPLOAD2 = IPPort.BASE_USER_SERVER2
			+ "/myuser/picUpload.do";
	public static final String HTTP_COMMON_PUPLOAD3 = IPPort.BASE_USER_SERVER3
			+ "/myuser/picUpload.do";

	/************************ 礼包相关接口 ************************************/

	public static final String HTTP_GET_GIFT1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/newgamegift.do";
	public static final String HTTP_GET_GIFT2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/newgamegift.do";
	public static final String HTTP_GET_GIFT3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/newgamegift.do";

	public static final String HTTP_GET_MYGIFT1 =IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/usergifts.do";
	public static final String HTTP_GET_MYGIFT2 =IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/usergifts.do";
	public static final String HTTP_GET_MYGIFT3 =IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/usergifts.do";

	public static final String HTTP_RECEIVE_GIFT1 = IPPort.BASE_GAMEBOX_SEVER1
			+ "/atetinterface/userreceivegift.do";
	public static final String HTTP_RECEIVE_GIFT2 = IPPort.BASE_GAMEBOX_SEVER2
			+ "/atetinterface/userreceivegift.do";
	public static final String HTTP_RECEIVE_GIFT3 = IPPort.BASE_GAMEBOX_SEVER3
			+ "/atetinterface/userreceivegift.do";

}
