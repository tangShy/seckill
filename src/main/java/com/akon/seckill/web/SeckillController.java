package com.akon.seckill.web;

import com.akon.seckill.dto.Exposer;
import com.akon.seckill.dto.SeckillExecution;
import com.akon.seckill.dto.SeckillResult;
import com.akon.seckill.entity.Seckill;
import com.akon.seckill.enums.SeckillStatEnum;
import com.akon.seckill.exception.RepeatKillException;
import com.akon.seckill.exception.SeckillCloseException;
import com.akon.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")	//url:???/???/{}/???
public class SeckillController {
	
	@Autowired
	private SeckillService seckillService;
	
	
	@RequestMapping(value = "/list",method = RequestMethod.GET)
	public String list(Model model){
		//list.jsp+mode=ModelAndView
		//????§Ò??
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		System.out.println("list==="+list);
		return "list";	//		?????/WEB_INF/jsp/"list".jsp
	}
	
	@RequestMapping(value="/{seckillId}/detail",method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model){
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
	
	//ajax,json?????????????
	@RequestMapping(value = "/{seckillId}/exposer",
					method = RequestMethod.GET,
					produces = {"application/json;chaeset=utf-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId")Long seckillId){
		SeckillResult<Exposer> result;
		
		try{
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true,exposer);
		}catch(Exception e){
			e.printStackTrace();
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId")Long seckillId,
												@PathVariable("md5")String md5,
												@CookieValue(value= "userPhone",required=false)Long userPhone){
		if(userPhone == null){
			return new SeckillResult<SeckillExecution>(false,"¦Ä???");
		}
		
		try{
//			SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
			//????????????›¥????
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExecution>(true,execution);
		}catch(RepeatKillException e1){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true,execution);
		}catch(SeckillCloseException e2){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.END);
			return new SeckillResult<SeckillExecution>(true,execution);
		}catch(Exception e){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true,execution);
		}

	}
	
	//????????
	@RequestMapping(value = "/time/now",method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date now = new Date();
		return new SeckillResult<Long>(true,now.getTime());
	}
	
}
