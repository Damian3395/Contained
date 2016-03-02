package com.contained.game.util;

import java.awt.Color;

public class ColorUtil {
	//Generate a color code from a String
	public static int colorHash(String s) {
        return (s.hashCode() & 11184810) + 4473924;
    }
	
	//Generate a hue spectrum over a number range
	public static Color hueGrad(float val, float max) {
		float hue = val/max;
		// Tweak saturation & val in green/blue area of spectrum to 
		// increase visibility
		float sat = 0.6f;
		float bright = 1.0f;
		if (hue >= 0.25 && hue <= 0.5) {
			sat = 0.6f + (hue - 0.25f);
			bright = 1.0f + (hue - 0.5f);
		}
		return Color.getHSBColor(hue, sat, bright);
	}
}
