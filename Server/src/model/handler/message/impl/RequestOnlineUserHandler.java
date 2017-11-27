package model.handler.message.impl;



import common.domain.Message;
import common.domain.Response;
import common.domain.ResponseHeader;
import common.enumeration.ResponseType;
import common.util.ProtoStuffUtil;
import model.handler.message.MessageHandler;
import model.user.UserManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RequestOnlineUserHandler extends MessageHandler {

    public void handle(Message message, SelectionKey selectionKey, Selector server) {
        System.out.println("收到请求好友列表消息");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        String lists = UserManager.getOnlineUsers();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setType(ResponseType.ONLINEUSERS);
        response.setResponseHeader(responseHeader);
        response.setResponsebody(lists.getBytes(StandardCharsets.UTF_8));
        try {
            socketChannel.write(ByteBuffer.wrap(
                    ProtoStuffUtil.serialize(response)
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
