package server;

import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import server.statistic.KeeperStatistic;
import server.statistic.RequestDoneStatus;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 27.08.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class HTTPCoder extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder> {

    private final KeeperStatistic keeper;

    public HTTPCoder(KeeperStatistic keeper) {
        this(4096, 8192, 8192, keeper);
    }

    /**
     * Creates a new instance with the specified decoder options.
     */
    public HTTPCoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, KeeperStatistic keeper) {
        super(new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize), new HttpResponseEncoder());
        this.keeper = keeper;
    }

    private static void p(Object object){
        System.out.println(object);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead");
        MyNioSocketChannel mns = (MyNioSocketChannel)ctx.channel();

//        p("chr "+ctx.channel().hashCode());
//        p("chr "+ctx.pipeline().hashCode());
        UnpooledUnsafeDirectByteBuf ub = (UnpooledUnsafeDirectByteBuf)msg;
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().toString().substring(1);
        Date date = new Date(System.currentTimeMillis());
        RequestDoneStatus rds = new RequestDoneStatus(ctx.channel().hashCode(), ip, null,date, 0,ub.readableBytes(),0);
        keeper.addIpStat(ip, date);
        mns.addRequest(rds);
//        keeper.addRequestDoneStatus(rds);
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("write");
        MyNioSocketChannel mns = (MyNioSocketChannel)ctx.channel();

//        p("wr "+ctx.channel().hashCode());
//        p("wr "+ctx.pipeline().hashCode());
        RequestDoneStatus rds = mns.getRequestAndRemove();
        if(rds!=null){
            DefaultFullHttpResponse d = (DefaultFullHttpResponse)msg;
            rds.setSendByte(d.content().readableBytes());
            keeper.addRequestDoneStatus(rds);
        }
        super.write(ctx, msg, promise);
    }

}
