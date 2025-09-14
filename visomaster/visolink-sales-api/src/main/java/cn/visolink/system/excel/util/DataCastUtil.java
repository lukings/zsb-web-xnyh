package cn.visolink.system.excel.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.06
 */
public class DataCastUtil {

    /**
     *
     * 当前时间加N小时
     * */
    public String addHourDate(String hour){
        long currentTime = System.currentTimeMillis() + Integer.parseInt(hour) * 60 * 60 * 1000;
        Date date = new Date(currentTime);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = "";
        nowTime = df.format(date);
        return nowTime;
    }
    /**
     *
     * 当前时间加N天
     * */
    public String addDayDate(String day){
        long currentTime = System.currentTimeMillis() + Integer.parseInt(day) * 24 * 60 * 60 * 1000;
        Date date = new Date(currentTime);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = "";
        nowTime = df.format(date);
        return nowTime;
    }



    /**
     *
     * 指定时间加N天 并跟当前时间判断早晚
     * */
    public int isPass(int day, String oldDay) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currdate = format.parse(oldDay);
        Calendar ca = Calendar.getInstance();
        ca.setTime(currdate);
        ca.add(Calendar.DATE, day);// num为增加的天数，可以改变的
        currdate = ca.getTime();
        String enddate = format.format(currdate);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String overTime = "";
        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return nowTime.compareTo(enddate);
    }

    /**
     * 指定时间增加N天
     * */
    public String excelAddDay(int day, String oldDay) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currdate = format.parse(oldDay);
        Calendar ca = Calendar.getInstance();
        ca.setTime(currdate);
        ca.add(Calendar.DATE, day);// num为增加的天数，可以改变的
        currdate = ca.getTime();
        String enddate = format.format(currdate);
        return enddate;
    }

    /**
     * 指定时间增加小时
     * */
    public String excelAddHour(int hour,String oldDay){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(oldDay);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);// 24小时制
        date = cal.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        return  format.format(date);
    }


    /**
     * 导入excel转化时间
     * */
    public String excelCaseDate(int account){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = new GregorianCalendar(1900,0,0);
        Date d = calendar.getTime();
        Calendar ca = Calendar.getInstance();
        ca.setTime(d);
        ca.add(Calendar.DATE, account);// num为增加的天数，可以改变的
        d = ca.getTime();
        String enddate = format.format(d);
        return enddate;
    }
}
