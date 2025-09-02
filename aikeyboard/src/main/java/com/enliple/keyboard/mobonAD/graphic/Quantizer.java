package com.enliple.keyboard.mobonAD.graphic;

import java.util.List;

/**
 * Definition of an algorithm that receives pixels and outputs a list of colors.
 */
public interface Quantizer {
    void quantize(final int[] pixels, final int maxColors, final Palette.Filter[] filters);
    List<Palette.Swatch> getQuantizedColors();
}
