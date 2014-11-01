/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum FireworkColor {

    AQUA (Color.AQUA),
    BLACK (Color.BLACK),
    BLUE (Color.BLUE),
    FUCHSIA (Color.FUCHSIA),
    GRAY (Color.GRAY),
    GREEN (Color.GREEN),
    LIME (Color.LIME),
    MAROON (Color.MAROON),
    NAVY (Color.NAVY),
    OLIVE (Color.OLIVE),
    ORANGE (Color.ORANGE),
    PURPLE (Color.PURPLE),
    RED (Color.RED),
    SILVER (Color.SILVER),
    TEAL (Color.TEAL),
    WHITE (Color.WHITE),
    YELLOW (Color.YELLOW),
    RANDOM (null);

    private Color color;

    private static Random random = new Random();

    FireworkColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        if(this != RANDOM) return this.color;
        return values()[random.nextInt(values().length)].getColor();
    }

    public static List<Color> getColors(List<String> input) {
        List<Color> output = new ArrayList<Color>();
        for (String i : input) {
            if (valueOf(i) == null) continue;
            output.add(valueOf(i).getColor());
        }
        return output;
    }

    public static List<FireworkColor> parseList(List<String> input) {
        List<FireworkColor> output = new ArrayList<FireworkColor>();
        for (String i : input) {
            if (valueOf(i) == null) continue;
            output.add(valueOf(i));
        }
        return output;
    }
}
