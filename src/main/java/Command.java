import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class Command extends ListenerAdapter {
	private List<String> whitelistChannels = new ArrayList<>();
	private List<UserRole> whitelistRoles = new ArrayList<>();

	public List<String> getWhitelistChannels() {return whitelistChannels;}
	public List<UserRole> getWhitelistRoles() {return whitelistRoles;}

	public void addWhitelistChannel(String id) {
		if(!whitelistChannels.contains(id))
			whitelistChannels.add(id);
	}

	public void removeWhitelistChannel(String id) {
		if(whitelistChannels.contains(id))
			whitelistChannels.remove(id);
	}

	public void addRole(UserRole role) {
		if(!whitelistRoles.contains(role))
			whitelistRoles.add(role);
	}

	public void removeRole(UserRole role) {
		if(whitelistRoles.contains(role))
			whitelistRoles.remove(role);
	}

	public Command(List<UserRole> whitelistRoles, List<String> whitelistChannels) {
		this.whitelistRoles = whitelistRoles;
		this.whitelistChannels = whitelistChannels;
	}

	public Command(List<String> whitelistChannels) {
		this.whitelistRoles.add(UserRole.all);
		this.whitelistChannels = whitelistChannels;
	}

	public Command() {
		this.whitelistRoles.add(UserRole.all);
	}

	public boolean isWhitelistChannel(MessageChannel channel) {
		return whitelistChannels.contains(channel.getId()) || whitelistChannels.size() == 0;
	}

	public boolean isUserAllowed(User user) {
		return whitelistRoles.contains(UserRole.all) ||
				whitelistRoles.contains(BotDB.getInstance().getUserRole(user));
	}

	public boolean canRunCommand(MessageReceivedEvent event) {
		return isWhitelistChannel(event.getChannel()) && isUserAllowed(event.getAuthor());
	}

	public void sendPrivateMessage(User user, String msg) {
		user.openPrivateChannel().queue((channel) ->
		{
			String utf8 = new String(msg.getBytes(), StandardCharsets.UTF_8);
			channel.sendMessageFormat(utf8).queue();
		});
	}
	public void sendMessage(MessageChannel channel, String msg) {
		if(whitelistChannels == null || channel.getId().equals("432981218246787085")) {
			String utf8 = new String(msg.getBytes(), StandardCharsets.UTF_8);
			channel.sendMessage(utf8).queue();
		}
		else {
			for (String c : whitelistChannels) {
				if (channel.getId().equals(c)) {
					String utf8 = new String(msg.getBytes(), StandardCharsets.UTF_8);
					channel.sendMessage(utf8).queue();
					return;
				}
			}
		}
	}
/*
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		JDA jda = event.getJDA();
		long responseNumber = event.getResponseNumber();

		User user = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();

		String msg = message.getContentDisplay();
		String[] params = msg.split(" ");
		String command = params[0];
		String paramsRaw = msg.substring(command.length()).trim();
	}
 */
}
