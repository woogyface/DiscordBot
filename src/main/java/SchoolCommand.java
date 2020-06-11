import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            if (command.equals("!sc")) {
                Rest rest = new Rest();
                rest.addQueryParam("op","login");
                rest.addQueryParam("opSchulnr","200311");
                rest.addQueryParam("passwort","D161tali$13run6");

                String response = rest.request("POST", "https://edu.brandenburg.de/webbcloud/index.htm");

                List<CloudFileInfo> cloudFiles = new ArrayList<>();

                Pattern tableRowPattern = Pattern.compile("<tr>(.+?)<\\/tr>", Pattern.DOTALL);
                Pattern titlePattern = Pattern.compile("Dokument-Titel: <\\/span>(.+?)<br>");
                Pattern classPattern = Pattern.compile("searchKlasseAndSubmit\\('(.+?)'");
                Pattern tagPattern = Pattern.compile("searchSchlagwortAndSubmit\\('(.+?)'");
                Pattern infoPattern = Pattern.compile("<td style=\"text-align: center\">(.+?)</td>.*?<td style=\"text-align: center\">(.+?)</td>.*?dokumentId=(.+?)\"", Pattern.DOTALL);

                Matcher tableRowMatcher = tableRowPattern.matcher(response);
                int counter = 0;
                while (tableRowMatcher.find()) {
                    counter++;
                    if(counter <= 1)
                        continue;

                    String filename = null;
                    String date = null;
                    String size = null;
                    String id = null;
                    List<String> classes = new ArrayList<>();
                    List<String> tags = new ArrayList<>();

                    Matcher titleMatcher = titlePattern.matcher(tableRowMatcher.group(1));
                    if (titleMatcher.find())
                        filename = titleMatcher.group(1);

                    Matcher infoMatcher = infoPattern.matcher(tableRowMatcher.group(1));
                    if (infoMatcher.find()) {
                        date = infoMatcher.group(1);
                        size = infoMatcher.group(2);
                        id = infoMatcher.group(3);
                    }

                    Matcher classMatcher = classPattern.matcher(tableRowMatcher.group(1));
                    while (classMatcher.find()) {
                        classes.add(classMatcher.group(1));
                    }

                    Matcher tagMatcher = tagPattern.matcher(tableRowMatcher.group(1));
                    while (tagMatcher.find()) {
                        tags.add(tagMatcher.group(1));
                    }
                    CloudFileInfo file = new CloudFileInfo(filename, date, size, id, classes, tags);
                    cloudFiles.add(file);

                }
                if(cloudFiles.size() > 0) {
                    //System.out.println(filename + ": " + date + ", " + size + ", " + id + " classes: " + classes.size() + " tags: " + tags.size());
                    CloudFileInfo file = cloudFiles.get(0);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(file.filename);
                    eb.setColor(Color.white);
                    eb.addField("Date", file.date, true);
                    eb.addField("Size", file.size, true);
                    eb.addField("ID", file.id, true);

                    sendMessage(channel, eb.build());
                }
            }
        }
    }
}
