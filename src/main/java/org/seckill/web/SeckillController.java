package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller // @Service @Component
@RequestMapping("/seckill") // url:/模块/资源/{id}/细分 /seckill/list
public class SeckillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(name = "/list", method = RequestMethod.GET)
    public String list(Model model){
        // 获取列表项
        List<Seckill> list = this.seckillService.getSeckillList();
        model.addAttribute("list", list);
        // list.jsp + model = ModelAndView
        return "list"; // /WEB-INF/jsp/list.jsp
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") long seckillId, Model model) {
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = )
    @ResponseBody
    public SeckillResult<Exposer> exposer(long seckillId){
        SeckillResult<Exposer> result;
        try{
            Exposer exposer = this.seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer, null);
        }catch (Exception e){
            this.logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, null, e.getMessage())
        }
        return result;
    }

}
