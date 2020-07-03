package rendering;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    public void init() {
    	glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void setBackgroundColor(int r, int g, int b) {
    	glClearColor(r / 255f, g / 255f, b / 255f, 0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    public void setCameraSize(int width, int height) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, 0, height, 1, -1);
		glMatrixMode(GL_MODELVIEW);
    }

    public void resetBinding() {
    	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

	public void drawRectangle(float x, float y, int w, int h) {
		glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(x, y);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(x + w, y);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(x + w, y + h);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(x, y + h);
        glEnd();
	}
	
	public void drawMesh(float[] vertices) {
		glBegin(GL_TRIANGLES);
		for (int i = 0; i < vertices.length; i += 8) {
			glTexCoord2f(vertices[i+6], vertices[i+7]);
            glVertex2f(vertices[i], vertices[i+1]);
		}
		glEnd();
	}
	
	public void dispose() {	}

    
}
