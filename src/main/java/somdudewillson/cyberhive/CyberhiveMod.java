package somdudewillson.cyberhive;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberItems;

@Mod(modid = CyberhiveMod.MODID, version = CyberhiveMod.VERSION)
public class CyberhiveMod {
	public static final String MODID = "cyberhive";
	public static final String VERSION = "0.1.0";
	
	@Mod.Instance(MODID)
	public static CyberhiveMod instance;
	
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(new CyberBlocks());
        MinecraftForge.EVENT_BUS.register(new CyberItems());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	
    }
}
