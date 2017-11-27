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

public class NormalMessageHandler extends MessageHandler {
    @Override
    public void handle(Message message, SelectionKey selectionKey, Selector server) {
        String receiver = message.getHeader().getReceiver();
        String sender = message.getHeader().getSender();
        SocketChannel sc = UserManager.getUserChannel(receiver);
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setType(ResponseType.NORMAL);
        responseHeader.setSender(sender);
        responseHeader.setTimestamp(message.getHeader().getTimestamp());
        response.setResponseHeader(responseHeader);
        response.setResponsebody(message.getBody());

        try {
            sc.write(ByteBuffer.wrap(
                    ProtoStuffUtil.serialize(response)
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
