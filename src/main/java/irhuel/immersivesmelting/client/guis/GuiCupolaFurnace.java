package irhuel.immersivesmelting.client.guis;

import irhuel.immersivesmelting.common.containers.ContainerCupolaFurnace;
import irhuel.immersivesmelting.common.tileentities.TileCupolaFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.*;

public class GuiCupolaFurnace extends GuiContainer {
    private static final ResourceLocation textureLoc = new ResourceLocation("immersivesmelting:textures/guis/gui_cupola_furnace.png");
    private TileCupolaFurnace tileEntity;

    public GuiCupolaFurnace(InventoryPlayer invPlayer, TileCupolaFurnace tileCupolaFurnace) {
        super(new ContainerCupolaFurnace(invPlayer, tileCupolaFurnace));

        this.xSize = 176;
        this.ySize = 158;

        this.tileEntity = tileCupolaFurnace;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(textureLoc);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        // draw progress bar for input item cook times
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                double cookProgress = tileEntity.getCookProgressRatio(x + y * 2);
                int yOffset = (int) ((1.0 - cookProgress) * 16);

                drawTexturedModalRect(
                        i + 41 + 21 * x, j + 24 + yOffset + 18 * y, //drawpos
                        176, yOffset,                               //texpos
                        3, 16 - yOffset);                           //texdims
            }
        }
        // draw progress bar for remaining burn time on current fuel
        double burnRemaining = tileEntity.getBurnRemainingRatio();
        int yOffset = (int) ((1.0 - burnRemaining) * 16);

        drawTexturedModalRect(i + 97, 51 + j + yOffset,
                176, yOffset,
                3, 16 - yOffset);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        fontRendererObj.drawString(this.tileEntity.getDisplayName().getUnformattedText(), 5, 5, Color.darkGray.getRGB());

        java.util.List<String> hoveringText = new ArrayList<String>();

        // add cook time to hovering text if over progress box
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                if (isInRect(guiLeft + 41 + 21 * x, guiTop + 24 + 18 * y, 3, 16, mouseX, mouseY)) {
                    hoveringText.add("Smelting Progress:");
                    int cookPercentage = (int) (tileEntity.getCookProgressRatio(x + y * 2) * 100);
                    hoveringText.add(cookPercentage + "%");
                }
            }
        }

        // add burn time remaining to hovering text if over progress box
        if (isInRect(guiLeft + 97, guiTop + 51, 3, 16, mouseX, mouseY)) {
            hoveringText.add("Burn Time Remaining:");
            hoveringText.add(tileEntity.getBurnRemainingSeconds() + "s");
        }

        // draw hovering text if not empty
        if (!hoveringText.isEmpty()){
            drawHoveringText(hoveringText, mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }
    }

    private static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }
}
