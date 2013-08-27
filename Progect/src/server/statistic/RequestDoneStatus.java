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
    //channel's hashcode
    private int id;

    private String ip;
    private String url;
    private Date date;

    private int sendByte;
    private int getByte;
    private int speed;

    public RequestDoneStatus(int id, String ip, String url, Date date, int sendByte, int getByte, int speed){
        this.id = id;
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

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSendByte() {
        return sendByte;
    }

    public void setSendByte(int sendByte) {
        this.sendByte = sendByte;
    }

    public int getGetByte() {
        return getByte;
    }

    public void setGetByte(int getByte) {
        this.getByte = getByte;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString(){
        Formatter f = new Formatter();
        f.format("|%15s|%50s|%30s|%12s|%16s|%9s",ip, url, date, sendByte, getByte, speed);
        return f.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
