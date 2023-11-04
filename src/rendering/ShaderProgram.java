package rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class ShaderProgram {

	// https://github.com/SilverTiger/lwjgl3-tutorial/
	private static final String defaultVertShader = "#version 330 core\n" + 
			"layout(location = 0) in vec2 position;\n" +
			"layout(location = 1) in vec2 texCoords;\n" +
	//		"in vec4 color;\n" + 
	//		"out vec4 vertexColor;\n" + 
			"out vec2 fragTexCoords;\n" +
			"uniform mat4 projection;\n" + 
	//		"uniform mat4 view;\n" + 
	//		"uniform mat4 model;\n" + 
			"void main() {\n" + 
			"    gl_Position = projection * vec4(position, 0.0, 1.0);\n" + 
			"    fragTexCoords = texCoords;\n" + 
			//		"    vertexColor = color;\n" + 
			"}";
	private static final String defaultFragShader = "#version 330 core\n" + 
			"in vec2 fragTexCoords;\n" + 
	//		"in vec4 vertexColor;\n" + 
			"out vec4 color;\n" + 
			"uniform sampler2D texture0;\n" + 
			"void main() {\n" + 
			"    vec4 textureColor = texture(texture0, fragTexCoords);\n" + 
			"    color = textureColor;\n" + 
			"}";

	private static final int[] defaultAttributes = new int[] {
		GL_FLOAT, 4, 2,
		GL_FLOAT, 4, 2
	};
	
	private int id;

	public final int[] attributes;
	public final int vertexSize;
	

	public ShaderProgram(int vertShader, int fragShader, int[] attributes, int nTextures) {
		id = createProgram(vertShader, fragShader);
		this.attributes = attributes;
		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException("Shader program linking failed: " + glGetProgramInfoLog(id));
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

	public ShaderProgram(String vertShader, String fragShader, int[] attributes, int nTextures) throws Exception {
		this(loadVertexShader(vertShader), loadFragmentShader(fragShader), attributes, nTextures);
	}

	public ShaderProgram() throws Exception {
		this(loadVertexShader(), loadFragmentShader(), defaultAttributes, 1);
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

	public static int createShader(String shaderCode, int shaderType) throws Exception {
		int shaderId = glCreateShader(shaderType);
		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);
		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new Exception("Shader compilation failed: " + glGetShaderInfoLog(shaderId));
		}
		return shaderId;
	}

	public static int loadVertexShader(String name) throws Exception {
		String shaderCode = readFileAsString(name);
		return createShader(shaderCode, GL_VERTEX_SHADER);
	}

	public static int loadFragmentShader(String name) throws Exception {
		String shaderCode = readFileAsString(name);
		return createShader(shaderCode, GL_FRAGMENT_SHADER);
	}

	public static int loadVertexShader() throws Exception {
		return createShader(defaultVertShader, GL_VERTEX_SHADER);
	}

	public static int loadFragmentShader() throws Exception {
		return createShader(defaultFragShader, GL_FRAGMENT_SHADER);
	}

	private static String readFileAsString(String filename) throws Exception {
		StringBuilder source = new StringBuilder();
		FileInputStream in = new FileInputStream(filename);
		Exception exception = null;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			Exception innerExc= null;
			try {
				String line;
				while((line = reader.readLine()) != null)
					source.append(line).append('\n');
			}
			catch(Exception exc) {
				exception = exc;
			}
			finally {
				try {
					reader.close();
				}
				catch(Exception exc) {
					if(innerExc == null)
						innerExc = exc;
					else
						exc.printStackTrace();
				}
			}
			if(innerExc != null)
				throw innerExc;
		}
		catch(Exception exc) {
			exception = exc;
		}
		finally {
			try {
				in.close();
			}
			catch(Exception exc) {
				if(exception == null)
					exception = exc;
				else
					exc.printStackTrace();
			}

			if(exception != null)
				throw exception;
		}
		return source.toString();
	}

}
