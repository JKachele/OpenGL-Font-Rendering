/******************************************
 *Project-------OpenGL-Font-Rendering
 *File----------GameFont.java
 *Author--------Justin Kachele
 *Date----------11/7/2022
 *License-------Mozilla Public License Version 2.0
 ******************************************/
package com.jkachele.font.fonts;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameFont {
    private String filePath;
    private int fontSize;
    private int width;
    private int height;
    private int lineHeight;
    private Map<Integer, CharInfo> charMap;

    public GameFont(String filePath, int fontSize) {
        this.filePath = filePath;
        this.fontSize = fontSize;
        this.charMap = new HashMap<>();
        generateBitmap();
    }

    public void generateBitmap() {
        Font font = new Font(this.filePath, Font.PLAIN, this.fontSize);

        // Create a temporary image to calculate font information
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(font);
        FontMetrics fontMetrics = graphics.getFontMetrics();

        int estFontWidth = (int)Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
        width = 0;
        height = fontMetrics.getHeight();
        lineHeight = fontMetrics.getHeight();
        int x = 0;
        int y = (int)(fontMetrics.getHeight() * 1.4f);

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                // Get the size for each codepoint glyph and update the actual width and height
                CharInfo charInfo = new CharInfo(x, y, fontMetrics.charWidth(i), fontMetrics.getHeight());
                charMap.put(i, charInfo);
                width = Math.max(x + fontMetrics.charWidth(i), width);

                x += charInfo.sourceWidth;
                if (x > estFontWidth) {
                    x = 0;
                    y += fontMetrics.getHeight() * 1.4f;
                    height += fontMetrics.getHeight() * 1.4f;
                }
            }
        }
        height += fontMetrics.getHeight() * 1.4f;
        graphics.dispose();

        // Create the final Texture
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                CharInfo info = charMap.get(i);
                info.calcTexCoords(width, height);
                graphics.drawString("" + (char)i, info.sourceX, info.sourceY);
            }
        }

        graphics.dispose();

        try {
            File file = new File("temp.png");
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
