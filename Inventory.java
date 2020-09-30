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

public class Inventory implements Serializable{
    
    public static ArrayList<String> registered_users = new ArrayList<>();
    private static ArrayList<String> logged_in_users = new ArrayList<>();
    public static Map<String,ArrayList<String>> group_details = new HashMap<String,ArrayList<String>>();
    public static Map<String,ArrayList<String>> msg_details = new HashMap<String,ArrayList<String>>();
    
    public Inventory(){
        registered_users = new ArrayList<String>();
        logged_in_users = new ArrayList<String>();
        group_details = new HashMap<String,ArrayList<String>>();
        msg_details = new HashMap<String,ArrayList<String>>();
    }
    
    public void get_user_names(){
        System.out.println("Registered Users");
        for(String name:registered_users)
            System.out.println(name);
        System.out.println("Groups");
        Set groups = group_details.keySet();
        System.out.println(groups);
    }
    
    public boolean send_msg(String group,String msg,String user){
        if(!group_exists(group))
            return false;
        if(!msg_details.containsKey(group))
            msg_details.put(group,new ArrayList<String>());
        ArrayList<String> a = group_details.get(group);
        if(!a.contains(user))
            return false;
        ArrayList<String> m = msg_details.get(group);
        if(m.size()==10)
            m.remove(0);
        m.add(msg);
        msg_details.replace(group, m);
        return true;
    }
    
    public String check_msg(String user){
        String msg="",group;
        ArrayList<String> m;
        Set groups = msg_details.keySet();
        Iterator group_name = groups.iterator();        
        while (group_name.hasNext()) { 
            group = (String)group_name.next();
            m = group_details.get(group);
            if(m.contains(user)){
                msg = msg + "\n" + group;
                m = msg_details.get(group);
                for(String mm:m)
                    msg = msg + "\n" + mm;
            }            
        } 
        return msg;
    }
    public void create_all_user_directory(String path){        
        for(String u:registered_users){
            System.out.println(u);
            File directory = new File(path+"/"+u);
            if (! directory.exists()){
                directory.mkdir();
            }
        }
    }
    
    public boolean group_exists(String name){
        return group_details.containsKey(name);
    }
    
    public boolean add_group(String name){
       if(group_exists(name))
           return false;
       ArrayList<String> t = new ArrayList<String>();
       group_details.put(name,t);
       return true;
    }
    
    public boolean join_group(String group,String user){
        if(!group_exists(group))
            return false;
        ArrayList<String> a = group_details.get(group);
        if(a.contains(user))
            return false;
        a.add(user);
        group_details.replace(group, a);
        return true;
    }
    
    public boolean leave_group(String group,String user){
        if(!group_exists(group))
            return false;
        ArrayList<String> a = group_details.get(group);
        if(!a.contains(user))
            return false;
        a.remove(user);
        group_details.replace(group, a);
        return true;
    }
    
    
    public Set list_groups(){
        return group_details.keySet();
    }
    
    public ArrayList<String> get_group_members(String group){
        if(!group_exists(group))
            return new ArrayList<String>();
        return group_details.get(group);
    }
    
    public boolean user_exists(String user){
        return registered_users.contains(user);
    }
    
    public boolean add_user(String user){
       if(user_exists(user))
           return false;
       registered_users.add(user);
       return true;
    }
    
    public boolean remove_user(String user){
        return registered_users.remove(user);
    }
    
    public boolean user_logged_in(String user){
        return logged_in_users.contains(user);
    }
    
    public boolean log_in(String user){
        if(!user_exists(user))
            return false;
        logged_in_users.add(user);
        return true;
    }
    
    public boolean log_out(String user){
        return logged_in_users.remove(user);
    }
}
