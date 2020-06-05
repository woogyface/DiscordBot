import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class SlotsCommand extends Command {
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
		String paramsRaw = msg.substring(command.length()).trim();

		if (event.isFromType(ChannelType.TEXT))
		{
			if(command.equals("!slots")) {
				GameSlots slots = new GameSlots();
				if(params.length > 1) {
					try {
						int amount = Integer.parseInt(params[1]);
						SlotsResult result = slots.roll(amount);
						StringBuilder sb = new StringBuilder()
								.append(result.getWheels().get(0))
								.append(" | ")
								.append(result.getWheels().get(1))
								.append(" | ")
								.append(result.getWheels().get(2))
								.append(" - ");

						if(result.hasWon()) {
							sb.append("Du hast ")
									.append(result.getWinAmount())
									.append(" :euro: ")
									.append(" gewonnen.");
						}
						else
							sb.append("Du hast verloren.");

						sendMessage(channel, sb.toString());
					} catch (NumberFormatException e) {
						sendMessage(channel, params[1] + " ist kein möglicher Einsatz.");
						e.printStackTrace();
					}
				}
			}
		}
	}
}
