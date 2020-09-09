package com.miniproject.hotwords.service.impl;

import com.miniproject.hotwords.common.Constants;
import com.miniproject.hotwords.model.Hotwords;
import com.miniproject.hotwords.model.Param;
import com.miniproject.hotwords.service.IHotWordsService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service
public class HotWordsServiceImpl implements IHotWordsService {
    private static final Logger LOG = LoggerFactory.getLogger(HotWordsServiceImpl.class);

    @Resource
    private JdbcTemplate jt;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private int HOTWORDS_NUMBER = Constants.DEFAULT_HOTWORDS_NUMBER;//默认10个
    private Map<String,String> adminHotWords = null;//用来存放手动指定的热词 顺序：热词 格式
    private int adminHotWordsNUM = 0;

    @Override
    public void getHotWordsNumber() {//启动的时候，从数据库获取配置的热词个数
        String sql = "select pvalue from sys_param where pname=?";
        this.HOTWORDS_NUMBER = jt.queryForObject(sql,new Object[]{Constants.HOTWORDS_NUMBER_KEY},Integer.class);
    }
    /**
    * @Description:  获取热词刷新频率
    * @Param:  isCron，true：返回cron表达式 false：只返回配置的小时数
    * @return:  String
    * @Author: huanghy
    * @Date: 2020/9/7
    */
    @Override
    public String getCron(boolean isCron){
        String sql = "select pvalue from sys_param where pname=?";//获取定时器动态配置
        String getCron=jt.queryForObject(sql,new Object[]{Constants.CRON_PARAM_KEY},String.class);
        LOG.info("Cron in DB:{}",getCron);
        if(StringUtils.isBlank(getCron)){//如果数据库没配置，或者查询失败，默认返回12小时执行一次
            getCron = Constants.DEFAULT_CRON;
            LOG.error("Get Cron is Fail");
        }
        if(isCron){//如果要获取cron表达式，则组装好返回
            String cronFormat = "0 0 0/%s * * ?";
            //String cronFormat = "0 0/%s * * * ?";
            //0 0/5 * * * ?
            getCron = String.format(cronFormat,new Object[]{getCron});
        }
        LOG.info("Cron DONE:{}",getCron);
        return getCron;
    }
    /**
    * @Description: 获取所有参数，因为目前能确定只有两个，为了快速开发，未做分页查询，正常要做分页查询
    * @return:  List<Map<String,Object>>
    * @Author: huanghy
    * @Date: 2020/9/7
    */
    @Override
    public List<Map<String,Object>> getParamList(){
        String sql = "select * from sys_param";
        List<Map<String,Object>> params = jt.queryForList(sql);
        return params;
    }
    /**
    * @Description:  通过id获取单个param参数
    * @Param:  id
    * @return:  Map<String,Object>
    * @Author: huanghy
    * @Date: 2020/9/7
    */
    @Override
    public Map<String,Object> getParam(String id){
        String sql = "select * from sys_param where id=?";
        Map<String,Object> param = jt.queryForMap(sql,new Object[]{id});
        return param;
    }
    /**
    * @Description:  对单条参数做修改操作
    * @Param:  id:主键 pvalue:具体值
    * @return:  boolean
    * @Author: huanghy
    * @Date: 2020/9/7
    */
    @Override
    @Transactional
    public boolean updateParam(Param param){
        String sql = "select pname from sys_param where id=?";
        List<String> params = jt.queryForList(sql,String.class,new Object[]{param.getId()});//不然是可以用count来判断数据是否存在，因为底下要用到pname来判断是否修改了热词个数
        if(params!=null && params.size()>0){
            sql = "update sys_param set pvalue=?,pdetail=? where id=?";
            jt.update(sql,new Object[]{param.getPvalue(),param.getPdetail(),param.getId()});
            String pname = params.get(0);
            if(pname.equals(Constants.HOTWORDS_NUMBER_KEY)){//如果修改的是获取热词个数，则修改属性
                this.HOTWORDS_NUMBER=Integer.valueOf(param.getPvalue());
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * @Description: 获取自定义热词，可传status过滤，为了快速开发，未做分页查询，正常要做分页查询
     * @return:  List<Map<String,Object>>
     * @Author: huanghy
     * @Date: 2020/9/7
     */
    @Override
    public List<Map<String,Object>> getAdminHotwords(String status){
        StringBuffer sql = new StringBuffer();
        sql.append("select id,seat,word,status,DATE_FORMAT(firsttime,'%Y-%c-%d %h:%i:%s') as firsttime,DATE_FORMAT(lasttime,'%Y-%c-%d %h:%i:%s') as lasttime from sys_hotwords ");
        List<Map<String,Object>> hotwords;
        if(StringUtils.isNotBlank(status)){
            sql.append("where status=? ");
            sql.append("order by seat asc");
            hotwords= jt.queryForList(sql.toString(),new Object[]{status});
        }else{
            sql.append("order by seat asc");
            hotwords = jt.queryForList(sql.toString());
        }
        return hotwords;
    }
    /**
    * @Description:  新增一个热词
    * @Param:  word：热词 seat：排位
    * @Author: huanghy
    * @Date: 2020/9/8
    */
    @Transactional
    @Override
    public boolean addAdminHotwords(String word,int seat){
        StringBuffer sql = new StringBuffer();
        sql.append("select count(id) from sys_hotwords where seat=?");
        int flag = jt.queryForObject(sql.toString(),new Object[]{seat},Integer.class);
        if(flag>0){//不允许seat重复
            return false;
        }
        sql.setLength(0);
        sql.append("INSERT INTO sys_hotwords VALUES (REPLACE(UUID(),\"-\",\"\"),?, ?,'1',now(),now())");
        jt.update(sql.toString(),new Object[]{word,seat});
        setAdminHotwords();
        return true;
    }
    /**
    * @Description: 修改一个热词属性
    * @Param:  ID：主键；word：热词；seat：排位顺序；status：状态
    * @return:
    * @Author: huanghy
    * @Date: 2020/9/8
    */
    @Transactional
    @Override
    public boolean updateAdminHotwords(Hotwords hotwords){
        StringBuffer sql = new StringBuffer();
        if(StringUtils.isNotBlank(hotwords.getSeat())){
            sql.append("select count(id) from sys_hotwords where seat=? and id !=?");
            int seat = jt.queryForObject(sql.toString(),new Object[]{hotwords.getSeat(),hotwords.getId()},Integer.class);
            if(seat>0){//不允许seat重复
                return false;
            }
        }
        List<String> param = new ArrayList<>();
        sql.setLength(0);
        sql.append("update sys_hotwords set lasttime=now()");
        if(StringUtils.isNotBlank(hotwords.getWord())){
            sql.append(",word=?");
            param.add(hotwords.getWord());
        }
        if(StringUtils.isNotBlank(hotwords.getStatus())){
            sql.append(",status=?");
            param.add(hotwords.getStatus());
        }
        if(StringUtils.isNotBlank(hotwords.getSeat())){
            sql.append(",seat=?");
            param.add(hotwords.getSeat());
        }
        sql.append(" where id=?");
        param.add(hotwords.getId());
        jt.update(sql.toString(),param.toArray());
        setAdminHotwords();
        return true;
    }
    /**
    * @Description:  把当前有效，并且排名小于热词个数的手动热词
    * @Param:
    * @return:
    * @Author: huanghy
    * @Date: 2020/9/9
    */
    @Override
    public void setAdminHotwords(){
        String sql = "select * from sys_hotwords where seat<? and status=1 order by seat asc";//把当前有效，并且排名小于热词个数的手动热词查出来
        List<Map<String,Object>>  wordsList= jt.queryForList(sql,new Object[]{HOTWORDS_NUMBER});
        this.adminHotWords = new HashMap<>();
        this.adminHotWordsNUM = wordsList.size();
        for (Map<String,Object> temp : wordsList){
            this.adminHotWords.put(String.valueOf(Integer.valueOf(temp.get("seat").toString())-1),temp.get("word").toString());
        }
    }
    /**
    * @Description:  新增热词到redis
    * @Param:
    * @return:
    * @Author: huanghy
    * @Date: 2020/9/9
    */
    @Override
    public void addHotWord(String hotWord){
        if (StringUtils.isBlank(hotWord))
            return;
        redisTemplate.opsForZSet().incrementScore("hotWord", hotWord, 1); // 加入排序zset
    }
    /**
    * @Description: 从redis获取热词统计
    * @return: {"code":200,"msg":"success","data":[["我是一个词","我是12","你是谁","你好","我是搜索","我是9","我是8","我是7","我是6","我是5"]]}
    * @Author: huanghy
    * @Date: 2020/9/9
    */
    @Override
    public List<Object> getHotWord(){
        List<Object> hotWords = redisTemplate.opsForList()
                .range("hotWordsList", 0, HOTWORDS_NUMBER);//取出除手动设置热词的实时热词
        return hotWords;
    }

    /**
    * @Description: 统计热词，生成热词缓存 hotWordsList
    * @Param:  clear:是否清除当前的搜索次数统计
    * @return:
    * @Author: huanghy
    * @Date: 2020/9/9
    */
    @Override
    public void createHotWord(boolean clear){
        Set<Object> sets = redisTemplate.opsForZSet()
                .reverseRangeByScore("hotWord", 0, Integer.MAX_VALUE, 0, HOTWORDS_NUMBER-adminHotWordsNUM);//取出除手动设置热词的实时热词
        List <String> hotwordList = new ArrayList<>();
        Map<String,String> adminHotWordsFlag = this.adminHotWords;
        for(Object ob : sets){//放ArrayList里方便插入手动热词
            hotwordList.add((String) ob);
        }
        if(hotwordList.size()<HOTWORDS_NUMBER){//如果实时搜索词数少于需要展示的，则从手动展示的词汇填充
            if(adminHotWordsNUM!=0){//如果有手动热词
                for (int i=0;i<HOTWORDS_NUMBER;i++){
                    if(adminHotWordsFlag.get(String.valueOf(i))!=null){//如果当前位置有设置手动热词，则插入
                        if(i>hotwordList.size()){
                            hotwordList.add(adminHotWordsFlag.get(String.valueOf(i)));
                        }else{
                            hotwordList.add(i,adminHotWordsFlag.get(String.valueOf(i)));
                        }
                    }
                }
            }
        }else{
            if(adminHotWordsNUM!=0){//如果有手动热词
                for (int i=0;i<hotwordList.size();i++){
                    if(adminHotWordsFlag.get(String.valueOf(i))!=null){//如果当前位置有设置手动热词，则插入
                        hotwordList.add(i,adminHotWordsFlag.get(String.valueOf(i)));
                    }
                }
            }
        }
        redisTemplate.opsForList().getOperations().delete("hotWordsList");//清空热词列表
        redisTemplate.opsForList().leftPushAll("hotWordsList",hotwordList);//放入最新统计的热词列表
        if(clear){
            redisTemplate.opsForZSet().getOperations().delete("hotWord");//清空当前的搜索记录，重新统计
        }
    }
    @Test
    public void test(){
        List<String> hotwordList = new ArrayList<>();
        hotwordList.add("A");
        hotwordList.add("B");
        hotwordList.add("C");
        hotwordList.add("F");
        Map<String,Object> adminHotWordsFlag = new HashMap<>();
        adminHotWordsFlag.put("3","D");
        adminHotWordsFlag.put("4","E");
        for (int i=0;i<hotwordList.size();i++){
            if(adminHotWordsFlag.get(String.valueOf(i))!=null){//如果当前位置有设置手动热词，则插入
                hotwordList.add(i,adminHotWordsFlag.get(String.valueOf(i)).toString());
            }
        }
        System.out.println(hotwordList.toString());
    }
}
