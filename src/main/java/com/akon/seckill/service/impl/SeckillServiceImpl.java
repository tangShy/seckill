package com.akon.seckill.service.impl;

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
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {
	
	//��־����
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//����һ�������ַ���(��ɱ�ӿ�)��salt��Ϊ���ұ����û��³����ǵ�md5ֵ��ֵ�������Խ����Խ��
	private final String salt = "kalkg_*&=20)haionqwj18u!@#!4jad";
	
	//ע��Service����
	@Autowired	//@Resource
	private SeckillDao seckillDao;
	
	@Autowired	//@Resource
	private SuccessKilledDao successkilledDao;
	
	@Autowired
	private RedisDao redisDao;
				
	//��ѯ������ɱ��Ʒ
	public List<Seckill> getSeckillList() {		
		return seckillDao.queryAll(0, 5);
	}
	
	//����id��ѯ������Ʒ
	public Seckill getById(long seckillId) {		
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		// �Ż��㣺�����Ż�������ʱ�Ļ�����ά��һ����
		// 1.����redis

		// Seckill seckill = seckillDao.queryById(seckillId);
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){	
			//2.�������ݿ�
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){	//˵���鲻�������ɱ��Ʒ�ļ�¼
				return new Exposer(false,seckillId);
			}else {
				// 3.����redis
				redisDao.putSeckill(seckill);
			}
		}
		
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();	//ϵͳ��ǰʱ��
		//������ɱδ����������ɱ����
		if(startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()){
			return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		
		//��ɱ����,������ɱ��Ʒ��id���ø��ӿڼ��ܵ�md5
		String md5 = getMD5(seckillId);		
		return new Exposer(true,md5,seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	//��ɱ�Ƿ�ɹ����ɹ�:����棬������ϸ��ʧ��:�׳��쳣������ع�
	/*@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite"); //��ɱ���ݱ���д��
		}
		
		//ִ����ɱ�߼��������+���ӹ�����ϸ
		Date nowTime = new Date();
		
		try{
			//�����
			int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
			if(updateCount <= 0){
				//û�и��¿���¼��˵����ɱ����
				throw new SeckillCloseException("seckill is closed~");
			}else {
				//��������˿�棬��ɱ�ɹ���������ϸ
				int insertCount = successkilledDao.insertSuccessKilled(seckillId, userPhone);
				//������ϸ�Ƿ��ظ������û��Ƿ��ظ���ɱ
				if(insertCount <= 0){
					throw new RepeatKillException("seckill repeated!");
				}else {
					//��ɱ�ɹ����õ��ɹ��������ϸ��¼�������سɹ���ɱ����Ϣ
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
			//���������쳣ת��Ϊ�������쳣
			throw new SeckillException("seckill inner error : "+e.getMessage());
		}
	}		*/

	//�Ż�����ԭ����update������棩�ٽ���insert�����빺����ϸ���Ĳ���ĳɣ���insert��update
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite"); //��ɱ���ݱ���д��
		}
		
		//ִ����ɱ�߼��������+���ӹ�����ϸ
		Date nowTime = new Date();
		
		try{
			//���¿�棬��ɱ�ɹ���������ϸ
			int insertCount = successkilledDao.insertSuccessKilled(seckillId, userPhone);
			//������ϸ�Ƿ��ظ������û��Ƿ��ظ���ɱ
			if(insertCount <= 0){
				throw new RepeatKillException("seckill repeated!");
			}else {
				//����棬�ȵ���Ʒ����
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if(updateCount <= 0){
					//û�и��¿���¼��˵����ɱ����
					throw new SeckillCloseException("seckill is closed~");
				}else{
					//��ɱ�ɹ����õ��ɹ��������ϸ��¼�������سɹ���ɱ����Ϣ
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
			//���������쳣ת��Ϊ�������쳣
			throw new SeckillException("seckill inner error : "+e.getMessage());
		}
	}
	
//	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5){
		if(md5 == null || !md5.equals(getMD5(seckillId))){
//			throw new SeckillException(seckillId, SeckillStatEnum.DATE_REWRITE); //��ɱ���ݱ���д��
			throw new SeckillException("seckill data rewrite"); //��ɱ���ݱ���д��
		}
		Date killTime = new Date();
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("seckillId", seckillId);
	    map.put("phone", userPhone);
	    map.put("killTime", killTime);
	    map.put("result", null);
	    // ִ�д������,result������
	    seckillDao.killByProcedure(map);
	    // ��ȡresult
	    int result = MapUtils.getInteger(map, "result", -2);
	    if (result == 1) {
	        SuccessKilled successKilled = successkilledDao.queryByIdWithSeckill(seckillId, userPhone);
	        return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
	    } else {
	        return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
	    }
	}

}
