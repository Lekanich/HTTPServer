package server;

import io.netty.channel.socket.nio.NioSocketChannel;
import server.statistic.RequestDoneStatus;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 27.08.13
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */
public class MyNioSocketChannel extends NioSocketChannel {

    private List<RequestDoneStatus> list;

    public MyNioSocketChannel(MyNioServerSocketChannel myNioServerSocketChannel, SocketChannel ch) {
        super(myNioServerSocketChannel, ch);
        list = new LinkedList<RequestDoneStatus>();
    }

    public RequestDoneStatus getRequest() {
        return list.size()>0?list.get(0):null;
    }

    public RequestDoneStatus getRequestAndRemove() {
        return list.size()>0?list.remove(0):null;
    }

    public void addRequest(RequestDoneStatus request) {
        list.add(request);
    }
}
