package net.gigimoi.zombietc.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.gigimoi.zombietc.EntityZZombie;
import net.gigimoi.zombietc.ZombieTC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

/**
 * Created by gigimoi on 8/2/2014.
 */
public class LivingManager {
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if(EntityPlayer.class.isAssignableFrom(event.entity.getClass())) {
            event.entity.registerExtendedProperties(ZombieTC.MODID, new ZombieTCPlayerProperties());
        }
    }
    int ticker = 0;
    @SubscribeEvent
    public void onTick(TickEvent event) {
        ticker++;
        if(event.side == Side.SERVER && event.phase == TickEvent.Phase.START && ticker % 20 == 0) {
            ticker = 0;
            World world = ZombieTC.proxy.getWorld(event.side);
            if(world == null) {
                return;
            }
            for(int i = 0; i < world.playerEntities.size(); i++) {
                EntityPlayer player = (EntityPlayer) world.playerEntities.get(i);
                ZombieTCPlayerProperties playerProperties = (ZombieTCPlayerProperties) player.getExtendedProperties(ZombieTC.MODID);
                if(playerProperties.timeSinceHit < 5) {
                    playerProperties.timeSinceHit++;
                } else if(player.getHealth() < player.getMaxHealth()) {
                    player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 10, 3));
                }
            }
        }
    }
    @SubscribeEvent
    public void onLivingSpawnEvent(LivingSpawnEvent event) {
    }
    @SubscribeEvent
    public void onAllowDespawn(LivingSpawnEvent.AllowDespawn event) {
        if(event.entityLiving.getClass() == EntityZZombie.class) {
            event.setResult(Event.Result.DENY);
        }
    }
    public static class ZombieTCPlayerProperties implements IExtendedEntityProperties {
        public int timeSinceHit;
        @Override
        public void saveNBTData(NBTTagCompound tag) {
            tag.setInteger("Time Since Hit", timeSinceHit);
        }
        @Override
        public void loadNBTData(NBTTagCompound tag) {
            timeSinceHit = tag.getInteger("Time Since Hit");
        }
        @Override
        public void init(Entity entity, World world) {
            timeSinceHit = 0;
        }
    }
}
