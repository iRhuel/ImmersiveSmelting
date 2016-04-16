package irhuel.immersivesmelting.common.blocks;

import irhuel.immersivesmelting.ISContent;
import irhuel.immersivesmelting.ImmersiveSmelting;
import irhuel.immersivesmelting.client.guis.GuiHandler;
import irhuel.immersivesmelting.common.tileentities.TileCupolaFurnace;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockCupolaFurnace extends BlockContainer {

    public BlockCupolaFurnace(String unlocalizedName) {
        super(Material.iron);
        this.setUnlocalizedName(unlocalizedName);
        this.setCreativeTab(ISContent.tabImmersiveSmelting);
        GameRegistry.registerBlock(this, unlocalizedName);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCupolaFurnace();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote)
            playerIn.openGui(ImmersiveSmelting.instance, GuiHandler.MOD_TILE_ENTITY_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileCupolaFurnace tileEntity = (TileCupolaFurnace) worldIn.getTileEntity(pos);
        InventoryHelper.dropInventoryItems(worldIn, pos, tileEntity);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getRenderType() {
        return 3;
    }
}
