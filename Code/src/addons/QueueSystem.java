package addons;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import test.QueueWindow;
import main.MyBot;

public class QueueSystem extends Addon {
	public QueueSystem()
	{
		myName = "QueueSystem";
	}
	private ArrayList<String> queue = new ArrayList<String>();
	private List<QueueAccount> list = new ArrayList<QueueAccount>();
	public ArrayList<String> getQueue()
	{
		ArrayList<String> result = new ArrayList<String>();
		int counter = 1;
		for(String user : queue)
		{
			String s = "";
			boolean found = false;
			for(QueueAccount qa : list)
			{
				if(user.equalsIgnoreCase(qa.name) && qa.nickname.length() != 0)
				{
					s = "["+counter+"] "+(qa.name+" ("+qa.nickname+")");
					found = true;
					break;
				}
			}
			if(!found)
				s = "["+counter+"] "+user;
			
			result.add(s);
			counter++;
		}
		return result;
	}
	private boolean isOpen = false;
	public boolean getIsOpen(){ return isOpen; }
	public void onMsg(String sender, String message)
	{
		Pattern p;
		Matcher m;
		if(MyBot.isOwner(sender))
		{
			p = getOrRegisterPattern("next", "^!queuenext(?<number>( \\d+)|)$");
			m = p.matcher(message);
			if(find(m))
			{
				String numberStr = m.group("number").trim();
				if(numberStr.length() == 0)
					numberStr = "1";
				int number = Integer.parseInt(numberStr);
				String outmsg = String.format("Next%s from Queue: ", numberStr.equals("1")?"":(" "+numberStr));
				for(int i = 0; i < number; i++)
				{
					if(queue.isEmpty())
					{
						outmsg += "<no one left in queue>";
						break;
					}
					
					String next = queue.remove(0);
					String nickname = "";
					for(QueueAccount qa : list)
					{
						if(qa.name.equalsIgnoreCase(next))
						{
							nickname = qa.nickname;
							if(nickname.length() != 0)
								nickname = " ("+nickname+")";
							break;
						}
					}
					outmsg += next+nickname+"    ";
				}
				msg(outmsg);
			}
			
			p = getOrRegisterPattern("openclose", "^!queue(?<openclose>open|close)$");
			m = p.matcher(message);
			if(find(m))
			{
				boolean wasOpen = isOpen;
				isOpen = (m.group("openclose").equalsIgnoreCase("open"));
				if(wasOpen != isOpen)
					//something changed
					msg("Queue is now "+(isOpen?"open":"closed")+".");
			}
					
		}
		
		p = getOrRegisterPattern("queue", "^!queue(?<nickname>( \\w+)|)$");
		m = p.matcher(message);
		if(find(m))
		{
			if(!isOpen)
			{
				msg("Queue is currently closed.");
			}
			else
			{
				String nickname;
				nickname = m.group("nickname").trim();
				for(int i = 0; i < list.size(); i++)
				{
					if(list.get(i).name.equalsIgnoreCase(sender))
					{
						if(nickname.length() != 0)
						{
							list.get(i).nickname = nickname;
						}
						break;
					}
				}
				if(nickname.length() != 0)
					nickname = " ("+nickname+")";
				
				if(queue.contains(sender))
				{
					msg(sender+nickname+", you're already enrolled in the queue, currently at position "+(queue.indexOf(sender)+1)+" of "+queue.size()+".");
				}
				else
				{
					queue.add(sender);
					msg(sender+nickname+" has been entered into the queue, currently at position "+queue.size()+".");
					list.add(new QueueAccount(sender, nickname));
				}
			}
		}
	}
	@Override 
	public void find2()
	{
		QueueWindow.forceRefresh();
	}
	protected class QueueAccount
	{
		public String name;
		public String nickname;
		QueueAccount(String name, String nickname)
		{
			this.name = name;
			this.nickname = nickname;
		}
	}
}
