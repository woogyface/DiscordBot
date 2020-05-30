import net.dv8tion.jda.api.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BotDB {
	private String db = "botdb.sqlite";
	private static BotDB instance;
	public static BotDB getInstance() {
		if(instance == null)
			instance = new BotDB();

		return instance;
	}

	public void createDB() {
		SQLiteDB.getInstance().connect(db);
		SQLiteDB.getInstance().disconnect();
	}

	public void createUserTable() {
		SQLiteDB.getInstance().connect(db);
		SQLiteDB.getInstance().execute("CREATE TABLE IF NOT EXISTS \"users\" ("+
				"\"id\"	INTEGER PRIMARY KEY AUTOINCREMENT,"+
				"\"name\" TEXT NOT NULL," +
				"\"role\" TEXT NOT NULL);"
		);
		SQLiteDB.getInstance().disconnect();
	}

	public boolean userExists(User user) {
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT count(*) as found FROM users WHERE name = ?",
				user.getAsTag()
		);
		try {
			if(set.next())
				return set.getBoolean("found");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		SQLiteDB.getInstance().disconnect();

		return false;
	}

	public boolean registerUser(User user, UserRole role) {
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT count(*) as found FROM users WHERE name = ?",
				user.getAsTag()
		);
		try {
			if(set.next()) {
				if(!set.getBoolean("found")) {
					SQLiteDB.getInstance().execute(
							"INSERT INTO users (name, role) VALUES (?,?)",
							user.getAsTag(),
							role
					);
					return true;
				}
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		SQLiteDB.getInstance().disconnect();

		return false;
	}

	public void setUserRole(User user, UserRole role) {
		SQLiteDB.getInstance().connect(db);
		int affected = SQLiteDB.getInstance().executeUpdate(
				"UPDATE users SET role = ? WHERE name = ?",
				role,
				user.getAsTag()
		);

		if(affected == 0) {
			SQLiteDB.getInstance().execute(
					"INSERT INTO users (name, role) VALUES (?,?)",
					user.getAsTag(),
					role
			);
		}

		SQLiteDB.getInstance().disconnect();
	}

	public UserRole getUserRole(User user) {
		UserRole role = UserRole.user;
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT role FROM users WHERE name = ?",
				user.getAsTag()
		);
		try {
			while (set.next()) {
				role = UserRole.valueOf(set.getString("role"));
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		SQLiteDB.getInstance().disconnect();

		return role;
	}
}
