package net.gigimoi.zombietc.item;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Created by gigimoi on 8/1/2014.
 */
public class ItemBaubleRingOfHealth extends ItemZombieTCBauble {
    static private ItemBaubleRingOfHealth _instance;

    private ItemBaubleRingOfHealth() {
        super();
        setUnlocalizedName("Ring of Health");
    }

    public static ItemBaubleRingOfHealth instance() {
        if (_instance == null) {
            _instance = new ItemBaubleRingOfHealth();
        }
        return _instance;
    }

    @Override
    public int getStackSlot() {
        return 2;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entityLivingBase) {

    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase entityLivingBase) {
    }
}