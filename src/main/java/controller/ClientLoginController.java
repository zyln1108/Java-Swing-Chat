package controller;

import run.ClientRun;
import tools.JsonTool;
import view.ClientLoginForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ClientLoginController {
    private ClientLoginForm clientLoginForm;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String name;

    public ClientLoginController(ClientLoginForm clientLoginForm){
        this.clientLoginForm = clientLoginForm;
        init();
    }

    private void init(){
        this.clientLoginForm.loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginBtnFlase();
                connectServer();
                loginBtnTrue();
            }
        });
    }

    private void loginBtnFlase(){
        this.clientLoginForm.loginBtn.setEnabled(false);
    }

    private void loginBtnTrue(){
        this.clientLoginForm.loginBtn.setEnabled(true);
    }

    private void connectServer(){
        String ip,username;
        int port;
        try{
            ip = this.clientLoginForm.serverIp.getText();
            port = Integer.parseInt(this.clientLoginForm.serverPort.getText());
            username = this.clientLoginForm.name.getText();
            if(ip == null || username == null || ip.trim().length()<=0 || username.trim().length()<=0){
                JOptionPane.showMessageDialog(null,"请输入将信息填写完整","提示",1);
                return;
            }else if(port <0 || port >65535){
                JOptionPane.showMessageDialog(null,"请输入正确的端口号","提示",1);
                return;
            }
        }catch (Exception e1){
            JOptionPane.showMessageDialog(null,"请输入正确的端口号","提示",1);
            return;
        }

        try{
            this.name = username;
            this.socket = new Socket(ip, port);
            Thread t = new Thread(() -> {
                isLogin(ip,port);
            });
            t.start();
            Map<String,Object> toJson = new HashMap<>();
            toJson.put("type",0);
            toJson.put("name",username);
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
            this.writer.write(JsonTool.objToString(toJson)+"\n");
            this.writer.flush();

        }catch (IOException e2){
            JOptionPane.showMessageDialog(null,"服务端连接失败！","错误 ",0);
            return;
        }
    }

    private void isLogin(String ip,int port){
        try{
            String msg;
            boolean status = false;
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
            while(!status && (msg = reader.readLine()) != null) {
                Map<String,Object> result;
                result = JsonTool.stringToObj(msg,Map.class);
                if((boolean) result.get("status")){
                    status = true;
                    ClientRun.frame.setVisible(false);
                    ClientController clientController = new ClientController(this.socket,this.name);
                }else{
                    JOptionPane.showMessageDialog(null,"登陆失败：用户名已存在","提示",1);
                    this.socket.shutdownOutput();
                    this.socket.shutdownInput();
                    this.socket.close();
                }
            }
        }catch (IOException e2){
            try {
                this.socket.shutdownOutput();
                this.socket.shutdownInput();
                this.socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e2.printStackTrace();
        }
    }
}
