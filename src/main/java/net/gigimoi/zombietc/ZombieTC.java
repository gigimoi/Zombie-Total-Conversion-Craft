package net.gigimoi.zombietc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.gigimoi.zombietc.block.*;
import net.gigimoi.zombietc.block.purchasable.BlockPurchaseEventLever;
import net.gigimoi.zombietc.block.purchasable.BlockPurchaseItemstack;
import net.gigimoi.zombietc.client.event.*;
import net.gigimoi.zombietc.entity.EntityZZombie;
import net.gigimoi.zombietc.event.*;
import net.gigimoi.zombietc.item.ItemNodeLinker;
import net.gigimoi.zombietc.item.weapon.WeaponLoader;
import net.gigimoi.zombietc.tile.*;
import net.gigimoi.zombietc.tile.purchasable.TilePurchaseEventLever;
import net.gigimoi.zombietc.tile.purchasable.TilePurchaseItemStack;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ZombieTC.MODID, version = ZombieTC.VERSION)
public class ZombieTC {
    public static final String MODID = "zombietc";
    public static final String VERSION = "0.3";
    public static final String NETWORK_CHANNEL = "channel_zombie_tc";

    public static final CreativeTabs tab = new CreativeTabsExt("Zombie Total Conversion");

    @Mod.Instance("zombietc")
    public static ZombieTC instance;

    public static SimpleNetworkWrapper network;
    public static GameManager gameManager;
    public static EditorModeManager editorModeManager;
    public static MouseManager mouseManager;
    public static KeyManager keyManager;
    public static MainGuiOverrideManager mainGuiOverrideManager;
    public static FogManager fogManager;
    public static PlayerBuffRenderManager playerBuffRenderManager;
    public static PlayerManager playerManager;
    public static GameOverlayManager gameOverlayManager;

    @SidedProxy(clientSide = "net.gigimoi.zombietc.client.ClientProxy", serverSide = "net.gigimoi.zombietc.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        proxy.registerGui();

        mouseManager = new MouseManager();
        editorModeManager = new EditorModeManager();
        gameManager = new GameManager();
        keyManager = new KeyManager();
        playerManager = new PlayerManager();
        gameOverlayManager = new GameOverlayManager();
        FMLCommonHandler.instance().bus().register(editorModeManager);
        FMLCommonHandler.instance().bus().register(gameManager);
        FMLCommonHandler.instance().bus().register(mouseManager);
        FMLCommonHandler.instance().bus().register(keyManager);
        FMLCommonHandler.instance().bus().register(playerManager);
        FMLCommonHandler.instance().bus().register(gameOverlayManager);
        MinecraftForge.EVENT_BUS.register(gameManager);
        MinecraftForge.EVENT_BUS.register(mouseManager);
        MinecraftForge.EVENT_BUS.register(editorModeManager);
        MinecraftForge.EVENT_BUS.register(keyManager);
        MinecraftForge.EVENT_BUS.register(playerManager);
        MinecraftForge.EVENT_BUS.register(gameOverlayManager);
        MinecraftForge.EVENT_BUS.register(new NaturalSpawnManager());

        EntityRegistry.registerModEntity(EntityZZombie.class, "Z Zombie", 1, this, 80, 3, true);

        registerItem(ItemNodeLinker.instance());
        WeaponLoader.register();

        registerBlock(BlockSpawner.zombie);
        registerBlock(BlockNode.instance);
        registerBlock(BlockNodeDoor.iron);

        registerBlock(BlockPlayerImpassible.instance);

        registerBlock(BlockPurchaseEventLever.instance, false);
        registerBlock(BlockBarricade.wooden);
        registerBlock(BlockPurchaseItemstack.instance);
        registerBlock(BlockChanceChest.instance);

        registerTileEntity(TileSpawner.class);
        registerTileEntity(TileBarricade.class);
        registerTileEntity(TileNode.class);
        registerTileEntity(TilePurchaseItemStack.class);
        registerTileEntity(TilePurchaseEventLever.class);
        registerTileEntity(TileNodeDoor.class);
        registerTileEntity(TileChanceChest.class);
        registerTileEntity(TileBlockImpassible.class);

        proxy.renderers();
        proxy.network();
        proxy.keyBinds();
    }

    public void registerTileEntity(Class c) {
        GameRegistry.registerTileEntity(c, c.getCanonicalName());
    }

    public void registerBlock(Block block) {
        registerBlock(block, true);
    }

    public void registerBlock(Block block, boolean setTextureName) {
        block.setCreativeTab(tab);
        if (setTextureName) block.setBlockTextureName(MODID + ":" + block.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(block, block.getUnlocalizedName());
    }

    public void registerItem(Item item) {
        registerItem(item, true);
    }

    public void registerItem(Item item, boolean setTextureName) {
        item.setCreativeTab(tab);
        if (setTextureName) item.setTextureName(MODID + ":" + item.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(item, item.getUnlocalizedName(), MODID);
    }

    private static class CreativeTabsExt extends CreativeTabs {
        public CreativeTabsExt(String lable) {
            super(lable);
        }

        @Override
        public Item getTabIconItem() {
            return ItemNodeLinker.instance();
        }
    }
}
