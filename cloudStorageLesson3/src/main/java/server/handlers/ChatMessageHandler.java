package server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatMessageHandler extends SimpleChannelInboundHandler<String> {

    static int cnt = 1;

    String id = "[user" + cnt++ + "]:";

    public static final ConcurrentLinkedDeque<ChannelHandlerContext> channels =
            new ConcurrentLinkedDeque<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected");
        channels.add(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println("Message from client: " + s);
        StringBuilder stringBuilder = new StringBuilder();
        if (s.startsWith("download ")) {
            String fileName = s.split(" ")[1];
            File fileToSend = new File(fileName);
            if(fileToSend.exists()) {
                Scanner scanner = new Scanner(fileToSend);
                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.nextLine());
                    stringBuilder.append("\n");
                }
            }
        } else if (s.startsWith("upload ")) {

        }
        System.out.println("SRV size " + stringBuilder.length());
        channels.forEach(c -> c.writeAndFlush(stringBuilder.toString()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected");
    }
}
