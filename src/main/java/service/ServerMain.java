package service;

import tools.tool;
import view.ServerMainForm;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private ServerMainForm serverMainForm;
    private ServerSocket serverSocket;

    public ServerMain(ServerMainForm serverMainForm){
        this.serverMainForm = serverMainForm;
    }

    public void start(int port,String ip){
        Thread ssThread = new Thread(() -> {
            try {
                this.serverSocket = new ServerSocket(port,50, InetAddress.getByName(ip));
                if(!this.serverSocket.isClosed()){
                    this.serverMainForm.startServer.setEnabled(false);
                    this.serverMainForm.serverStatusField.setText("已启动");
                    this.serverMainForm.logTextArea.append(tool.getTime()+" 启动服务\n");
                    this.serverMainForm.logTextArea.setCaretPosition(this.serverMainForm.logTextArea.getDocument().getLength());
                    while (true){
                        Socket sock = this.serverSocket.accept();
                        Thread serverHandle = new ServerHandle(sock,this.serverMainForm);
                        serverHandle.start();
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                JOptionPane.showMessageDialog(null,"启动服务失败：请检查IP是否正确，端口是否被占用","启动服务",2);
            }
        });
        ssThread.start();
    }

}
