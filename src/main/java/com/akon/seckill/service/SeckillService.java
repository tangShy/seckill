package com.akon.seckill.service;

import java.util.List;

import com.akon.seckill.dto.Exposer;
import com.akon.seckill.dto.SeckillExecution;
import com.akon.seckill.entity.Seckill;
import com.akon.seckill.exception.RepeatKillException;
import com.akon.seckill.exception.SeckillCloseException;
import com.akon.seckill.exception.SeckillException;

/**
 * 业务接口：站在“使用者”角度设计接口
 * 三个方面：方法定义粒度，参数，返回类型（return类型/异常）
 * 1.方法定义粒度，方法定义的要非常清楚2.参数，要越简练越好 3.返回类型(return
 * 类型一定要友好/或者return异常，我们允许的异常)
 * @author lenovo
 *
 */
public interface SeckillService {
	
	/**
	 * 查询所有秒杀记录
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * 查询单个秒杀记录
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	
	/**
	 * 秒杀开启时输出秒杀接口地址，
	 * 否则输出系统时间和秒杀时间
	 * @param seckillId
	 * @return 根据对应的状态返回对应的状态实体
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * 执行秒杀操作，有可能失败，有可能成功，所以要抛出我们允许的异常
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return 根据不同的结果返回不同的实体信息
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5) throws SeckillException,
    RepeatKillException, SeckillCloseException;
}
	
	
	
	
	
