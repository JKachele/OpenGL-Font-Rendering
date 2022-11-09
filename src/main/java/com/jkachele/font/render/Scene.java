/******************************************
 *Project-------OpenGL-Font-Rendering
 *File----------Scene.java
 *Author--------Justin Kachele
 *Date----------11/9/2022
 *License-------MIT License
 ******************************************/
package com.jkachele.font.render;

import com.jkachele.font.fonts.GameFont;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL20C.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Scene {
    private static int vaoID;
    private static int vboID;
    private static int eboID;

    private static ShaderParser defaultShader;

    static GameFont font;

    private static float[] vertexArray = {
            -0.5f, -0.5f, 0.0f,           0.0f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,     // 0: Bottom Left
            -0.5f,  0.5f, 0.0f,           0.0f, 1.0f,     0.0f, 1.0f, 0.0f, 1.0f,     // 1: Top Left
             0.5f,  0.5f, 0.0f,           1.0f, 1.0f,     0.0f, 0.0f, 1.0f, 1.0f,     // 2: Top Right
             0.5f, -0.5f, 0.0f,           1.0f, 0.0f,     1.0f, 1.0f, 1.0f, 1.0f      // 3: Bottom Right
    };

    private static int[] elementArray = {
            0, 2, 1,
            0, 3, 2
    };

    public static void init() {
        font = new GameFont("assets/fonts/JetBrainsMono.ttf", 128);
        Vector2f[] uvCoords = font.getCharacter('B').texCoords;
        vertexArray[3]  = uvCoords[0].x;
        vertexArray[4]  = uvCoords[0].y;
        vertexArray[12] = uvCoords[1].x;
        vertexArray[13] = uvCoords[1].y;
        vertexArray[21] = uvCoords[2].x;
        vertexArray[22] = uvCoords[2].y;
        vertexArray[30] = uvCoords[3].x;
        vertexArray[31] = uvCoords[3].y;


        defaultShader = new ShaderParser("assets/shaders/default.glsl");
        defaultShader.compile();

        // ============================================================
        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        // ============================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int uvSize = 2;
        int colorSize = 4;
        int vertexSizeBytes = (positionsSize + uvSize + colorSize) * Float.BYTES;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, uvSize, GL_FLOAT, false, vertexSizeBytes,
                (positionsSize) * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, colorSize, GL_FLOAT, false, vertexSizeBytes,
                (positionsSize + uvSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    public static void update(float dt) {
        defaultShader.use();

        // Upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTextureID());

        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glBindVertexArray(0);
    }
}
