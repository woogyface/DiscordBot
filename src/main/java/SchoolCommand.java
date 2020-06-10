import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class SchoolCommand extends Command {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!canRunCommand(event))
            return;
        User user = event.getAuthor();
        if(user.isBot())
            return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        String msg = message.getContentDisplay();
        String[] params = msg.split(" ");
        String command = params[0];

        if (event.isFromType(ChannelType.TEXT)) {
            if (command.equals("!schoolcheck")) {
                RestResponse response = Rest.request("POST", "https://edu.brandenburg.de/webbcloud/index.htm", "op=login&opSchulnr=200331&passwort=D161tali$13run6", "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");

                for (Map.Entry<String, List<String>> entry : response.responseHeader.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
                System.out.println();
                List<String> setCookie = response.responseHeader.get("Set-Cookie");
                String cookie = setCookie.get(0).substring(0, setCookie.get(0).indexOf(";"));
                System.out.println(cookie);

                response = Rest.request("GET", "https://edu.brandenburg.de/webbcloud/index.htm", "schulnr=200331", "Cookie", cookie, "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");

                for (Map.Entry<String, List<String>> entry : response.responseHeader.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
        }
    }
}
