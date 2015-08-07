package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

/**
 * @ClassName: Comment
 * @Description: 评论实体类
 * @author: Liuqin
 * @date 2012-12-6 上午11:48:07
 * 
 */
@TableDescription(name = "comment")
public class Comment extends BaseModel implements AutoType, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 评论ID
	private String gameCommId;
	// 用户名
	private String nickName;
	// 用户头像url
	private String avator;
	// 评论内容
	private String commContent;
	// 评论创建时间
	private long createTime;

	public Comment() {
		super();
	}

	public Comment(String commentId, String userName, String avator,
			String content, long createTime) {
		super();
		this.gameCommId = commentId;
		this.nickName = userName;
		this.avator = avator;
		this.commContent = content;
		this.createTime = createTime;
	}

	public String getGameCommId() {
		return gameCommId;
	}

	public void setGameCommId(String commentId) {
		this.gameCommId = commentId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String userName) {
		this.nickName = userName;
	}

	public String getAvator() {
		return avator;
	}

	public void setAvator(String avator) {
		this.avator = avator;
	}

	public String getCommContent() {
		return commContent;
	}

	public void setCommContent(String content) {
		this.commContent = content;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Comment[commentId:" + gameCommId + " userName:" + nickName
				+ " avator:" + avator + " content:" + commContent
				+ " createTime:" + createTime + "]";
	}
}
