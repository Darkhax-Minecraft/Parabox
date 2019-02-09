package net.darkhax.parabox.block;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasicTickable;
import net.darkhax.parabox.Parabox;
import net.darkhax.parabox.util.ParaboxUserData;
import net.darkhax.parabox.util.WorldSpaceTimeManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityParabox extends TileEntityBasicTickable {

    private static final int max = Integer.MAX_VALUE;
    private static final int maxRecieve = 120000;
    private static final int cycleTime = 24000;
    private static final NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());

    protected EnergyHandlerParabox energyHandler;
    protected UUID ownerId;
    protected String ownerName;
    protected int remainingTicks = 0;
    protected int generatedPoints = 0;
    protected boolean active = false;
    protected boolean confirmed = false;

    public TileEntityParabox () {

        this.energyHandler = new EnergyHandlerParabox(max, maxRecieve);
    }

    public int getPower () {

        return this.energyHandler.getEnergyStored();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability (Capability<T> capability, EnumFacing facing) {

        if (!this.isActive()) {

            return null;
        }

        if (capability == CapabilityEnergy.ENERGY) {

            return (T) this.energyHandler;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability (Capability<?> capability, EnumFacing facing) {

        if (!this.active) {

            return false;
        }

        return capability == CapabilityEnergy.ENERGY;
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        if (this.ownerId != null) {

            dataTag.setUniqueId("Owner", this.ownerId);
        }

        dataTag.setInteger("StoredPower", this.energyHandler.getEnergyStored());
        dataTag.setInteger("RemainingTicks", this.remainingTicks);
        dataTag.setLong("Points", this.generatedPoints);
        dataTag.setBoolean("Active", this.active);
        dataTag.setBoolean("Confirmed", this.hasConfirmed());
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.ownerId = dataTag.getUniqueId("Owner");
        this.energyHandler = new EnergyHandlerParabox(max, maxRecieve, dataTag.getInteger("StoredPower"));
        this.remainingTicks = dataTag.getInteger("RemainingTicks");
        this.generatedPoints = dataTag.getInteger("Points");
        this.active = dataTag.getBoolean("Active");
        this.setConfirmation(dataTag.getBoolean("Confirmed"));
    }

    @Override
    public void onEntityUpdate () {

        if ((WorldSpaceTimeManager.getWorldData() == null || !WorldSpaceTimeManager.getWorldData().getBackupFile().exists()) && this.active && !WorldSpaceTimeManager.isSaving() && !WorldSpaceTimeManager.requireSaving()) {

            this.setActive(false);
        }

        if (this.world.isRemote || !this.active) {

            return;
        }

        if (this.remainingTicks < 0) {

            this.remainingTicks = cycleTime;
        }

        this.remainingTicks--;

        if (this.remainingTicks % 20 == 0) {

            this.sync();
        }

        // Check if a day has passed.
        if (this.remainingTicks == 0) {

            final int requiredPower = this.getRequiredPower();

            // Power demands met.
            if (this.energyHandler.getEnergyStored() >= requiredPower) {

                this.generatedPoints++;
                this.energyHandler.setEnergy(this.energyHandler.getEnergyStored() - requiredPower);
                final ParaboxUserData ownerData = WorldSpaceTimeManager.getWorldData().getUserData(this.ownerId);
                ownerData.setPoints(this.generatedPoints);
                WorldSpaceTimeManager.saveCustomWorldData();
                this.sendOwnerMessage(TextFormatting.LIGHT_PURPLE, "info.parabox.update.daily", this.getRequiredPower());
            }

            // Power demands not met.
            else {

                this.sendOwnerMessage(TextFormatting.RED, "info.parabox.update.deactivate", this.generatedPoints);
                this.setActive(false);
            }
        }

        // 30 second warning, and one minute warnng.
        else if ((this.remainingTicks == 600 || this.remainingTicks == 1200) && this.energyHandler.getEnergyStored() < this.getRequiredPower()) {

            this.sendOwnerMessage(TextFormatting.RED, "info.parabox.update.warn", this.generatedPoints, this.getMissingPower(), Parabox.ticksToTime(this.getRemainingTicks()));
        }
    }

    private void sendOwnerMessage (TextFormatting color, String message, Object... args) {

        final EntityPlayer player = this.getPlayer();

        if (player != null) {

            Parabox.sendMessage(player, color, message, args);
        }
    }

    private EntityPlayer getPlayer () {

        return this.world.getPlayerEntityByUUID(this.ownerId);
    }

    public int getMissingPower () {

        return Math.max(this.getRequiredPower() - this.energyHandler.getEnergyStored(), 0);
    }

    public int getRequiredPower () {

        return (this.generatedPoints + 1) * 100000;
    }

    public int getRemainingTicks () {

        return this.remainingTicks;
    }

    public IEnergyStorage getEnergyHandler () {

        return this.energyHandler;
    }

    public void setActive (boolean state) {

        this.active = state;
        final ParaboxUserData ownerData = WorldSpaceTimeManager.getWorldData().getUserData(this.ownerId);

        if (ownerData != null) {

            ownerData.setActive(state);
        }

        if (state) {

            WorldSpaceTimeManager.initiateWorldBackup();
        }

        else {

            this.generatedPoints = 0;
            this.remainingTicks = 0;

            if (ownerData != null) {

                ownerData.setPoints(0);
            }

            WorldSpaceTimeManager.saveCustomWorldData();
            WorldSpaceTimeManager.handleFailState();
        }
    }

    public boolean isActive () {

        return this.active;
    }

    public boolean isOwner (EntityPlayer player) {

        return this.ownerId != null && this.ownerId.equals(player.getUniqueID());
    }

    public void setConfirmation (boolean state) {

        this.confirmed = state;
    }

    public boolean hasConfirmed () {

        return this.confirmed;
    }

    public UUID getOwnerId () {

        return this.ownerId;
    }

    @SideOnly(Side.CLIENT)
    public List<String> getInfo (List<String> entries, EntityPlayer player) {

        if (!this.isOwner(player)) {

            entries.add(TextFormatting.RED + I18n.format("parabox.status.unauthorized"));
        }

        else if (this.active) {

            entries.add(I18n.format("parabox.status.target", format.format(this.getRequiredPower())));
            entries.add(I18n.format("parabox.status.power", format.format(this.getPower())));
            entries.add(I18n.format("parabox.status.missing", format.format(this.getMissingPower())));
            entries.add(I18n.format("parabox.status.cycle", Parabox.ticksToTime(this.getRemainingTicks())));
            entries.add(I18n.format("parabox.status.points", this.generatedPoints));
        }

        else {

            entries.add(I18n.format("parabox.status.offline"));
        }

        return entries;
    }
}