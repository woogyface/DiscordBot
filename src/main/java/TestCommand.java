import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TestCommand extends Command {
	private BufferedImage createBitmap(int width, int height, int[] data) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.getRaster().setPixels(0, 0, width, height, data);

		return image;
	}

	private void saveImage(BufferedImage image, String filename) {
		try {
			File file = new File("./Images/" + filename + ".png");
			file.getParentFile().mkdirs();
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File loadImage(String filename) {
		return new File("./Images/" + filename + ".png");
	}

	private String filenameFromDateTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy_MM_dd___HH_mm_ss");
		Date d = new Date(System.currentTimeMillis());
		return f.format(d);
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
			if(command.equals("!test")) {
				int width = 100;
				int height = 100;
				Random rand = new Random();
				int[] data = new int[width*height * 4];
				for(int y = 0; y < height * 4; y+=4) {
					for(int x = 0; x < width * 4; x+=4) {
						data[y * width + x + 0] = rand.nextInt(255);
						data[y * width + x + 1] = rand.nextInt(255);
						data[y * width + x + 2] = rand.nextInt(255);
						data[y * width + x + 3] = 255;

						//data[y * width + x] = new Color(r, g, b, a).getRGB();
					}
				}
				BufferedImage img = createBitmap(width, height, data);

				String filename = filenameFromDateTime();
				saveImage(img, filename);

				sendFile(channel, loadImage(filename), "Dein Bild:");
			}
		}
		else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
		{
			if(command.equals("!test")) {
				sendPrivateMessage(user, paramsRaw);
			}
		}
	}
}
