package view;

import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tt extends JFrame{
//    private TextField tfText;
//    public tt(){
//        this.setBounds(10, 10, 10, 10);
//        this.setLayout(new BorderLayout());
//        tfText = new TextField();
//        this.add(tfText);
//
//        tfText.addActionListener((l)->{
//            String str = tfText.getText().trim();
//            System.out.println(str);
//            tfText.setText("");
//
//        });
//        this.setVisible(true);
//    }

    public static void main(String[] args){
        H h = new H();
        for(int i=0;i<1;i++)
        new Thread(new Threadtest()).start();

    }



}

class H{
    private static Map<String,String> u;
    public H(){
        u = new HashMap<>();
        u.put("user1", "1");
        u.put("user2", "2");
        u.put("user3", "5");
        u.put("user4", "3");
        u.put("user5", "4");
    }

    public static String get(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> map : u.entrySet()){
            System.out.println(map.getValue());
            sb.append(map.getValue());
        }
        return sb.toString();
    }

}


 class Threadtest implements Runnable{

    @Override
    public void run() {
        System.out.println(H.get());
    }
}
