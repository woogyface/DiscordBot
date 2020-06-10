import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class RegisterCommand extends Command {
	public RegisterCommand(String... whitelistChannels) {
		super(whitelistChannels);
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		User user = event.getAuthor();
		if(user.isBot())
			return;

		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();

		String msg = message.getContentDisplay();
		String[] params = msg.split(" ");
		String command = params[0];

		if (event.isFromType(ChannelType.TEXT))
		{
			if (command.equals("!register")) {
				if(BotDB.getInstance().registerUser(user, UserRole.user)) {
					sendMessage(channel, user + " You are now: " + BotDB.getInstance().getUserRole(user));
				}
				else {
					sendMessage(channel, user + "You're already registered as: " + BotDB.getInstance().getUserRole(user));
				}
			}
		}
	}
}
