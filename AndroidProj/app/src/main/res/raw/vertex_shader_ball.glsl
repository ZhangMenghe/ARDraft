//vertex_shader_ball.glsl
uniform mat4 u_Matrix;
attribute vec4 a_Position;
varying vec4 vPosition;
attribute vec2 a_TextureCoordinates;
varying vec2 v_TextureCoordinates;
void main()                    
{                              
    gl_Position = u_Matrix * a_Position;
    vPosition = a_Position;
    v_TextureCoordinates = a_TextureCoordinates;	 
} 