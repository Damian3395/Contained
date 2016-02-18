package mod.gamescience;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

/**
 * Actions to perform when custom keyboard inputs are received.
 */
public class KeyInputHandler {

	private VisualizerGUI gui;
	
	public KeyInputHandler(VisualizerGUI guiReference) {
		this.gui = guiReference;
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (KeyBindings.toggleVisualizerGUI.isPressed()) {
			gui.guiRender = !gui.guiRender;
		}
	}
	
}
