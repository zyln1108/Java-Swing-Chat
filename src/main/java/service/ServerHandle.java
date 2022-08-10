package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import controller.ServerController;
import tools.JsonTool;
import tools.tool;
import view.ServerMainForm;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerHandle extends Thread{
    private Socket socket;
    private ServerMainForm serverMainForm;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String name;

    public ServerHandle(Socket socket, ServerMainForm serverMainForm) {
        this.socket = socket;
        this.serverMainForm = serverMainForm;

        InputStream input = null;
        OutputStream output = null;
        try {
            input = this.socket.getInputStream();
            output = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
    }

    public void run(){
        String msg;
        try{
            while ((msg = reader.readLine()) != null){
                Map<String,Object> result;
                try{
                    result = tools.JsonTool.stringToObj(msg,Map.class);
                }catch (JsonProcessingException e){
                    //e.printStackTrace();
                    continue;
                }
                switch ((int)result.get("type")){
                    //获取用户列表
                    case -1:
                        Map<String,Object> toJson0 = new HashMap<>();
                        toJson0.put("type","str");
                        toJson0.put("userList",getUserList());
                        this.writer.write(tools.JsonTool.objToString(toJson0)+"\n");
                        this.writer.flush();
                        break;
                    //用户注册
                    case 0:
                        String toMsg = null;
                        Map<String,Object> toJson = new HashMap<>();
                        if(ServerController.userInfo.containsKey((String) result.get("name"))){
                            toMsg = "用户名已存在";
                            toJson.put("status",false);
                            toJson.put("type","str");
                            toJson.put("content",toMsg);
                            this.writer.write(tools.JsonTool.objToString(toJson)+"\n");
                            this.writer.flush();
                        }else{
                            this.name = (String) result.get("name");
                            ServerController.userInfo.put((String) result.get("name"),this.socket);
                            serverMainForm.onlineNumField.setText(String.valueOf(ServerController.userInfo.size()));
                            toMsg = tool.getTime()+" 用户 "+(String) result.get("name")+" 加入了聊天,当前人数："+ServerController.userInfo.size();
                            toJson.put("status",true);
                            toJson.put("type","str");
                            toJson.put("content",toMsg);
                            toJson.put("userList",getUserList());
                            serverMainForm.logTextArea.append(toMsg+"\n");
                            serverMainForm.logTextArea.setCaretPosition(serverMainForm.logTextArea.getDocument().getLength());
                            sendAll(tools.JsonTool.objToString(toJson)+"\n",true);
                        }
                        break;
                        //私聊消息
                    case 1:
                        String toMsg2;
                        Map<String,Object> toJson2 = new HashMap<>();
                        if(!ServerController.userInfo.containsKey((String) result.get("toName"))){
                            toMsg2 = tool.getTime()+" 用户 "+(String)result.get("toName")+" 已离线，无法私聊";
                            toJson2.put("type","str");
                            toJson2.put("content",toMsg2);
                            send(tools.JsonTool.objToString(toJson2)+"\n",(String) result.get("name"));
                        }else{
                            toMsg2 = tool.getTime()+" "+(String)result.get("name")+"@"+(String)result.get("toName")+"："+(String)result.get("content");
                            toJson2.put("type","str");
                            toJson2.put("content",toMsg2);
                            if(result.get("toName").equals(result.get("name"))){
                                send(tools.JsonTool.objToString(toJson2)+"\n",(String) result.get("toName"));
                            }else{
                                send(tools.JsonTool.objToString(toJson2)+"\n",(String) result.get("toName"));
                                send(tools.JsonTool.objToString(toJson2)+"\n",(String) result.get("name"));
                            }
                        }
                        serverMainForm.logTextArea.append(toMsg2+"\n");
                        serverMainForm.logTextArea.setCaretPosition(serverMainForm.logTextArea.getDocument().getLength());
                        break;
                        //群聊消息
                    case 2:
                        String toMsg3;
                        Map<String,Object> toJson3 = new HashMap<>();
                        toMsg3 = tool.getTime()+" "+(String)result.get("name")+"："+(String) result.get("content");
                        toJson3.put("type","str");
                        toJson3.put("content",toMsg3);
                        sendAll(tools.JsonTool.objToString(toJson3)+"\n",true);
                        serverMainForm.logTextArea.append(toMsg3+"\n");
                        serverMainForm.logTextArea.setCaretPosition(serverMainForm.logTextArea.getDocument().getLength());
                        break;
                    case 3:
                        String toMsg4;
                        Map<String,Object> toJson4 = new HashMap<>();
                        if(!ServerController.userInfo.containsKey((String) result.get("toName"))){
                            toMsg4 = tool.getTime()+" 用户 "+(String)result.get("toName")+" 已离线，无法私聊";
                            toJson4.put("type","str");
                            toJson4.put("content",toMsg4);
                            send(tools.JsonTool.objToString(toJson4)+"\n",(String) result.get("name"));
                            serverMainForm.logTextArea.append(toMsg4+"\n");
                            serverMainForm.logTextArea.setCaretPosition(serverMainForm.logTextArea.getDocument().getLength());
                        }else{
                            toMsg4 = tool.getTime()+" "+(String)result.get("name")+"@"+(String)result.get("toName")+"："+(String) result.get("content");
                            toJson4.put("type","file");
                            toJson4.put("content",toMsg4);
                            toJson4.put("name",(String)result.get("name"));
                            toJson4.put("fileName",(String)result.get("fileName"));
                            toJson4.put("fileBase64",(String)result.get("fileBase64"));
                            send(tools.JsonTool.objToString(toJson4)+"\n",(String) result.get("toName"));
                            send(tools.JsonTool.objToString(toJson4)+"\n",(String) result.get("name"));
                            serverMainForm.logTextArea.append(toMsg4+"\n");
                            serverMainForm.logTextArea.setCaretPosition(serverMainForm.logTextArea.getDocument().getLength());
                        }
                        break;
                    case 4:
                        String toMsg5;
                        Map<String,Object> toJson5 = new HashMap<>();
                        toMsg5 = tool.getTime()+" "+(String)result.get("name")+"："+(String) result.get("content");
                        toJson5.put("type","file");
                        toJson5.put("content",toMsg5);
                        toJson5.put("name",(String)result.get("name"));
                        toJson5.put("fileName",(String)result.get("fileName"));
                        toJson5.put("fileBase64",(String)result.get("fileBase64"));
                        sendAll(tools.JsonTool.objToString(toJson5)+"\n",true);
                        serverMainForm.logTextArea.append(toMsg5+"\n");
                        serverMainForm.logTextArea.setCaretPosition(serverMainForm.logTextArea.getDocument().getLength());
                        break;
                }
            }
        }catch (IOException e){
            //e.printStackTrace();
           if(ServerController.userInfo.containsKey(this.name)){
               ServerController.userInfo.remove(this.name);
               serverMainForm.onlineNumField.setText(String.valueOf(ServerController.userInfo.size()));
               String toMsg = null;
               Map<String,Object> toJson = new HashMap<>();
               toMsg = tool.getTime()+" 用户 "+this.name+" 退出了聊天,当前人数："+ServerController.userInfo.size();
               toJson.put("type","str");
               toJson.put("content",toMsg);
               toJson.put("userList",getUserList());
               sendAll(tools.JsonTool.objToString(toJson)+"\n",false);
               serverMainForm.logTextArea.append(toMsg+"\n");
               serverMainForm.logTextArea.setCaretPosition(serverMainForm.logTextArea.getDocument().getLength());
           }
        }
    }


    private void send(String msg,String name){
        try {
            Socket toSocket = ServerController.userInfo.get(name);
            if(toSocket != null){
                OutputStream output = toSocket.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
                writer.write(msg);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAll(String msg,boolean sendThis){
        for (Map.Entry<String,Socket> clientSocket : ServerController.userInfo.entrySet()) {
            if(this.socket.equals(clientSocket.getValue()) && !sendThis){
                continue;
            }
            Thread t = new Thread(() -> {
                try{
                    OutputStream output = clientSocket.getValue().getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
                    writer.write(msg);
                    writer.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }

    private ArrayList<String> getUserList(){
        ArrayList<String> nameList = new ArrayList<>();
        for (String name : ServerController.userInfo.keySet()) {
            nameList.add(name);
        }
        return nameList;
    }
}
