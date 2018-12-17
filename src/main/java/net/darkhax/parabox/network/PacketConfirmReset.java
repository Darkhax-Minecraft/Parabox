package net.darkhax.parabox.network;

import net.darkhax.bookshelf.network.TileEntityMessage;
import net.darkhax.parabox.block.TileEntityParabox;
import net.darkhax.parabox.util.ParaboxUserData;
import net.darkhax.parabox.util.WorldSpaceTimeManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConfirmReset extends TileEntityMessage<TileEntityParabox> {
    
    public PacketConfirmReset() {
        
    }
    
    public PacketConfirmReset(BlockPos pos) {
        
        super(pos);
    }
    
    @Override
    public final IMessage handleMessage (MessageContext context) {
        
        super.handleMessage(context);
        return new PacketRefreshGui();
    }
    
    @Override
    public void getAction () {
        
        if (this.tile.isOwner(this.context.getServerHandler().player)) {
            
            this.tile.setConfirmation(!this.tile.hasConfirmed());
            this.tile.sync();
            final ParaboxUserData userData = WorldSpaceTimeManager.getWorldData().getUserData(this.tile.getOwnerId());
            
            if (userData != null) {
                
                userData.setHasConfirmed(this.tile.hasConfirmed());
                WorldSpaceTimeManager.saveCustomWorldData();
            }
            
            WorldSpaceTimeManager.triggerCollapse(this.context.getServerHandler().player.getServerWorld());
        }
    }
}
