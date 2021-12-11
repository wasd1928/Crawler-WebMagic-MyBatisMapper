package com.xuchen.pojo;
import java.sql.Date;

public class Commodity {
    private String name;
    private String price;
    private String condition;
    private Date searchTime;
    private String stock;
    private String originalPrice;
    private String soldNumber;
    private String shipping;
    private String delivery;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Commodity(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Date getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Date searchTime) {
        this.searchTime = searchTime;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getSoldNumber() {
        return soldNumber;
    }

    public void setSoldNumber(String soldNumber) {
        this.soldNumber = soldNumber;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    @Override
    public String toString() {
        return  "name='" + name + '\'' +
                "\nprice='" + price + '\'' +
                "\ncondition='" + condition + '\'' +
                ", searchTime=" + searchTime +
                ", stock='" + stock + '\'' +
                ", originalPrice='" + originalPrice + '\'' +
                ", soldNumber='" + soldNumber + '\'' +
                ", shipping='" + shipping + '\'' +
                ", delivery='" + delivery + '\'' +
                "\nurl='" + url + '\'';
    }
}
