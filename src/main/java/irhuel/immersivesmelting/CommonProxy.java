package irhuel.immersivesmelting;

import irhuel.immersivesmelting.ISContent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ISContent.preInit();
    }

    public void init(FMLInitializationEvent event) {
        ISContent.init();
    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}
