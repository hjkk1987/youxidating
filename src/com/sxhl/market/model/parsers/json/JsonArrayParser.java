package com.sxhl.market.model.parsers.json;

import java.io.IOException;

import com.google.gson.stream.JsonReader;
import com.sxhl.market.model.entity.AutoType;
import com.sxhl.market.model.entity.Group;
/**
 * @author wsd
 * @Description:负责Json数组的解析
 * @date 2012-12-5 上午10:01:06
 */
public class JsonArrayParser extends AbstractParser<Group> {
	/**
	* @Fields mSubParser :数组中单个对象的解析器
	*/
	private AbstractParser<? extends AutoType> mSubParser;

	public JsonArrayParser(AbstractParser<? extends AutoType> subParser) {
		this.mSubParser = subParser;
	}
	/**
	 * 模板方法，用于解析数组对象
	 */
	@Override
	public Group<AutoType> parserInner(JsonReader reader) {
		// TODO Auto-generated method stub
		Group<AutoType> group = new Group<AutoType>();

		try {
			reader.beginArray();
			while (reader.hasNext()) {
				group.add(mSubParser.parserInner(reader));
			}
			reader.endArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return group;
	}
}
