import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class SayCommand extends Command {
    public SayCommand() {
		super(Arrays.asList("720163524822958100", "432981218246787085"));
	}

    @Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if(!canRunCommand(event))
			return;
		User user = event.getAuthor();
		if(user.isBot())
			return;

		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();

		String msg = message.getContentDisplay();
		String[] params = msg.split(" ");
		String command = params[0];
		String paramsRaw = msg.substring(command.length()).trim();

		if (event.isFromType(ChannelType.TEXT))
		{
			if(command.equals("!say")) {
				sendMessage(channel, paramsRaw);
			}
		}
		else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
		{
			if(command.equals("!say")) {
				sendPrivateMessage(user, paramsRaw);
			}
		}
	}
}
