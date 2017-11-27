package view;

import common.domain.Message;
import common.domain.MessageHeader;
import common.enumeration.MessageType;
import common.util.ProtoStuffUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatWindow extends JFrame implements ActionListener{
    JTextArea jta;
    JTextField jtf;
    JButton jb;
    JPanel jp;
    String username;
    String friendId;
    private Socket s;
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //QqChat qqChat=new QqChat("1");
    }

    public ChatWindow(String ownerId,String friend,Socket s)
    {
        this.username = ownerId;
        this.friendId=friend;
        this.s = s;
        jta=new JTextArea();
        jtf=new JTextField(15);
        jb=new JButton("发送");
        jb.addActionListener(this);
        jp=new JPanel();
        jp.add(jtf);
        jp.add(jb);

        this.add(jta,"Center");
        this.add(jp,"South");
        this.setTitle(ownerId+" 正在和 "+friend+" 聊天");
        this.setIconImage((new ImageIcon("image/qq.gif").getImage()));
        this.setSize(300, 200);
        this.setVisible(true);


    }

    //写一个方法，让它显示消息
    public void showMessage(String m)
    {
//        String info=m.getSender()+" 对 "+m.getGetter()+" 说:"+m.getCon()+"\r\n";
        this.jta.append("\r\n");
        this.jta.append(m);
    }

    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        if(arg0.getSource()==jb) {
//            //如果用户点击了，发送按钮
            Message m=new Message();
            MessageHeader messageHeader = new MessageHeader();
            messageHeader.setSender(this.username);
            messageHeader.setReceiver(this.friendId);
            messageHeader.setType(MessageType.NORMAL);
            messageHeader.setTimestamp(System.currentTimeMillis());
            m.setHeader(messageHeader);
            String content = jtf.getText();
            jtf.setText("");
            m.setBody(content.getBytes(StandardCharsets.UTF_8));
            String c = this.username + "对" + this.friendId +
                    "说：" + content + "\r\n";
            this.showMessage(c);
            try{
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.write(ProtoStuffUtil.serialize(m));
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


}
