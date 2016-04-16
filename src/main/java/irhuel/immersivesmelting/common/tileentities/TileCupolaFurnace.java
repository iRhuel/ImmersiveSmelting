package irhuel.immersivesmelting.common.tileentities;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

import java.util.Arrays;

public class TileCupolaFurnace extends TileEntity implements ITickable, IInventory {

    // index 0-3 = input, 4 = fuel, 5 = output
    private ItemStack[] furnaceItemStacks = new ItemStack[6];

    private boolean isMaster;
    private int burnTimeRemaining;  // number of ticks remaining on current piece of fuel
    private int fuelBurnTime;  // initial fuel value of the currently burning fuel
    private int[] itemCookTime = new int[4];   // current cook time for input slots

    private static final int COOK_TIME_FOR_COMPLETION = 400;  // the number of ticks to smelt

    @Override
    public int getSizeInventory() {
        return furnaceItemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return furnaceItemStacks[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stackInSlot = furnaceItemStacks[index];
        ItemStack stackRemoved;

        if (stackInSlot == null)
            return null;

        if (stackInSlot.stackSize <= count) {
            stackRemoved = stackInSlot;
            setInventorySlotContents(index, null);
        } else {
            stackRemoved = stackInSlot.splitStack(count);
            if (stackInSlot.stackSize == 0)
                setInventorySlotContents(index, null);
        }
        markDirty();
        return stackRemoved;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        furnaceItemStacks[index] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit())
            stack.stackSize = getInventoryStackLimit();

        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (this.worldObj.getTileEntity(this.pos) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
                if (isItemValidForInputSlot(stack) && furnaceItemStacks[index] == null)
                    return true;
            case 4:
                return isItemFuel(stack);
            case 5:
                return false;
            default:
                return false;
        }
    }

    public boolean isItemValidForInputSlot(ItemStack stack) {
        return !isItemFuel(stack);
    }

    public boolean isItemValidForFuelSlot(ItemStack stack) {
        return isItemFuel(stack);
    }

    public boolean isItemValidForOutputSlot(ItemStack stack) {
        return false;
    }

    @Override
    public void update() {
        for (int i = 0; i < 4; i++) {
            ItemStack input = furnaceItemStacks[i];
            if (input != null && canSmelt(i)) {
                startBurnFuel();
                cookItem(i);
            }
            else {
                if (input == null) itemCookTime[i] = 0;
                if (itemCookTime[i] > 0 && !isBurning()) itemCookTime[i]--;
            }
        }
        if (isBurning()) burnTimeRemaining--;
    }

    private boolean isBurning() {
        return this.burnTimeRemaining > 0;
    }

    private void startBurnFuel() {
        if (!this.isBurning()) {
            boolean inventoryChanged = false;
            if (this.furnaceItemStacks[4] != null && isItemFuel(this.furnaceItemStacks[4]) && this.furnaceItemStacks[4].stackSize > 0) {
                this.burnTimeRemaining = this.fuelBurnTime = getFuelBurnTime(this.furnaceItemStacks[4]);
                this.furnaceItemStacks[4].stackSize--;
                inventoryChanged = true;
                if (this.furnaceItemStacks[4].stackSize == 0)
                    this.furnaceItemStacks[4] = null;
            }
            if (inventoryChanged) markDirty();
        }
    }

    private boolean canSmelt(int index) {
        ItemStack input = this.furnaceItemStacks[index];
        ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input);
        if (output == null) return false;
        if (this.furnaceItemStacks[5] == null) return true;
        if (!this.furnaceItemStacks[5].isItemEqual(output)) return false;
        int result = this.furnaceItemStacks[5].stackSize + output.stackSize;
        return result <= getInventoryStackLimit() && result <= this.furnaceItemStacks[5].getMaxStackSize();
    }

    private void cookItem(int index) {
        if (isBurning())
            itemCookTime[index]++;
        if (itemCookTime[index] == COOK_TIME_FOR_COMPLETION) {
            smeltItem(index);
            itemCookTime[index] = 0;
        }
    }

    private void smeltItem(int index) {
        ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[index]);

        if (this.furnaceItemStacks[5] == null)
            this.furnaceItemStacks[5] = smeltingResult.copy();
        else if (this.furnaceItemStacks[5].getItem() == smeltingResult.getItem())
            this.furnaceItemStacks[5].stackSize += smeltingResult.stackSize;
        this.furnaceItemStacks[index].stackSize--;
        if (furnaceItemStacks[index].stackSize <= 0)
            furnaceItemStacks[index] = null;
        markDirty();
    }

    public boolean isItemFuel(ItemStack stack) {
        return stack != null && getFuelBurnTime(stack) > 0;
    }

    private int getFuelBurnTime(ItemStack stack) {
        if (stack == null) {
            return 0;
        } else {
            Item item = stack.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
                Block block = Block.getBlockFromItem(item);
                if (block == Blocks.coal_block) return 16000;
                else return 0;
            }
            if (item == Items.coal) return 1600;
        }
        return 0;
    }

    public int getBurnRemainingSeconds() {
        if (burnTimeRemaining <= 0) return 0;
        return burnTimeRemaining / 20;
    }

    public double getBurnRemainingRatio() {
        if (burnTimeRemaining <= 0) return 0;
        double ratio = (double) burnTimeRemaining / (double) fuelBurnTime;
        return MathHelper.clamp_double(ratio, 0.0, 1.0);
    }

    public double getCookProgressRatio(int index) {
        double ratio = (double) itemCookTime[index] / (double) COOK_TIME_FOR_COMPLETION;
        return MathHelper.clamp_double(ratio, 0.0, 1.0);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < this.furnaceItemStacks.length; i++) {
            if (this.furnaceItemStacks[i] != null) {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte("Slot", (byte) i);
                this.furnaceItemStacks[i].writeToNBT(stackTag);
                tagList.appendTag(stackTag);
            }
        }
        compound.setTag("Items", tagList);
        compound.setInteger("burnTimeRemaining", this.burnTimeRemaining);
        compound.setInteger("fuelBurnTime", this.fuelBurnTime);
        compound.setTag("itemCookTime", new NBTTagIntArray(this.itemCookTime));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        final byte NBT_TYPE = 10;
        NBTTagList tagList = compound.getTagList("Items", NBT_TYPE);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound stackTag = tagList.getCompoundTagAt(i);
            int slot = stackTag.getByte("Slot") & 255;
            this.furnaceItemStacks[slot] = ItemStack.loadItemStackFromNBT(stackTag);
        }
        this.burnTimeRemaining = compound.getInteger("burnTimeRemaining");
        this.fuelBurnTime = compound.getInteger("fuelBurnTime");
        this.itemCookTime = Arrays.copyOf(compound.getIntArray("itemCookTime"), 4);
    }

    @Override
    public void clear() {
        Arrays.fill(furnaceItemStacks, null);
    }

    @Override
    public String getName() {
        return "container.cupola_furnace.name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        int metadata = getBlockMetadata();
        return new S35PacketUpdateTileEntity(this.pos, metadata, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stackremoved = furnaceItemStacks[index];

        if (stackremoved != null)
            setInventorySlotContents(index, null);

        return stackremoved;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public int getField(int id) {
        if (id == 0) return this.burnTimeRemaining;
        if (id == 1) return this.fuelBurnTime;
        if (id > 1 && id <= 5) return itemCookTime[id - 2];
        System.err.println("Invalid field ID in TileCupolaFurnace.getField:" + id);
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == 0) this.burnTimeRemaining = value;
        else if (id == 1) this.fuelBurnTime = value;
        else if (id > 1 && id <= 5) this.itemCookTime[id - 2] = value;
        else System.err.println("Invalid field ID in TileCupolaFurnace.setField:" + id);
    }

    @Override
    public int getFieldCount() {
        return 6;
    }
}
