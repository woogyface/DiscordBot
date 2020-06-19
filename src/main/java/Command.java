import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.EmoteManager;

import javax.annotation.Nonnull;
import java.io.File;
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
		whitelistChannels.remove(id);
	}

	public void addRole(UserRole role) {
		if(!whitelistRoles.contains(role))
			whitelistRoles.add(role);
	}

	public void removeRole(UserRole role) {
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
		return isWhitelistChannel(event.getChannel()) && isUserAllowed(event.getAuthor()) ||
				event.getGuild().getId().equals("432981218246787083");
	}

	public void sendPrivateMessage(User user, String msg) {
		user.openPrivateChannel().queue((channel) ->
		{
			String utf8 = new String(msg.getBytes(), StandardCharsets.UTF_8);
			channel.sendMessage(utf8).queue();
		});
	}

	public void sendPrivateMessage(User user, MessageEmbed msg) {
		user.openPrivateChannel().queue((channel) ->
				channel.sendMessage(msg).queue());
	}

	public void sendMessage(MessageChannel channel, String msg) {
		String utf8 = new String(msg.getBytes(), StandardCharsets.UTF_8);
		channel.sendMessage(utf8).queue();
	}

	public void sendMessage(MessageChannel channel, MessageEmbed msg) {
		channel.sendMessage(msg).queue();
	}

	public void sendFile(MessageChannel channel, File file) {
		channel.sendFile(file).queue();
	}

	public void sendFile(MessageChannel channel, File file, String msg) {
		String utf8 = new String(msg.getBytes(), StandardCharsets.UTF_8);
		channel.sendMessage(utf8).addFile(file).queue();
	}

	public void sendFileWithReaction(MessageChannel channel, File file, String... reaction) {
		channel.sendFile(file).queue(message ->
		{
			for(String r : reaction) {
				message.addReaction(r).queue();
			}
		});
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
