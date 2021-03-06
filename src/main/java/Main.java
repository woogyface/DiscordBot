import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.*;

public class Main {
	public static String version = "0.1";
	public static void main(String[] args) {
		String discordApiKey;
		try {
			File file = new File("discordApiKey.txt");
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
		BotDB.getInstance().createBankTable();

		try {
			JDA jda = new JDABuilder(discordApiKey)
				.addEventListeners(
						new SayCommand("720163524822958100"),
						new RegisterCommand("720163524822958100"),
						new RoleCommand("720163524822958100"),
						new BankCommand("720163487351046195"),
						new SlotsCommand("720163487351046195"),
						new JavaCommand("720163524822958100")
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
