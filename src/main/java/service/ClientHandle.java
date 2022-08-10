package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.JsonTool;
import tools.tool;
import view.ClientMainForm;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class ClientHandle extends Thread{
    private Socket socket;
    private ClientMainForm clientMainForm;
    private String name;

    public ClientHandle(Socket socket, ClientMainForm clientMainForm, String name){
        this.socket = socket;
        this.clientMainForm = clientMainForm;
        this.name = name;
    }

    public void run(){
       try{
           String msg;
           BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
           while((msg = reader.readLine()) != null && msg.length() > 0) {
               Map<String,Object> result;
               try{
                   result = tools.JsonTool.stringToObj(msg,Map.class);
               }catch (JsonProcessingException e){
                   e.printStackTrace();
                   continue;
               }
               if("str".equals(result.get("type"))){
                   if(result.containsKey("userList")){
                       DefaultListModel userModel = new DefaultListModel();
                       for(String name:(ArrayList<String>)result.get("userList")){
                           userModel.addElement(name);
                       }
                       this.clientMainForm.userList.setModel(userModel);
                   }
                   if(result.containsKey("content")){
                       this.clientMainForm.chatTextArea.append(result.get("content") +"\n");
                       this.clientMainForm.chatTextArea.setCaretPosition(this.clientMainForm.chatTextArea.getDocument().getLength());
                   }
               }else if("file".equals(result.get("type"))){
                   if(!this.name.equals((String) result.get("name"))){
                       String saveDirectory = this.clientMainForm.saveDirectory.getText();
                       if(saveDirectory.trim().length() <= 0){
                           JOptionPane.showMessageDialog(null,"保存接收文件失败：保存目录为空","提示",1);
                       }else{
                           File file = new File(saveDirectory);
                           if(!file.exists() && !file.isDirectory()){
                               file.mkdirs();
                           }
                           byte[] fileByte = Base64.getDecoder().decode((String) result.get("fileBase64"));
                           OutputStream output = new FileOutputStream(saveDirectory+"\\"+tool.getTime2()+"_"+result.get("fileName"));
                           output.write(fileByte);
                           output.flush();
                           output.close();
                       }
                   }
                   this.clientMainForm.chatTextArea.append(result.get("content") +"\n");
                   this.clientMainForm.chatTextArea.setCaretPosition(this.clientMainForm.chatTextArea.getDocument().getLength());
               }
           }
       }catch (IOException e){
           int result = JOptionPane.showConfirmDialog(null, "服务端已关闭：退出客户端", "提示", 2, 1);
           if(result == JOptionPane.OK_OPTION){
               System.exit(0);
           }
       }
    }
}
