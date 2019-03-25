package com.akon.seckill.dao;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.akon.seckill.entity.SuccessKilled;

@RunWith(SpringJUnit4ClassRunner.class)
//����Junit spring�������ļ�
@ContextConfiguration({ "classpath:spring/spring-dao.xml" })
public class SuccessKilledDaoTest {

	@Resource
	private SuccessKilledDao successKilledDao;
	
	@Test
	public void testInsertSuccessKilled() {
		/*
		 * ��һ��insertCount=1
		 * �ڶ���insertCount=0
		 */
		long seckillId = 1000L;
		long userPhone = 17707950001L;
		int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
		System.out.println("insertCount="+insertCount);
	}

	@Test
	public void testQueryByIdWithSeckill() {
		//SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone") long userPhone);
		long seckillId = 1000L;
		long userPhone = 17707950000L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
		System.out.println("successKilled="+successKilled);
		
	}

}
