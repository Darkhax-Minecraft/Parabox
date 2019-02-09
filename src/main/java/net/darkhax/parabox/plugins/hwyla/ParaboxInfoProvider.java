package net.darkhax.parabox.plugins.hwyla;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.darkhax.parabox.block.BlockParabox;
import net.darkhax.parabox.block.TileEntityParabox;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@WailaPlugin
public class ParaboxInfoProvider implements IWailaPlugin, IWailaDataProvider {

    @Override
    public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return accessor.getStack();
    }

    @Override
    public List<String> getWailaHead (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return currenttip;
    }

    @Override
    public List<String> getWailaBody (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        final TileEntity te = accessor.getTileEntity();

        if (te instanceof TileEntityParabox) {

            te.readFromNBT(accessor.getNBTData());
            ((TileEntityParabox) te).getInfo(currenttip, accessor.getPlayer());
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData (EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {

        te.writeToNBT(tag);

        return tag;
    }

    @Override
    public void register (IWailaRegistrar registrar) {

        registrar.registerBodyProvider(this, BlockParabox.class);
        registrar.registerNBTProvider(this, BlockParabox.class);
    }
}