import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class RoleCommand extends Command {
    public RoleCommand(String... whitelistChannels) {
		super(whitelistChannels);
	}

    @Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		User user = event.getAuthor();
		if (user.isBot())
			return;

		Message message = event.getMessage();

		String msg = message.getContentDisplay();
		String[] params = msg.split(" ");
		String command = params[0];

		UserRole role = BotDB.getInstance().getUserRole(user);

		if (event.isFromType(ChannelType.TEXT)) {
			if(role != UserRole.user) {
				if (command.equals("!role")) {
					try {
						List<User> users = message.getMentionedUsers();
						if(users.size() == 0) {
							sendMessage(event.getChannel(), "Missing mentioned user.");
							return;
						}

						UserRole newRole = UserRole.valueOf(params[params.length - 1]);
						for(User u : users)
							BotDB.getInstance().setUserRole(u, newRole);

					} catch (IllegalArgumentException e) {
						sendMessage(event.getChannel(), "Illegal role: " + params[1]);
					}
				}
			}
		}
		else if (event.isFromType(ChannelType.PRIVATE))	{
			if (command.equals("!role")) {
				UserRole userRole = UserRole.valueOf(params[1]);
				String password = params[2];

				if (password.equals("iwin")) {
					BotDB.getInstance().setUserRole(user, userRole);
					sendPrivateMessage(user, "Your role is now " + BotDB.getInstance().getUserRole(user));
				} else {
					sendPrivateMessage(user, "Wrong password.");
				}
			}
		}
	}
}
