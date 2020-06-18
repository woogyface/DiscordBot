import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.annotation.Nonnull;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaCommand extends Command {
    public JavaCommand() {
        super(Arrays.asList(UserRole.owner), Arrays.asList("720163524822958100"));
    }

    private Path saveSource(String source) throws IOException {
        String tmpProperty = System.getProperty("java.io.tmpdir");
        Path sourcePath = Paths.get(tmpProperty, "JavaCommandClass.java");
        Files.writeString(sourcePath, source);
        return sourcePath;
    }

    private Path compileSource(Path javaFile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, javaFile.toFile().getAbsolutePath());
        return javaFile.getParent().resolve("JavaCommandClass.class");
    }

    private Exception runClass(Path javaClass)
            throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        URL classUrl = javaClass.getParent().toFile().toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
        Class<?> clazz = Class.forName("JavaCommandClass", true, classLoader);
        try {
            Constructor constructor = clazz.getConstructor();
            //Object obj = constructor.newInstance();

            PrintStream defaultOut = System.out;
            System.setOut(new PrintStream(new File("output.txt")));
            constructor.newInstance();
            System.setOut(defaultOut);

        } catch (NoSuchMethodException | InvocationTargetException | FileNotFoundException e) {
            return e;
        }
        return null;
    }

    private void printOutput(MessageChannel channel) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("output.txt"));
            if(lines.size() > 10 || (lines.size() > 0 && lines.get(0).length() > 100)) {
                channel.sendFile(new File("output.txt")).queue();
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
        if(!canRunCommand(event))
            return;

        User user = event.getAuthor();
        if (user.isBot())
            return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        String msg = message.getContentDisplay();
        if(msg.length() < "!java".length())
            return;

        String command = msg.substring(0, "!java".length());

        if (event.isFromType(ChannelType.TEXT)) {
            if (command.equals("!java")) {
                //System.out.println(msg);
                Pattern pattern = Pattern.compile("!java\\s```Java\\s(?<source>.*)```", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(msg);
                if(matcher.find()) {
                    String source = matcher.group("source");
                    StringBuilder sb = new StringBuilder();
                    sb.append("public class JavaCommandClass {\n");
                    sb.append(source);
                    sb.append("\n}");
                    try {
                        Path javaFile = saveSource(sb.toString());
                        Path classFile = compileSource(javaFile);
                        Exception e = runClass(classFile);
                        if(e != null)
                            sendMessage(channel, e.getMessage());
                        else
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
