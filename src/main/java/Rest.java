import org.apache.commons.collections4.KeyValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Rest {
	public static String Get(String url) {
		BufferedReader reader = null;
		StringBuilder sb = null;
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod("GET");

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			String input;
			while((input = reader.readLine()) != null) {
				sb.append(input);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb != null ? sb.toString() : "";
	}

	public static String Post(String url, String data) {
		BufferedReader reader = null;
		StringBuilder sb = null;
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			DataOutputStream dataStream = new DataOutputStream(connection.getOutputStream());
			dataStream.writeBytes(data);
			dataStream.flush();
			dataStream.close();

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			String input;
			while((input = reader.readLine()) != null) {
				sb.append(input);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb != null ? sb.toString() : "";
	}
}
