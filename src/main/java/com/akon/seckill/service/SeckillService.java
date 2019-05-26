package com.akon.seckill.service;

import com.akon.seckill.dto.Exposer;
import com.akon.seckill.dto.SeckillExecution;
import com.akon.seckill.entity.Seckill;
import com.akon.seckill.exception.RepeatKillException;
import com.akon.seckill.exception.SeckillCloseException;
import com.akon.seckill.exception.SeckillException;

import java.util.List;

/**
 * ҵ��ӿڣ�վ�ڡ�ʹ���ߡ��Ƕ���ƽӿ�
 * �������棺�����������ȣ��������������ͣ�return����/�쳣��
 * 1.�����������ȣ����������Ҫ�ǳ����2.������ҪԽ����Խ�� 3.��������(return
 * ����һ��Ҫ�Ѻ�/����return�쳣������������쳣)
 * @author lenovo
 *
 */
public interface SeckillService {
	
	/**
	 * ��ѯ������ɱ��¼
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * ��ѯ������ɱ��¼
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	
	/**
	 * ��ɱ����ʱ�����ɱ�ӿڵ�ַ��
	 * �������ϵͳʱ�����ɱʱ��
	 * @param seckillId
	 * @return ���ݶ�Ӧ��״̬���ض�Ӧ��״̬ʵ��
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * ִ����ɱ�������п���ʧ�ܣ��п��ܳɹ�������Ҫ�׳�����������쳣
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return ���ݲ�ͬ�Ľ�����ز�ͬ��ʵ����Ϣ
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5) throws SeckillException,
    RepeatKillException, SeckillCloseException;
	
	/**
	 * ���ô洢������ִ����ɱ����������Ҫ�׳��쳣
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
}
	
	
	
	
	
