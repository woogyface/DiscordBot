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

	public void createBankTable() {
		SQLiteDB.getInstance().connect(db);
		SQLiteDB.getInstance().execute("CREATE TABLE IF NOT EXISTS \"bank\" ("+
				"\"userid\"	INTEGER PRIMARY KEY,"+
				"\"money\" INTEGER," +
				"\"debt\" INTEGER);"
		);
		SQLiteDB.getInstance().disconnect();
	}

	public void setDebt(User user, int debt) {
		SQLiteDB.getInstance().connect(db);
		int affected = SQLiteDB.getInstance().executeUpdate(
				"UPDATE bank SET debt=? WHERE name=(SELECT id FROM users WHERE name=?)",
				debt,
				user.getAsTag()
		);

		if(affected == 0) {
			SQLiteDB.getInstance().execute(
					"INSERT INTO bank (userid, money, debt) VALUES (?,?)",
					user.getAsTag(),
					debt
			);
		}

		SQLiteDB.getInstance().disconnect();
	}

	public int getDebt(User user) {
		int debt = 0;
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT debt FROM bank WHERE userid=(SELECT id FROM users WHERE name=?)",
				user.getAsTag()
		);
		try {
			if (set.next()) {
				debt = set.getInt("debt");
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		SQLiteDB.getInstance().disconnect();

		return debt;
	}

	public void addDebt(User user, int debt) {
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT debt FROM bank WHERE userid=(SELECT id FROM users WHERE name=?)",
				user.getAsTag()
		);
		try {
			if (set.next()) {
				debt += set.getInt("debt");

				int affected = SQLiteDB.getInstance().executeUpdate(
						"UPDATE bank SET debt=? WHERE name=(SELECT id FROM users WHERE name=?)",
						debt,
						user.getAsTag()
				);

				if(affected == 0) {
					SQLiteDB.getInstance().execute(
							"INSERT INTO bank (userid, money, debt) VALUES (?,?)",
							user.getAsTag(),
							debt
					);
				}
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		SQLiteDB.getInstance().disconnect();
	}

	public void payDebt(User user, int amount) {
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT money, debt FROM bank WHERE userid=(SELECT id FROM users WHERE name=?)",
				user.getAsTag()
		);
		try {
			if (set.next()) {
				int money = set.getInt("money");
				int debt = set.getInt("debt");
				if(debt - amount < 0)
					amount = debt;
				if(money - amount < 0)
					amount = money;

				debt -= amount;
				money -= amount;

				int affected = SQLiteDB.getInstance().executeUpdate(
						"UPDATE bank SET debt=?, money=? WHERE name = (SELECT id FROM users WHERE name=?)",
						debt,
						money,
						user.getAsTag()
				);

				if(affected == 0) {
					SQLiteDB.getInstance().execute(
							"INSERT INTO bank (userid, money, debt) VALUES (?,?,?)",
							user.getAsTag(),
							money, debt
					);
				}
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		SQLiteDB.getInstance().disconnect();
	}

	public void addMoney(User user, int amount) {
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT money FROM bank WHERE userid=(SELECT id FROM users WHERE name=?)",
				user.getAsTag()
		);
		try {
			if (set.next()) {
				int money = set.getInt("money") + amount;

				int affected = SQLiteDB.getInstance().executeUpdate(
						"UPDATE bank SET money=? WHERE name=(SELECT id FROM users WHERE name=?)",
						money,
						user.getAsTag()
				);

				if(affected == 0) {
					SQLiteDB.getInstance().execute(
							"INSERT INTO bank (userid, money) VALUES (?,?)",
							user.getAsTag(),
							money
					);
				}
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		SQLiteDB.getInstance().disconnect();
	}

	public void setMoney(User user, int money) {
		SQLiteDB.getInstance().connect(db);
		int affected = SQLiteDB.getInstance().executeUpdate(
				"UPDATE bank SET money = ? WHERE name = (SELECT id FROM users WHERE name = ?)",
				money,
				user.getAsTag()
		);

		if(affected == 0) {
			SQLiteDB.getInstance().execute(
					"INSERT INTO bank (userid, money, debt) VALUES (?,?,?)",
					user.getAsTag(),
					money, 0
			);
		}

		SQLiteDB.getInstance().disconnect();
	}

	public int getMoney(User user) {
		int money = 0;
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT money FROM bank WHERE userid = (SELECT id FROM users WHERE name = ?)",
				user.getAsTag()
		);
		try {
			if (set.next()) {
				money = set.getInt("money");
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		SQLiteDB.getInstance().disconnect();

		return money;
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
