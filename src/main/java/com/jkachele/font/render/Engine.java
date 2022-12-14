/******************************************
 *Project-------OpenGL-Font-Rendering
 *File----------Engine.java
 *Author--------Justin Kachele
 *Date----------11/9/2022
 *License-------MIT License
 ******************************************/
package com.jkachele.font.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class Engine implements Runnable{
    private final Thread GAME_LOOP_THREAD;

    public Engine(int width, int height, String title) {
        GAME_LOOP_THREAD = new Thread(this, "GAME_LOOP_THREAD");
        Window.init(width, height, title);
    }

    public void start() {
        GAME_LOOP_THREAD.start();
    }

    @Override
    public void run() {
        try {
            Window.start();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void gameLoop() {
        Scene.init();

        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        // Set the clear color
        glClearColor(1, 1, 1, 1);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(Window.glfwWindow) ) {
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if (dt >= 0) {
                Scene.update(dt);
            }

            glfwSwapBuffers(Window.glfwWindow); // swap the color buffers

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
