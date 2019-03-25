package com.akon.seckill.dto;

import com.akon.seckill.entity.SuccessKilled;
import com.akon.seckill.enums.SeckillStatEnum;

/**
 * ��װִ����ɱ��Ľ�����Ƿ���ɱ�ɹ�
 * @author lenovo
 *
 */
public class SeckillExecution {
	private long seckillId;
	
	//��ɱִ�н����״̬
	private int state;
	
	//״̬�����ı�ʶ
	private String stateInfo;
	
	//����ɱ�ɹ�ʱ����Ҫ������ɱ�ɹ��Ķ����ȥ
	private SuccessKilled successkilled;

	//��ɱ�ɹ�����������Ϣ
	public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successkilled) {
		super();
		this.seckillId = seckillId;
		this.state = statEnum.getState();
		this.stateInfo = statEnum.getInfo();
		this.successkilled = successkilled;
	}
	
	//��ɱʧ��
	public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
		super();
		this.seckillId = seckillId;
		this.state = statEnum.getState();
		this.stateInfo = statEnum.getInfo();
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public SuccessKilled getSuccesskilled() {
		return successkilled;
	}

	public void setSuccesskilled(SuccessKilled successkilled) {
		this.successkilled = successkilled;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SeckillExecution [seckillId=" + seckillId + ", state=" + state + ", stateInfo=" + stateInfo
				+ ", successkilled=" + successkilled + "]";
	}
	
}
