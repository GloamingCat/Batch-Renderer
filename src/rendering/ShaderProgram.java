package rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ShaderProgram {

	// https://github.com/SilverTiger/lwjgl3-tutorial/
	private static final String defaultVertShader = """
            #version 330 core
            layout(location = 0) in vec2 position;
            layout(location = 1) in vec2 texCoords;
            layout(location = 2) in vec4 vertexColor;
            out vec4 fragColor;
            out vec2 fragTexCoords;
            uniform mat4 projection;
            void main() {
                gl_Position = projection * vec4(position, 0.0, 1.0);
                fragTexCoords = texCoords;
                fragColor = vertexColor;
            }""";
	private static final String defaultFragShader = """
            #version 330 core
            in vec2 fragTexCoords;
            in vec4 fragColor;
            out vec4 color;
            uniform sampler2D texture0;
            void main() {
                vec4 textureColor = texture(texture0, fragTexCoords);
                color = fragColor * textureColor;
            }""";

	public static final int[] defaultShaderAttributes = new int[] {
		GL_FLOAT, 4, 2,
		GL_FLOAT, 4, 2,
		GL_FLOAT, 4, 4
	};
	
	public static final int[] defaultAttributes = new int[] {
		GL_FLOAT, 4, 2,
		GL_FLOAT, 4, 2,
		GL_FLOAT, 4, 4,
		GL_FLOAT, 4, 3
	};
	
	private final int id;

	public final int[] attributes;
	public final int vertexSize;
	
	public ShaderProgram(int vertShader, int fragShader, int[] attributes, int nTextures) {
		id = createProgram(vertShader, fragShader);
		this.attributes = attributes;
		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
			throw new UnsatisfiedLinkError("Shader program linking failed: " + glGetProgramInfoLog(id));
		}
		bind();
		//glBindFragDataLocation(id, 0, "color");
		for (int unit = 0; unit < nTextures; unit++) {
			int textureLocation = glGetUniformLocation(id, "texture" + unit);
			glUniform1i(textureLocation, unit);
		}
		int error = glGetError();
		if (error != GL_NO_ERROR) {
			System.err.println("Shader Error: " + error);
		}
		int vertexSize = 0;
		int nAtt = attributes.length / 3;
		for (int i = 0; i < nAtt; i++) {
			int typeSize = attributes[3*i+1];
			int n = attributes[3*i+2];
			vertexSize += typeSize * n;
		}
		this.vertexSize = vertexSize;
	}

	public ShaderProgram(String vertShader, String fragShader, int[] attributes, int nTextures) {
		this(loadVertexShader(vertShader), loadFragmentShader(fragShader), attributes, nTextures);
	}
	
	public ShaderProgram(String vertShader, String fragShader, int[] attributes) {
		this(loadVertexShader(vertShader), loadFragmentShader(fragShader), attributes, 1);
	}

	public ShaderProgram() {
		this(loadVertexShader(), loadFragmentShader(), defaultShaderAttributes, 1);
	}

	public ShaderProgram(String vertShader, String fragShader) {
		this(vertShader, fragShader, defaultAttributes);
	}

	public void bind() {
		glUseProgram(id);
	}

	public void bindAttribute(int i, String name) {
		glBindAttribLocation(id, i, name);
	}

	public int getAttributeLocation(CharSequence name) {
		return glGetAttribLocation(id, name);
	}

	public int getUniformLocation(CharSequence name) {
		return glGetUniformLocation(id, name);
	}

	public void dispose() {
		glDeleteProgram(id);
	}

	public static int createProgram(int vertShader, int fragShader) {
		int shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertShader);
		glAttachShader(shaderProgram, fragShader);
		return shaderProgram;
	}

	public static int createShader(String shaderCode, int shaderType) {
		int shaderId = glCreateShader(shaderType);
		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);
		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new OpenGLException("Shader compilation failed: " + glGetShaderInfoLog(shaderId));
		}
		return shaderId;
	}

	public static int loadVertexShader(String name) {
		String shaderCode = readFileAsString(name);
		return createShader(shaderCode, GL_VERTEX_SHADER);
	}

	public static int loadFragmentShader(String name) {
		String shaderCode = readFileAsString(name);
		return createShader(shaderCode, GL_FRAGMENT_SHADER);
	}

	public static int loadVertexShader() {
		return createShader(defaultVertShader, GL_VERTEX_SHADER);
	}

	public static int loadFragmentShader() {
		return createShader(defaultFragShader, GL_FRAGMENT_SHADER);
	}

	private static String readFileAsString(String filename) {
		StringBuilder source = new StringBuilder();
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try (InputStream in = classloader.getResourceAsStream(filename)) {
			if (in == null) {
				System.err.println("Couldn't find shader file: " + filename);
				return "";
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
				String line;
				while((line =reader.readLine())!=null)
						source.append(line).append('\n');
			} catch (IOException e) {
				System.err.println("Shader file loading failed: " + filename);
			}
		} catch (IOException e) {
			System.err.println("Couldn't find shader file: " + filename);
		}
		return source.toString();
	}

}
