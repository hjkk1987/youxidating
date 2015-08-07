package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

@TableDescription(name = "countLevelInfo")
public class CountLevelInfo extends BaseModel implements AutoType, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String gameId;
	private Integer downCount;
	private double StarLevel;

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public Integer getDownCount() {
		return downCount;
	}

	public void setDownCount(Integer downCount) {
		this.downCount = downCount;
	}

	public double getStarLevel() {
		return StarLevel;
	}

	public void setStarLevel(double starLevel) {
		StarLevel = starLevel;
	}

	public String toString() {
		return "CountLevelInfo [gameId=" + gameId + ",  downCount=" + downCount
				+ ", starLevel=" + StarLevel + "]";
	}
}
