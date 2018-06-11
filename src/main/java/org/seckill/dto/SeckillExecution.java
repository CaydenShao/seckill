package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;

/**
 * 封装秒杀执行后结果
 * */
public class SeckillExecution {

    private long seckillId;

    // 秒杀执行结果状态
    private int state;

    // 秒杀表示
    private String stateInfo;

    private SuccessKilled successKilled;

    public SeckillExecution(long seckillId, SeckillStateEnum stateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public SeckillExecution(long seckillId, SeckillStateEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateInfo;
    }
}
