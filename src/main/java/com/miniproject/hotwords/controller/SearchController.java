package com.miniproject.hotwords.controller;

import com.miniproject.hotwords.common.Result;
import com.miniproject.hotwords.common.ResultUtil;
import com.miniproject.hotwords.service.IHotWordsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class SearchController {
    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    private IHotWordsService hotWordsService;

    /**
     * @Description:  搜索-添加热词
     * @return:
     * @Author: huanghy
     * @Date: 2020/9/8
     */
    @RequestMapping(value = "/search/{word}",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> searchWord(@PathVariable String word){
        hotWordsService.addHotWord(word);
        return ResultUtil.makeSuccess(word);
    }

    /** 
    * @Description: 获取热词
    * @Param:  
    * @return:  
    * @Author: huanghy
    * @Date: 2020/9/9 
    */ 
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public Result<Object> getHotWords(String word){
        List<Object> hotwods= hotWordsService.getHotWord();
        return ResultUtil.makeSuccess(hotwods);
    }
}
