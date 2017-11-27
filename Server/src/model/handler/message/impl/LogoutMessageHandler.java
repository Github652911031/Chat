package model.handler.message.impl;

import common.domain.Message;
import common.domain.Response;
import common.domain.ResponseHeader;
import common.enumeration.ResponseType;
import common.util.ProtoStuffUtil;
import lombok.extern.slf4j.Slf4j;
import model.handler.message.MessageHandler;
import model.user.UserManager;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LogoutMessageHandler extends MessageHandler {

    @Override
    public void handle(Message message, SelectionKey selectionKey, Selector server) {
        try{
            String username = message.getHeader().getSender();
            SocketChannel sc = (SocketChannel)selectionKey.channel();
            UserManager.logout(username);
            Response response = new Response();
            ResponseHeader responseHeader = new ResponseHeader();
            responseHeader.setType(ResponseType.NOTIFY_OUT);
            response.setResponseHeader(responseHeader);
            response.setResponsebody(username.getBytes(StandardCharsets.UTF_8));
            byte[] r = ProtoStuffUtil.serialize(response);
            super.broadcast(r,server);
            selectionKey.cancel();
            sc.close();
            sc.socket().close();
            log.info("客户端退出");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
