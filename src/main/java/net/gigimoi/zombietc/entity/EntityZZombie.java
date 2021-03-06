package net.gigimoi.zombietc.entity;

import com.stackframe.pathfinder.Dijkstra;
import net.gigimoi.zombietc.ZombieTC;
import net.gigimoi.zombietc.block.BlockNode;
import net.gigimoi.zombietc.event.PlayerManager;
import net.gigimoi.zombietc.util.pathfinding.MCNode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import scala.Int;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gigimoi on 7/14/2014.
 */
public class EntityZZombie extends EntityZombie {
    public static class Properties implements IExtendedEntityProperties {
        float speed;
        float damage;
        public static String PropertiesIdentifier = "Entity ZZombie Properties";
        private static Properties getProp(Entity entity) {
            return (Properties) entity.getExtendedProperties(PropertiesIdentifier);
        }
        public static float getSpeed(Entity entity) {
            return getProp(entity).speed;
        }
        public static void setSpeed(Entity entity, float speed) {
            getProp(entity).speed = speed;
        }
        public static float getDamage(Entity entity) {
            return getProp(entity).damage;
        }
        public static void setDamage(Entity entity, float damage) {
            getProp(entity).damage = damage;
        }
        @Override
        public void saveNBTData(NBTTagCompound tag) {
            tag.setFloat("Speed", speed);
            tag.setFloat("Damage", damage);
        }

        @Override
        public void loadNBTData(NBTTagCompound tag) {
            speed = tag.getFloat("Speed");
            damage = tag.getFloat("Damage");
        }

        @Override
        public void init(Entity entity, World world) {
        }
    }
    static Random _r = new Random();
    double targetX;
    double targetY;
    double targetZ;
    boolean hasSetDefaultTarget = false;
    boolean yieldingToOtherZombie = false;
    MCNode lastPassed;
    float speed = 1f;

    public EntityZZombie(World world) {
        super(world);
        this.tasks.taskEntries = new ArrayList();
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, Float.MAX_VALUE));
        this.targetTasks.taskEntries = new ArrayList();
        this.setSize(0.6F, 1.8F);
        this.registerExtendedProperties(Properties.PropertiesIdentifier, new Properties());
        Properties.setSpeed(this, 1f);
        Properties.setDamage(this, 2f);
        if(_r.nextBoolean() && ZombieTC.gameManager.wave > 1) {
            this.setCurrentItemOrArmor(1, new ItemStack(Items.leather_boots));
            Properties.setSpeed(this, Properties.getSpeed(this) + 0.1f);
            if(ZombieTC.gameManager.wave >= 5 && _r.nextBoolean()) {
                this.setCurrentItemOrArmor(1, new ItemStack(Items.iron_boots));
                Properties.setSpeed(this, Properties.getSpeed(this) + 0.1f);
            }
            else if(ZombieTC.gameManager.wave >= 8 && _r.nextBoolean()) {
                this.setCurrentItemOrArmor(1, new ItemStack(Items.diamond_boots));
                Properties.setSpeed(this, Properties.getSpeed(this) + 0.2f);
            }
        }
        if(ZombieTC.gameManager.wave > 10 || _r.nextInt(3) == 0) {
            if(ZombieTC.gameManager.wave > 0 && ZombieTC.gameManager.wave <= 2) {
                this.setCurrentItemOrArmor(0, new ItemStack(Items.wooden_sword));
                Properties.setDamage(this, Properties.getDamage(this) + 1);
            }
            if(ZombieTC.gameManager.wave > 2 && ZombieTC.gameManager.wave <= 4) {
                this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
                Properties.setDamage(this, Properties.getDamage(this) + 3);
            }
            if(ZombieTC.gameManager.wave > 4 && ZombieTC.gameManager.wave <= 7) {
                this.setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
                Properties.setDamage(this, Properties.getDamage(this) + 5);
            }
            if(ZombieTC.gameManager.wave > 7) {
                this.setCurrentItemOrArmor(0, new ItemStack(Items.diamond_sword));
                Properties.setDamage(this, Properties.getDamage(this) + 7);
            }
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((int)(ZombieTC.gameManager.wave * 1.7) + 1.3);
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 0;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (ZombieTC.editorModeManager.enabled) {
            isDead = true;
        } else {
            if (!yieldingToOtherZombie) {
                move();
            } else {
                yieldingToOtherZombie = false;
            }
            if (!isDead && this.getHealth() > 0) {
                EntityPlayer nearest = worldObj.getClosestPlayerToEntity(this, Int.MaxValue());
                if (nearest != null && Vec3.createVectorHelper(posX, posY, posZ).distanceTo(Vec3.createVectorHelper(nearest.posX, nearest.posY, nearest.posZ)) < 1.5) {
                    nearest.attackEntityFrom(
                            new DamageSource("Zombie"), Properties.getDamage(this)
                    );
                    ((PlayerManager.ZombieTCPlayerProperties) nearest.getExtendedProperties(ZombieTC.MODID)).timeSinceHit = 0;
                }
            } else {
                this.setSize(0.0F, 0.0F);
            }
        }
    }

    @Override
    protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {//Don't drop equipment
    }

    private void move() {
        if (!hasSetDefaultTarget) {
            targetX = posX;
            targetY = posY;
            targetZ = posZ;
            hasSetDefaultTarget = true;
        }
        List nearbyEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(posX - 1, posY - 1, posZ - 1, posX + 1, posY + 1, posZ + 1));
        for(int i = 0; i < nearbyEntities.size(); i++) {
            Entity entityRaw = (Entity)nearbyEntities.get(i);
            if(entityRaw.getClass() == EntityZZombie.class) {
                if(entityRaw != this && _r.nextInt(2) == 0) {
                    ((EntityZZombie)entityRaw).yieldingToOtherZombie = true;
                }
            }
        }
        float speed = Properties.getSpeed(this) * (ZombieTC.gameManager.wave > 4 ? 0.95f : 0.85f);
        getMoveHelper().setMoveTo(targetX, targetY, targetZ, speed);
        if (targetY > posY) {
            getJumpHelper().setJumping();
        }
        if (_r.nextInt(16) == 5 || Vec3.createVectorHelper(posX, posY, posZ).distanceTo(Vec3.createVectorHelper(targetX, targetY, targetZ)) < 0.5) {
            resetTarget();
        }
    }

    private void resetTarget() {
        EntityPlayer player = worldObj.getClosestPlayerToEntity(this, Double.MAX_VALUE);
        if (player != null) {
            Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
            Vec3 pos = Vec3.createVectorHelper(posX, posY, posZ);
            if (playerPos.distanceTo(pos) < 1.5 && getEntitySenses().canSee(player)) {
                targetX = player.posX;
                targetY = player.posY;
                targetZ = player.posZ;
            } else {
                ArrayList<MCNode> goal = new ArrayList();
                goal.add(BlockNode.getClosestToPosition(worldObj, playerPos, false));
                MCNode start = BlockNode.getClosestToPosition(worldObj, pos, false);
                if (start != null && goal.get(0) != null && BlockNode.nodes != null) {
                    List<MCNode> path = new Dijkstra<MCNode>().findPath(BlockNode.nodes, start, goal);
                    if (path != null) {
                        if (path.get(0).position.toVec3().addVector(0.5, 0, 0.5).distanceTo(Vec3.createVectorHelper(posX, posY, posZ)) < 0.5) {
                            lastPassed = path.get(0);
                        }
                        if (lastPassed == path.get(0)) {
                            if (path.size() > 1) {
                                targetX = path.get(1).position.xCoord + 0.5;
                                targetY = path.get(1).position.yCoord;
                                targetZ = path.get(1).position.zCoord + 0.5;
                            }
                        } else {
                            if (path.size() > 0) {
                                targetX = path.get(0).position.xCoord + 0.5;
                                targetY = path.get(0).position.yCoord;
                                targetZ = path.get(0).position.zCoord + 0.5;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected Item getDropItem() {
        return null;
    }

    @Override
    protected void dropRareDrop(int p_70600_1_) {
    }

    @Override
    protected void addRandomArmor() {
        System.out.println("Tried to add armor");
    }

    @Override
    protected void damageEntity(DamageSource source, float amount) {
        super.damageEntity(source, amount);
        this.hurtResistantTime = 0;
    }
}