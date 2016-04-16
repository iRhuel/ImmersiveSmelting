package irhuel.immersivesmelting.client.guis;

import irhuel.immersivesmelting.common.containers.ContainerCupolaFurnace;
import irhuel.immersivesmelting.common.tileentities.TileCupolaFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int MOD_TILE_ENTITY_GUI = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == MOD_TILE_ENTITY_GUI)
            return new ContainerCupolaFurnace(player.inventory, (TileCupolaFurnace) world.getTileEntity(new BlockPos(x, y, z)));
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == MOD_TILE_ENTITY_GUI)
            return new GuiCupolaFurnace(player.inventory, (TileCupolaFurnace) world.getTileEntity(new BlockPos(x, y, z)));
        return null;
    }
}
