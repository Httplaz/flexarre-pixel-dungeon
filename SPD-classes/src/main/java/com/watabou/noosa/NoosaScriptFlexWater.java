package com.watabou.noosa;

import com.badlogic.gdx.Gdx;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Uniform;

public class NoosaScriptFlexWater extends NoosaScriptNoLighting{

    private static String shader;
    public Uniform uTime;

    public NoosaScriptFlexWater()
    {
        uTime = uniform("uTime");
    }

    public static NoosaScriptFlexWater get() {
        return Script.use(NoosaScriptFlexWater.class);
    }

    @Override
    protected String shader() {
        if (shader == null) {
            shader = Gdx.files.internal("shaders/waterShader.glsl").readString();
        }
        return shader;
    }
}
