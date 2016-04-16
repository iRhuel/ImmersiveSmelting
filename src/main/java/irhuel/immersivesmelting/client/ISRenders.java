package irhuel.immersivesmelting.client;

import irhuel.immersivesmelting.ISContent;
import irhuel.immersivesmelting.ImmersiveSmelting;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public final class ISRenders {

    public static void preInitISRenders() {

    }

    public static void initISRenders() {
        reg(ISContent.cupolaFurnace, 0);
    }

    // default render register
    public static void reg(Block block, int meta, String file) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(ImmersiveSmelting.MODID + ":" + file, "inventory"));
    }

    public static void reg(Block block, int meta) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(ImmersiveSmelting.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory"));
    }

    public static void reg(Item item, int meta) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(ImmersiveSmelting.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
}
