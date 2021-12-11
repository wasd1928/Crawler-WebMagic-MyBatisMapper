package com.xuchen.pipeline;

import com.xuchen.mapper.CommodityMapper;
import com.xuchen.pojo.Commodity;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// This pipeline is specialized for commodity operation
public class SqlPipeline implements Pipeline {
    CommodityMapper commodityMapper = null;
    int processTimes = 0;

    public int getProcessTimes() {
        return processTimes;
    }

    public SqlPipeline(CommodityMapper commodityMapper){
        this.commodityMapper = commodityMapper;
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        Map<String, Object> result = resultItems.getAll();

        String name = (String)result.get("name");
        String price = (String)result.get("price");
        String condition = (String)result.get("condition");
        Date searchTime = new Date(new java.util.Date().getTime());
        String stock = (String)result.get("stock");
        String originalPrice = (String)result.get("originalPrice");
        String soldNumber = (String)result.get("soldNumber");
        String shipping = (String)result.get("shipping");
        String delivery = (String)result.get("delivery");
        String url = (String)result.get("url");
        Commodity newCommodity = new Commodity(name);
        newCommodity.setPrice(price);
        newCommodity.setCondition(condition);
        newCommodity.setSearchTime(searchTime);
        newCommodity.setStock(stock);
        newCommodity.setOriginalPrice(originalPrice);
        newCommodity.setSoldNumber(soldNumber);
        newCommodity.setShipping(shipping);
        newCommodity.setDelivery(delivery);
        newCommodity.setUrl(url);
        commodityMapper.addOne(newCommodity);
        processTimes += 1;
    }
}
