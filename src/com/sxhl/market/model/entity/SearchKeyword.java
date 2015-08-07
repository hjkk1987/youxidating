package com.sxhl.market.model.entity;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

/** 
 * @ClassName: SearchKeyword 
 * @Description: 搜索关键词实体类
 * @author: Liuqin
 * @date 2012-12-5 下午1:57:09 
 *  
 */  
@TableDescription(name="searchKeyword")
public class SearchKeyword extends BaseModel implements AutoType {
	private String keyword;

	public SearchKeyword() {
		super();
	}

	public SearchKeyword(String keyword) {
		super();
		this.keyword = keyword;
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return keyword+" ";
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}	
}
