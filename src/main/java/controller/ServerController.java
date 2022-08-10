package controller;

import service.ServerMain;
import view.ServerMainForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerController {
    private ServerMainForm serverMainForm;
    public static Map<String, Socket> userInfo = new HashMap<>();

    public ServerController(ServerMainForm serverMainForm){
        this.serverMainForm = serverMainForm;
        init();
    }

    private void init(){

        //启动服务按钮事件
        serverMainForm.startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int port = Integer.parseInt(serverMainForm.serverPort.getText());
                    String ip =serverMainForm.serverIp.getText();
                    if(port <0 || port >65535){
                        JOptionPane.showMessageDialog(null,"请输入正确的端口号","提示",1);
                    }else{
                        ServerMain serverMain = new ServerMain(serverMainForm);
                        serverMain.start(port,ip);
                    }
                }catch (Exception e1){
                    //e1.printStackTrace();
                    JOptionPane.showMessageDialog(null,"请输入正确的端口号","提示",1);
                }
            }
        });

        //清除日志按钮事件
        serverMainForm.clearLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverMainForm.logTextArea.setText(null);
            }
        });

        //关闭服务按钮事件
        serverMainForm.stopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

}
