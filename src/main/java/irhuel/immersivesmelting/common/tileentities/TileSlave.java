package irhuel.immersivesmelting.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class TileSlave extends TileEntity {
    private boolean hasMaster;
    private int masterX, masterY, masterZ;

    public boolean checkMultiBlockForm() {


        return false;
    }

    public void setupStructure() {

    }

    public boolean checkForMaster() {


        return false;
    }

    public void resetStructure() {

    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("masterX", masterX);
        compound.setInteger("masterY", masterY);
        compound.setInteger("masterZ", masterZ);
        compound.setBoolean("hasMaster", hasMaster);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        masterX = compound.getInteger("masterX");
        masterY = compound.getInteger("masterY");
        masterZ = compound.getInteger("masterZ");
        hasMaster = compound.getBoolean("hasMaster");
    }

    public boolean hasMaster() {
        return hasMaster;
    }

    public int getMasterX() {
        return masterX;
    }

    public int getMasterY() {
        return masterY;
    }

    public int getMasterZ() {
        return masterZ;
    }

    public void setHasMaster(boolean hasMaster) {
        this.hasMaster = hasMaster;
    }

    public void setMasterPos(BlockPos pos) {
        masterX = pos.getX();
        masterY = pos.getY();
        masterZ = pos.getZ();
    }
}
