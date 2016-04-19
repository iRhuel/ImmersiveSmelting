package irhuel.immersivesmelting;

import irhuel.immersivesmelting.client.guis.GuiHandler;
import irhuel.immersivesmelting.common.blocks.*;
import irhuel.immersivesmelting.common.tileentities.TileCupolaFurnace;
import irhuel.immersivesmelting.common.tileentities.TileMBSlave;
import net.minecraft.block.BlockContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ISContent {

    public static BlockContainer cupolaFurnace;
    public static BlockContainer slave;

    public static void preInit() {
        cupolaFurnace = new BlockCupolaFurnace("cupola_furnace");
        slave = new BlockMBSlave("mb_slave");
    }

    public static void init() {
        registerTE(TileCupolaFurnace.class, "cupola_furnace_tile");
        registerTE(TileMBSlave.class, "mb_slave_tile");

        NetworkRegistry.INSTANCE.registerGuiHandler(ImmersiveSmelting.instance, new GuiHandler());
    }

    // Custom creative tab creation
    public static CreativeTabs tabImmersiveSmelting = new CreativeTabs("immersivesmelting") {
        public Item getTabIconItem() {
            return null;
        }
        public ItemStack getIconItemStack() {
            return new ItemStack(cupolaFurnace);
        }
    };

    private static void registerTE(Class<? extends TileEntity> tileClass, String unlocalizedName) {
        GameRegistry.registerTileEntity(tileClass, unlocalizedName);
    }
}
