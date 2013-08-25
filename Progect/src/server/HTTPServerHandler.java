package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 25.08.13
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServerHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf CONTENT =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello from ALex", CharsetUtil.UTF_8));
    private static final String ADDRESS_HELLO = "/hello";
    private static final String ADDRESS_REDIRECT = "/redirect?url=";
    private static final String ADDRESS_STATUS =  "/status";

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public static void p(Object p){
        System.out.println(p);
    }

    public static void pp(Object p){
        System.out.print(p);
    }

    public static void pz(){
        p("-----------------");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            System.out.println("========Start========");
            System.out.println(req.getUri());
            if(req.getUri().equals("/favicon.ico")) return;
            if(req.getUri().equals(ADDRESS_HELLO)){
                Thread.sleep(10000);
            }
            if(req.getUri().startsWith(ADDRESS_REDIRECT)){
                String newURI = getNewURI(req.getUri());
                sendRedirect(ctx, newURI);
                return;
            }
            System.out.println("========Finish=======");
            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
            boolean keepAlive = HttpHeaders.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, CONTENT.duplicate());
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        p("exceptionCa");
        cause.printStackTrace();
        ByteBuf badWord = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Very very bad word. Because we have truble on the server.", CharsetUtil.UTF_8));
        ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, badWord.duplicate());
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    private String getNewURI(String oldURI){
        String newURI;
        try {
            newURI = URLDecoder.decode(oldURI, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            p("Sorry");
            newURI = URLDecoder.decode(oldURI);
        }
        newURI = newURI.substring(ADDRESS_REDIRECT.length()+1, newURI.length()-1);
        return newURI;
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, newUri);

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
