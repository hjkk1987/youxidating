/**
 * Copyright 2009 Joe LaPenna
 */

package com.sxhl.market.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @ClassName: Preferences.java
 * @Description: SharePreferences变量封装
 * @author 吴绍东
 * @date 2012-12-21 上午11:18:36
 */
public class Preferences {
    public static final String PREFERENCE_GAME_RECOMMEND = "game_recommend";
    public static final String PREFERENCE_GAME_HOT = "game_hot";
    public static final String PREFERENCE_GAME_TOPICS = "game_topics";

    /**
     * @author wsd
     * @Description：获取史诗推荐栏目访问状态  
     *  0：activity第一启动，需要请求新的数据
     *  1：activity第一启动，不请求网络，直接加载数据库数据
     * @date 2012-12-21 上午11:21:51
     */
    public static Integer getGameRecommendState(SharedPreferences prefs) {
        return prefs.getInt(PREFERENCE_GAME_RECOMMEND, 0);
    }
    /**
     * @author wsd
     * @Description:设置精品游戏栏目的状态
     * @param state 0：activity第一启动，需要请求新的数据; 1：activity第一启动，不请求网络，直接加载数据库数据
     * @date 2012-12-21 上午11:23:22
     */
    public static void setGameRecommendState(final Editor editor, int state) {
        editor.putInt(PREFERENCE_GAME_RECOMMEND, state);
        editor.commit();
    }

    /**
     * @author wsd
     * @Description：获取精品游戏栏目访问状态  
     *  0：activity第一启动，需要请求新的数据
     *  1：activity第一启动，不请求网络，直接加载数据库数据
     * @date 2012-12-21 上午11:21:51
     */
    public static Integer getGameHotState(SharedPreferences prefs) {
        return prefs.getInt(PREFERENCE_GAME_HOT, 0);
    }
    /**
     * @author wsd
     * @Description:设置史诗推荐栏目的状态
     * @param state 0：activity第一启动，需要请求新的数据; 1：activity第一启动，不请求网络，直接加载数据库数据
     * @date 2012-12-21 上午11:23:22
     */
    public static void setGameHotState(final Editor editor, int state) {
        editor.putInt(PREFERENCE_GAME_HOT, state);
        editor.commit();
    }
    /**
     * @author wsd
     * @Description：获取游戏专题栏目访问状态  
     *  0：activity第一启动，需要请求新的数据
     *  1：activity第一启动，不请求网络，直接加载数据库数据
     * @date 2012-12-21 上午11:21:51
     */
    public static Integer getGameTopicsState(SharedPreferences prefs) {
        return prefs.getInt(PREFERENCE_GAME_TOPICS, 0);
    }
    /**
     * @author wsd
     * @Description:设置游戏专题栏目的状态
     * @param state 0：activity第一启动，需要请求新的数据; 1：activity第一启动，不请求网络，直接加载数据库数据
     * @date 2012-12-21 上午11:23:22
     */
    public static void setGameTopicsState(final Editor editor, int state) {
        editor.putInt(PREFERENCE_GAME_TOPICS, state);
        editor.commit();
    }
}
