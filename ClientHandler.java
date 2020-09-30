/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ajinkya
 */
import java.io.*;  
import java.util.*; 
import java.net.*;

public class ClientHandler extends Thread{
    final DataInputStream din; 
    final DataOutputStream dout; 
    final Socket s; 
    final String path;
    Inventory inventory;
    public String path_list="";
    
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Inventory inventory, String path){
        this.s = s;
        this.din = dis;
        this.dout= dos;
        this.inventory = inventory;
        this.path = path;
    }
    
    public void listFiles(String path)
    {
        File folder = new File(path);
        File[] files = folder.listFiles();
        for (File file : files)
        {
            if (file.isFile())
            {
                path_list = path_list +"\n" + file.getPath();
            }
            else if (file.isDirectory())
            {
                listFiles(file.getPath());
            }
        }
    }
    
    public void run(){
        try{
            String request="",response="";     
            String current_user="";
            while(!request.equals("stop")){
                response = "invalid command";
                request = din.readUTF();
                System.out.println("Client Says: "+request);
                if(request.length()==0){
                    dout.writeUTF("no_cmd");
                    dout.flush();
                    continue;
                }
                String[] cmd=request.split("\\s");
                switch(cmd[0]){
                    default :
                        response = "invalid command";
                        break;
                    case "stop":
                        request = "stop";
                        response = "connection closed";
                        inventory.log_out(current_user);
                        Server.store_Inventory(inventory);
                        break;
                    case "create_user":
                        if(cmd.length>=2){
                            String user_name = request.substring(cmd[0].length());
                            if(inventory.add_user(user_name.trim())){
                                response = "user account is created successfully!!! :)";
                                File directory = new File(path+"/"+user_name.trim());
                                if (! directory.exists()){
                                    directory.mkdir();
                                }
                                Server.store_Inventory(inventory);
                            }                                
                            else
                                response = "sorry, user already exists. Use different user_name :(";
                        }
                        break;
                    case "remove_user":
                        if(cmd.length>=2){
                            String user_name = request.substring(cmd[0].length());
                            if(inventory.remove_user(user_name.trim())){
                                response = "user account is deleted successfully!!! :)";
                                Server.store_Inventory(inventory);
                            }                                
                            else
                                response = "sorry, user does not exists. Use different user_name :(";
                        }
                        break;
                    case "login":
                        if(cmd.length>=2){
                            if(!current_user.equals("")){
                                response = current_user+" is already logged in";
                                break;
                            }
                            String user_name = request.substring(cmd[0].length());
                            if(inventory.log_in(user_name.trim())){
                                response = "login successfull! :)";
                                current_user = cmd[1];
                            }
                            else
                                response = "Login failed. Either user does not exists or already logged in. Try register/logout.";
                        }
                        break;
                    case "logout":
                        if(inventory.log_out(current_user)){
                            response = "you have been logged out of the system.";
                            current_user = "";
                            Server.store_Inventory(inventory);
                        }                        
                        else
                            response = "Please login before you logout.";
                        break;
                    case "create_group":
                        if(cmd.length>=2){
                            if(current_user.equals("")){
                                response = "Please login first";
                                break;
                            } 
                            String group__name = request.substring(cmd[0].length());
                            if(inventory.add_group(group__name.trim()))
                                response = "Group created successfully :)";
                            else
                                response = "Group already Exists.";
                            Server.store_Inventory(inventory);
                        }
                        break;
                    case "share_msg":
                        if(cmd.length>=3){
                            if(current_user.equals("")){
                                response = "Please login first.";
                                break;
                            }
                            String temp = request.substring(cmd[0].length());
                            temp = temp.trim();
                            String[] loc = temp.split("\"");
                            if(loc.length!=3){
                                response = "Please provide both group name and message.";
                                break;
                            }
                            String msg = loc[2].trim();
                            if(msg.isEmpty()){
                                response = "Message is Empty.";
                                break;
                            }         
                            if(inventory.send_msg(loc[1].trim(), current_user+" : "+msg,current_user))
                                response = "Message sent to all group members.";
                            else
                                response = "Either you are not group member or Group does not Exists.";
                        }
                        break;
                    case "check_msg":
                        if(cmd.length==1){
                            if(current_user.equals("")){
                                response = "Please login first.";
                                break;
                            }                                                       
                            String msg = inventory.check_msg(current_user);
                            if(msg.isEmpty())
                                response = "No messages";
                            else
                                response = msg;
                        }
                        break;
                    case "list_groups":
                        if(current_user.equals("")){
                            response = "Please login first";
                            break;
                        } 
                        Set groups = inventory.list_groups();
                        Iterator group_name = groups.iterator();
                        response = "";
                        while (group_name.hasNext()) { 
                            response = response + "\n" + group_name.next(); 
                        } 
                        break;
                    case "join_group":
                        if(cmd.length>=2){
                            if(current_user.equals("")){
                                response = "Please login first";
                                break;
                            }
                            String group__name = request.substring(cmd[0].length());
                            if(inventory.join_group(group__name.trim(), current_user))
                                response = "Group joined successfully :)";
                            else
                                response = "Sorry, Either group does not exists or you are already a member";
                            Server.store_Inventory(inventory);
                        }
                        break;
                    case "leave_group":
                        if(cmd.length>=2){
                            if(current_user.equals("")){
                                response = "Please login first";
                                break;
                            }
                            String group__name = request.substring(cmd[0].length());                            
                            if(inventory.leave_group(group__name.trim(), current_user))
                                response = "Group leaved successfully :)";
                            else
                                response = "Sorry, Either group does not exists or you have already left it";
                            Server.store_Inventory(inventory);
                        }
                        break;
                    case "list_detail":
                        if(cmd.length>=2){
                            if(current_user.equals("")){
                                response = "Please login first";
                                break;
                            }
                            try{
                                String group__name = new String();
                                group__name = request.substring(cmd[0].length());
                                ArrayList<String> users = inventory.get_group_members(group__name.trim());
                                if(users == null || users.size()==0){
                                    response = "No members in this group";
                                    break;
                                }
                                if(!users.contains(current_user)){
                                    response = "You are not member of this group";
                                    break;
                                }
                                response = "";
                                path_list = "";
                                for(String name:users){
                                    path_list = path_list + "\n" + name;
                                    listFiles(name);
                                }
                                response = path_list;
                                path_list = "";
                            }
                            catch(Exception e){
                                System.out.println(e);
                                response = "Failed";
                                break;
                            }                            
                        }
                        break;
                    case "move_file":
                        if(cmd.length>=3){
                            if(current_user.equals("")){
                                response = "Please login first";
                                break;
                            }  
                            String temp = request.substring(cmd[0].length());
                            temp = temp.trim();
                            String[] loc = temp.split("\"");
                            if(loc.length!=3){
                                response = "Please provide both source and destination.";
                                break;
                            }
                            File source = new File(loc[1].trim());
                            File destination = new File(loc[2].trim());
                            System.out.println(source.getPath()+" "+destination.getPath());
                            if(source.renameTo(destination))
                                response = "File moved successfully :)";
                            else
                                response = "File move Failed. Check file path again. :(";
                        }
                        break;
                    case "create_folder":
                        if(cmd.length>=2){ 
                            if(current_user.equals("")){
                                response = "Please login first";
                                break;
                            }
                            try{
                                String folder_name = request.substring(cmd[0].length());
                                File directory = new File(path+"/"+current_user+"/"+folder_name.trim());
                                if (! directory.exists()){
                                    directory.mkdirs();
                                    response = "Folder created sussessfully. :)";
                                }   
                                else
                                    response = "Folder already exists. :)";
                            }
                            catch(Exception e){
                                System.out.println(e);
                                response = "Sorry. Failed to create Folder. Please check the path provided :( ";
                            }
                        }
                        break;
                    case "upload_udp":
                        if(current_user.equals("")){
                            response = "Please login first";
                            break;
                        }
                        response ="Ready";
                        dout.writeUTF(response);
                        dout.flush();
                        String file_name_udp = din.readUTF();
                        long file_size_to_recieve_udp = Long.parseLong(din.readUTF());
                        File f_udp = new File(current_user+"/"+file_name_udp);
                        if(f_udp.exists()){
                            response = "File already Present";
                            break;
                        }
                        response = "Ready to recieve";                        
                        dout.writeUTF(response);
                        dout.flush();
                        
                        DatagramSocket dsoc = new DatagramSocket(10000);                        
                        FileOutputStream bos_udp = new FileOutputStream(f_udp);
                        byte[] buf = new byte[63*1024];
                        DatagramPacket pkg = new DatagramPacket(buf, buf.length);
                        while(true)
                        {
                            dsoc.receive(pkg);
                            if (new String(pkg.getData(), 0, pkg.getLength()).equals("end"))
                            {
                                System.out.println("Documents received");
                                bos_udp.close();
                                dsoc.close();
                                break;
                            }
                            bos_udp.write(pkg.getData(), 0, pkg.getLength());
                            bos_udp.flush();
                        }
                        bos_udp.close();
                        dsoc.close();
                        response = "File Recieved.";                        
                        break;
                    case "upload":
                        if(current_user.equals("")){
                            response = "Please login first";
                            break;
                        }                        
                        String file_name = din.readUTF();
                        long file_size_to_recieve = Long.parseLong(din.readUTF());
                        File f = new File(current_user+"/"+file_name);
                        if(f.exists()){
                            response = "File already Present";
                            break;
                        }
                        response = "Ready to recieve";                        
                        dout.writeUTF(response);
                        dout.flush();                        
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                        long file_size_recieved=0;
                        System.out.println("Recieving "+current_user+"/"+file_name+" with size "+file_size_to_recieve);
                        int bytesRead = 0;   
                        byte[] contents = new byte[10000];
                        while(file_size_recieved!=file_size_to_recieve && (bytesRead=din.read(contents))!=-1){
                            bos.write(contents, 0, bytesRead); 
                            bos.flush();
                            file_size_recieved = file_size_recieved+bytesRead;
                            System.out.println("No. of Bytes Received : " + bytesRead);
                        }                            
                        bos.close(); 
                        response = "File Recived";
                        System.out.println("Recieved");
                        break;
                    case "get_file":
                        if(current_user.equals("")){
                            response = "Please login first";
                            break;
                        }
                        dout.writeUTF("send file name");
                        dout.flush();
                        request = din.readUTF();
                        System.out.println(request);
                        File upf = new File(request);
                        if(!upf.exists() || !upf.isFile()){
                            response = "FNE";
                            break;
                        }
                        response = String.valueOf(upf.length());
                        dout.writeUTF(response);
                        dout.flush();
                        
                        byte[] contents_send;
                        long fileLength = upf.length(); 
                        System.out.println("File size "+fileLength);
                        long current = 0;
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(upf)); 
                        while(current!=fileLength){ 
                            int size = 10000;
                            if(fileLength - current >= size)
                                current += size;    
                            else{ 
                                size = (int)(fileLength - current); 
                                current = fileLength;
                            } 
                            contents_send = new byte[size]; 
                            bis.read(contents_send, 0, size); 
                            dout.write(contents_send);
                            dout.flush();
                            System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
                        }                 
                        System.out.println("Done sending...waiting for server confirmation");
                        request = din.readUTF();
                        System.out.println(request);                        
                        response = "Done Sending";
                        break;
                }                
                dout.writeUTF(response);
                dout.flush();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }        
    }
}
