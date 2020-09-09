package com.miniproject.hotwords.service;

import com.miniproject.hotwords.model.Hotwords;
import com.miniproject.hotwords.model.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IHotWordsService {
    void getHotWordsNumber();
    String getCron(boolean isCron);
    List<Map<String,Object>> getParamList();
    Map<String,Object> getParam(String id);
    boolean updateParam(Param param);
    List<Map<String,Object>> getAdminHotwords(String status);
    boolean addAdminHotwords(String word,int seat);
    boolean updateAdminHotwords(Hotwords hotwords);
    void addHotWord(String hotWord);
    //Set<Object> getHotWord();
    void setAdminHotwords();
    void createHotWord(boolean clear);
    List<Object> getHotWord();
}
