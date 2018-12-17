package net.darkhax.parabox.util;

import com.google.gson.annotations.Expose;

import net.minecraft.util.math.BlockPos;

public class ParaboxUserData {
    
    @Expose
    private boolean hasConfirmed;
    
    @Expose
    private boolean isActive;

    @Expose
    private BlockPos position;
    
    @Expose
    private int points;
    
    public int getPoints () {
        
        return points;
    }
    
    public void setPoints (int points) {
        
        this.points = points;
    }
    
    public boolean isHasConfirmed () {
        
        return this.hasConfirmed;
    }
    
    public void setHasConfirmed (boolean hasConfirmed) {
        
        this.hasConfirmed = hasConfirmed;
    }
    
    public BlockPos getPosition () {
        
        return this.position;
    }
    
    public void setPosition (BlockPos position) {
        
        this.position = position;
    }
       
    public boolean isActive () {
        
        return isActive;
    }

    public void setActive (boolean isActive) {
        
        this.isActive = isActive;
    }
}