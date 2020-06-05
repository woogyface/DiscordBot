import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public abstract class Command extends ListenerAdapter {
	public void sendPrivateMessage(User user, String msg) {
		user.openPrivateChannel().queue((channel) ->
		{
			channel.sendMessageFormat(msg).queue();
		});
	}
	public void sendMessage(MessageChannel channel, String msg) {
		channel.sendMessage(msg).queue();
	}

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
}
