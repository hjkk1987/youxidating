package com.sxhl.market.model.database;


import java.lang.annotation.Documented;  
import java.lang.annotation.Inherited;  
import java.lang.annotation.Retention;  
import java.lang.annotation.Target;  
import java.lang.annotation.ElementType;  
import java.lang.annotation.RetentionPolicy;  
/**
 * @ClassName: TableDescription.java
 * @Description: 实体bean的表名映射描述
 * @author 吴绍东
 * @date 2012-12-12 下午12:49:58
 */
@Target(ElementType.TYPE)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
@Inherited  
public @interface TableDescription {
	public String name() default "";  
    public String description() default "no description"; 
}
