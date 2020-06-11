import org.apache.commons.collections4.KeyValue;
import org.sqlite.util.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rest {
	private Map<String, String> queryParam = new HashMap<>();
	private Map<String, String> header = new HashMap<>();

	public void addQueryParam(String key, String value) {
		queryParam.put(key, value);
	}

	public void addHeader(String key, String value) {
		header.put(key, value);
	}

	private void setHeader(HttpsURLConnection connection) {
		if(header != null && header.size() > 0) {
			for(Map.Entry<String, String> entry : header.entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	private void setQueryParam(HttpsURLConnection connection) {
		if(queryParam != null && queryParam.size() > 0) {
			connection.setDoOutput(true);

			try {
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, String> entry : queryParam.entrySet()) {
					sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
					sb.append("=");
					sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
					sb.append("&");
				}
				String params = sb.toString();
				params = params.substring(0, params.length() - 1);

				DataOutputStream dataStream = new DataOutputStream(connection.getOutputStream());
				dataStream.writeBytes(params);
				dataStream.flush();
				dataStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String request(String method, String url) {
		BufferedReader reader = null;
		String response = null;
		try {
			HttpsURLConnection connection = (HttpsURLConnection)new URL(url).openConnection();
			connection.setRequestMethod(method);
			connection.setUseCaches(false);

			setHeader(connection);
			setQueryParam(connection);

			String cookieHeader = connection.getHeaderField("Set-Cookie");
			List<HttpCookie> cookiesList = HttpCookie.parse(cookieHeader);
			CookieManager cookieManager = new CookieManager();
			cookiesList.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));

			if(cookieManager.getCookieStore().getCookies().size() > 0) {
				connection.disconnect();
				connection = (HttpsURLConnection) new URL(url).openConnection();
				List<String> cookieString = new ArrayList<>();
				for (HttpCookie c : cookieManager.getCookieStore().getCookies())
					cookieString.add(c.toString());
				connection.setRequestProperty("Cookie", StringUtils.join(cookieString, ";"));

				setHeader(connection);
				setQueryParam(connection);
			}

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder sb = new StringBuilder();

			String input;
			while((input = reader.readLine()) != null) {
				sb.append(input);
				sb.append("\n");
			}
			reader.close();

			response = sb.toString();
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

		return response;
	}
}
