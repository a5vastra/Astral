package addons;

import test.ChatWindow;

public class ChatSystem extends Addon{
	public ChatSystem()
	{
		myName = ADDONS.Chat;
	}
	@Override
	public void onMsg(String sender, String message)
	{
		ChatWindow.OnMessage(sender, message);
	}
}
