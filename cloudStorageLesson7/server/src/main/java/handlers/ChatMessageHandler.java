package handlers;

import common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.*;

/**
 * Основной хендлер сервера
 */
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
        // Получен файл для загрузки в хранилище
        if (msg instanceof FileRequest) {
            FileRequest fileRequest = (FileRequest) msg;
            Files.write(Paths.get(fileRequest.getPathInCloud() + "/" + fileRequest.getFileName()),
                    fileRequest.getContent(), StandardOpenOption.CREATE);
            ctx.writeAndFlush(new CommandRequest(CommandType.UPLOAD, "OK"));
        } else if (msg instanceof CommandRequest) {
            System.out.println("Request");
            CommandRequest commandRequest = (CommandRequest) msg;
            switch (commandRequest.getCommandType()) {
                // Получен запрос списка файлов
                case LIST:
                    System.out.println("LIST");
                    TableRequest tableRequest = new TableRequest();
                    String stringRoot = commandRequest.getArg1();
                    if (!Files.exists(Paths.get(stringRoot)) &&
                            stringRoot.indexOf("/") == stringRoot.lastIndexOf("/")) {
                        Files.createDirectory(Paths.get(stringRoot));
                    }
                    tableRequest.createTable(Paths.get(stringRoot));
                    System.out.println("SIZE OF TABLE " + tableRequest.getFileTable().size());
                    ctx.writeAndFlush(tableRequest);
                    break;
                // Получен запрос на скачивание из хранилища
                case DOWNLOAD:
                    if (Files.exists(Paths.get(commandRequest.getArg1()))) {
                        System.out.println("DOWNLOAD " + commandRequest.getArg1());
                        FileRequest fileRequest = new FileRequest(Path.of(commandRequest.getArg1()));
                        ctx.writeAndFlush(fileRequest);
                    }
                    break;
                // Получен запрос на удаление
                case REMOVE:
                    if (Files.exists(Paths.get(commandRequest.getArg1()))) {
                        Files.delete(Paths.get(commandRequest.getArg1()));
                        ctx.writeAndFlush(new CommandRequest(CommandType.REMOVE, "OK"));
                    } else {
                        ctx.writeAndFlush(new CommandRequest(CommandType.REMOVE, "ERROR"));
                    }
                    break;
                // Получен запрос на переименование
                case RENAME:
                    if (Files.exists(Paths.get(commandRequest.getArg1()))) {
                        Files.move(Paths.get(commandRequest.getArg1()), Paths.get(commandRequest.getArg2()),
                                StandardCopyOption.REPLACE_EXISTING);
                        ctx.writeAndFlush(new CommandRequest(CommandType.RENAME, "OK"));
                    } else {
                        ctx.writeAndFlush(new CommandRequest(CommandType.RENAME, "ERROR"));
                    }
                    break;
                // Получен запрос на создание новой папки
                case MKDIR:
                    if(!Files.exists(Paths.get(commandRequest.getArg1()))
                            || !Files.isDirectory(Paths.get(commandRequest.getArg1()))) {
                        Files.createDirectory(Paths.get(commandRequest.getArg1()));
                        ctx.writeAndFlush(new CommandRequest(CommandType.MKDIR, "OK"));
                    } else {
                      ctx.writeAndFlush(new CommandRequest(CommandType.MKDIR, "EXIST"));
                    }
                    break;
                // Получен запрос на авторизацию
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
                // Получен запрос на регистрацию
                case REGISTER:
                    System.out.println("REGISTER");
                    String login1 = commandRequest.getArg1();
                    String password1 = commandRequest.getArg2();
                    if (userService.getStorage(login1, password1) == null) {
                        boolean userAdded = userService.addUser(login1, password1);
                        if (userAdded) {
                            ctx.writeAndFlush(new CommandRequest(CommandType.REGISTER, "OK", userService.getStorage(login1, password1)));
                        }
                    } else {
                        ctx.writeAndFlush(new CommandRequest(CommandType.REGISTER, "BUSY"));
                    }
                    break;
            }
        }

    }
}
