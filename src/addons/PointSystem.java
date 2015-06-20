package addons;

import helpers.FileManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import test.PointWindow;
import main.MyBot;

public class PointSystem extends Addon{
	List<PointAccount> pointAccounts = new ArrayList<PointAccount>();
	public PointSystem()
	{
		myName = "PointSystem";
		this.addTimer(MyTasks.AutomaticPoints.name(), 0, 60);
		Load();
		Save();
	}
	@Override
	public void Load()
	{
		FileManager fm = new FileManager(getName());
		map = fm.Load(getName());
		settings = mapGet(MyInformation.Settings.name());
		for(Entry<String, String> ePointAccount : mapGet(MyInformation.Points.name()).entrySet())
		{
			PointAccount pa = new PointAccount(ePointAccount.getKey(), Integer.parseInt(ePointAccount.getValue()));
			pointAccounts.add(pa);
		}
	}
	@Override
	public void Save()
	{
		FileManager fm = new FileManager(getName());
		{
			map.put(MyInformation.Settings.name(), settings);
			HashMap<String, String> pointAccountsHash = new HashMap<String, String>();
			for(PointAccount pa : pointAccounts)
			{
				pointAccountsHash.put(pa.name, pa.points+"");
			}
			map.put(MyInformation.Points.name(), pointAccountsHash);
		}
		fm.Create(getName(), map);
	}
	@Override
	public void onJoin(String user)
	{
		PointAccount pa;
		if((pa = findPointAccount(user)) != null)
		{
			pa.present = true;
		}
		else
		{
			pointAccounts.add(new PointAccount(user, 0));
			onJoin(user);
		}
	}
	@Override
	public void onExit(String user)
	{
		PointAccount pa;
		if((pa = findPointAccount(user)) != null)
		{
			pa.present = false;
		}
	}
	@Override
	public void onMsg(String sender, String message)
	{
		boolean changed = false;
		Pattern p;
		Matcher m;
		if(MyBot.isOwner(sender))
		{
			p = getOrRegisterPattern("pointAward", "^!pointaward (?<name>\\w+) (?<points>\\d+)$");
			m = p.matcher(message);
			if(find(m))
			{
				if(attemptToEditPoints(m.group("name"), Integer.parseInt(m.group("points"))))
					msg(sender+" awards "+m.group("points")+" points to "+m.group("name")+".");
				changed = true;
			}
			
			p = getOrRegisterPattern("pointSet", "^!pointset (?<name>\\w+) (?<points>\\d+)$");
			m = p.matcher(message);
			if(find(m))
			{
				int cur;
				PointAccount pa;
				if((pa = findPointAccount(m.group("name")))!= null)
				{
					cur = pa.getPoints();
					if(attemptToEditPoints(m.group("name"), Integer.parseInt(m.group("points")) - cur))
					{
						msg(sender+" sets "+m.group("name")+"'s points to "+m.group("points")+".");
						changed = true;
					}
				}
			}
			
			p = getOrRegisterPattern("pointAwardAll", "^!pointawardall (?<points>\\d+)$");
			m = p.matcher(message);
			if(find(m))
			{
				for(PointAccount pa : pointAccounts)
				{
					if(pa.present)
						pa.addPoints(Integer.parseInt(m.group("points")));
				}
				msg(sender+" awards "+m.group("points")+" points to everyone in chat!");
				changed = true;
			}
		
			String pointSettingsString = "("
					+ "pointName"
					+ ")";
			p = getOrRegisterPattern("pointSettingsString", "^!pointsetting (?<settingName>"+pointSettingsString+") (?<settingInfo>\\w+)$");
			m = p.matcher(message);
			if(find(m))
			{
				HashMap<String,String> settings = mapGet(MyInformation.Settings.name());
				settings.put(m.group("settingName"), m.group("settingInfo"));
				map.put(MyInformation.Settings.name(), settings);
				msg("Change to "+m.group("settingName")+" confirmed.");
				changed = true;
			}
			
			String pointSettingsInteger = "("
					+ "minimumMessagesRequired|"
					+ "minimumMinutesRequired|"
					+ "pointReward"
					+ "followerRewardMultiplier"
					+ ")";
			p = getOrRegisterPattern("pointSettingsInteger", "^!pointsetting (?<settingName>"+pointSettingsInteger+") (?<settingInfo>\\d+)$");
			m = p.matcher(message);
			if(find(m))
			{
				HashMap<String,String> settings = mapGet(MyInformation.Settings.name());
				settings.put(m.group("settingName"), m.group("settingInfo"));
				map.put(MyInformation.Settings.name(), settings);
				msg("Change to "+m.group("settingName")+" confirmed.");
				changed = true;
			}
		}
		
		p = getOrRegisterPattern("pointGive", "^!pointgive (?<receiver>\\w+) (?<points>\\d+)$");
		m = p.matcher(message);
		if(find(m))
		{
			if(attemptToEditPoints(sender, -Integer.parseInt(m.group("points"))) &&
					attemptToEditPoints(m.group("receiver"), +Integer.parseInt(m.group("points"))))
			{
				msg(String.format("%s (%s) gave %s to %s (%s)", 
						sender,
						findPointAccount(sender).points,
						m.group("points"),
						m.group("receiver"),
						findPointAccount(m.group("receiver")).points));
				changed = true;
			}
		}
		
		p = getOrRegisterPattern("pointCheck", "^!pointcheck(| (?<toCheck>\\w+))$");
		m = p.matcher(message);
		if(find(m))
		{
			String s;
			if((s = m.group("toCheck")) == null || s.length() == 0)
				s = sender;
			PointAccount account;
			if((account = findPointAccount(s)) != null)
			{
				msg(s+" has "+account.points+" points.");
			}
			else
			{
				msg("Could not find account '"+s+"'.");
			}
				
				
		}
		
		
		{
			PointAccount pa = findPointAccount(sender);
			if(pa == null)
				pointAccounts.add(pa = new PointAccount(sender, 0));
			pa.addMessage();
		}
		
		if(changed)
		{
			PointWindow.forceRefresh();
			Save();
		}
	}
	@Override
	public void doneWithTime(String key)
	{
		MyTasks keyTask = MyTasks.valueOf(key);
		switch(keyTask)
		{
		case AutomaticPoints:
			for(PointAccount e : pointAccounts)
			{
				e.addMinute();
			}
			break;
		}
	}
	public boolean attemptToEditPoints(String from, int amount)
	{
		PointAccount account = findPointAccount(from);
		if(account == null)
		{
			account = new PointAccount(from, 0);
			pointAccounts.add(account);
			return account.addPoints(amount);
		}
		return account.addPoints(amount);
	}
	public PointAccount findPointAccount(String name) //may return null
	{
		PointAccount account = null;
		for(PointAccount pa : pointAccounts)
		{
			if(pa.name.equalsIgnoreCase(name))
			{
				account = pa;
				break;
			}
		}
		return account;
	}
	public void setPointsTo(String name, int value)
	{
		PointAccount pa;
		if((pa = findPointAccount(name)) != null)
		{
			msg("!pointset "+name+" "+(value));
		}
	}
	public void setSettingTo(String name, String value)
	{
		msg("!pointsetting "+name+" "+value);
	}
	protected int minimumMinutesElapsed(){return Integer.parseInt(nullCoalescingSetting("minimumMinutesElapsed","5"));}
	protected int minimumMessagesElapsed(){return Integer.parseInt(nullCoalescingSetting("minimumMessagesElapsed","5"));}
	protected int pointReward(){return Integer.parseInt(nullCoalescingSetting("pointReward","0"));}
	protected int followerRewardMultiplier(){return Integer.parseInt(nullCoalescingSetting("followerRewardMultiplier","100"));}
	protected String nullCoalescingSetting(String s, String def){
		s = settings.get(s);
		return s == null?def:s;
	}
	public class PointAccount
	{
		String name;
		private int points;
		boolean present = false;
		int minutesElapsed;
		int messagesElapsed;
		public final int initPoints; 
		public PointAccount(String name, int points)
		{
			this.name = name;
			this.points = points;
			this.initPoints = points;
		}
		public int getPoints(){return points;}
		public String getName(){return name;}
		public void addMinute()
		{
			if(present)
			{
				minutesElapsed ++;
				check();
			}
		}
		public void addMessage()
		{
			if(present)
			{
				messagesElapsed ++;
				check();
			}
		}
		public void check()
		{
			if(present && minutesElapsed >= minimumMinutesElapsed() && messagesElapsed >= minimumMessagesElapsed())
			{
				minutesElapsed = messagesElapsed = 0;
				addPoints(pointReward()*(MyBot.isFollower(name)?(int)(followerRewardMultiplier()/100f):1));
			}
		}
		public boolean addPoints(int toAdd)
		{
			boolean result = (points + toAdd >= 0);
			if(result)
				points += toAdd;
			else
				msg(name+", you don't have enough <points> to do that.");
			return result;
		}
	}
	
	
	public List<PointAccount> getTopRanked(int num)
	{
		if(num > 25)
			num = 25;
		List<PointAccount> result = new ArrayList<PointAccount>();
		result.addAll(pointAccounts);
		result.sort(new PointComparator());
		if(num < result.size())
			result.subList(0, num);
		return result;
	}
	static class PointComparator implements Comparator<PointAccount>
	 {
	     public int compare(PointAccount b, PointAccount a)
	     {
	         return ((Integer)a.points).compareTo((Integer)b.points);
	     }
	 }
	enum MyTasks
	{
		AutomaticPoints
	}
	enum MyInformation
	{
		Settings, Points
	}
}
