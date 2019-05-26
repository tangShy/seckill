package com.akon.seckill.service;

import com.akon.seckill.dto.Exposer;
import com.akon.seckill.dto.SeckillExecution;
import com.akon.seckill.entity.Seckill;
import com.akon.seckill.exception.RepeatKillException;
import com.akon.seckill.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�������ļ�
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                      "classpath:spring/spring-service.xml"})
@Transactional  //�����ڲ���ʱִ�������֮�����������Զ��ع�
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
		long seckillId = 1002;
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
		//�Ľ�
		long seckillId = 1001;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			logger.info("exposer={}",exposer);
			long userPhone = 17807950002L;
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
			//��ɱδ����
			logger.warn("exposer={}",exposer);
		}
	}
	
	@Test
	public void executeSeckillProcedure(){
	    long seckillId = 1001L;
	    long phone = 13680115101L;
	    Exposer exposer = seckillService.exportSeckillUrl(seckillId);
	    logger.info("exposer={}", exposer);
	    if (exposer.isExposed()) {
	        String md5 = exposer.getMd5();
	        SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
	        logger.info("execution={}", execution);
	    }
	}

}
