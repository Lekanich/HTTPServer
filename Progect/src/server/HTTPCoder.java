package server;

import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import server.statistic.KeeperStatistic;
import server.statistic.RequestDoneStatus;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;


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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyNioSocketChannel mns = (MyNioSocketChannel)ctx.channel();
        UnpooledUnsafeDirectByteBuf ub = (UnpooledUnsafeDirectByteBuf)msg;

        String ip = ((InetSocketAddress) mns.remoteAddress()).getAddress().toString().substring(1);
        Date date = new Date(System.currentTimeMillis());
        RequestDoneStatus rds = new RequestDoneStatus(String.valueOf(mns.hashCode()*ip.hashCode()), ip, null,date, 0,ub.readableBytes(),0);
        keeper.addIpStat(ip, date);
        mns.addRequest(rds);
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //get channel with the local keeper requestDoneStatus
        MyNioSocketChannel mns = (MyNioSocketChannel)ctx.channel();

        RequestDoneStatus rds = mns.getRequestAndRemove();
        if(rds!=null){
            DefaultFullHttpResponse d = (DefaultFullHttpResponse)msg;
            //count the bytes of the message
            rds.setSendByte(d.content().readableBytes());
            //count the bytes of the headers
            for(Map.Entry<String, String> h : d.headers().entries()) {
                rds.addSendByte(h.getKey().length()+h.getValue().length());
            }
            rds.setBackDate(new Date(System.currentTimeMillis()));
            keeper.addRequestDoneStatus(rds);
        }
        super.write(ctx, msg, promise);
    }

}
