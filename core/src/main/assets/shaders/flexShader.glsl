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
precision highp float;
#endif

int mod(int x, int y) {
    return x - y * (x / y);
}



varying vec2 vUV;
uniform sampler2D uTex;
uniform sampler2D uMap;
uniform int uMapWidth;
uniform int uMapHeight;
uniform int uTilesetWidth;
uniform int uTilesetHeight;
uniform float uTime;

void main() {
float tilesetSize = 48.;

vec4 tInfo = texture2D(uMap, vUV);
int tileIdx = int(tInfo.r*255.);
int tileX = mod(tileIdx, uTilesetWidth);
int tileY = tileIdx/uTilesetWidth;
vec2 fUV = fract(vUV*vec2(uMapWidth, uMapHeight));
vec2 tUV = vec2(tileX, tileY) + fUV;
gl_FragColor = texture2D(uTex, tUV/vec2(uTilesetWidth, uTilesetHeight)) * vec4(1.);
    if(tileIdx>253)
    discard;
}