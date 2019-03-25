package com.akon.seckill.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.akon.seckill.dao.SeckillDao;
import com.akon.seckill.dao.SuccessKilledDao;
import com.akon.seckill.dao.cache.RedisDao;
import com.akon.seckill.dto.Exposer;
import com.akon.seckill.dto.SeckillExecution;
import com.akon.seckill.entity.Seckill;
import com.akon.seckill.entity.SuccessKilled;
import com.akon.seckill.enums.SeckillStatEnum;
import com.akon.seckill.exception.RepeatKillException;
import com.akon.seckill.exception.SeckillCloseException;
import com.akon.seckill.exception.SeckillException;
import com.akon.seckill.service.SeckillService;

@Service
public class SeckillServiceImpl implements SeckillService {
	
	//日志对象
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//加入一个混淆字符串(秒杀接口)的salt，为了我避免用户猜出我们的md5值，值任意给，越复杂越好
	private final String salt = "kalkg_*&=20)haionqwj18u!@#!4jad";
	
	//注入Service依赖
	@Autowired	//@Resource
	private SeckillDao seckillDao;
	
	@Autowired	//@Resource
	private SuccessKilledDao successkilledDao;
	
	@Autowired
	private RedisDao redisDao;
				
	//查询所有秒杀商品
	public List<Seckill> getSeckillList() {		
		return seckillDao.queryAll(0, 5);
	}
	
	//根据id查询单个商品
	public Seckill getById(long seckillId) {		
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		// 优化点：缓存优化——超时的基础上维护一致性
		// 1.访问redis

		// Seckill seckill = seckillDao.queryById(seckillId);
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){	
			//2.访问数据库
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){	//说明查不到这个秒杀产品的记录
				return new Exposer(false,seckillId);
			}else {
				// 3.放入redis
				redisDao.putSeckill(seckill);
			}
		}
		
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();	//系统当前时间
		//若是秒杀未开启或者秒杀结束
		if(startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()){
			return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		
		//秒杀开启,返回秒杀商品的id、用给接口加密的md5
		String md5 = getMD5(seckillId);		
		return new Exposer(true,md5,seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	//秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
	/*@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite"); //秒杀数据被重写了
		}
		
		//执行秒杀逻辑：减库存+增加购买明细
		Date nowTime = new Date();
		
		try{
			//减库存
			int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
			if(updateCount <= 0){
				//没有更新库存记录，说明秒杀结束
				throw new SeckillCloseException("seckill is closed~");
			}else {
				//否则更新了库存，秒杀成功，增加明细
				int insertCount = successkilledDao.insertSuccessKilled(seckillId, userPhone);
				//检查改明细是否重复，即用户是否重复秒杀
				if(insertCount <= 0){
					throw new RepeatKillException("seckill repeated!");
				}else {
					//秒杀成功，得到成功插入的明细记录，并返回成功秒杀的信息
					SuccessKilled successKilled = successkilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilled);
				}
			}
		}catch(SeckillCloseException e1){
			throw e1;
		}catch(RepeatKillException  e2){
			throw e2;
		}catch(Exception  e){
			logger.error(e.getMessage(),e);
			//将编译期异常转换为运行期异常
			throw new SeckillException("seckill inner error : "+e.getMessage());
		}
	}		*/

	//优化：将原本先update（减库存）再进行insert（插入购买明细）的步骤改成：先insert再update
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite"); //秒杀数据被重写了
		}
		
		//执行秒杀逻辑：减库存+增加购买明细
		Date nowTime = new Date();
		
		try{
			//更新库存，秒杀成功，增加明细
			int insertCount = successkilledDao.insertSuccessKilled(seckillId, userPhone);
			//检查改明细是否重复，即用户是否重复秒杀
			if(insertCount <= 0){
				throw new RepeatKillException("seckill repeated!");
			}else {
				//减库存，热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if(updateCount <= 0){
					//没有更新库存记录，说明秒杀结束
					throw new SeckillCloseException("seckill is closed~");
				}else{
					//秒杀成功，得到成功插入的明细记录，并返回成功秒杀的信息
					SuccessKilled successKilled = successkilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilled);
				}
			}
		}catch(SeckillCloseException e1){
			throw e1;
		}catch(RepeatKillException  e2){
			throw e2;
		}catch(Exception  e){
			logger.error(e.getMessage(),e);
			//将编译期异常转换为运行期异常
			throw new SeckillException("seckill inner error : "+e.getMessage());
		}
	}

}
