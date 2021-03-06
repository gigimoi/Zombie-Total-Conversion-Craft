package net.gigimoi.zombietc.block;

import net.gigimoi.zombietc.ZombieTC;
import net.gigimoi.zombietc.client.tilerenderer.TileRendererBarricade;
import net.gigimoi.zombietc.entity.EntityZZombie;
import net.gigimoi.zombietc.event.GameManager;
import net.gigimoi.zombietc.net.map.MessageAddBarricade;
import net.gigimoi.zombietc.net.map.MessageRemoveBarricade;
import net.gigimoi.zombietc.tile.TileBarricade;
import net.gigimoi.zombietc.util.Point3;
import net.gigimoi.zombietc.util.TextureHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by gigimoi on 7/16/2014.
 */
public class BlockBarricade extends BlockContainerZTC implements IItemRenderer {
    public static BlockBarricade wooden = new BlockBarricade("Wooden");

    public BlockBarricade(String prefix) {
        super(Material.rock);
        setBlockName(prefix + " Barricade");
        setHardness(1.0f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileBarricade();
    }

    @Override
    public int getLightOpacity() {
        return 0;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess access, int x, int y, int z, int meta) {
        return false;
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List mask, Entity entity) {
        if (entity != null) {
            if (entity.getClass() == EntityZZombie.class) {
                if (((TileBarricade) world.getTileEntity(x, y, z)).damage == 6) {
                    return;
                }
                super.addCollisionBoxesToList(world, x, y, z, aabb, mask, entity);
            } else {
                if (!ZombieTC.editorModeManager.enabled) {
                    super.addCollisionBoxesToList(world, x, y, z, aabb, mask, entity);
                }
            }
        } else {
            super.addCollisionBoxesToList(world, x, y, z, aabb, mask, entity);
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        super.setBlockBoundsBasedOnState(access, x, y, z);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess access, int x, int y, int z) {
        return false;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        GameManager.blockBarricades.add(new Point3(x, y, z));
        ZombieTC.network.sendToAll(new MessageAddBarricade(x, y, z));
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int side) {
        super.breakBlock(world, x, y, z, block, side);
        for (int i = 0; i < GameManager.blockBarricades.size(); i++) {
            Point3 vec = GameManager.blockBarricades.get(i);
            if (vec.distanceTo(new Point3(x, y, z)) < 0.01) {
                GameManager.blockBarricades.remove(i);
            }
        }
        ZombieTC.network.sendToAll(new MessageRemoveBarricade(x, y, z));
    }


    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPushMatrix();
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            //EntityPlayer player = (EntityPlayer) data[1];
            GL11.glTranslated(0, 1, 0);
        }
        TextureHelper.bindTexture(new ResourceLocation(ZombieTC.MODID,
                "textures/blocks/" + getUnlocalizedName().substring(5) + ".png")
        );
        TileRendererBarricade.model.renderAll();
        GL11.glPopMatrix();
    }
}
