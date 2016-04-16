package irhuel.immersivesmelting.common.containers;

import irhuel.immersivesmelting.common.tileentities.TileCupolaFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCupolaFurnace extends Container {

    /**
     * SLOTS                            INDEX
     * <p>
     * 0-8 = hotbar slots               0-8
     * 9-35 = player inventory slots    9-35
     * <p>
     * 36-41 = furnace slots
     * 36-39 = input slots              0-3
     * 40 = fuel slot                   4
     * 41 = output slot                 5
     */

    private TileCupolaFurnace tileCupolaFurnace;

    public ContainerCupolaFurnace(InventoryPlayer invPlayer, TileCupolaFurnace tileCupolaFurnace) {
        this.tileCupolaFurnace = tileCupolaFurnace;

        // hotbar slots
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 134));
        }
        // player inv slots
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + x * 18, 76 + y * 18));
            }
        }
        // furnace input slots
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                addSlotToContainer(new inputSlot(tileCupolaFurnace, x + y * 2, 24 + x * 21, 24 + y * 18));
            }
        }
        // furnace fuel slot
        addSlotToContainer(new fuelSlot(tileCupolaFurnace, 4, 80, 51));
        //furnace output slot
        addSlotToContainer(new outputSlot(tileCupolaFurnace, 5, 125, 27));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileCupolaFurnace.isUseableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot sourceSlot = this.inventorySlots.get(index);
        if (sourceSlot == null || !sourceSlot.getHasStack()) return null;
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index >= 0 && index < 36) {
            if (tileCupolaFurnace.isItemFuel(sourceStack)) {
                if (!mergeItemStack(sourceStack, 40, 41, false)) {
                    return null;
                }
            }
            else if (!mergeInputStack(sourceStack, 36, 40, false)) {
                return null;
            }
        } else if (index >= 36 && index < 42) {
            if (!mergeItemStack(sourceStack, 0, 36, false)) {
                return null;
            }
        } else {
            System.err.print("Invalid slotIndex:" + index);
            return null;
        }

        if (sourceStack.stackSize == 0) {
            sourceSlot.putStack(null);
        } else {
            sourceSlot.onSlotChanged();
        }

        sourceSlot.onPickupFromSlot(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    private boolean mergeInputStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        boolean success = false;
        int index = startIndex;

        if (useEndIndex)
            index = endIndex - 1;

        Slot slot;
        ItemStack stackInSlot;

        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!useEndIndex && index < endIndex || useEndIndex && index >= startIndex)) {
                slot = this.inventorySlots.get(index);
                stackInSlot = slot.getStack();

                if (stackInSlot != null && stackInSlot.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == stackInSlot.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, stackInSlot)) {
                    int l = stackInSlot.stackSize + stack.stackSize;
                    int maxsize = Math.min(stack.getMaxStackSize(), slot.getItemStackLimit(stack));

                    if (l <= maxsize) {
                        stack.stackSize = 0;
                        stackInSlot.stackSize = l;
                        slot.onSlotChanged();
                        success = true;
                    } else if (stackInSlot.stackSize < maxsize) {
                        stack.stackSize -= stack.getMaxStackSize() - stackInSlot.stackSize;
                        stackInSlot.stackSize = stack.getMaxStackSize();
                        slot.onSlotChanged();
                        success = true;
                    }
                }

                if (useEndIndex) {
                    --index;
                } else {
                    ++index;
                }
            }
        }

        if (stack.stackSize > 0) {
            if (useEndIndex) {
                index = endIndex - 1;
            } else {
                index = startIndex;
            }

            while (!useEndIndex && index < endIndex || useEndIndex && index >= startIndex && stack.stackSize > 0) {
                slot = this.inventorySlots.get(index);
                stackInSlot = slot.getStack();

                // Forge: Make sure to respect isItemValid in the slot.
                if (stackInSlot == null && slot.isItemValid(stack)) {
                    if (stack.stackSize <= slot.getItemStackLimit(stack)) {
                        slot.putStack(stack.copy());
                        stack.stackSize = 0;
                        success = true;
                        break;
                    } else {
                        ItemStack newstack = stack.copy();
                        newstack.stackSize = slot.getItemStackLimit(stack);
                        slot.putStack(newstack);
                        stack.stackSize -= slot.getItemStackLimit(stack);
                        success = true;
                    }
                }

                if (useEndIndex) {
                    --index;
                } else {
                    ++index;
                }
            }
        }

        return success;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.tileCupolaFurnace.closeInventory(playerIn);
    }

    private int[] cachedFields;

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean allFieldsHaveChanged = false;
        boolean fieldHasChanged[] = new boolean[tileCupolaFurnace.getFieldCount()];
        if (cachedFields == null) {
            cachedFields = new int[tileCupolaFurnace.getFieldCount()];
            allFieldsHaveChanged = true;
        }
        for (int i = 0; i < cachedFields.length; ++i) {
            if (allFieldsHaveChanged || cachedFields[i] != tileCupolaFurnace.getField(i)) {
                cachedFields[i] = tileCupolaFurnace.getField(i);
                fieldHasChanged[i] = true;
            }
        }
        for (ICrafting icrafting : this.crafters) {
            for (int fieldID = 0; fieldID < tileCupolaFurnace.getFieldCount(); ++fieldID) {
                if (fieldHasChanged[fieldID]) {
                    icrafting.sendProgressBarUpdate(this, fieldID, cachedFields[fieldID]);
                }
            }
        }
    }

    private class inputSlot extends Slot {

        inputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return tileCupolaFurnace.isItemValidForInputSlot(stack);
        }
    }

    private class fuelSlot extends Slot {

        fuelSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return tileCupolaFurnace.isItemValidForFuelSlot(stack);
        }
    }

    private class outputSlot extends Slot {

        outputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return tileCupolaFurnace.isItemValidForOutputSlot(stack);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        tileCupolaFurnace.setField(id, data);
    }
}
