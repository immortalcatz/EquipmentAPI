package thecodewarrior.equipment.common.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import thecodewarrior.equipment.common.EquipmentMod;
import thecodewarrior.equipment.common.container.InventoryBaubles;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.io.Files;

public class PlayerHandler {

	private static HashMap<String, InventoryBaubles> playerBaubles = new HashMap<String, InventoryBaubles>();

	public static void clearPlayerBaubles(EntityPlayer player) {
		playerBaubles.remove(player.getCommandSenderName());
	}

	public static InventoryBaubles getPlayerBaubles(EntityPlayer player) {
		if (!playerBaubles.containsKey(player.getCommandSenderName())) {
			InventoryBaubles inventory = new InventoryBaubles(player);
			playerBaubles.put(player.getCommandSenderName(), inventory);
		}
		return playerBaubles.get(player.getCommandSenderName());
	}

	public static void setPlayerBaubles(EntityPlayer player,
			InventoryBaubles inventory) {
		playerBaubles.put(player.getCommandSenderName(), inventory);
	}

	public static void loadPlayerBaubles(EntityPlayer player, File file1, File file2) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				NBTTagCompound data = null;
				boolean save = false;
				if (file1 != null && file1.exists()) {
					try {
						FileInputStream fileinputstream = new FileInputStream(
								file1);
						data = CompressedStreamTools
								.readCompressed(fileinputstream);
						fileinputstream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (file1 == null || !file1.exists() || data == null
						|| data.hasNoTags()) {
					EquipmentMod.log.warn("Data not found for "
							+ player.getCommandSenderName()
							+ ". Trying to load backup data.");
					if (file2 != null && file2.exists()) {
						try {
							FileInputStream fileinputstream = new FileInputStream(
									file2);
							data = CompressedStreamTools
									.readCompressed(fileinputstream);
							fileinputstream.close();
							save = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				if (data != null) {
					InventoryBaubles inventory = new InventoryBaubles(player);
					inventory.readNBT(data);
					playerBaubles.put(player.getCommandSenderName(), inventory);
					if (save)
						savePlayerBaubles(player, file1, file2);
				}
			} catch (Exception exception1) {
				EquipmentMod.log.fatal("Error loading thecodewarrior.equipment inventory");
				exception1.printStackTrace();
			}
		}
	}

	public static void savePlayerBaubles(EntityPlayer player, File file1, File file2) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				if (file1 != null && file1.exists()) {
					try {
						Files.copy(file1, file2);
					} catch (Exception e) {
						EquipmentMod.log
								.error("Could not backup old thecodewarrior.equipment file for player "
										+ player.getCommandSenderName());
					}
				}

				try {
					if (file1 != null) {
						InventoryBaubles inventory = getPlayerBaubles(player);
						NBTTagCompound data = new NBTTagCompound();
						inventory.saveNBT(data);

						FileOutputStream fileoutputstream = new FileOutputStream(
								file1);
						CompressedStreamTools.writeCompressed(data,
								fileoutputstream);
						fileoutputstream.close();

					}
				} catch (Exception e) {
					EquipmentMod.log.error("Could not save thecodewarrior.equipment file for player "
							+ player.getCommandSenderName());
					e.printStackTrace();
					if (file1.exists()) {
						try {
							file1.delete();
						} catch (Exception e2) {
						}
					}
				}
			} catch (Exception exception1) {
				EquipmentMod.log.fatal("Error saving thecodewarrior.equipment inventory");
				exception1.printStackTrace();
			}
		}
	}
}
