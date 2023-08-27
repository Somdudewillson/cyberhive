package somdudewillson.cyberhive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import somdudewillson.cyberhive.common.ContactEvents;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberItems;

@Mod(CyberhiveMod.MODID)
public class CyberhiveMod {
	public static final String MODID = "cyberhive";
	public static final String VERSION = "0.1.0";

    // Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

    public CyberhiveMod() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
//        MinecraftForge.EVENT_BUS.register(new CyberBlocks());
//        MinecraftForge.EVENT_BUS.register(new CyberItems());
//        MinecraftForge.EVENT_BUS.register(new ContactEvents());
        FMLJavaModLoadingContext.get().getModEventBus().register(new CyberBlocks());
        FMLJavaModLoadingContext.get().getModEventBus().register(new CyberItems());
        
        MinecraftForge.EVENT_BUS.register(new ContactEvents());
        
	    CyberhiveMod.LOGGER.debug("Registered to EVENT_BUS");
    }
}
