package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SeckillDao seckillDao;

    private SuccessKilledDao successKilledDao;

    private final String salt = "q5qwt345rewtaera3w5344352";

    @Override
    public List<Seckill> getSeckillList(){
        return this.seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId){
        return this.seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId){
        Seckill seckill = this.seckillDao.queryById(seckillId);
        if(seckill == null){
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        // 系统当前时间
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 转化特定字符串的过程，不可逆
        String md5 = this.getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/" + this.salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException{
        if(md5 == null || !md5.equals(this.getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        try {
            // 执行秒杀逻辑：减库存 + 记录购买行为
            Date nowTime = new Date();
            int updateCount = this.seckillDao.reduceNumber(seckillId, nowTime);
            if(updateCount <= 0){
                // 没有更新到记录，秒杀结束
                throw new SeckillCloseException("seckill is closed");
            }else{
                // 记录购买行为
                int insertCount = this.successKilledDao.insertSuccessKilled(seckillId, userPhone);
                // 唯一：seckillId, userPhone
                if(insertCount <= 0){
                    // 重复秒杀
                    throw new RepeatKillException("seckill repeated");
                }else{
                    // 秒杀成功
                    SuccessKilled successKilled = this.successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            this.logger.error(e.getMessage(), e);
            // 所有编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error" + e.getMessage());
        }

    }

}
