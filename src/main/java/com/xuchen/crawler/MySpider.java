package com.xuchen.crawler;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
//import javax.swing.JFrame;


import java.util.concurrent.atomic.AtomicLong;

public class MySpider extends Spider {

    public MySpider (PageProcessor pageProcessor){
        super(pageProcessor);
    }


}
