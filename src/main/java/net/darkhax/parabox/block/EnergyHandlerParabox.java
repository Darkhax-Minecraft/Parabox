package net.darkhax.parabox.block;

import net.minecraftforge.energy.EnergyStorage;

public class EnergyHandlerParabox extends EnergyStorage {
    
    public EnergyHandlerParabox(int capacity, int maxTransfer) {
        
        super(capacity, maxTransfer, 0, 0);
    }
    
    public EnergyHandlerParabox(int capacity, int maxReceive, int energy) {
        
        super(capacity, maxReceive, 0, energy);
    }
    
    public void setEnergy (int energy) {
        
        this.energy = energy;
    }
}