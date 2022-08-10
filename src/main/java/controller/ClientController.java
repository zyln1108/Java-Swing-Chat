package controller;

import com.formdev.flatlaf.FlatDarkLaf;
import service.ClientHandle;
import service.ServerHandle;
import tools.JsonTool;
import tools.SwingUtils;
import view.ClientMainForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ClientController {
    private ClientMainForm clientMainForm;
    private Socket socket;
    private String name;

    ClientController(Socket socket,String name){
        this.socket = socket;
        this.name = name;
        creatClientMainForm();
        Thread serverHandle = new ClientHandle(this.socket,this.clientMainForm,this.name);
        serverHandle.start();
        getUserList();
        init();
    }

    private void init(){
        //消息输入框回车事件
        this.clientMainForm.content.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyChar()==KeyEvent.VK_ENTER ){
                    send();
                }
            }
        });

        //发送消息按钮事件
        this.clientMainForm.send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        //选择文件保存目录按钮事件
        this.clientMainForm.selectDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDirectory();
            }
        });

        //选择发送文件按钮事件
        this.clientMainForm.selectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });

        //发送文件按钮事件
        this.clientMainForm.sendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFileBtn(false);
                sendFile();
                sendFileBtn(true);
            }
        });
    }

    private void selectDirectory(){
        JFileChooser chooser=new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret=chooser.showOpenDialog(null);
        if(ret==JFileChooser.APPROVE_OPTION) {
            File file=chooser.getSelectedFile();
            this.clientMainForm.saveDirectory.setText(file.getAbsolutePath());
        }
    }

    private void selectFile(){
        JFileChooser chooser=new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int ret=chooser.showOpenDialog(null);
        if(ret==JFileChooser.APPROVE_OPTION) {
            File file=chooser.getSelectedFile();
            this.clientMainForm.sendFilePath.setText(file.getAbsolutePath());
        }
    }

    private void getUserList(){
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
            Map<String,Object> toJson = new HashMap<>();
            toJson.put("type",-1);
            writer.write(JsonTool.objToString(toJson)+"\n");
            writer.flush();
        } catch (IOException e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null,"获取用户列表失败！","错误 ",0);
        }
    }

    private void sendFileBtn(boolean status){
        this.clientMainForm.sendFile.setEnabled(status);
    }

    private void sendFile() {
        String sendFilePath = this.clientMainForm.sendFilePath.getText();
        String sendRange = (String) this.clientMainForm.sendRange.getSelectedItem();
        File file = new File(sendFilePath);
        if(sendFilePath.trim().length() <= 0){
            JOptionPane.showMessageDialog(null,"发送文件不能为空","提示",1);
            return;
        }else if(!file.exists()){
            JOptionPane.showMessageDialog(null,"发送文件不存在","提示",1);
            return;
        }
        byte[] buffer;
        try(InputStream input = new FileInputStream(sendFilePath)){
            buffer = new byte[input.available()];
            input.read(buffer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"发送文件失败：读取文件错误","错误 ",0);
            return;
        }
        if("私聊".equals(sendRange)){
            if(this.clientMainForm.userList.isSelectionEmpty()){
                JOptionPane.showMessageDialog(null,"请选择私聊用户","提示",1);
            }else if(this.name.equals(this.clientMainForm.userList.getSelectedValue().toString())){
                JOptionPane.showMessageDialog(null,"私聊对象不能为自己","提示",1);
            }else {
                try {
                    String toName = this.clientMainForm.userList.getSelectedValue().toString();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
                    Map<String,Object> toJson = new HashMap<>();
                    toJson.put("type",3);
                    toJson.put("name",this.name);
                    toJson.put("toName",toName);
                    toJson.put("content","【文件】"+file.getName());
                    toJson.put("fileName",file.getName());
                    toJson.put("fileBase64", Base64.getEncoder().encodeToString(buffer));
                    writer.write(JsonTool.objToString(toJson)+"\n");
                    writer.flush();
                    this.clientMainForm.sendFilePath.setText(null);
                } catch (IOException e) {
                    //e.printStackTrace();
                    JOptionPane.showMessageDialog(null,"发送失败！","错误 ",0);
                }
            }
        }else if("群聊".equals(sendRange)){
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
                Map<String,Object> toJson = new HashMap<>();
                toJson.put("type",4);
                toJson.put("name",this.name);
                toJson.put("content","【文件】"+file.getName());
                toJson.put("fileName",file.getName());
                toJson.put("fileBase64", Base64.getEncoder().encodeToString(buffer));
                writer.write(JsonTool.objToString(toJson)+"\n");
                writer.flush();
                this.clientMainForm.sendFilePath.setText(null);
            } catch (IOException e) {
                //e.printStackTrace();
                JOptionPane.showMessageDialog(null,"发送失败！","错误 ",0);
            }
        }
    }

    private void send(){
        String sendRange = (String) this.clientMainForm.sendRange.getSelectedItem();
        String content = this.clientMainForm.content.getText();
        if(content == null || content.trim().length() <= 0){
            JOptionPane.showMessageDialog(null,"发送消息不能为空","提示",1);
        }else if("私聊".equals(sendRange)){
            if(this.clientMainForm.userList.isSelectionEmpty()){
                JOptionPane.showMessageDialog(null,"请选择私聊用户","提示",1);
            }else if(this.name.equals(this.clientMainForm.userList.getSelectedValue().toString())){
                JOptionPane.showMessageDialog(null,"私聊对象不能为自己","提示",1);
            }else{
                try {
                    String toName = this.clientMainForm.userList.getSelectedValue().toString();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
                    Map<String,Object> toJson = new HashMap<>();
                    toJson.put("type",1);
                    toJson.put("name",this.name);
                    toJson.put("toName",toName);
                    toJson.put("content",content);
                    writer.write(JsonTool.objToString(toJson)+"\n");
                    writer.flush();
                    this.clientMainForm.content.setText(null);
                } catch (IOException e) {
                    //e.printStackTrace();
                    JOptionPane.showMessageDialog(null,"发送失败！","错误 ",0);
                }
            }
        }else if ("群聊".equals(sendRange)){
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
                Map<String,Object> toJson = new HashMap<>();
                toJson.put("type",2);
                toJson.put("name",this.name);
                toJson.put("content",content);
                writer.write(JsonTool.objToString(toJson)+"\n");
                writer.flush();
                this.clientMainForm.content.setText(null);
            } catch (IOException e) {
                //e.printStackTrace();
                JOptionPane.showMessageDialog(null,"发送失败！","错误 ",0);
            }
        }
    }

    private void creatClientMainForm(){
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "UI风格设置", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame frame = new JFrame("Chat客户端");
        Image image = Toolkit.getDefaultToolkit().getImage(frame.getClass().getResource("/icon.png"));
        frame.setIconImage(image);
        ClientMainForm clientMainForm = new ClientMainForm();
        frame.setContentPane(clientMainForm.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.setSize(frame, 0.55, 0.5);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        this.clientMainForm = clientMainForm;
    }
}
