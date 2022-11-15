package de.simonsator.partyandfriends.extensions.webauth.connection;

import de.simonsator.partyandfriends.communication.sql.pool.PoolSQLCommunication;

import java.sql.*;

public class WAMySQL extends PoolSQLCommunication {
	private final String TABLE_PREFIX;

	public WAMySQL(String pTablePrefix) {
		super();
		this.TABLE_PREFIX = pTablePrefix;
		importDB();
	}

	private void importDB() {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" +
					TABLE_PREFIX + "authenticated` (`player_id` INT(8) NOT NULL, `web_id` INT(8) NOT NULL, FOREIGN KEY (player_id) REFERENCES "
					+ TABLE_PREFIX + "players(player_id))");
			prepStmt.executeUpdate();
			prepStmt.close();
			prepStmt = con.prepareStatement("CREATE TABLE IF NOT EXISTS `"
					+ TABLE_PREFIX + "auth_waiting_for_verification` " +
					"(`player_id` INT(8) NOT NULL, `web_id` INT(8) NOT NULL, `auth_key` CHAR(38) NOT NULL, FOREIGN KEY (player_id) REFERENCES " +
					TABLE_PREFIX + "players(player_id))");
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(prepStmt);
		}
	}

	public boolean auth(int pPlayerID, String authKey) {
		Connection con = getConnection();
		ResultSet rs = null;
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement("select web_id from `" +
					TABLE_PREFIX + "auth_waiting_for_verification` WHERE player_id=? AND auth_key=? LIMIT 1");
			prepStmt.setInt(1, pPlayerID);
			prepStmt.setString(2, authKey);
			rs = prepStmt.executeQuery();
			if (rs.next()) {
				removeAuthKey(pPlayerID, authKey);
				insertAuth(pPlayerID, rs.getInt("web_id"));
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, prepStmt);
		}
		return false;
	}

	private void insertAuth(int pPlayerID, int pWebId) {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement(
					"insert into " + TABLE_PREFIX + "authenticated values (?, ?)");
			prepStmt.setInt(1, pPlayerID);
			prepStmt.setInt(2, pWebId);
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(prepStmt);
		}
	}

	private void removeAuthKey(int pPlayerID, String pAuthKey) {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement(
					"DELETE FROM " + TABLE_PREFIX + "auth_waiting_for_verification WHERE player_id=? AND auth_key=? Limit 1");
			prepStmt.setInt(1, pPlayerID);
			prepStmt.setString(2, pAuthKey);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(prepStmt);
		}
	}

	public boolean isAuthenticated(int pPlayerID) {
		Connection con = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			rs = (stmt = con.createStatement()).executeQuery("select player_id from " + TABLE_PREFIX
					+ "authenticated WHERE player_id='" + pPlayerID + "' LIMIT 1");
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, stmt);
		}
		return false;
	}
}
