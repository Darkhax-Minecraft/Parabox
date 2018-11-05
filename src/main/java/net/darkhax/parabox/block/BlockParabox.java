package net.darkhax.parabox.block;

import net.darkhax.bookshelf.block.BlockTileEntity;
import net.darkhax.parabox.Parabox;
import net.darkhax.parabox.gui.GuiParabox;
import net.darkhax.parabox.util.ParaboxUserData;
import net.darkhax.parabox.util.WorldSpaceTimeManager;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockParabox extends BlockTileEntity {
    
    public BlockParabox() {
        
        super(Material.ROCK);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setSoundType(SoundType.STONE);
    }
    
    @Override
    public TileEntity createNewTileEntity (World worldIn, int meta) {
        
        return new TileEntityParabox();
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        
        final TileEntityParabox parabox = getParabox(world, pos);
        
        if (parabox != null) {
            
            parabox.ownerId = placer.getUniqueID();
            parabox.ownerName = placer.getName();
        }
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
        if (!worldIn.isRemote && !WorldSpaceTimeManager.isSaving()) {
            
            WorldSpaceTimeManager.initiateWorldBackup();
            
            TileEntityParabox box = getParabox(worldIn, pos);
            
            if (box != null && box.isOwner(playerIn) && WorldSpaceTimeManager.getWorldData().getUserData(playerIn.getUniqueID()) == null) {
                
                final ParaboxUserData data = new ParaboxUserData();
                data.setPosition(pos);
                data.setHasConfirmed(false);
                WorldSpaceTimeManager.getWorldData().addUser(playerIn.getUniqueID(), data);
                WorldSpaceTimeManager.saveCustomWorldData();
            }
        }
        
        else {
            
            playerIn.openGui(Parabox.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        
        return true;
    }
    
    @Override
    public boolean removedByPlayer (IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        
        return false;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        
        return false;
    }
    
    public static TileEntityParabox getParabox(World world, BlockPos pos) {
        
        final TileEntity tile = world.getTileEntity(pos);
        
        return tile instanceof TileEntityParabox ? (TileEntityParabox) tile : null;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
}