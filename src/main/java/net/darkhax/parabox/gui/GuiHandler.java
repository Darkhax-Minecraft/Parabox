package net.darkhax.parabox.gui;

import net.darkhax.parabox.block.BlockParabox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int PARABOX_CONSOLE = 0;

    @Override
    public Object getServerGuiElement (int id, EntityPlayer player, World world, int x, int y, int z) {

        return null;
    }

    @Override
    public Object getClientGuiElement (int id, EntityPlayer player, World world, int x, int y, int z) {

        if (id == PARABOX_CONSOLE) {

            return new GuiParabox(BlockParabox.getParabox(world, new BlockPos(x, y, z)), player);
        }

        return null;
    }
}