package nukeduck.armorchroma;

import java.io.File;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ArmorChroma.MODID, name = "Armor Chroma", version = "1.2")
public class ArmorChroma {
	/** Minecraft instance for convenience purposes (don't want to call {@link Minecraft#getMinecraft()} every time). */
	public static final Minecraft mc = Minecraft.getMinecraft();

	/** Main instance of the mod */
	@Instance(ArmorChroma.MODID)
	public static ArmorChroma INSTANCE;
	/** Mod ID<br/>
	 * Always use this rather than literals. */
	public static final String MODID = "armorchroma";

	/** Logger instance for error and info messages */
	public Logger logger;

	/** Mod configuration to load into */
	public final Config config;
	/** Instance of the Gui class for rendering */
	private final GuiArmor armor;

	public ArmorChroma() {
		this.config = new Config();
		this.armor = new GuiArmor();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		this.logger = e.getModLog();
		this.config.directory = new File(e.getModConfigurationDirectory(), "armorchroma");
		this.config.init(
				new File(this.config.directory, MODID + ".cfg"),
				new File(this.config.directory, "icons.json"));
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	boolean press = false;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderOverlay(RenderGameOverlayEvent.Pre e) {
		if(e.getType() == ElementType.ARMOR) {
			e.setCanceled(true); // Don't want anything else rendering on top
			this.armor.renderArmorBar(e.getResolution().getScaledWidth(), e.getResolution().getScaledHeight());
		}

		if(!Constants.DEBUG) return;
		if(!press && (press = Keyboard.isKeyDown(Keyboard.KEY_Y))) {
			this.config.reload();
		} else {
			press = Keyboard.isKeyDown(Keyboard.KEY_Y);
		}
	}

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e) {
		if(!Constants.DEBUG) return;

		if(e.getItemStack().getItem() instanceof ItemArmor) {
			e.getToolTip().add(((ItemArmor) e.getItemStack().getItem()).getArmorMaterial().toString());
		}
		e.getToolTip().add(Item.REGISTRY.getNameForObject(e.getItemStack().getItem()).toString());
	}
}
