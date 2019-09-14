package de.simonsator.partyandfriends.extensions.webauth;

import de.simonsator.partyandfriends.api.PAFExtension;
import de.simonsator.partyandfriends.communication.sql.MySQLData;
import de.simonsator.partyandfriends.extensions.webauth.commands.AuthSubCommand;
import de.simonsator.partyandfriends.extensions.webauth.configuration.WAConfiguration;
import de.simonsator.partyandfriends.extensions.webauth.connection.WAMySQL;
import de.simonsator.partyandfriends.friends.commands.Friends;
import de.simonsator.partyandfriends.main.Main;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;

import java.io.File;
import java.io.IOException;

public class WAPlugin extends PAFExtension {
	@Override
	public void onEnable() {
		try {
			ConfigurationCreator config = new WAConfiguration(new File(getDataFolder(), "config.yml"), this);
			MySQLData mySQLData = new MySQLData(Main.getInstance().getConfig().getString("MySQL.Host"),
					Main.getInstance().getConfig().getString("MySQL.Username"), Main.getInstance().getConfig().get("MySQL.Password").toString(),
					Main.getInstance().getConfig().getInt("MySQL.Port"), Main.getInstance().getConfig().getString("MySQL.Database"),
					Main.getInstance().getConfig().getString("MySQL.TablePrefix"), Main.getInstance().getConfig().getBoolean("MySQL.UseSSL"));
			Friends.getInstance().addCommand(new AuthSubCommand(config, new WAMySQL(mySQLData)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
