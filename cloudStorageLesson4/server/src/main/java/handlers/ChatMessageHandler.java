package handlers;

import common.AbstractRequest;
import common.CommandRequest;
import common.CommandType;
import common.TableRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Paths;


public class ChatMessageHandler extends SimpleChannelInboundHandler<AbstractRequest> {

    UserServiceImpl userService = new UserServiceImpl();

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
        if (msg instanceof CommandRequest) {
            System.out.println("Request");
            CommandRequest commandRequest = (CommandRequest) msg;
            switch (commandRequest.getCommandType()) {
                case LIST:
                    System.out.println("LIST");
                    TableRequest tableRequest = new TableRequest();
                    String stringRoot = commandRequest.getArg1();
                    if (!Files.exists(Paths.get(stringRoot)) &&
                            stringRoot.indexOf("/") == stringRoot.lastIndexOf("/")) {
                        Files.createDirectory(Paths.get(stringRoot));
                    }
                    tableRequest.createTable(Paths.get(stringRoot));
                    System.out.println(tableRequest.getFileTable().size());
                    ctx.writeAndFlush(tableRequest);
                    ctx.writeAndFlush(new CommandRequest(CommandType.LIST, "HELLO"));
                    break;
                case DOWNLOAD:
                    System.out.println("DOWNLOAD");
                    break;
                case CD:
                    System.out.println("CD");
                    break;
                case AUTH:
                    System.out.println("AUTH");
                    String login = commandRequest.getArg1();
                    String password = commandRequest.getArg2();
                    if (userService.getStorage(login, password) != null) {
                        ctx.writeAndFlush(new CommandRequest(CommandType.AUTH, "OK", userService.getStorage(login, password)));
                    } else {
                        ctx.writeAndFlush(new CommandRequest(CommandType.AUTH, "ERROR"));
                    }
                    break;
                case REGISTER:
                    System.out.println("REGISTER");
                    String login1 = commandRequest.getArg1();
                    String password1 = commandRequest.getArg2();
                    if (userService.getStorage(login1, password1) == null) {
                        userService.addUser(login1, password1);
                        ctx.writeAndFlush(new CommandRequest(CommandType.REGISTER, "OK", userService.getStorage(login1, password1)));
                    } else {
                        ctx.writeAndFlush(new CommandRequest(CommandType.REGISTER, "BUSY"));
                    }
                    break;
            }
        }

    }
}
