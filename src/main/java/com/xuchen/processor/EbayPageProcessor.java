package com.xuchen.processor;

import com.xuchen.pipeline.ListPipeline;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashSet;
import java.util.List;

public class EbayPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(2).setSleepTime(500);
//    private Site site = Site.me();
    HashSet<String> namePool = new HashSet<String>();
    String name = null;
    @Override
    public void process(Page page) {
        // In order to avoid visiting same pages
        // Add more requests
        List<String> requests = page.getHtml().links().regex("https://www.ebay.com/itm/.+").all();
//        for(int i =0; i<5; i++){
//            System.out.println(requests.get(i));
//        }
        page.addTargetRequests(requests);

        Document doc = Jsoup.parse(page.getHtml().toString());
        page.putField("url", page.getUrl().regex("https://www.ebay.com/itm/\\w+").toString());
        try {
            name = doc.select("h1#itemTitle").first().text().substring(14);
            page.putField("name", name);
            page.putField("price", doc.select("span#prcIsum").first().text());
            page.putField("condition",doc.select("div#vi-itm-cond").first().text());
            page.putField("stock", doc.select("span#qtySubTxt").first().text());
            page.putField("soldNumber", doc.select("#why2buy > div > div:nth-child(2) > span").first().text());
            page.putField("shipping",doc.select("#shSummary > span:nth-child(1) > strong").first().text());
            page.putField("delivery",doc.select("#delSummary > div > span.vi-acc-del-range > b").first().text());
            // Sometimes there is no original price.
            page.putField("originalPrice", doc.select("span.vi-originalPrice").first().text());



        } catch (Exception e) {
            System.out.println("There is some error parsing the content. It's fine to continue.");

        }
        if(page.getResultItems().get("name")==null || page.getResultItems().get("price")==null) {
            page.setSkip(true);
        }
        else if(ifVisited(name)){
            page.setSkip(true);
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    private boolean ifVisited(String name) {
        if(namePool.contains(name)){
            return true;
        }
        namePool.add(name);
        return false;
    }
}
