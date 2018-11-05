package net.darkhax.parabox.network;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRefreshGui extends SerializableMessage {
    
    public PacketRefreshGui() {
        
    }
    
    @Override
    public IMessage handleMessage (MessageContext context) {
        
        Minecraft.getMinecraft().currentScreen.initGui();
        return null;
    }
}
