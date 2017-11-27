package model.handler.message;


import common.domain.Message;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class MessageHandler {

    abstract public void handle(Message message, SelectionKey selectionKey,Selector server);

    protected void broadcast(byte[] data, Selector server) throws IOException {
        for (SelectionKey key : server.keys()) {
            Channel client = key.channel();
            if (client instanceof SocketChannel) {
                SocketChannel dest = (SocketChannel) client;
                dest.write(ByteBuffer.wrap(data));
            }
        }
    }

}
