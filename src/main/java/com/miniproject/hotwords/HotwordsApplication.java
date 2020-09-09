package com.miniproject.hotwords;

import com.miniproject.hotwords.service.IHotWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotwordsApplication implements CommandLineRunner {

    @Autowired
    private IHotWordsService hotWordsService;

    public static void main(String[] args) {
        SpringApplication.run(HotwordsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        hotWordsService.getHotWordsNumber();//从数据库把数值的热词个数参数查出来
        hotWordsService.setAdminHotwords();
    }
}
