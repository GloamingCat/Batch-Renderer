package rendering;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Screen {

	public final int width, height;
	public final Texture texture;

	private final int id;
	private final FloatBuffer projectionMatrixBuffer;

    public Screen(int width, int height, boolean isWindow) throws OpenGLException {
		this.width = width;
		this.height = height;
		if (glfwGetCurrentContext() == 0)
			throw new OpenGLException("No bound context.");
        Matrix4f projectionMatrix;
        if (isWindow) {
			id = 0;
			texture = null;
			projectionMatrix = Matrix4f.orthographic(0, width, height, 0, 1, -1);
		} else {
			id = glGenFramebuffers();
			texture = new Texture(width, height, 4);
			texture.bindToFrameBuffer(id);
			projectionMatrix = Matrix4f.orthographic(0, width, 0, height, 1, -1);
		}
		projectionMatrixBuffer = MemoryUtil.memAllocFloat(16);
		projectionMatrix.toBuffer(projectionMatrixBuffer);
	}

	public void bind(ShaderProgram program) {
		glBindFramebuffer(GL_FRAMEBUFFER, id);
		if (program != null) {
			int location = program.getUniformLocation("projection");
			glUniformMatrix4fv(location, false, projectionMatrixBuffer);
		}
		glViewport(0, 0, width, height);
	}
	
	public void dispose(boolean keepTexture) {
		if (id != 0)
			glDeleteFramebuffers(id);
		if (texture != null && !keepTexture)
			texture.dispose();
	}

	public void dispose() {
		dispose(false);
	}

}
