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
		SQLiteDB.getInstance().execute("CREATE TABLE IF NOT EXISTS \"bank\" (" +
				"\"id\"	TEXT PRIMARY KEY," +
				"\"money\" INTEGER DEFAULT 0," +
				"\"debt\" INTEGER DEFAULT 0);"
		);
		SQLiteDB.getInstance().disconnect();
	}

	public void setDebt(User user, int debt) {
		SQLiteDB.getInstance().connect(db);
		int affected = SQLiteDB.getInstance().executeUpdate(
				"UPDATE bank SET debt=? WHERE id=?",
				debt,
				user.getAsTag()
		);

		if (affected == 0) {
			SQLiteDB.getInstance().execute(
					"INSERT INTO bank (id, debt) VALUES (?,?)",
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
				"SELECT debt FROM bank WHERE id=?",
				user.getAsTag()
		);

		if (SQLiteDB.getInstance().next(set)) {
			debt = SQLiteDB.getInstance().getInt(set,"debt", 0);
		}

		SQLiteDB.getInstance().disconnect();

		return debt;
	}

	public void addDebt(User user, int debt) {
		SQLiteDB.getInstance().connect(db);
		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT debt FROM bank WHERE id=?",
				user.getAsTag()
		);

		if (SQLiteDB.getInstance().next(set)) {
			debt += SQLiteDB.getInstance().getInt(set,"debt", 0);
		}

		int affected = SQLiteDB.getInstance().executeUpdate(
				"UPDATE bank SET debt=? WHERE id=?",
				debt,
				user.getAsTag()
		);

		if (affected == 0) {
			SQLiteDB.getInstance().execute(
					"INSERT INTO bank (id, debt) VALUES (?,?)",
					user.getAsTag(),
					debt
			);
		}

		SQLiteDB.getInstance().disconnect();
	}

	public void payDebt(User user, int amount) {
		SQLiteDB.getInstance().connect(db);

		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT money, debt FROM bank WHERE id=?",
				user.getAsTag()
		);

		if (SQLiteDB.getInstance().next(set)) {
			int money = SQLiteDB.getInstance().getInt(set, "money", 0);
			int debt = SQLiteDB.getInstance().getInt(set, "debt", 0);
			if(debt - amount < 0)
				amount = debt;
			if(money - amount < 0)
				amount = money;

			debt -= amount;
			money -= amount;

			int affected = SQLiteDB.getInstance().executeUpdate(
					"UPDATE bank SET debt=?, money=? WHERE id=?",
					debt,
					money,
					user.getAsTag()
			);

			if(affected == 0) {
				SQLiteDB.getInstance().execute(
						"INSERT INTO bank (id, money, debt) VALUES (?,?,?)",
						user.getAsTag(),
						money, debt
				);
			}
		}
		SQLiteDB.getInstance().disconnect();
	}

	public void addMoney(User user, int amount) {
		SQLiteDB.getInstance().connect(db);

		ResultSet set = SQLiteDB.getInstance().executeQuery(
				"SELECT money FROM bank WHERE id=?",
				user.getAsTag()
		);

		if (SQLiteDB.getInstance().next(set)) {
			amount += SQLiteDB.getInstance().getInt(set, "money", 0);
		}

		int affected = SQLiteDB.getInstance().executeUpdate(
				"UPDATE bank SET money=? WHERE id=?",
				amount,
				user.getAsTag()
		);

		if(affected == 0) {
			SQLiteDB.getInstance().execute(
					"INSERT INTO bank (id, money) VALUES (?,?)",
					user.getAsTag(),
					amount
			);
		}

		SQLiteDB.getInstance().disconnect();
	}

	public void setMoney(User user, int money) {
		SQLiteDB.getInstance().connect(db);

		int affected = SQLiteDB.getInstance().executeUpdate(
				"UPDATE bank SET money=? WHERE id=?",
				money,
				user.getAsTag()
		);

		if (affected == 0) {
			SQLiteDB.getInstance().execute(
					"INSERT INTO bank (id, money) VALUES (?,?)",
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
				"SELECT money FROM bank WHERE id=?",
				user.getAsTag()
		);

		if (SQLiteDB.getInstance().next(set)) {
			money = SQLiteDB.getInstance().getInt(set, "money", 0);
		}

		SQLiteDB.getInstance().disconnect();

		return money;
	}

	public void createUserTable() {
		SQLiteDB.getInstance().connect(db);
		SQLiteDB.getInstance().execute("CREATE TABLE IF NOT EXISTS \"users\" (" +
				"\"id\"	INTEGER PRIMARY KEY AUTOINCREMENT," +
				"\"name\" TEXT NOT NULL," +
				"\"role\" TEXT NOT NULL);"
		);
		SQLiteDB.getInstance().disconnect();
	}

	public boolean userExists(User user) {
		SQLiteDB.getInstance().connect(db);

		ResultSet set = SQLiteDB.getInstance().executeQuery(
			"SELECT count(*) as found FROM users WHERE name=?",
			user.getAsTag()
		);
		if(SQLiteDB.getInstance().next(set))
			return SQLiteDB.getInstance().getBoolean(set, "found", false);

		SQLiteDB.getInstance().disconnect();

		return false;
	}

	public boolean registerUser(User user, UserRole role) {
		SQLiteDB.getInstance().connect(db);

		ResultSet set = SQLiteDB.getInstance().executeQuery(
			"SELECT count(*) as found FROM users WHERE name=?",
			user.getAsTag()
		);
		if(SQLiteDB.getInstance().next(set)) {
			if(!SQLiteDB.getInstance().getBoolean(set, "found", false)) {
				SQLiteDB.getInstance().execute(
						"INSERT INTO users (name, role) VALUES (?,?)",
						user.getAsTag(),
						role
				);
				return true;
			}
		}

		SQLiteDB.getInstance().disconnect();

		return false;
	}

	public void setUserRole(User user, UserRole role) {
		SQLiteDB.getInstance().connect(db);

		int affected = SQLiteDB.getInstance().executeUpdate(
				"UPDATE users SET role=? WHERE name=?",
				role,
				user.getAsTag()
		);

		if (affected == 0) {
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
				"SELECT role FROM users WHERE name=?",
				user.getAsTag()
		);

		if (SQLiteDB.getInstance().next(set)) {
			role = UserRole.valueOf(SQLiteDB.getInstance().getString(set, "role", "user"));
		}

		SQLiteDB.getInstance().disconnect();

		return role;
	}
}
