uniform mat4 u_Matrix;
attribute vec4 a_Position;
attribute vec4 a_Color;
varying  vec4 v_Color;

void main()
{
    gl_Position = u_Matrix * vec4(a_Position.x * 0.1, a_Position.y * 0.1, a_Position.z * 0.1, a_Position.w);
    v_Color = a_Color;
}