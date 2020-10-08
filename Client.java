/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ajinkya
 */
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
public class Client {
    public static void main(String args[]) throws Exception{
        Socket s = null;
        DataInputStream din = null;
        DataOutputStream dout = null;
        String path = System.getProperty("user.dir");
        try{
            String ip = args[0];
//            String ip = "10.1.33.98";
            s = new Socket(ip,9999);
            System.out.println("Connected to server");
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
            String s1="",s2="";
            String[] cmd;
            while(!s1.equals("stop")){
                System.out.print("cmd: ");
                s1 = br.readLine();
                cmd = s1.split("\\s");
                if(cmd[0].equals("upload")){
                    String file_location= s1.substring(cmd[0].length()).trim();                    
                    System.out.println(path+"/"+file_location);
                    File f = new File(file_location);
                    if(f.exists()){    
                        s1 = "upload";
                        dout.writeUTF(s1);
                        dout.writeUTF(f.getName());
                        dout.writeUTF(String.valueOf(f.length()));
                        dout.flush();
                        s2 = din.readUTF();
                        if(s2.equals("Please login first")){
                            System.out.println(s2);
                            continue;
                        }
                        if(s2.equals("File already Present")){
                            System.out.println(s2);
                            continue;
                        }                        
                        System.out.println(s2);
                        System.out.println("File is being uploaded");
                        byte[] contents;
                        long fileLength = f.length(); 
                        System.out.println("File size "+fileLength);
                        long current = 0;
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f)); 
                        while(current!=fileLength){ 
                            int size = 10000;
                            if(fileLength - current >= size)
                                current += size;    
                            else{ 
                                size = (int)(fileLength - current); 
                                current = fileLength;
                            } 
                            contents = new byte[size]; 
                            bis.read(contents, 0, size); 
                            dout.write(contents);
                            dout.flush();
                            System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
                        }                 
                        System.out.println("Done sending...waiting for server confirmation");
                        s2 = din.readUTF();
                        System.out.println("Server Says: "+s2);
                    }
                    else{
                        System.out.println("File does not exists");                        
                    }
                    continue;
                }
                if(cmd[0].equals("get_file")){
                    String file_name= s1.substring(cmd[0].length());
                    String[] fn = file_name.split("/");
                    if(fn.length<2){
                        System.out.println("Provide group name, user name and filepath also.");
                        continue;
                    }
                    int boost=0;
                    if(fn[0].trim().equals(""))
                        boost++;
                    file_name=fn[boost+1].trim();
                    for(int i=boost+2;i<fn.length;i++)
                        file_name = file_name + "/" +fn[i].trim();
                    System.out.println(file_name);
                    File f = new File(fn[fn.length-1]);
                    if(f.exists()){
                        System.out.println("File already Exists.");
                        continue;
                    }
                    dout.writeUTF(cmd[0]);
                    dout.flush();
                    s1=din.readUTF();
                    if(s1.equals("Please login first")){
                        System.out.println(s1);
                        continue;
                    }                        
                    s1=file_name;
                    dout.writeUTF(s1);
                    dout.flush();
                    s2 = din.readUTF();
                    if(s2.equals("FNE")){
                        System.out.println("File does not Exists on server");
                        continue;
                    }
                    long file_size_to_recieve = Long.parseLong(s2);
                    
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                    long file_size_recieved=0;
                    System.out.println("Recieving "+file_name+" with size "+file_size_to_recieve);
                    int bytesRead = 0;   
                    byte[] contents = new byte[10000];
                    while(file_size_recieved!=file_size_to_recieve && (bytesRead=din.read(contents))!=-1){
                        bos.write(contents, 0, bytesRead); 
                        bos.flush();
                        file_size_recieved = file_size_recieved+bytesRead;
                        System.out.println("No. of Bytes Received : " + bytesRead);
                    }                            
                    bos.close(); 
                    s2 = "File Recived";
                    System.out.println("Recieved");
                    dout.writeUTF(s2);
                    dout.flush();  
                    s2 = din.readUTF();
                    System.out.println("Server Says: "+s2);
                    continue;
                }
                if(cmd[0].equals("upload_udp")){
                    String file_location= s1.substring(cmd[0].length()).trim(); 
                    file_location = file_location.trim();
                    System.out.println(path+"/"+file_location);
                    File f = new File(file_location);
                    if(f.exists()){    
                        s1 = "upload_udp";
                        dout.writeUTF(s1);
                        dout.flush();
                        s2 = din.readUTF();
                        if(s2.equals("Please login first")){
                            System.out.println(s2);
                            continue;
                        }       
                        System.out.println(s2);
                        dout.writeUTF(f.getName());
                        dout.flush();
                        dout.writeUTF(String.valueOf(f.length()));
                        dout.flush();
                        s2 = din.readUTF(); //should get Ready to recieve
                        if(s2.equals("File already Present")){
                            System.out.println(s2);
                            continue;
                        }
                        System.out.println(s2);
                        TimeUnit.SECONDS.sleep(2);
                        DatagramSocket send = new DatagramSocket();                        
                        FileInputStream bis = new FileInputStream(f);
                        byte[] buf = new byte[63*1024];
                        int len;

                        DatagramPacket pkg = new DatagramPacket(buf, buf.length,InetAddress.getByName(ip),10000);
                        while((len=bis.read(buf))!=-1)
                        {
                            send.send(pkg);
                        }
                        try{
                            buf = "end".getBytes();
                            DatagramPacket endpkg = new DatagramPacket(buf, buf.length,InetAddress.getByName(ip),10000);
                            send.send(endpkg);
                            buf = "end".getBytes();
                            endpkg = new DatagramPacket(buf, buf.length,InetAddress.getByName(ip),10000);
                            send.send(endpkg);
                            buf = "end".getBytes();
                            endpkg = new DatagramPacket(buf, buf.length,InetAddress.getByName(ip),10000);
                            send.send(endpkg);
                        }
                        catch(Exception e){
                            System.out.println(e);
                        }                        
                        System.out.println("Send the file.");                        
                        bis.close();
                        send.close();
                        s2 = din.readUTF();
                        System.out.println("Server Says: "+s2);
                        continue;
                    }
                    else{
                        System.out.println("File does not exists");
                        continue;
                    }
                    
                }
                dout.writeUTF(s1);
                dout.flush();
                s2 = din.readUTF();
                System.out.println("Server Says: "+s2);
            }
            din.close();
            dout.close();
            s.close();
        }
        catch(UnknownHostException u) 
        { 
            System.out.println(u); 
        } 
        catch(IOException i) 
        { 
            System.out.println(i); 
        } 
        catch(Exception e){
            System.out.println(e);
        }
        
    }
}
