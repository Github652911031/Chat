package view;

import com.sun.media.sound.ModelStandardDirector;
import com.sun.xml.internal.ws.api.handler.MessageHandler;
import com.sun.xml.internal.ws.client.ClientTransportException;
import common.domain.Message;
import common.domain.MessageHeader;
import common.domain.Response;
import common.domain.User;
import common.enumeration.MessageType;
import common.enumeration.ResponseCode;
import common.util.ProtoStuffUtil;

import javax.sound.midi.SysexMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static javafx.application.Application.launch;

public class ClientLogin extends JFrame implements ActionListener{
//    JLabel jbl1;
//
//    //定义中部需要的组件
//    //.中部有三个JPanel,有一个叫选项卡窗口管理
//    JTabbedPane jtp;
//    JPanel jp2,jp3,jp4;
//    JLabel jp2_jbl1,jp2_jbl2,jp2_jbl3,jp2_jbl4;
//    JButton jp2_jb1;
//    JTextField jp2_jtf;
//    JPasswordField jp2_jpf;
//    JCheckBox jp2_jcb1,jp2_jcb2;
//
//
//
//    //定义南部需要的组件
//    JPanel jp1;
//    JButton jp1_jb1,jp1_jb2,jp1_jb3;

    private Socket s;
    public static final int PORT = 9000;
    private String username;
    private RecieveHandler listener;
    private boolean isConnected = false;
    private FriendList friendList;
    private boolean isLogin = false;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ClientLogin qqClientLogin=new ClientLogin();
    }

    public ClientLogin(){
        friendList = new FriendList();
        initNetwork();
        this.friendList.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                disConnect();
                System.exit(0);
            }
        });
    }

    private void disConnect() {
        try {
            System.out.println("退出");
            logout();
            if (!isConnected) {
                return;
            }
            this.listener.shutdown();
            Thread.sleep(10);
            s.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void logout() throws IOException {
        if (!isLogin) {
            return;
        }
        System.out.println("客户端发送下线请求");
        Message message = new Message();
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setType(MessageType.LOGOUT);
        messageHeader.setTimestamp(System.currentTimeMillis());
        messageHeader.setSender(this.username);
        message.setHeader(messageHeader);
        byte[] data = ProtoStuffUtil.serialize(message);
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        dos.write(data);

    }

    public void initFrame(){
//        //处理北部
//        jbl1=new JLabel(new ImageIcon("image/tou.gif"));
//
//        //处理中部
//        jp2=new JPanel(new GridLayout(3,3));
//
//        jp2_jbl1=new JLabel("QQ号码",JLabel.CENTER);
//        jp2_jbl2=new JLabel("QQ密码",JLabel.CENTER);
//        jp2_jbl3=new JLabel("忘记密码",JLabel.CENTER);
//        jp2_jbl3.setForeground(Color.blue);
//        jp2_jbl4=new JLabel("申请密码保护",JLabel.CENTER);
//        jp2_jb1=new JButton(new ImageIcon("image/clear.gif"));
//        jp2_jtf=new JTextField();
//        jp2_jpf=new JPasswordField();
//        jp2_jcb1=new JCheckBox("隐身登录");
//        jp2_jcb2=new JCheckBox("记住密码");
//
//        //把控件按照顺序加入到jp2
//        jp2.add(jp2_jbl1);
//        jp2.add(jp2_jtf);
//        jp2.add(jp2_jb1);
//        jp2.add(jp2_jbl2);
//        jp2.add(jp2_jpf);
//        jp2.add(jp2_jbl3);
//        jp2.add(jp2_jcb1);
//        jp2.add(jp2_jcb2);
//        jp2.add(jp2_jbl4);
//        //创建选项卡窗口
//        jtp=new JTabbedPane();
//        jtp.add("QQ号码",jp2);
//        jp3= new JPanel();
//        jtp.add("手机号码",jp3);
//        jp4=new JPanel();
//        jtp.add("电子邮件",jp4);
//
//        //处理南部
//        jp1=new JPanel();
//        jp1_jb1=new JButton(new ImageIcon("image/denglu.gif"));
//        //响应用户点击登录
//        jp1_jb1.addActionListener(this);
//        jp1_jb2=new JButton(new ImageIcon("image/quxiao.gif"));
//
//        jp1_jb3=new JButton(new ImageIcon("image/xiangdao.gif"));
//
//        //把三个按钮放入到jp1
//        jp1.add(jp1_jb1);
//        jp1.add(jp1_jb2);
//        jp1.add(jp1_jb3);
//
//        this.add(jbl1,"North");
//        this.add(jtp,"Center");
//        //..把jp1放在南部
//        this.add(jp1,"South");
//        this.setSize(350, 240);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setVisible(true);


    }

    public void initNetwork(){
        try {
            s = new Socket("127.0.0.1", ClientLogin.PORT);
            String username = JOptionPane.showInputDialog("请输入用户名");
            String password = JOptionPane.showInputDialog("请输入密码");
            User u=new User();
            u.setUsername(username.trim());
            u.setPasswd(new String(password));
            Message message = new Message();
            message.setHeader(new MessageHeader(
                    u.getUsername(), MessageType.LOGIN,System.currentTimeMillis()));
            message.setBody(password.getBytes(StandardCharsets.UTF_8));
            byte[] data = ProtoStuffUtil.serialize(message);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.write(data);
//        System.out.println(data);
            this.username = u.getUsername();
            this.isConnected = true;
            this.listener = new RecieveHandler();
            new Thread(listener).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        //如果用户点击登录
    }

    private class RecieveHandler implements Runnable{
        private byte[] bytes;


        public RecieveHandler(){
            bytes = new byte[1024];
        }


        public void shutdown(){isConnected = false;}
        @Override
        public void run() {

            while(isConnected){

                try{
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    int len = dis.read(bytes);
//                    System.out.println(new String(bytes,0,len,StandardCharsets.UTF_8));
                    Response response = ProtoStuffUtil.deserialize(bytes,0,len,Response.class);
                    System.out.println(response.getResponseHeader().getType());
                    switch(response.getResponseHeader().getType()){
                        case PROMPT:
                            Integer code = response.getResponseHeader().getResponseCode();
                            System.out.println(code);
                            if(code != null){
                                if(code == ResponseCode.LOGIN_SUCCESS.getCode()){
                                    //登陆成功，跳转好友列表界面
                                    ClientLogin.this.isLogin = true;
                                    friendList.init(ClientLogin.this.username,ClientLogin.this.s);
                                    //发送请求好友列表消息
                                    Message m = new Message();
                                    MessageHeader messageHeader = new MessageHeader();
                                    messageHeader.setSender(ClientLogin.this.username);
                                    messageHeader.setTimestamp(System.currentTimeMillis());
                                    messageHeader.setType(MessageType.REQUEST_ONLINEUSERS);
                                    m.setHeader(messageHeader);
                                    dos.write(ProtoStuffUtil.serialize(m));
                                    ClientLogin.this.dispose();
                                }else if(code == ResponseCode.LOGIN_FAILURE.getCode()){
                                    String mess = new String(response.getResponsebody(), StandardCharsets.UTF_8);
                                    JOptionPane.showMessageDialog(ClientLogin.this,mess);
                                }
                            }
                            break;
                        case NORMAL:
                            String sender = response.getResponseHeader().getSender();
                            String content = new String(response.getResponsebody(),StandardCharsets.UTF_8);
                            String m = sender + "对" + ClientLogin.this.username + "说：" +
                                    content;
                            ChatWindow chatWindow = ClientLogin.this.friendList.getChatWindow(sender);
                            if(chatWindow == null){
                                System.out.println("聊天窗口未打开");
                            }
                            else{
                                chatWindow.showMessage(m);
                            }


                            break;
                        case NOTIFY:
                            if(ClientLogin.this.isLogin){
                                ClientLogin.this.friendList.updateFriend_add(
                                        new String(response.getResponsebody(),StandardCharsets.UTF_8)
                                );
                            }
                            break;
                        case ONLINEUSERS:
                            if(ClientLogin.this.isLogin){
                                ClientLogin.this.friendList.updateFriend_get(
                                        new String(response.getResponsebody(),StandardCharsets.UTF_8)
                                );
                            }
                            break;
                        default:
                            break;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
