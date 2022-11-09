/******************************************
 *Project-------OpenGL-Font-Rendering
 *File----------Main.java
 *Author--------Justin Kachele
 *Date----------11/7/2022
 *License-------Mozilla Public License Version 2.0
 ******************************************/
package com.jkachele.font;

import com.jkachele.font.render.Engine;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(540, 960, "Font Rendering");
        engine.start();
    }
}
