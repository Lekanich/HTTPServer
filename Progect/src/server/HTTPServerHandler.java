package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import server.statistic.KeeperStatistic;
import server.statistic.RequestDoneStatus;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 25.08.13
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServerHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf CONTENT_HELLO =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello from ALex", CharsetUtil.UTF_8));
    private static final ByteBuf CONTENT_WRONG_ADDRESS =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("We work. Please choose another address.", CharsetUtil.UTF_8));
    private static final ByteBuf CONTENT_ERROR =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Very very bad word. Because we have trouble on the server.\r\n", CharsetUtil.UTF_8));;

    private static final String ADDRESS_HELLO = "/hello";
    private static final String ADDRESS_REDIRECT = "/redirect?url=";
    private static final String ADDRESS_STATUS =  "/status";
    private static final String ADDRESS_PREFIX = "http://";
    private static final long WAIT = 10L;

    private final KeeperStatistic keeper;
    private RequestDoneStatus rds;

    public HTTPServerHandler(KeeperStatistic keeper){
        super();
        this.keeper = keeper;
    }

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
        keeper.incCountConnected();
//        p("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        keeper.decCountConnected();
//        p("channelInactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String uri = req.getUri();
            MyNioSocketChannel mns = (MyNioSocketChannel)ctx.channel();
            RequestDoneStatus rds = mns.getRequest();
            if(rds!=null){
                String uriForTable = decoder(uri);
                rds.setUrl(uriForTable);
            }

            //processing speed results
            String strangerURL = uri.substring(1);
            if(keeper.isSystemRequest(strangerURL)){
                keeper.decIpStat(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().toString().substring(1));
                RequestDoneStatus rdsV = keeper.getRequest(strangerURL);
                if(rdsV!=null){
                    long timeFirst = rdsV.getBackDate().getTime();
                    long timeLast = rds.getDate().getTime();
                    long countTime = timeLast-timeFirst;
//                    p("Count time: "+countTime);
                    int countBytes = rds.getGetByte()+rdsV.getSendByte();
//                    p("Count bytes: "+countBytes);
                    int speed = (int)(1000*(double)countBytes/(double)countTime);
//                    p("Speed: "+speed);
                    rdsV.setId(String.valueOf(mns.hashCode()*strangerURL.hashCode()));
                    rdsV.setSpeed(speed);
                    rds.setContent(rdsV);
                    uri = rdsV.getUrl();
                    keeper.removeRequestDoneStatus(rdsV);
                }
            }else{
                //set for count speed
                String newStrangeURI = ADDRESS_PREFIX+HttpHeaders.getHost(req)+"/"+rds.getId();
                keeper.addSystemRequest(rds.getId());
//                p(newStrangeURI);
                sendRedirect(ctx, newStrangeURI);
                return;
            }

//            if(uri.equals("/favicon.ico")) {
//                //remember in keeper this request
//                if(rds!=null)
//                    keeper.addRequestDoneStatus(mns.getRequestAndRemove());
//                return;
//            }
            if(uri.equals(ADDRESS_HELLO)){
                Thread.sleep(WAIT);
                sendText(ctx, req, CONTENT_HELLO);
            }
            if(uri.startsWith(ADDRESS_REDIRECT)){
                //get URI for redirect
                String newURI = getNewURI(uri);
                //add to keeper new or not new redirect URL
                keeper.addUrlRedirectStat(newURI);
                sendRedirect(ctx, newURI);
                return;
            }
            if(uri.equals(ADDRESS_STATUS)){
               ByteBuf status = Unpooled.copiedBuffer(keeper.getStat(), CharsetUtil.UTF_8);
               sendText(ctx, req, status);
            }else{
               sendText(ctx, req, CONTENT_WRONG_ADDRESS);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        p("exceptionCa");
//        cause.printStackTrace();
        sendError(ctx, HttpResponseStatus.BAD_REQUEST);
        ctx.close();
    }

    private static String decoder(String oldURI){
        String newURI;
        try {
            newURI = URLDecoder.decode(oldURI, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                newURI = URLDecoder.decode(oldURI, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }
        return newURI;
    }

    private static String getNewURI(String oldURI){
        String newURI = decoder(oldURI);
        newURI = newURI.substring(ADDRESS_REDIRECT.length()+1, newURI.length()-1);
        if(! newURI.startsWith(ADDRESS_PREFIX)) newURI = ADDRESS_PREFIX+newURI;
        return newURI;
    }

    private static void sendText(ChannelHandlerContext ctx, HttpRequest req, ByteBuf message) throws InterruptedException {
        if (HttpHeaders.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }
        boolean keepAlive = HttpHeaders.isKeepAlive(req);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, message.duplicate());
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            ctx.write(response);
        }
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, newUri);
        // Close the connection as soon as the error message is sent.
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status, CONTENT_ERROR.duplicate());
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        // Close the connection as soon as the error message is sent.
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }
}
