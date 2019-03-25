package com.akon.seckill.dto;

import com.akon.seckill.entity.SuccessKilled;
import com.akon.seckill.enums.SeckillStatEnum;

/**
 * 封装执行秒杀后的结果：是否秒杀成功
 * @author lenovo
 *
 */
public class SeckillExecution {
	private long seckillId;
	
	//秒杀执行结果的状态
	private int state;
	
	//状态的明文标识
	private String stateInfo;
	
	//当秒杀成功时，需要传递秒杀成功的对象回去
	private SuccessKilled successkilled;

	//秒杀成功返回所有信息
	public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successkilled) {
		super();
		this.seckillId = seckillId;
		this.state = statEnum.getState();
		this.stateInfo = statEnum.getInfo();
		this.successkilled = successkilled;
	}
	
	//秒杀失败
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
