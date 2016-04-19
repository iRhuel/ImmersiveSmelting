package irhuel.immersivesmelting.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

public class TileMBSlave extends TileEntity implements ISidedInventory {
    private TileCupolaFurnace master;

    public TileCupolaFurnace getMaster() {
        return master;
    }

    public void setMaster(TileCupolaFurnace master) {
        this.master = master;
    }

    @Override
    public int getSizeInventory() {
        return master != null ? master.getSizeInventory() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return master != null ? master.getStackInSlot(index) : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return master != null ? master.decrStackSize(index, count) : null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return master != null ? removeStackFromSlot(index) : null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (master != null)
            master.setInventorySlotContents(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return master != null ? master.getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return master != null && master.isItemValidForSlot(index, stack);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getName() {
        return "container.mb_slave.name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return null;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return master != null ? master.getSlotsForFace(side) : null;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return master != null && master.canInsertItem(index, itemStackIn, direction);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return master != null && master.canExtractItem(index, stack, direction);
    }
}
