package helpers;
import java.util.Timer;
import java.util.TimerTask;

import addons.Addon;


public class MiniTimer extends TimerTask{
	Timer timer;
	String key;
	Addon addon;
	public MiniTimer(Addon addon, String key, long delay, long period)
	{
		timer = new Timer();
		this.addon = addon;
		this.key = key;
		if(period > 0)
			timer.schedule(this, delay*1000, period*1000);
		else
			timer.schedule(this, delay*1000);
	}
	@Override
	public void run() {
		addon.doneWithTime(key);
	}
	public void Cancel()
	{
		timer.cancel();
	}
}
