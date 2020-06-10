import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class BankCommand extends Command {
    public BankCommand(String... whitelistChannels) {
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
            if (command.equals("!bank")) {
                if(params.length > 1) {
                    switch (params[1].toLowerCase()) {
                        case "geld", "money" -> {
                            int money = BotDB.getInstance().getMoney(user);
                            sendMessage(channel, user.getAsMention() + ", du hast " + money + " :euro:");
                        }
                        case "schulden", "debt" -> {
                            int debt = BotDB.getInstance().getDebt(user);
                            sendMessage(channel, user.getAsMention() + ", du schuldest " + debt + " :euro:");
                        }
                        case "kredit", "credit" -> {
                            int debt = BotDB.getInstance().getDebt(user);
                            int money = BotDB.getInstance().getMoney(user);
                            if (debt > 0 && money > 0) {
                                sendMessage(channel, user.getAsMention() + ", du hast noch " + debt + " :euro: Schulden. Kredit verweigert.");
                            } else {
                                BotDB.getInstance().addDebt(user, 100);
                                BotDB.getInstance().addMoney(user, 100);
                                sendMessage(channel, user.getAsMention() + ", du hast 100 :euro: Kredit bekommen.");
                            }
                        }
                        case "zahlen", "pay" -> {
                            if (params.length > 2) {
                                try {
                                    int amount = Integer.parseInt(params[2]);
                                    BotDB.getInstance().payDebt(user, amount);
                                    int debt = BotDB.getInstance().getDebt(user);
                                    if (debt > 0)
                                        sendMessage(channel, user.getAsMention() + ", du schuldest noch " + debt + " :euro:");
                                    else
                                        sendMessage(channel, user.getAsMention() + ", du hast keine Schulden mehr.");
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    sendMessage(channel, user.getAsMention() + ", das war eine ungültige Eingabe");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
