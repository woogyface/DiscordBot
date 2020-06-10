import org.apache.commons.collections4.KeyValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rest {
	public static RestResponse get(String url) {
		BufferedReader reader = null;
		StringBuilder sb = null;
		String response = null;
		Map<String, List<String>> headers = new HashMap<>();
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod("GET");

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			String input;
			while((input = reader.readLine()) != null) {
				sb.append(input);
			}
			response = sb.toString();
			headers = connection.getHeaderFields();
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

		return new RestResponse(response, headers);
	}

	public static RestResponse post(String url, String data) {
		BufferedReader reader = null;
		StringBuilder sb = null;
		String response = null;
		Map<String, List<String>> headers = new HashMap<>();
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
			response = sb.toString();
			headers = connection.getHeaderFields();
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

		return new RestResponse(response, headers);
	}

	public static RestResponse request(String method, String url, String data) {
		BufferedReader reader = null;
		StringBuilder sb = null;
		String response = null;
		Map<String, List<String>> headers = new HashMap<>();
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod(method);
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
			response = sb.toString();
			headers = connection.getHeaderFields();
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

		return new RestResponse(response, headers);
	}

	public static RestResponse request(String method, String url, String data, String... headerProperties) {
		BufferedReader reader = null;
		StringBuilder sb = null;
		String response = null;
		Map<String, List<String>> headers = new HashMap<>();
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			for(int i = 1; i < headerProperties.length; i++) {
				connection.setRequestProperty(headerProperties[i - 1], headerProperties[i]);
			}

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
			response = sb.toString();
			headers = connection.getHeaderFields();
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

		return new RestResponse(response, headers);
	}
}
