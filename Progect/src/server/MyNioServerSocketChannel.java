package server;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 27.08.13
 * Time: 20:15
 * To change this template use File | Settings | File Templates.
 */
public class MyNioServerSocketChannel extends NioServerSocketChannel{

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = javaChannel().accept();
        try {
            if (ch != null) {
                buf.add(new MyNioSocketChannel(this, ch));
                return 1;
            }
        } catch (Throwable t) {

            try {
                ch.close();
            } catch (Throwable t2) {
            }
        }

        return 0;
    }


}
