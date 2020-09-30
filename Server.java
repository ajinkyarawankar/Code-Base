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
public class Server {
    
    public static Inventory get_Inventory(){
        System.out.println("Configuring Server.....");
        Inventory temp = new Inventory();
        File f = new File("registered_users.txt");
        if(f.exists()){
            try{                
                ObjectInputStream in = new ObjectInputStream(new FileInputStream("registered_users.txt"));
                temp.registered_users = (ArrayList) in.readObject();
                System.out.println("User Details loaded from file");
                String path = System.getProperty("user.dir");
                System.out.println("Current root directory: "+path);
                temp.create_all_user_directory(path);       
            }
            catch(Exception e){
                System.out.println(e);                
            }
        }        
        f = new File("group_details.txt");
        if(f.exists()){
            try{                
                ObjectInputStream in = new ObjectInputStream(new FileInputStream("group_details.txt"));
                temp.group_details = (HashMap<String,ArrayList<String>>) in.readObject();
                System.out.println("Group Details loaded from file");              
            }
            catch(Exception e){
                System.out.println(e);                
            }
        }  
        f = new File("msg_details.txt");
        if(f.exists()){
            try{                
                ObjectInputStream in = new ObjectInputStream(new FileInputStream("msg_details.txt"));
                temp.msg_details = (HashMap<String,ArrayList<String>>) in.readObject();
                System.out.println("Msg Details loaded from file");              
            }
            catch(Exception e){
                System.out.println(e);                
            }
        }
        temp.get_user_names();
        return temp;        
    }
    public static void store_Inventory(Inventory inventory){
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("registered_users.txt"));  
            out.writeObject(inventory.registered_users);  
            out.flush();  
            out.close(); 
            System.out.println("User Details stored to file.");
        }
        catch(Exception e){
            System.out.println("User Details couldn't be stored to file.");
            System.out.println(e);
        }
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("group_details.txt"));  
            out.writeObject(inventory.group_details);  
            out.flush();  
            out.close(); 
            System.out.println("Group Details stored to file.");
        }
        catch(Exception e){
            System.out.println("Group Details couldn't be stored to file.");
            System.out.println(e);
        }
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("msg_details.txt"));  
            out.writeObject(inventory.msg_details);  
            out.flush();  
            out.close(); 
            System.out.println("Msg Details stored to file.");
        }
        catch(Exception e){
            System.out.println("Msg Details couldn't be stored to file.");
            System.out.println(e);
        }
    }
    
    public static void main(String args[]) throws Exception{
        String path = System.getProperty("user.dir");
        Inventory inventory = get_Inventory();
//        inventory.get_user_names();
        ServerSocket ss = new ServerSocket(9999);
        System.out.println("server is up and running");
        int x=0;
        while (true)  
        { 
            Socket s = null;       
            try 
            {                 
                s = ss.accept();                   
                System.out.println("A new client is connected : " + s);                                   
                DataInputStream din = new DataInputStream(s.getInputStream()); 
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());                   
                System.out.println("Assigning new thread for this client");                    
                Thread t = new ClientHandler(s, din, dout, inventory, path);              
                t.start();                   
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
            store_Inventory(inventory);
            if(x==2)
                break;
        }         
        ss.close();
    }
}
