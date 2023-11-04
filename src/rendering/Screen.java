package rendering;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Screen {

	public final int width, height;
	public final Texture texture;

	private final int id;
	private final FloatBuffer projectionMatrixBuffer;
	private final Matrix4f projectionMatrix;

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		id = 0;
		texture = null;
		projectionMatrix = Matrix4f.orthographic(0, width, height, 0, 1, -1);
		projectionMatrixBuffer = MemoryUtil.memAllocFloat(16);
		projectionMatrix.toBuffer(projectionMatrixBuffer);
	}
	
	public Screen(int width, int height, int twidth, int theight) {
		this.width = twidth;
		this.height = theight;
		id = glGenFramebuffers();
		texture = new Texture(twidth, theight, 4);
		texture.bindToFrameBuffer(id);
		projectionMatrix = Matrix4f.orthographic(0, width, 0, height, 1, -1);
		projectionMatrixBuffer = MemoryUtil.memAllocFloat(16);
		projectionMatrix.toBuffer(projectionMatrixBuffer);
	}

	public void bind(ShaderProgram program) {
		glBindFramebuffer(GL_FRAMEBUFFER, id);
		if (program != null) {
			int location = program.getUniformLocation("projection");
			glUniformMatrix4fv(location, false, projectionMatrixBuffer);
		} else {
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, width, height, 0, 1, -1);
			glMatrixMode(GL_MODELVIEW);
		}
	}

	public void dispose() {
		if (id != 0)
			glDeleteFramebuffers(id);
		if (texture != null)
			texture.dispose();
	}

}
