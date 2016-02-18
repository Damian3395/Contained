package mod.gamescience;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

/**
 * Declaration of custom keyboard inputs for this mod.
 */
public class KeyBindings {

	public static KeyBinding toggleVisualizerGUI;
	
	public static void init() {
		toggleVisualizerGUI = new KeyBinding("key.dataVisGUI"
				, Keyboard.KEY_0
				, "key.categories."+DataVis.MODID);
		
		ClientRegistry.registerKeyBinding(toggleVisualizerGUI);
	}
	
}
