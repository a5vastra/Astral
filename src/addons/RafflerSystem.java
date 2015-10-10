package addons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.MyBot;

public class RafflerSystem extends Addon {
	public RafflerSystem()
	{
		myName = ADDONS.Raffler;
		Pattern numberPattern = getOrRegisterPattern("number", "\\d+");
		settingList = new HashMap<SETTINGS, Pattern>();
		settingList.put(SETTINGS.EntryFee, numberPattern);
		settingList.put(SETTINGS.Reward, numberPattern);
		settingList.put(SETTINGS.Timer, numberPattern);
		settingList.put(SETTINGS.Limit, numberPattern);
		settingList.put(SETTINGS.MaxEntriesPer, numberPattern);
		
		actionList = new ArrayList<ACTIONS>();
		for(ACTIONS a : ACTIONS.values())
			{actionList.add(a);} 
		
		customMessages.put(ACTIONS.Cancel.name()     , "Raffle has been canceled.");
		customMessages.put(ACTIONS.Claim.name()      , "$u has claimed their raffle prize!");
		customMessages.put(ACTIONS.Clear.name()      , "Raffle has been cleared.");
		customMessages.put(ACTIONS.End.name()        , "Raffle has ended.");
		customMessages.put(ACTIONS.Enter.name()      , "$u has been entered into the raffle.");
		customMessages.put(ACTIONS.Start.name()      , "Raffle has begun.");
		customMessages.put(ACTIONS.NextWinner.name() , "The next winner has been chosen.");
		customMessages.put(ACTIONS.Refund.name()     , "Raffle is being refunded.");
		customMessages.put(ACTIONS.Show.name()       , "");
	}
	PointSystem _pointSystem;
	PointSystem getPointSystem(){ 
		return _pointSystem = 
				(_pointSystem != null? 
						_pointSystem :
						(PointSystem)MyBot.instance.getAddon(ADDONS.Point)
				)
		;
	}
	@Override
	public void onMsg(String user, String msg){
		if(msg.equalsIgnoreCase("!raffle"))
			msg = "!raffle enter";
		
		Pattern p;
		Matcher m;
		boolean isOwner = MyBot.isOwner(user) || MyBot.isBot(user);
		{
			if(msg.equalsIgnoreCase("!raffle "+ACTIONS.Enter.name()))
			{
				renter(user);
			}
			else if(msg.equalsIgnoreCase("!raffle "+ACTIONS.Claim.name()))
			{
				rclaim(user);
			}
		}
		if(isOwner)
		{
			p = getOrRegisterPattern("NumberSettings", "^!raffle (?<inner>.+)$");
			m = p.matcher(msg);
			if(m.find())
			{
				Pattern p2;
				Matcher m2;
				{
					String settingsString = "";
					for(SETTINGS e : settingList.keySet())
					{
						settingsString += "|"+e;
					}
					settingsString = "("+settingsString.substring(1)+")"; //eliminate the first |
					p2 = getOrRegisterPattern("SingleKeyVal", "^(?<key>"+settingsString+")(:|=| )(?<val>\\d+)$");
					m2 = p2.matcher(m.group("inner"));
					String key, val;
					if(find(m2))
					{
						key = m2.group("key");
						val = m2.group("val");
						changeSetting(key, val);
						return;
					}
					else
					{
						boolean once = false;
						p2 = getOrRegisterPattern("MultiKeyVal", ("(|"+_start+")")+"(?<key>"+settingsString+")(:|=)(?<val>\\d+)"+("(|"+_end+")"));
						m2 = p2.matcher(m.group("inner"));
						while(find(m2))
						{
							key = m2.group("key");
							val = m2.group("val");
							changeSetting(key, val);
							once = true;
						}
						if(once)
							return;
					}
				}
				{
					String actionString = "";
					for(ACTIONS a : actionList)
					{
						actionString += "|"+a;
					}
					actionString = "("+actionString.substring(1)+")";
					p2 = getOrRegisterPattern("Action", "("+actionString+")");
					m2 = p2.matcher(m.group());
					if(find(m2))
					{
						String group = m2.group();
						ACTIONS action = null;
						for(ACTIONS a : actionList)
						{
							if(a.name().equalsIgnoreCase(group))
							{
								action = a;
								break;
							}
						}
						if(action == null)
							return;
						
						switch(action)
						{
						case Start:
							if(started)
								return;
							started = true;
							raffleEntrants = new ArrayList<String>();
							raffleWinners = new HashMap<String, Integer>();
							break;
						case End:
							if(!started)
								return;
							started = false;
							break;
						case Clear:
							raffleEntrants.clear();
							raffleWinners.clear();
							break;
						case Cancel:
							if(!started)
								return;
							raffleEntrants.clear();
							started = false;
							break;
						case NextWinner:
							Random r = new Random();
							if(raffleEntrants.size() == 0)
								return;
							String winner = raffleEntrants.get(r.nextInt(raffleEntrants.size()));
							raffleEntrants.remove(winner);
							msg("Winner: "+winner);
							if(!raffleWinners.containsKey(winner))
								raffleWinners.put(winner, 0);
							raffleWinners.put(winner, raffleWinners.get(winner)+getNumberSetting(SETTINGS.Reward));
							break;
						case Refund:
							for(String entrant : raffleEntrants)
								getPointSystem().attemptToEditPoints(entrant, getNumberSetting(SETTINGS.EntryFee));
							onMessage(MyBot.instance.getName(), "!raffle cancel");
							break;
						case Show:
							String out = "";
							out += "There are "+raffleEntrants.size()+" entrants so far.";
							msg(out);
							out = "[started: "+started+"]   ";
							for(String s : getSettings().keySet())
							{
								out += "["+s+": "+getSettings().get(s)+"]   ";
							}
							msg(out);
							return;
						default: return;
						}
						if(customMessages.containsKey(action.name()))
							msg(customMessages.get(action.name()), user);
					}
				}
				
			}			
		}
		if(foundHere)
			Save();
	}
	private void renter(String user)
	{
		if(!started)
			return;
		if(getNumberSetting(SETTINGS.Limit) < raffleEntrants.size())
		{
			msg("Sorry $u, this raffle is full! The limit is "+getNumberSetting(SETTINGS.Limit)+" entries.", user);
			return;
		}
		int nTimesEntered = 0;
		for(String entrants : raffleEntrants)
		{
			if(entrants.equalsIgnoreCase(user))
			{
				nTimesEntered++;
			}
		}
		if(nTimesEntered > getNumberSetting(SETTINGS.MaxEntriesPer))
		{
			msg("Sorry $u, you're already entered the maximum amount ("+getNumberSetting(SETTINGS.MaxEntriesPer)+") times.", user);
			return;
		}
		if(!getPointSystem().attemptToEditPoints(user, -getNumberSetting(SETTINGS.EntryFee)))
		{
			msg("Sorry $u, you don't have enough points.", user);
			return;
		}
		raffleEntrants.add(user);
		msg(customMessages.get(ACTIONS.Enter.name()), user);
	}
	private void rclaim(String user)
	{
		if(!raffleWinners.containsKey(user))
		{
			msg("Sorry $u, you don't have a prize to claim.", user);
			return;
		}
		getPointSystem().attemptToEditPoints(user, raffleWinners.get(user));
		raffleWinners.remove(user);
		msg(customMessages.get(ACTIONS.Claim.name()), user);
	}
	private void changeSetting(String key, String val)
	{
		SETTINGS s = null;// = SETTINGS.valueOf(key);
		for(SETTINGS e : SETTINGS.values())
		{
			if(e.name().equalsIgnoreCase(key))
			{
				s = e;
				break;
			}
		}
		if(s == null)
			return;
		
		Pattern p = settingList.get(s); //get the pattern from settingList 
		Matcher m = p.matcher(val);     //make sure that the val matches the pattern 
		if(m.find())
		{
			getSettings().put(key, val);
			msg("Successfully changed raffle "+key+" to "+val);
		}
	}
	private boolean started = false;
	private List<String> raffleEntrants;
	private HashMap<String, Integer> raffleWinners;
	private HashMap<SETTINGS, Pattern> settingList;
	private List<ACTIONS> actionList;
	private enum SETTINGS{Timer, Reward, EntryFee, Limit, MaxEntriesPer}
	private enum ACTIONS{Start, End, Clear, Enter, Cancel, Claim, NextWinner, Refund, Show}
	private int getNumberSetting(SETTINGS s)
	{
		String out;
		try
		{
			if(getSettings().containsKey(s.name()))
				out = getSettings().get(s.name());
			else
				out = "0";
			return Integer.parseInt(out);			
		}
		catch(Exception e)
		{
			return -1;
		}
	}
}
/*
Possible Settings:

Timer : \d+
Reward : \d+
EntryFee : \d+
Limit : \d+
Possible Actions:

Start | End
Clear
Enter
Cancel
Claim
*/