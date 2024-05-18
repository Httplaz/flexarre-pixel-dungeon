/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa;

import com.badlogic.gdx.Gdx;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Uniform;

//This class should be used on heavy pixel-fill based loads when lighting is not needed.
// It skips the lighting component of the fragment shader, giving a significant performance boost

//Remember that switching programs is expensive
// if this script is to be used many times try to block them together
public class NoosaScriptFlex extends NoosaScript {

    private static String shader;
    public Uniform uMap;
    public Uniform uMapWidth;
    public Uniform uMapHeight;
    public Uniform uTilesetWidth;
    public Uniform uTilesetHeight;

    public Uniform uTime;
    public NoosaScriptFlex() {
        super();
        uMap = uniform("uMap");
        uMapWidth = uniform("uMapWidth");
        uMapHeight = uniform("uMapHeight");
        uTilesetWidth = uniform("uTilesetWidth");
        uTilesetHeight = uniform("uTilesetHeight");
        uTime = uniform("uTime");
    }

    public static NoosaScriptFlex get() {
        return Script.use(NoosaScriptFlex.class);
    }

    public void setAtlasAndMapTexUnitId(int atlasId, int mapId) {
        uTex.value1i(atlasId);
        uMap.value1i(mapId);
    }

    @Override
    public void lighting(float rm, float gm, float bm, float am, float ra, float ga, float ba, float aa) {
        //Does nothing
    }

    @Override
    protected String shader() {
        if (shader == null) {
            shader = Gdx.files.internal("shaders/flexShader.glsl").readString();
        }
        return shader;
    }
}
