package de.simonsator.partyandfriends.extensions.webauth.commands;

import de.simonsator.partyandfriends.api.friends.abstractcommands.FriendSubCommand;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.extensions.webauth.connection.WAMySQL;
import de.simonsator.partyandfriends.pafplayers.mysql.OnlinePAFPlayerMySQL;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class AuthSubCommand extends FriendSubCommand {
	private final WAMySQL CONNECTION;
	private final ConfigurationCreator CONFIGURATION;

	public AuthSubCommand(ConfigurationCreator pConfig, WAMySQL con) {
		super(pConfig.getStringList("Command.Names"), pConfig.getInt("Command.Priority"), pConfig.getString("Messages.CommandUsage"),
				pConfig.getString("Command.Permission"));
		CONFIGURATION = pConfig;
		CONNECTION = con;
	}

	@Override
	public void onCommand(OnlinePAFPlayer pPlayer, String[] args) {
		if (CONNECTION.isAuthenticated(((OnlinePAFPlayerMySQL) pPlayer).getPlayerID())) {
			pPlayer.sendMessage(PREFIX + CONFIGURATION.getString("Messages.AlreadyAuthenticated"));
			return;
		}
		if (args.length == 1) {
			TextComponent text = new TextComponent(TextComponent.fromLegacyText(PREFIX +
					CONFIGURATION.getString("Messages.NoAuthKey")));
			text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
					CONFIGURATION.getString("AuthWebsite")));
			sendError(pPlayer, text);
			return;
		}
		if (CONNECTION.auth(((OnlinePAFPlayerMySQL) pPlayer).getPlayerID(), args[1])) {
			pPlayer.sendMessage(PREFIX + CONFIGURATION.getString("Messages.Authenticated"));
			return;
		}
		sendError(pPlayer, new TextComponent(
				TextComponent.fromLegacyText(PREFIX + CONFIGURATION.getString("Messages.AuthKeyWrong"))));
	}
}
