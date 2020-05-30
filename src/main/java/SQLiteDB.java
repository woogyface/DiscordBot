import org.apache.commons.collections4.KeyValue;

import java.net.URL;
import java.sql.*;

public class SQLiteDB {
	private static Connection connection;
	private static SQLiteDB instance;
	public static SQLiteDB getInstance() {
		if(instance == null)
			instance = new SQLiteDB();

		return instance;
	}

	public void connect(String db) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		disconnect();
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+db);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}
	//public void connect() {
	//	connect(":memory:");
	//}

	public void disconnect() {
		try {
			if(connection != null && !connection.isClosed())
				connection.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	public boolean execute(String sql, Object... params) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement stmnt = connection.prepareStatement(sql);

				//addParams(stmnt, params);
				for(int i = 0; i < params.length; i++)
					stmnt.setObject(i + 1, params[i]);

				return stmnt.execute();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return false;
	}

	public ResultSet executeQuery(String sql, Object... params) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement stmnt = connection.prepareStatement(sql);

				//addParams(stmnt, params);
				for(int i = 0; i < params.length; i++)
					stmnt.setObject(i + 1, params[i]);

				return stmnt.executeQuery();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return null;
	}

	public int executeUpdate(String sql, Object... params) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement stmnt = connection.prepareStatement(sql);

				//addParams(stmnt, params);
				for(int i = 0; i < params.length; i++)
					stmnt.setObject(i + 1, params[i]);

				return stmnt.executeUpdate();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return 0;
	}

	private void addParams(PreparedStatement stmnt, Object... params) {
		for(int i = 0; i < params.length; i++) {
			try {
				if (Byte.class.equals(params[i].getClass())) {
					stmnt.setByte(i + 1, (byte) params[i]);
				} else if (Short.class.equals(params[i].getClass())) {
					stmnt.setShort(i + 1, (short) params[i]);
				} else if (Integer.class.equals(params[i].getClass())) {
					stmnt.setInt(i + 1, (int) params[i]);
				} else if (Long.class.equals(params[i].getClass())) {
					stmnt.setLong(i + 1, (long) params[i]);
				} else if (Float.class.equals(params[i].getClass())) {
					stmnt.setFloat(i + 1, (float) params[i]);
				} else if (Double.class.equals(params[i].getClass())) {
					stmnt.setDouble(i + 1, (double) params[i]);
				} else if (Boolean.class.equals(params[i].getClass())) {
					stmnt.setBoolean(i + 1, (boolean) params[i]);
				} else if (String.class.equals(params[i].getClass())) {
					stmnt.setString(i + 1, (String) params[i]);
				} else if (Date.class.equals(params[i].getClass())) {
					stmnt.setDate(i + 1, (Date) params[i]);
				} else if (Time.class.equals(params[i].getClass())) {
					stmnt.setTime(i + 1, (Time) params[i]);
				} else if (Blob.class.equals(params[i].getClass())) {
					stmnt.setBlob(i + 1, (Blob) params[i]);
				} else if (Timestamp.class.equals(params[i].getClass())) {
					stmnt.setTimestamp(i + 1, (Timestamp) params[i]);
				} else if (URL.class.equals(params[i].getClass())) {
					stmnt.setURL(i + 1, (URL) params[i]);
				} else {
					stmnt.setObject(i + 1, params[i]);
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}
	}
}
