package irhuel.immersivesmelting.common.blocks;

import irhuel.immersivesmelting.ISContent;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockMBSlave extends BlockContainer {

    public BlockMBSlave(String unlocalizedName) {
        super(Material.iron);
        this.setUnlocalizedName(unlocalizedName);
        this.setCreativeTab(ISContent.tabImmersiveSmelting);
        GameRegistry.registerBlock(this, unlocalizedName);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public int getRenderType() {
        return 3;
    }
}
