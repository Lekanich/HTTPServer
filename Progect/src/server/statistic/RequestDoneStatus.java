package server.statistic;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Formatter;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 26.08.13
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class RequestDoneStatus {
    private String ip;
    private String url;
    private Date date;
    private int sendByte;
    private int getByte;
    private int speed;

    public RequestDoneStatus(String ip, String url, Date date, int sendByte, int getByte, int speed){
        this.ip = ip;
        this.url = url;
        this.date = date;

        this.sendByte = sendByte;
        this.getByte = getByte;
        this.speed = speed;
    }

    public String getIp() {
        return ip;
    }

    public String getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }

    public int getSendByte() {
        return sendByte;
    }

    public int getGetByte() {
        return getByte;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString(){
        Formatter f = new Formatter();
        f.format("|%15s|%50s|%30s|%12s|%16s|",ip, url, date, sendByte, getByte, speed);
        return f.toString();
    }
}
