package org.fl.util.swing;

import java.awt.Color;
import java.lang.reflect.Field;

public class ColorHelpers {

	public static Color parse(String colorAsString) {
		Color color;
		try {
		    Field field = Class.forName("java.awt.Color").getField(colorAsString);
		    color = (Color)field.get(null);
		} catch (Exception e) {
			// It is not a string (such as Red, BLUE...)
			// try RGB number
			try {
				return Color.decode(colorAsString) ;
			} catch (Exception e1) {
				color = null ; 
			}
		}
		return color ;
	}
}
