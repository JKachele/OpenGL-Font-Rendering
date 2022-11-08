/******************************************
 *Project-------OpenGL-Font-Rendering
 *File----------CharInfo.java
 *Author--------Justin Kachele
 *Date----------11/7/2022
 *License-------MIT License
 ******************************************/
package com.jkachele.font.fonts;

import org.joml.Vector2f;

public class CharInfo {
    public int sourceX;
    public int sourceY;
    public int sourceWidth;
    public int sourceHeight;

    public Vector2f[] texCoords = new Vector2f[4];

    public CharInfo(int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    public void calcTexCoords(int fontWidth, int fontHeight) {
        float x0 = (float)sourceX / (float)fontWidth;
        float y0 = (float)sourceY / (float)fontHeight;
        float x1 = (float)(sourceX + sourceWidth) / (float)fontWidth;
        float y1 = (float)(sourceY + sourceHeight) / (float)fontHeight;

        texCoords[0] = new Vector2f(x0, y0);
        texCoords[1] = new Vector2f(x1, y0);
        texCoords[2] = new Vector2f(x1, y1);
        texCoords[3] = new Vector2f(x0, y1);
    }
}
