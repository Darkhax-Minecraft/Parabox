package net.darkhax.parabox.gui;

import java.io.IOException;
import java.util.ArrayList;

import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.parabox.Parabox;
import net.darkhax.parabox.block.TileEntityParabox;
import net.darkhax.parabox.network.PacketActivate;
import net.darkhax.parabox.network.PacketConfirmReset;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiParabox extends GuiScreen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Parabox.MODID, "textures/gui/parabox-background.png");
    private static final int xSize = 176;
    private static final int ySize = 115;
    private static final String[] SPINNER = { "|", "/", "-", "\\" };
    private final TileEntityParabox tile;
    private final EntityPlayer user;

    private int loadTime;
    private int startX;
    private int startY;

    private boolean active;
    private boolean confirmed;

    private GuiButton statusButton;
    private GuiButton confirmationButton;

    public GuiParabox (TileEntityParabox tile, EntityPlayer user) {

        this.tile = tile;
        this.user = user;

        this.active = tile.isActive();
        this.confirmed = tile.hasConfirmed();
    }

    @Override
    public void initGui () {

        this.startX = (this.width - xSize) / 2;
        this.startY = (this.height - ySize) / 2;

        this.buttonList.clear();
        this.loadTime = MathsUtils.nextIntInclusive(1, 4);
        this.statusButton = new GuiButton(0, this.startX + 14, this.startY + 89, 60, 20, I18n.format("parabox.button." + (this.active ? "deactivate" : "activate")));
        this.confirmationButton = new GuiButton(1, this.startX + xSize - 74, this.startY + 89, 60, 20, I18n.format("parabox.button.loop." + (this.confirmed ? "on" : "off")));
        this.buttonList.add(this.statusButton);
        this.buttonList.add(this.confirmationButton);

        if (!this.tile.isOwner(this.user)) {

            this.statusButton.enabled = false;
            this.confirmationButton.enabled = false;
            this.confirmationButton.visible = false;
        }

        if (!this.active) {

            this.confirmationButton.enabled = false;
            this.confirmationButton.visible = false;
        }
    }

    @Override
    protected void actionPerformed (GuiButton button) throws IOException {

        if (button == this.statusButton) {

            Parabox.NETWORK.sendToServer(new PacketActivate(this.tile.getPos()));
            this.active = !this.active;
            this.confirmed = false;
        }

        else if (button == this.confirmationButton) {

            Parabox.NETWORK.sendToServer(new PacketConfirmReset(this.tile.getPos()));
            this.confirmed = !this.confirmed;
        }
    }

    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();
        this.drawBackgroundImage();

        final int worldTicks = this.mc.player.ticksExisted;

        // Handle loading screen text
        if (this.loadTime > 0) {

            this.drawString(this.fontRenderer, SPINNER[worldTicks % 12 / 3], this.startX + 10, this.startY + 12, 16777215);

            if (worldTicks % 20 == 0) {

                this.loadTime--;
            }
        }

        else {

            int lineNum = 0;

            for (final String line : this.tile.getInfo(new ArrayList<>(), this.user)) {

                final int offset = this.fontRenderer.FONT_HEIGHT * lineNum + 2 * lineNum;
                this.drawString(this.fontRenderer, line, this.startX + 10, this.startY + 12 + offset, 16777215);
                lineNum++;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.statusButton.isMouseOver() && this.statusButton.enabled) {

            this.drawHoveringText(I18n.format("parabox.tip." + (this.active ? "deactivate" : "activate")), mouseX, mouseY + this.fontRenderer.FONT_HEIGHT);
        }

        else if (this.confirmationButton.isMouseOver() && this.confirmationButton.enabled) {

            this.drawHoveringText(I18n.format("parabox.tip.loop." + (!this.confirmed ? "off" : "on")), mouseX, mouseY + this.fontRenderer.FONT_HEIGHT);
        }
    }

    @Override
    public boolean doesGuiPauseGame () {

        return false;
    }

    private void drawBackgroundImage () {

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        final int i = (this.width - xSize) / 2;
        final int j = (this.height - ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
    }
}