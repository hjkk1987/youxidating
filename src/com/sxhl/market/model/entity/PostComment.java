package com.sxhl.market.model.entity;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

/**
 * 
 * @ClassName: PostComment
 * @Description: TODO这个类用于描述回复的帖子评论列表
 * @author A18ccms a18ccms_gmail_com
 * @date 2012-12-10 下午5:03:07
 * 
 */
@TableDescription(name="postComment")
public class PostComment extends BaseModel implements AutoType {
	private int postCommentId;// 帖子评论Id
	private String avator;// 用户的头像
	private String userId;// 用户账号Id
	private String content;// 回复的内容
	private int points;// 积分
	private int level;// 用户等级
	private long createTime;
	private String nickName;
	private Long sysTime;//系统当前时间
	
	
	
	public Long getSysTime() {
		return sysTime;
	}

	public void setSysTime(Long sysTime) {
		this.sysTime = sysTime;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String userName) {
		this.nickName = userName;
	}

	public PostComment() {
		super();
	}

	public PostComment(int postCommentId, String avator, String userAccountId,
			String content, int points, int level) {
		super();
		this.postCommentId = postCommentId;
		this.avator = avator;
		this.userId = userAccountId;
		this.content = content;
		this.points = points;
		this.level = level;
	}

	public int getPostCommentId() {
		return postCommentId;
	}

	public void setPostCommentId(int postCommentId) {
		this.postCommentId = postCommentId;
	}

	public String getAvator() {
		return avator;
	}

	public void setAvator(String avator) {
		this.avator = avator;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserAccountId(String userAccountId) {
		this.userId = userAccountId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "PostComment [postCommentId=" + postCommentId + ", avator="
				+ avator + ", userAccountId=" + userId + ", content="
				+ content + ", points=" + points + ", level=" + level
				+ ", createTime=" + createTime + ", userName=" + nickName + "]";
	}



}
