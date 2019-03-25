package com.akon.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.akon.seckill.entity.Seckill;

public interface SeckillDao {
	
	/**
	 * �����
	 * 
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	/*
	 * ���������β�����������������ʱ����Ҫ�ڲ���ǰ����@Param����������ϸ�ע�����֮���
	 * ��������ʱ��������Sun�ṩ��Ĭ�ϱ�������javac���ڱ�����Class�ļ��лᶪʧ����
	 * ��ʵ�����ƣ������е��βλ����������arg0��arg1�ȣ���ֻ��һ������ʱ������ν������
	 * ��������������������ʱ�����뷽���Ĳ����ͻ��Ҳ�����Ӧ���βΡ���ΪJava�βε����⣬����
	 * �ڶ���������Ͳ���ʱ��Ҫ��@Paramע�����ֿ�����
	 */
	int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);
	
	/**
	 * ����id��ѯ��ɱ��Ʒ
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);
	
	/**
	 * ����ƫ������ѯ��ɱ��Ʒ�б�
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(@Param("offset")int offset,@Param("limit")int limit);
}
