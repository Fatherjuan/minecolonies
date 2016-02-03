package com.minecolonies.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Java8 functional interfaces for {@link net.minecraft.inventory.IInventory}
 * TODO: Only partially done.
 * Most methods will be remapping of parameters to reduce duplication.
 * TODO: Get overloading right without erasure clashes
 */
public class InventoryFunctions
{

    /**
     * Search for a stack in an Inventory matching the predicate.
     * (IInventory, Integer) -> Boolean
     * @param inventory the inventory to search in
     * @param tester the function to use for testing slots
     * @return true if it found a stack
     */
    public static boolean matchFirstInInventory(IInventory inventory, BiFunction<Integer,ItemStack, Boolean> tester)
    {
        return matchFirstInInventory(inventory, inv -> slot -> stack -> tester.apply(slot,stack));
    }

    /**
     * Topmost matchFirst function, will stop after it finds the first itemstack.
     * @param inventory the inventory to search in
     * @param tester the function to use for testing slots
     * @return true if it found a stack
     */
    private static boolean matchFirstInInventory(IInventory inventory,
                                                Function<IInventory,
                                                        Function<Integer,
                                                                Function<ItemStack,
                                                                        Boolean>>> tester)
    {
        return matchInInventory(inventory,tester,true);
    }

    /**
     * Topmost function to actually loop over the inventory.
     * Will return if it found something.
     * @param inventory the inventory to loop over
     * @param tester the function to use for testing slots
     * @param stopAfterFirst if it should stop executing after finding one stack that applies
     * @return true if it found a stack
     */
    private static boolean matchInInventory(IInventory inventory,
                                           Function<IInventory,
                                                   Function<Integer,
                                                           Function<ItemStack,
                                                                   Boolean>>> tester,
                                           boolean stopAfterFirst)
    {
        if (inventory == null)
        {
            return false;
        }
        int size = inventory.getSizeInventory();
        boolean foundOne = false;
        for (int slot = 0; slot < size; slot++)
        {
            ItemStack stack = inventory.getStackInSlot(slot);
            //Unchain the function and apply it
            if (tester.apply(inventory).apply(slot).apply(stack))
            {
                foundOne = true;
                if (stopAfterFirst)
                {
                    return true;
                }
            }
        }
        return foundOne;
    }
}
