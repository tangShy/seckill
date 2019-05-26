package com.akon.seckill.dao;

import com.akon.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {
	
	/**
	 * 减库存
	 * 
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	/*
	 * 当方法的形参在两个及两个以上时，需要在参数前加上@Param，如果不加上该注解会在之后的
	 * 测试运行时报错。这是Sun提供的默认编译器（javac）在编译后的Class文件中会丢失参数
	 * 的实际名称，方法中的形参会变成无意义的arg0、arg1等，在只有一个参数时就无所谓，但当
	 * 参数在两个和两个以上时，传入方法的参数就会找不到对应的形参。因为Java形参的问题，所以
	 * 在多个基本类型参数时需要用@Param注解区分开来。
	 */
	int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);
	
	/**
	 * 根据id查询秒杀商品
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);
	
	/**
	 * 根据偏移量查询秒杀商品列表
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(@Param("offset")int offset,@Param("limit")int limit);
	
	/**
	 * 使用存储过程执行秒杀
	 * @param paramMap
	 */
	void killByProcedure(Map<String,Object> paramMap);
}
