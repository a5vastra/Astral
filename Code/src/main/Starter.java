package main;

import helpers.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;

import test.BaseLauncher;
import test.ChatLauncher;
import test.SetupLauncher;
import controller.ControllerChat;


public class Starter {
    
    public static void main(String[] args) throws Exception {
    	File file = new File("Settings.xml");
        if(args.length != 0)
        {
        	Start(args[0],args[1],args[2]);
        }
        else if(file.exists())
        {
        	String ownerName = null;
        	String botName = null;
        	String oauth = null;
        	FileManager fm = new FileManager("Settings");
        	HashMap<String, HashMap<String, String>> map = fm.Load("Settings");
        	for(Entry<String, HashMap<String, String>> e : map.entrySet())
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
        			}
        		}
        	}
        	if(oauth != null && ownerName != null && botName != null)
        		Start(oauth, ownerName, botName);
        }
        else
        {
        	//Start("oauth:b2myk8mwj2dq3q8fh1fgosuosnr7op", "a5vastra", "a5vbot");
        	SetupLauncher.begin();
        }
    }
    public static void Start(String auth, String owner, String botName) throws Exception
    {
        MyBot bot = new MyBot(owner, botName);
        
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