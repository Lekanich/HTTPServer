package server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import server.statistic.KeeperStatistic;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 25.08.13
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServerInitializer extends ChannelInitializer<SocketChannel> {

    private KeeperStatistic keeper;

    public HTTPServerInitializer(KeeperStatistic keeper){
        super();
        this.keeper = keeper;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //p.addLast("ssl", new SslHandler(engine));

        p.addLast("codec", new HttpServerCodec());
        p.addLast("handler", new HTTPServerHandler(keeper));
    }
}