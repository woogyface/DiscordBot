import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
public class Discord {
    public Discord() {
        System.out.println("world");
    }
}
*/

public class JavaCommand extends Command {
    public JavaCommand(String... whitelistChannels) {
        super(whitelistChannels);
    }

    private Path saveSource(String source) throws IOException {
        String tmpProperty = System.getProperty("java.io.tmpdir");
        Path sourcePath = Paths.get(tmpProperty, "Discord.java");
        Files.writeString(sourcePath, source);
        return sourcePath;
    }

    private Path compileSource(Path javaFile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, javaFile.toFile().getAbsolutePath());
        return javaFile.getParent().resolve("Discord.class");
    }

    private void runClass(Path javaClass)
            throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        URL classUrl = javaClass.getParent().toFile().toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
        Class<?> clazz = Class.forName("Discord", true, classLoader);
        PrintStream defaultOut = System.out;
        try {
            System.setOut(new PrintStream(new File("output.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        clazz.newInstance();
        System.setOut(defaultOut);
    }

    private void printOutput(MessageChannel channel) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("output.txt"));
            if(lines.size() > 50) {
                sendMessage(channel, "Output ist zu lang. Max 50 Zeilen");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("```\n");
            for(String line : lines)
                sb.append(line).append("\n");
            sb.append("```");
            sendMessage(channel, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot())
            return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        String msg = message.getContentDisplay();
        int index = msg.indexOf("\n");
        if(index == -1) {
            sendMessage(channel, "Ungültige Eingabe");
            return;
        }
        String command = msg.substring(0, index);

        if (event.isFromType(ChannelType.TEXT)) {
            if (command.equals("!java")) {
                //System.out.println(msg);
                Pattern pattern = Pattern.compile("!java\\s```Java\\s(?<source>.*)```", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(msg);
                if(matcher.find()) {
                    String source = matcher.group("source");
                    StringBuilder sb = new StringBuilder();
                    sb.append("public class Discord {");
                    sb.append("public Discord() {");
                    sb.append(source);
                    sb.append("}}");
                    try {
                        Path javaFile = saveSource(sb.toString());
                        Path classFile = compileSource(javaFile);
                        runClass(classFile);

                        printOutput(channel);
                    } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                        sendMessage(channel, "Compile Error:```"+e.getMessage()+"```");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
