package server.statistic;

import io.netty.bootstrap.ServerBootstrap;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 26.08.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class KeeperStatistic {

    private static final String FIRST_STAT="Count requests: ";
    private static final String SECOND_STAT="Count unique requests: ";
    private static final String THIRD_STAT="Table #1\r\n|      Ip       |  Count  |      Last";
    private static final String FOURTH_STAT="Table #2\r\n|               URL               |  Count requests: ";
    private static final String FIFTH_STAT="Count connections: ";
    private static final String SIXTH_STAT="Table #3\r\n|      Ip       |                        URL                       |" +
            "             Time             | Send bytes | Received bytes |  Speed  ";
    private static final String EMPTY = " No requests or memory is empty.";

    private static final byte MAX_SIZE_CAPACITY = 16;

    private Map<String, Integer> ipStatCount;
    private Map<String, Date> ipStatLast;
    private Map<String, Integer> urlStatRedirect;
    private List<RequestDoneStatus> list;

    private AtomicInteger connected;
    public Set<String> systemRequests;

    public KeeperStatistic(){
        ipStatCount = new HashMap<String, Integer>();
        ipStatLast = new HashMap<String, Date>();
        urlStatRedirect = new HashMap<String, Integer>();
        list = new LinkedList<RequestDoneStatus>();
        connected = new AtomicInteger(0);
        systemRequests = new HashSet<String>();
    }

    public static void p(Object o){
        System.out.println(o);
    }

    public boolean isSystemRequest(String value){
        synchronized (systemRequests){
            return systemRequests.remove(value);
        }
    }

    public void addSystemRequest(String value){
        synchronized (systemRequests){
            systemRequests.add(value);
        }
    }

    public synchronized void addUrlRedirectStat(String url){
        int counter = 0;
        if(urlStatRedirect.containsKey(url)){
            counter = urlStatRedirect.get(url);
        }
        counter++;
        urlStatRedirect.put(url, counter);
    }

    public synchronized void addRequestDoneStatus(RequestDoneStatus rds){
        if(list.size()==MAX_SIZE_CAPACITY){
            list.remove(0);
        }
        list.add(rds);
    }

    public synchronized boolean removeRequestDoneStatus(RequestDoneStatus rds){
        return list.remove(rds);
    }

    public synchronized void decIpStat(String ip){
        int counter = 0;
        if(ipStatCount.containsKey(ip)){
            counter = ipStatCount.get(ip);
        }
        counter--;
        ipStatCount.put(ip, counter);
    }

    public synchronized void addIpStat(String ip, Date date){
        int counter = 0;
        if(ipStatCount.containsKey(ip)){
            counter = ipStatCount.get(ip);
        }
        counter++;
        ipStatCount.put(ip, counter);
        ipStatLast.put(ip, date);
    }

    public synchronized String getStat(){
        //1
        StringBuilder sb = new StringBuilder(FIRST_STAT);
        Set<Map.Entry <String, Integer>> ipSCEntry =ipStatCount.entrySet();
        int sum = 0;
        for(Map.Entry m : ipSCEntry)
            sum += (Integer)m.getValue();
        sb.append(" : ").append(sum).append("\r\n");
        //2
        sb.append("\r\n").append(SECOND_STAT).append("\r\n");
        for(Map.Entry m : ipSCEntry)
            sb.append(m.getKey()).append(" : ").append(m.getValue()).append("\r\n");
        //3
        sb.append("\r\n").append(THIRD_STAT).append("\r\n");
        Set<Map.Entry <String, Date>> ipSDEntry =ipStatLast.entrySet();
        Formatter f;
        Iterator<Map.Entry<String, Integer>> iteratorIpSC =  ipSCEntry.iterator();
        Iterator<Map.Entry<String, Date>> iteratorIpSD =  ipSDEntry.iterator();
        while(iteratorIpSC.hasNext()){
            f = new Formatter();
            Map.Entry<String, Integer> entryIpCount = iteratorIpSC.next();
            f.format("|%15s|%9s|%s", entryIpCount.getKey(), entryIpCount.getValue(), iteratorIpSD.next().getValue());
            sb.append(f.toString()).append("\r\n");
        }
        //4
        sb.append("\r\n").append(FOURTH_STAT).append("\r\n");
        Set<Map.Entry <String, Integer>> urlEntry =urlStatRedirect.entrySet();
        if(!urlStatRedirect.isEmpty()){
            for(Map.Entry<String, Integer> entry : urlEntry ){
                f = new Formatter();
                f.format("|%33s|%s", entry.getKey(), entry.getValue());
                sb.append(f.toString()).append("\r\n");
            }
        }else{
            sb.append(EMPTY).append("\r\n");
        }
        //5
        sb.append("\r\n").append(FIFTH_STAT).append(" : ").append(connected.get()).append("\r\n");
        //6
        sb.append("\r\n").append(SIXTH_STAT).append("\r\n");
        if(!list.isEmpty()){
            for(RequestDoneStatus rds : list){
                sb.append(rds).append("\r\n");
            }
        }else{
            sb.append(EMPTY).append("\r\n");
        }
        return sb.toString();
    }

    public int incCountConnected(){
        return connected.incrementAndGet();
    }

    public int decCountConnected(){
        return connected.decrementAndGet();
    }

    public synchronized Map<String, Integer> getIpStatCount() {
        return ipStatCount;
    }

    public synchronized Map<String, Date> getIpStatLast() {
        return ipStatLast;
    }

    public synchronized Map<String, Integer> getUrlStatRedirect() {
        return urlStatRedirect;
    }

    public synchronized List<RequestDoneStatus> getListLastDoneRequests() {
        return list;
    }

    public RequestDoneStatus getRequest(String id) {
        for(RequestDoneStatus rds : list)
            if(rds.getId().equals(id)) return rds;
        return null;
    }
}
