uniform mat4 uCamera;
uniform mat4 uModel;
attribute vec4 aXYZW;
attribute vec2 aUV;
varying vec2 vUV;
void main() {
gl_Position = uCamera * uModel * aXYZW;
vUV = aUV;
}

//

#ifdef GL_ES
precision mediump float;
#endif
varying vec2 vUV;
uniform sampler2D uTex;
uniform vec4 uColorM;
uniform vec4 uColorA;
void main() {
gl_FragColor = texture2D( uTex, vUV ) * uColorM + uColorA;
}