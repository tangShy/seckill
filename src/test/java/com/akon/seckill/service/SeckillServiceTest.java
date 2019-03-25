package com.akon.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.akon.seckill.dto.Exposer;
import com.akon.seckill.dto.SeckillExecution;
import com.akon.seckill.entity.Seckill;
import com.akon.seckill.exception.RepeatKillException;
import com.akon.seckill.exception.SeckillCloseException;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                      "classpath:spring/spring-service.xml"})
//@Transactional  可用于测试时执行完操作之后所有事务自动回滚
public class SeckillServiceTest {
	
	private final Logger logger= LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetSeckillList() throws Exception {
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list={}",list);
	}

	@Test
	public void testGetById() throws Exception {
		long seckillId = 1000L;
		Seckill seckill = seckillService.getById(seckillId);
		logger.info("seckill={}",seckill);
	}

	@Test
	public void testExportSeckillUrl() throws Exception {
		long seckillId = 1001;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		logger.info("exposer={}",exposer);
	}

	@Test
	public void testExecuteSeckill() throws Exception {
		/*long seckillId = 1001;
		long userPhone = 1770795000L;
		String md5 = "393d676ad3df7331d19cfc6b55fcf238";
		
		try{
			SeckillExecution excution = seckillService.executeSeckill(seckillId, userPhone, md5);
			logger.info("result={}",excution);
		}catch(RepeatKillException e) {
			logger.error(e.getMessage());
		}catch(SeckillCloseException e1) {
			logger.error(e1.getMessage());
		}	*/
		//改进
		long seckillId = 1001;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			logger.info("exposer={}",exposer);
			long userPhone = 17707950002L;
			String md5 = exposer.getMd5();
			
			try{
				SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
				logger.info("result={}",execution);
			}catch(RepeatKillException e){
				logger.error(e.getMessage());
			}catch(SeckillCloseException e1) {
				logger.error(e1.getMessage());
			}
		}else {
			//秒杀未开启
			logger.warn("exposer={}",exposer);
		}
	}

}
