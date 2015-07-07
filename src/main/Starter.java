package main;

import helpers.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeMap;
import java.util.Map.Entry;

import test.BaseLauncher;
import test.ChatLauncher;
import test.SetupLauncher;
import controller.ControllerChat;


public class Starter {
    
    public static void main(String[] args)
    {
    	try
    	{
	    	File file = new File("Settings.xml");
	        if(args.length != 0)
	        {
	        	Start(args[0],args[1],args[2],args.length >= 3?args[3]:"");
	        }
	        else if(file.exists())
	        {
	        	String ownerName = null;
	        	String botName = null;
	        	String oauth = null;
	        	String botOperator = "";
	        	FileManager fm = new FileManager("Settings");
	        	TreeMap<String, TreeMap<String, String>> map = fm.Load("Settings");
	        	for(Entry<String, TreeMap<String, String>> e : map.entrySet())
	        	{
	        		for(Entry<String, String> e2 : e.getValue().entrySet())
	        		{
	        			switch(e2.getKey())
	        			{
	        			case "ChannelOwner":
	        				ownerName = e2.getValue();
	        				break;
	        			case "BotName":
	        				botName = e2.getValue();
	        				break;
	        			case "BotOauth":
	        				oauth = e2.getValue();
	        				break;
	        			case "BotOperator":
	        				botOperator = e2.getValue();
	        				break;
	        			}
	        		}
	        	}
	        	if(oauth != null && ownerName != null && botName != null)
	        		Start(oauth, ownerName, botName, botOperator);
	        }
	        else
	        {
	        	//Start("oauth:b2myk8mwj2dq3q8fh1fgosuosnr7op", "a5vastra", "a5vbot");
	        	SetupLauncher.begin();
	        }
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public static void Start(String auth, String owner, String botName, String botOperator) throws Exception
    {
        MyBot bot = new MyBot(owner, botName, botOperator);
        
        bot.setVerbose(true);
        
        //while(true)
        {
	        try
	        {
	        	bot.connect("irc.twitch.tv", 6667, auth);
	        }
	        catch(Exception e)
	        {
	        	System.out.println("Connection failed, try again later.");
	        	//continue;
	        }
	        //break;
        }
    	
        bot.joinChannel("#"+owner);  

        
        BaseLauncher.begin();
    }
}