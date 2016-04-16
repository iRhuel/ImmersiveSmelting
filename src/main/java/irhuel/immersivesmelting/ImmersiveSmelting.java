package irhuel.immersivesmelting;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ImmersiveSmelting.MODID, name = ImmersiveSmelting.MODNAME, version = ImmersiveSmelting.VERSION)
public class ImmersiveSmelting
{
    public static final String MODID = "immersivesmelting";
    public static final String MODNAME = "Immersive Smelting";
    public static final String VERSION = "0.1.0";

    @Instance
    public static ImmersiveSmelting instance = new ImmersiveSmelting();

    @SidedProxy(clientSide="irhuel.immersivesmelting.ClientProxy", serverSide="irhuel.immersivesmelting.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
