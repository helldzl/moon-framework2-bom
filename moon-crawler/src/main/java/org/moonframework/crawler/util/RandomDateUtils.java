package org.moonframework.crawler.util;

import org.moonframework.crawler.parse.PageRankUrl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by LiuKai on 2017/9/27.
 */
public class RandomDateUtils {
    private String beginDate;
    private String endDate;

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    //输入 开始日期 和 结束日期
   public static Date getRandomDateTime(String beginDate,String endDate){
       return randomDate(beginDate,endDate);
   }
    //输入开始日期 （结束日期是当天）
    public static Date getRandomDateTime(String beginDate){
        return getRandomDateTime(beginDate,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
    //米饭从2016-09-01 到现在    时间随机
    public static Date getRandomDateTime(){
        return getRandomDateTime("2016-09-01");
    }

    public static  String getHMS(){
        //一天秒数是86400s
        Random random = new Random();
        Integer totalSeconds= random.nextInt(86400*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(totalSeconds);
        return sdf.format(date);
    }


    private static Date randomDate(String beginDate, String endDate){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);

            if(start.getTime() >= end.getTime()){
                return null;
            }

            long date = random(start.getTime(),end.getTime());

            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static long random(long begin,long end){
        long rtn = begin + (long)(Math.random() * (end - begin));
        if(rtn == begin || rtn == end){
            return random(begin,end);
        }
        return rtn;
    }
}
