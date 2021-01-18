package com.gmail.goosius.townycultures.command;

import com.gmail.goosius.townycultures.Messaging;
import com.gmail.goosius.townycultures.metadata.TownMetaDataController;
import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.bukkit.util.ChatTools;
import com.gmail.goosius.townycultures.settings.Translation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.List;

public class CultureChatCommand implements CommandExecutor, TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Collections.emptyList();
	}

	private void showCultureCommunicationHelp(CommandSender sender) {
		sender.sendMessage(ChatTools.formatCommand("Eg", "/cc", "[msg]", ""));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player && args.length > 0)
			parseCultureCommunicationCommand((Player) sender, args);
		else
			showCultureCommunicationHelp(sender);

		return true;
	}

	/**
	 * Send message to any online players in culture
	 */
	private void parseCultureCommunicationCommand(Player player, String[] args) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(args[0]);
		for(int i = 1; i < args.length; i++) {
			stringBuilder.append(" ").append(args[i]);
		}
		String basicMessage = stringBuilder.toString();

		//Ensure the sender is in a town
		String townCulture = "";
		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		if(resident != null && resident.hasTown()) {
			try{
				townCulture = TownMetaDataController.getTownCulture(resident.getTown());
			} catch (NotRegisteredException e) {
				Messaging.sendErrorMsg(player, Translation.of("msg_err_command_disable"));
				return;
			}
		} else {
			Messaging.sendErrorMsg(player, Translation.of("msg_err_command_disable"));
			return;
		}

		String formattedMessage = String.format(Translation.of("culture_chat_message"), townCulture, resident.getName(), basicMessage);

		Resident otherResident;
		for(Player otherPlayer: BukkitTools.getOnlinePlayers()) {
			try {
				otherResident = TownyUniverse.getInstance().getResident(otherPlayer.getUniqueId());
				if (otherResident != null
						&& otherResident.hasTown()
						&& TownMetaDataController.getTownCulture(otherResident.getTown()).equalsIgnoreCase(townCulture)) {

					//Send message
					otherPlayer.sendMessage(formattedMessage);
					//TownyMessaging........
				}

			} catch (NotRegisteredException nre) {
				//Player town not found
			}
		}
	}
}