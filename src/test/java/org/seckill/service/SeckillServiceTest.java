package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = this.seckillService.getSeckillList();
        this.logger.info("list={}", list);
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = this.seckillService.getById(id);
        this.logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long id = 1000;
        Exposer exposer = this.seckillService.exportSeckillUrl(id);
        this.logger.info("exposer={}", exposer);
    }

    @Test
    public void seckillLogic() throws Exception {
        long id = 1000;
        Exposer exposer = this.seckillService.exportSeckillUrl(id);
        this.logger.info("exposer={}", exposer);
        if(exposer.isExposed()) {
            long phone = 18827389685L;
            String md5 = "86ec11fad34f79b268ae52b9153a4cbb";
            try{
                SeckillExecution execution = this.seckillService.executeSeckill(id, phone, md5);
                this.logger.info("result={}", execution);
            }catch (RepeatKillException e){
                this.logger.error(e.getMessage(), e);
            }catch (SeckillCloseException e){
                this.logger.error(e.getMessage());
            }
        }else{
            // 秒杀未开启
            this.logger.warn("秒杀未开启");
        }
    }

    @Test
    public void executeSeckill() {
        long id = 1000;
        long phone = 18827389685L;
        String md5 = "86ec11fad34f79b268ae52b9153a4cbb";
        try{
            SeckillExecution execution = this.seckillService.executeSeckill(id, phone, md5);
            this.logger.info("result={}", execution);
        }catch (RepeatKillException e){
            this.logger.error(e.getMessage(), e);
        }catch (SeckillCloseException e){
            this.logger.error(e.getMessage());
        }
    }
}