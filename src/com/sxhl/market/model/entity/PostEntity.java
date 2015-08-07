package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

/**
 * 
 * @ClassName: PostEntity 
 * @Description: 广场实体类
 * @author 孔德升 
 * @date 2012-12-10 下午2:49:12
 */
@TableDescription(name="postEntity")
public class PostEntity extends BaseModel implements AutoType,Serializable  {
	/**
	 * @FieldsserialVersionUID：TODO
	 */
	private static final long serialVersionUID = 1914152541369445444L;
	//帖子ID
	private int postId;
	//帖子内容
	private String content;
	//用户id
	private String userId;
	//用户名称
	private String nickName;
	//用户头像
	private String avator;
	//更新时间
	private Long updateTime;
	//创建时间
	private Long createTime;
	//评论次数
	private int commentCount;
	//顶贴次数
	private int upCount;
	//板块ID
	private Integer plateId;
	//系统的当前时间
	private Long sysTime;
	
	public PostEntity() {
		super();
	}
	
	
	public PostEntity(int postId, String content, String userId,
			String nickName, String avator, Long updateTime, Long createTime,
			int commentCount, int upCount, Integer plateId) {
		super();
		this.postId = postId;
		this.content = content;
		this.userId = userId;
		this.nickName = nickName;
		this.avator = avator;
		this.updateTime = updateTime;
		this.createTime = createTime;
		this.commentCount = commentCount;
		this.upCount = upCount;
		this.plateId = plateId;
	}

	
	public Long getSysTime() {
		return sysTime;
	}


	public void setSysTime(Long sysTime) {
		this.sysTime = sysTime;
	}


	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
	public Integer getPlateId() {
		return plateId;
	}


	public void setPlateId(Integer plateId) {
		this.plateId = plateId;
	}


	public int getPostId() {
		return postId;
	}
	public void setPostId(int postId) {
		this.postId = postId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getAvator() {
		return avator;
	}
	public void setAvator(String avator) {
		this.avator = avator;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public int getUpCount() {
		return upCount;
	}
	public void setUpCount(int upCount) {
		this.upCount = upCount;
	}


	@Override
	public String toString() {
		return "PostEntity [postId=" + postId + ", content=" + content
				+ ", userId=" + userId + ", nickName=" + nickName + ", avator="
				+ avator + ", updateTime=" + updateTime + ", createTime="
				+ createTime + ", commentCount=" + commentCount + ", upCount="
				+ upCount + ", plateId=" + plateId + "]";
	}

}
