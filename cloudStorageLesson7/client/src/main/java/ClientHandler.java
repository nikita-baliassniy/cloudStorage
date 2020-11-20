import common.AbstractRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Хендлер для обработки сообщений клиента
 */
public class ClientHandler extends SimpleChannelInboundHandler<AbstractRequest> {

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractRequest msg) {
        if (callback != null) {
            callback.callback(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }
}