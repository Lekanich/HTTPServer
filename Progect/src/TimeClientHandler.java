import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        System.out.println("handler Added");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        System.out.println("handler Removed");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("channelRead");
        UnixTime buf = (UnixTime) msg;
        System.out.println(buf);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}