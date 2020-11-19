package server.handlers;

import common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ChatMessageHandler extends SimpleChannelInboundHandler<AbstractRequest> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client disconnected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractRequest msg) throws Exception {
        System.out.println("GOT A MSG");
        if (msg instanceof CommandRequest) {
            System.out.println("Request");
            CommandRequest commandRequest = (CommandRequest) msg;
            switch (commandRequest.getCommandType()) {
                case AUTH:
                    System.out.println("AUTH");
                    break;
                case REGISTER:
                    System.out.println("REGISTER");
                    break;
                case LIST:
                    System.out.println("GET LIST");
                    break;
                case DOWNLOAD:
                    System.out.println("DOWNLOAD");
                    break;
                case CD:
                    System.out.println("CD");
                    break;
            }
        }

    }
}
