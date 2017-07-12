package com.weixing.print.manager;

/**
 * Created by wb-cuiweixing on 2015/10/8.
 */
public class ProductItem {
    private String name;
    private String num;
    private String price;
    private String sum;
    private String extraStr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getExtraStr() {
        return extraStr;
    }

    public void setExtraStr(String extraStr) {
        this.extraStr = extraStr;
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "name='" + name + '\'' +
                ", num='" + num + '\'' +
                ", price='" + price + '\'' +
                ", sum='" + sum + '\'' +
                ", extraStr='" + extraStr + '\'' +
                '}';
    }
}
