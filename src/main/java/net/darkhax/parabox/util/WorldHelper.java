package net.darkhax.parabox.util;

import java.io.File;

import net.darkhax.bookshelf.util.WorldUtils;
import net.darkhax.parabox.Parabox;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class WorldHelper {

    private static final File BACKUP = new File("backup");

    public static File getBackupDir () {

        if (!BACKUP.exists()) {

            BACKUP.mkdirs();
        }

        return BACKUP;
    }

    public static void disconnectAll () {

        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server != null) {

            final EntityPlayerMP[] players = server.getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]);

            for (final EntityPlayerMP player : players) {

                player.connection.disconnect(new TextComponentTranslation("info.parabox.disconnect"));
            }
        }
    }

    public static void saveWorld () {

        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        Parabox.LOG.info("Saving player data for world.");

        if (server.getPlayerList() != null) {

            server.getPlayerList().saveAllPlayerData();
        }

        for (final WorldServer world : server.worlds) {

            try {
                Parabox.LOG.info("Saving data for world: " + WorldUtils.getWorldName(world));
                world.saveAllChunks(true, null);
            }
            catch (final MinecraftException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void shutdown () {

        FMLCommonHandler.instance().getMinecraftServerInstance().initiateShutdown();
    }
}