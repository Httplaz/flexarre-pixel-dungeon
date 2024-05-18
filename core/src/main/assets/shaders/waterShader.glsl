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
varying vec2 vUV;
uniform sampler2D uTex;
uniform float uTime;

#define TAU 6.28318530718
#define MAX_ITER 3

vec3 water(vec2 fragCoord)
{
    vec2 uv = fragCoord.xy;

    vec2 p = mod(uv*TAU, TAU)-250.0;
    vec2 i = vec2(p);
    float c = 1.0;
    float inten = .005;

    for (int n = 0; n < MAX_ITER; n++)
    {
        float t = uTime * (1.0 - (3.5 / float(n+1)));
        i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
        c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten),p.y / (cos(i.y+t)/inten)));
    }
    c /= float(MAX_ITER);
    c = 1.17-pow(c, 1.4);
    vec3 colour = vec3(pow(abs(c), 8.0));
    colour = clamp(colour + vec3(0.0, 0.35, 0.5), 0.0, 1.0);

    return colour;
}


void main() {
    gl_FragColor = texture2D( uTex, vUV )*vec4(water(vUV), 1.);
}