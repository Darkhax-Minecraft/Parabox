package net.darkhax.parabox.block;

import net.darkhax.bookshelf.util.WorldUtils;
import net.darkhax.parabox.Parabox;
import net.darkhax.parabox.util.WorldSpaceTimeManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemBlockParabox extends ItemBlock {
    
    public ItemBlockParabox(Block block) {
        
        super(block);
    }
    
    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        
        // Prevent placing in non surface dimensions
        if (WorldUtils.getDimensionId(world) != 0) {
            
            Parabox.sendMessage(player, TextFormatting.RED, "info.parabox.dimension");
            return false;
        }
        
        // Prevent a player from placing two boxes.
        else if (!world.isRemote && WorldSpaceTimeManager.getWorldData().getUserData(player.getUniqueID()) != null) {
            
            Parabox.sendMessage(player, TextFormatting.RED, "info.parabox.duplicate");
            return false;
        }
        
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}