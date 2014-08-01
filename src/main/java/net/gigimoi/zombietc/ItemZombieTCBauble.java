package net.gigimoi.zombietc;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Created by gigimoi on 8/1/2014.
 */
public abstract class ItemZombieTCBauble extends Item implements IBauble {
    protected ItemZombieTCBauble() {
        setMaxStackSize(1);
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase entityLivingBase) {
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase entityLivingBase) {
        System.out.println(entityLivingBase.getClass());
        if(entityLivingBase.getClass() == EntityPlayerMP.class) {
            EntityPlayer entityPlayer = (EntityPlayer) entityLivingBase;
            entityPlayer.addChatMessage(new ChatComponentText(stack.getDisplayName() + " is bound to you"));
        }
        return false;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        if(EntityPlayer.class.isAssignableFrom(entity.getClass())) {
            EntityPlayer player = (EntityPlayer)entity;
            BaublesApi.getBaubles(player).setInventorySlotContents(2, stack);
            for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
                if(player.inventory.getStackInSlot(i) == stack) {
                    player.inventory.setInventorySlotContents(i, null);
                    if(world.isRemote) player.addChatMessage(new ChatComponentText(stack.getDisplayName() + " has been equipped"));
                    break;
                }
            }
        }
        super.onUpdate(stack, world, entity, par4, par5);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        entityItem.isDead = true;
        return true;
    }
}
