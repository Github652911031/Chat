package model.handler.message.impl;

import common.domain.*;
import common.enumeration.ResponseCode;
import common.enumeration.ResponseType;
import common.util.ProtoStuffUtil;
import model.handler.message.MessageHandler;
import model.property.PromptMsgProperty;
import model.user.UserManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class LoginMessageHandler extends MessageHandler {


    @Override
    public void handle(Message message, SelectionKey selectionKey, Selector server) {
        String username = message.getHeader().getSender();
        String passwd = new String(message.getBody(), StandardCharsets.UTF_8);

        SocketChannel sc = (SocketChannel)selectionKey.channel();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setType(ResponseType.PROMPT);
        responseHeader.setTimestamp(System.currentTimeMillis());
        try {
            if (UserManager.login(username, passwd, sc)) {
                //返回客户端登陆请求
                System.out.println("登陆成功");

                responseHeader.setResponseCode(ResponseCode.LOGIN_SUCCESS.getCode());
                response.setResponseHeader(responseHeader);
                byte[] r = ProtoStuffUtil.serialize(response);
                System.out.println(r);
                sc.write(ByteBuffer.wrap(r));
                Thread.sleep(10);

                //群发登陆消息
                Response broadcast = new Response();
                ResponseHeader broadcastH = new ResponseHeader();
                broadcastH.setType(ResponseType.NOTIFY);
                broadcastH.setTimestamp(System.currentTimeMillis());
                broadcast.setResponseHeader(broadcastH);
                broadcast.setResponsebody(username.getBytes(StandardCharsets.UTF_8));
                byte[] br = ProtoStuffUtil.serialize(broadcast);
                super.broadcast(br,server);

            }else{
                System.out.println("登陆失败");
                responseHeader.setResponseCode(ResponseCode.LOGIN_FAILURE.getCode());
                response.setResponseHeader(responseHeader);
                response.setResponsebody(new String(PromptMsgProperty.LOGIN_FAILURE).getBytes(StandardCharsets.UTF_8));
                byte[] r = ProtoStuffUtil.serialize(response);
                sc.write(ByteBuffer.wrap(r));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
