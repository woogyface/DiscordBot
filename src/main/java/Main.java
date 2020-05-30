import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.EnumSet;

public class Main {
	public static void main(String[] args) {
		String discordApiKey;
		File file = new File("discordApiKey.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			StringBuilder sb = new StringBuilder();
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
			discordApiKey = sb.toString();
		} catch (FileNotFoundException e) {
			System.out.println("discordApiKey.txt is missing!");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		BotDB.getInstance().createDB();
		BotDB.getInstance().createUserTable();

		try {
			JDA jda = new JDABuilder(discordApiKey)
				.addEventListeners(
						new SayCommand(),
						new RegisterCommand(),
						new RoleCommand()
				)
				.build();

			jda.awaitReady();
			// Disable parts of the cache
			//builder.setDisabledCacheFlags(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE));
			// Enable the bulk delete event
			//builder.setBulkDeleteSplittingEnabled(false);
			// Disable compression (not recommended)
			//builder.setCompression(Compression.NONE);
			// Set activity (like "playing Something")
			//builder.setActivity(Activity.watching("TV"));

		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
