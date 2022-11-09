/******************************************
 *Project-------OpenGL-Font-Rendering
 *File----------GameFont.java
 *Author--------Justin Kachele
 *Date----------11/7/2022
 *License-------Mozilla Public License Version 2.0
 ******************************************/
package com.jkachele.font.fonts;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class GameFont {
    private String filePath;
    private int fontSize;
    private int width;
    private int height;
    private int lineHeight;
    private Map<Integer, CharInfo> charMap;

    int textureID;

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

        uploadTexture(image);
    }

    private void uploadTexture(BufferedImage image) {
        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                byte alpha = (byte)((pixel >> 24) & 0xFF);
                buffer.put(alpha);
                buffer.put(alpha);
                buffer.put(alpha);
                buffer.put(alpha);
            }
        }
        buffer.flip();

        // Generate the texture on GPU
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set the texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);   // Wrap in x direction
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);   // Wrap in y direction

        // When stretching and shrinking the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);  // Stretching
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);  // Shrinking

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        buffer.clear();
    }

    public CharInfo getCharacter(int codePoint) {
        return charMap.getOrDefault(codePoint, new CharInfo(0, 0, 0, 0));
    }

    public int getTextureID() {
        return textureID;
    }
}
