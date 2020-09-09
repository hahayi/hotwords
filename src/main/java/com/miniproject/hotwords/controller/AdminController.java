package com.miniproject.hotwords.controller;

import com.miniproject.hotwords.common.Result;
import com.miniproject.hotwords.common.ResultUtil;
import com.miniproject.hotwords.model.Hotwords;
import com.miniproject.hotwords.model.Param;
import com.miniproject.hotwords.service.IHotWordsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AdminController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private IHotWordsService hotWordsService;
    /**
    * @Description:  获取参数列表/admin/params
    * @return:  {"code":200,"msg":"success","data":[{"id":"a31800e0f10d11eabb776c4f4a84b70b","pname":"hot_words_number","pvalue":"10"},{"id":"b93a08d6f10e11eabb776c4f4a84b70b","pname":"cron","pvalue":"12"}]}
    * @Author: huanghy
    * @Date: 2020/9/7
    */
    @RequestMapping(value = "/admin/params",method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String,Object>>> params(){
        List<Map<String,Object>> data = hotWordsService.getParamList();
        return ResultUtil.makeSuccess(data);
    }

    /**
    * @Description: 获取单个参数
    * @Param:  paramId参数id
    * @return:  {"code":200,"msg":"success","data":{"id":"b93a08d6f10e11eabb776c4f4a84b70b","pname":"cron","pvalue":"12"}}
    * @Author: huanghy
    * @Date: 2020/9/7
    */
    @RequestMapping(value = "/admin/params/{paramId}",method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> param(@PathVariable String paramId){
        Map<String,Object> data = hotWordsService.getParam(paramId);
        return ResultUtil.makeSuccess(data);
    }
    /**
    * @Description: 修改相关参数
    * @Param: paramId:主键 pvalue：设置的数值
    * @return:{"code":200,"msg":"success","data":null}
    * @Author: huanghy
    * @Date: 2020/9/7
    */
    @RequestMapping(value = "/admin/params/{paramId}",method = RequestMethod.PUT)
    @ResponseBody
    public Result<Map<String,Object>> updateParam(@PathVariable String paramId, Param param){
        if(StringUtils.isBlank(param.getPvalue())){//值不能为空
            return ResultUtil.makeErr("参数错误");
        }
        param.setId(paramId);
        boolean isSuccess = hotWordsService.updateParam(param);
        if(isSuccess){
            return ResultUtil.makeSuccess();
        }else{
            return ResultUtil.makeErr("修改失败");
        }
    }
    /**
    * @Description:  获取自定义热词，可传status过滤
    * @Param:
    * @return:
    * @Author: huanghy
    * @Date: 2020/9/8
    */
    @RequestMapping(value = {"/admin/hotwords/","admin/hotwords/{status}"},method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String,Object>>> hotWords(@PathVariable(required = false) String status){
        List<Map<String,Object>> data = hotWordsService.getAdminHotwords(status);
        return ResultUtil.makeSuccess(data);
    }
    /**
    * @Description:  新增一个手动热词
    * @Param:  word：热词；seat：排位
    * @return:  {"code":200,"msg":"success","data":null}
    * @Author: huanghy
    * @Date: 2020/9/8
    */
    @RequestMapping(value = {"/admin/hotwords"},method = RequestMethod.POST)
    @ResponseBody
    public Result<List<Map<String,Object>>> addHotWords(@RequestParam(value = "word") String word,@RequestParam(value = "seat") int seat){
        if(StringUtils.isBlank(word)||0==seat){
            return ResultUtil.makeErr("参数错误");
        }
        boolean flag = hotWordsService.addAdminHotwords(word,seat);
        if(!flag){
            return ResultUtil.makeErr("热词顺序不能重复，请修改后再试！");
        }
        return ResultUtil.makeSuccess();
    }

    /**
     * @Description:  修改一个手动热词
     * @Param:  ID：主键；word：热词；seat：排位顺序；status：状态
     * @return:  {"code":200,"msg":"success","data":null}
     * @Author: huanghy
     * @Date: 2020/9/8
     */
    @RequestMapping(value = {"/admin/hotwords"},method = RequestMethod.PUT)
    @ResponseBody
    public Result<List<Map<String,Object>>> updateHotWords(Hotwords hotwords){
        if(StringUtils.isBlank(hotwords.getId())){
            return ResultUtil.makeErr("参数错误");
        }
        boolean flag = hotWordsService.updateAdminHotwords(hotwords);
        if(!flag){
            return ResultUtil.makeErr("热词顺序不能重复，请修改后再试！");
        }else{
            return ResultUtil.makeSuccess();
        }
    }
    /**
     * @Description:  手动生成当前热词
     * @return:  {"code":200,"msg":"success","data":null}
     * @Author: huanghy
     * @Date: 2020/9/8
     */
    @RequestMapping(value = {"/admin/createHotwords"},method = RequestMethod.GET)
    @ResponseBody
    public Result<String> createHotwords(){
        hotWordsService.createHotWord(false);
        return ResultUtil.makeSuccess();
    }
}
