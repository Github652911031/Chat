package model;

import com.sun.corba.se.pept.transport.ListenerThread;
import common.domain.Message;
import common.domain.MessageHeader;
import common.util.ProtoStuffUtil;
import lombok.extern.slf4j.Slf4j;
import model.handler.message.MessageHandler;
import model.handler.message.impl.LoginMessageHandler;
import model.handler.message.impl.LogoutMessageHandler;
import model.handler.message.impl.NormalMessageHandler;
import model.handler.message.impl.RequestOnlineUserHandler;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MyQqServer {
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final int PORT = 9000;
    public static final String QUIT = "QUIT";
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private ExecutorService readPool;
    private ListenerThread listenerThread;

    public MyQqServer(){
        try {
            log.info("服务器启动");
            serverSocketChannel = ServerSocketChannel.open();
            //切换为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            ServerSocket socket = serverSocketChannel.socket();
            socket.bind(new InetSocketAddress("localhost",MyQqServer.PORT));
            //获得选择器
            selector = Selector.open();
            //将channel注册到selector上
            //第二个参数是选择键，用于说明selector监控channel的状态
            //可能的取值：SelectionKey.OP_READ OP_WRITE OP_CONNECT OP_ACCEPT
            //监控的是channel的接收状态
            serverSocketChannel.register(selector,  SelectionKey.OP_ACCEPT);
            this.readPool = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10), new ThreadPoolExecutor.CallerRunsPolicy());
            this.listenerThread = new ListenerThread();
            new Thread(listenerThread).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ListenerThread extends Thread {

        @Override
        public void interrupt() {
            try {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                super.interrupt();
            }
        }

        @Override
        public void run() {
            try {
                //如果有一个及以上的客户端的数据准备就绪
                while (!Thread.currentThread().isInterrupted()) {
                    //当注册的事件到达时，方法返回；否则,该方法会一直阻塞
                    selector.select();
                    //获取当前选择器中所有注册的监听事件
                    for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                        SelectionKey key = it.next();
                        //删除已选的key,以防重复处理
                        it.remove();
                        //如果"接收"事件已就绪
                        if (key.isAcceptable()) {
                            //交由接收事件的处理器处理
                            handleAcceptRequest();
                        } else if (key.isReadable()) {
                            //如果"读取"事件已就绪
                            //取消可读触发标记，本次处理完后才打开读取事件标记
                            key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                            //交由读取事件的处理器处理
                            readPool.execute(new ReadEventHandler(key));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void shutdown() {
            Thread.currentThread().interrupt();
        }
    }


    private void handleAcceptRequest() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(this.selector,SelectionKey.OP_READ);
            log.info("服务器连接客户端:{}",socketChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class ReadEventHandler implements Runnable {
        private ByteBuffer buf;
        private SocketChannel client;
        private ByteArrayOutputStream baos;
        private SelectionKey key;

        public ReadEventHandler(SelectionKey key) {
            this.buf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            this.key = key;
            this.client = (SocketChannel) key.channel();
            baos = new ByteArrayOutputStream();
        }

        @Override
        public void run() {

            try {
                int size;
                while( (size = client.read(buf)) > 0){
                    buf.flip();
                    baos.write(buf.array(),0,size);
                    buf.clear();

                }
                if(size == -1){
                    key.cancel();
                    client.close();
                    return;
                }
                log.info("读取完毕，继续监听");

                baos.toByteArray();

                key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                key.selector().wakeup();

                byte[] bytes = baos.toByteArray();
                baos.close();
                Message message = ProtoStuffUtil.deserialize(baos.toByteArray(),Message.class);
                MessageHandler messageHandler;
                log.info("接收到消息:{}",message.getHeader().getType());
                switch (message.getHeader().getType()){
                    case LOGIN:
                        messageHandler = new LoginMessageHandler();
                        messageHandler.handle(message,key,selector);
                        break;
                    case LOGOUT:
                        messageHandler = new LogoutMessageHandler();
                        messageHandler.handle(message,key,selector);
                        break;
                    case REQUEST_ONLINEUSERS:
                        new RequestOnlineUserHandler().handle(message,key,selector);
                        break;
                    case NORMAL:
                        new NormalMessageHandler().handle(message,key,selector);
                        break;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        new MyQqServer();
    }

}



