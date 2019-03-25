package com.akon.seckill.dao;

import org.apache.ibatis.annotations.Param;

import com.akon.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	
	/**
	 * ���빺����ϸ�����Թ����ظ�
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	int insertSuccessKilled(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);
	
	/**
	 * ����id��ѯSuccessKilled��Я����ɱ��Ʒ����ʵ��
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone") long userPhone);
	
}
