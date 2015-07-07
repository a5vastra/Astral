package addons;
import helpers.MiniTimer;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.MyBot;


public abstract class Addon {
	protected String myName;
	public String getName(){ return myName; }
	private boolean enabled = true;
	private TreeMap<String, Pattern> patterns = new TreeMap<String, Pattern>();
	protected TreeMap<String, TreeMap<String, String>> map = new TreeMap<String, TreeMap<String, String>>();
	private TreeMap<String, MiniTimer> timers = new TreeMap<String, MiniTimer>();
	protected TreeMap<String, String> settings = new TreeMap<String, String>();
	public Addon()
	{
		
	}
	public final boolean find(Matcher m)
	{
		if(m.find())
		{
			MyBot.instance.messageFound();
			foundHere = true;
			return true;
		}
		return false;
	}
	//on find, do this (override)
	public void find2()
	{
		
	}
	public boolean check(boolean b)
	{
		if(b)
			MyBot.instance.messageFound();
		return b;
	}
	boolean foundHere = false; 
	public final void onMessage(String sender, String message)
	{
		Pattern p = getOrRegisterPattern("enabledisable", "^!(?<endis>enable|disable) "+getName()+"$");
		Matcher m = p.matcher(message);
		if(find(m))
		{
			switch(m.group("endis"))
			{
			case "enable":
				enable();
				break;
			case "disable":
				disable();
				break;
			}
		}
		if(!enabled)
			return;
		onMsg(sender, message);
		if(foundHere)
		{
			find2();
			foundHere = false;
		}
	}
	public void onMsg(String sender, String message)
	{
		
	}
	public void onJoin(String user)
	{
		
	}
	public void onExit(String user)
	{
		
	}
	public boolean enabled()
	{
		return enabled;
	}
	public void enable()
	{
		enabled = true;
	}
	public void disable()
	{
		enabled = false;
	}
	public Pattern getOrRegisterPattern(String name, String regex)
	{
		if(!patterns.containsKey(name))
			patterns.put(name, Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
		//System.out.println(name+"\n\t"+regex);
		return patterns.get(name);
	}
	public void addTimer(String key, long delay, long period)
	{
		timers.put(key, new MiniTimer(this, key, delay, period));
	}
	public void doneWithTime(String key) {
		
	}
	public void Load()
	{
		
	}
	public void Save()
	{
		
	}
	public void msg(String msg)
	{
		MyBot.message(msg);
	}
	public void error(String msg)
	{
		
	}
	public TreeMap<String, String> getSettings()
	{
		return mapGet("Settings");
	}
	public TreeMap<String, String> mapGet(String name)
	{
		if(!map.containsKey(name))
			map.put(name, new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER));
		return map.get(name);
	}
	protected String nullCoalescingSetting(String s, String def){
		s = settings.get(s);
		return s == null?def:s;
	}
}
