package de.simonsator.partyandfriends.extensions.webauth;

import de.simonsator.partyandfriends.api.PAFExtension;
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
			Friends.getInstance().addCommand(new AuthSubCommand(config,
					new WAMySQL(Main.getInstance().getGeneralConfig().getString("MySQL.TablePrefix"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
