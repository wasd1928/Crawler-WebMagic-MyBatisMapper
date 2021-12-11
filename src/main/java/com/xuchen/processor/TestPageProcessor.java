package com.xuchen.processor;

import com.xuchen.pipeline.ListPipeline;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    @Override
    public void process(Page page) {
        Document doc = Jsoup.parse(page.getHtml().toString());
        page.putField("name", doc.select("h1.it-ttl").first().text());
        page.putField("price", doc.select("span#prcIsum").first().text());
        page.putField("condition",doc.select("div#vi-itm-cond").first().text());
        page.putField("shipping",doc.select("span strong.sh_gr_bld_new").first().text());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {


        Spider.create(new TestPageProcessor())
                .addUrl("https://www.ebay.com/sch/i.html?_from=R40&_trksid=p2380057.m570.l1313&_nkw=monitor&_sacat=0")
                .addPipeline(new ListPipeline())
                .run();
    }
}
