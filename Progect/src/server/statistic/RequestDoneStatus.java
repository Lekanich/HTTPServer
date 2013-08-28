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

    /**
     * Version-id, used for remember
     */
    private String id;

    private String ip;
    private String url;
    private Date date;
    private Date backDate;

    private int sendByte;
    private int getByte;
    private int speed;

    public RequestDoneStatus(String id, String ip, String url, Date date, int sendByte, int getByte, int speed){
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

    public void addSendByte(int sendByte){
        this.sendByte+=sendByte;
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

    public void setContent(RequestDoneStatus rds){
        this.id = rds.id;
        this.ip = rds.ip;
        this.url = rds.url;
        this.date = rds.date;
        this.backDate = rds.backDate;
        this.getByte = rds.getByte;
        this.sendByte = rds.sendByte;
        this.speed = rds.speed;
    }

    @Override
    public String toString(){
        Formatter f = new Formatter();
        f.format("|%15s|%50s|%30s|%12s|%16s|%9s",ip, url, date, sendByte, getByte, speed);
        return f.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getBackDate() {
        return backDate;
    }

    public void setBackDate(Date backDate) {
        this.backDate = backDate;
    }
}
