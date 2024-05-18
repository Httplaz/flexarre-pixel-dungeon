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

import static java.lang.Math.random;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Texture;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import com.watabou.utils.RectF;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class Tilemap extends Visual {

    protected int cid = 1;
    protected SmartTexture atlas;
    protected Texture map;
    protected TextureFilm tileset;

    protected int[] data;
    protected int mapWidth;
    protected int mapHeight;
    protected int size;
    protected float[] vertices;
    protected FloatBuffer quads;
    protected Vertexbuffer buffer;
    private float cellW;
    private float cellH;
    private Rect updating;
    private volatile Rect updated;
    private boolean fullUpdate;
    private int topLeftUpdating;
    private int bottomRightUpdating;

    public Tilemap(Object tx, TextureFilm tileset) {

        super(0, 0, 0, 0);

        this.atlas = TextureCache.get(tx);
        this.tileset = tileset;

        this.map = new Texture();
        //this.map.bitmap(TextureCache.getBitmap(tx));
        {
            //this.map.bitmap(TextureCache.getBitmap(tx));
            this.map.filter(Texture.NEAREST, Texture.NEAREST);
            //this.map.wrap(Texture.REPEAT, Texture.REPEAT);
        }
        //map = new Texture();
        //map = atlas;
        int[] bytes = new int[48 * 48];

        for (int i = 0; i < 48; i++)
            for (int j = 0; j < 48; j++) {
                bytes[j * 48 + i] = 1 + Random.Int(0xffffff)*256;//data[j * 48 + i];//i * 48 + j;
            }
        //map.bitmap(atlas.bitmap);
        //map.pixels(atlas.width, atlas.height, bytes);
        map.pixels(48, 48, bytes);

        RectF r = tileset.get(0);
        cellW = tileset.width(r);
        cellH = tileset.height(r);

        vertices = new float[16];

        updated = new Rect();
    }

    public void map(int[] data, int cols) {

        this.data = data;

        mapWidth = cols;
        mapHeight = data.length / cols;
        size = mapWidth * mapHeight;

        width = cellW * mapWidth;
        height = cellH * mapHeight;

        quads = Quad.createSet(size);

        float screenMesh[] = new float[16];
        {
            screenMesh[0] = 0;
            screenMesh[1] = mapHeight * cellH;

            screenMesh[2] = 0;
            screenMesh[3] = 1;

            screenMesh[4] = mapWidth * cellW;
            screenMesh[5] = mapHeight * cellH;

            screenMesh[6] = 1;
            screenMesh[7] = 1;

            screenMesh[8] = mapWidth * cellW;
            screenMesh[9] = 0;

            screenMesh[10] = 1;
            screenMesh[11] = 0;

            screenMesh[12] = 0;
            screenMesh[13] = 0;

            screenMesh[14] = 0;
            screenMesh[15] = 0;
        }

        if (cid == 1) {
            ((Buffer)quads).clear();
            quads.put(screenMesh);
        }

        updateMap();
    }

    public Image image(int x, int y) {
        if (!needsRender(x + mapWidth * y)) {
            return null;
        } else {
            Image img = new Image(atlas);
            img.frame(tileset.get(data[x + mapWidth * y]));
            return img;
        }
    }

    //forces a full update, including new buffer
    public synchronized void updateMap() {
        updated.set(0, 0, mapWidth, mapHeight);
        fullUpdate = true;
    }

    public synchronized void updateMapCell(int cell) {
        updated.union(cell % mapWidth, cell / mapWidth);
    }

    private synchronized void moveToUpdating() {
        updating = new Rect(updated);
        updated.setEmpty();
    }

    protected void updateVertices() {
        moveToUpdating();



        int lx = fullUpdate? mapWidth:updating.width();
        int ly = fullUpdate? mapHeight:updating.height();

        int ox = fullUpdate? 0 : updating.left;
        int oy = fullUpdate? 0: updating.top;

        int[] bytes = new int[lx * ly];

        for (int i = 0; i < lx; i++)
            for (int j = 0; j < ly; j++) {
                bytes[j * lx + i] = data[(j+oy) * mapWidth + i+ox] + Random.Int(0x00fffffe)*256;
            }
        if(fullUpdate){
            map.pixels(mapWidth, mapHeight, bytes);
        }
        else {
            map.pixels(ox, oy, lx, ly, bytes);
        }
        fullUpdate = false;
    }

    float time = 0.f;
    @Override
    public void draw() {
        //TODO HACK
        ((NoosaScriptFlex) script()).uTilesetWidth.value1i(tileset.getTexWidth()/16);
        ((NoosaScriptFlex) script()).uTilesetHeight.value1i(tileset.getTexHeight()/16);

        time+=0.01f;
        super.draw();

        //TODO HACK
        ((NoosaScriptFlex) script()).uMapWidth.value1i(mapWidth);
        ((NoosaScriptFlex) script()).uMapHeight.value1i(mapHeight);
        ((NoosaScriptFlex) script()).uTime.value1f(time);


        if (!updated.isEmpty()) {
            updateVertices();
            if (buffer == null)
                buffer = new Vertexbuffer(quads);
            topLeftUpdating = -1;
        }
        NoosaScriptFlex script = (NoosaScriptFlex) script();

        script.setAtlasAndMapTexUnitId(0, 1);

        Texture.activate(0);
        atlas.bind();

        Texture.activate(1);
        map.bind();


        //Texture.activate(1);
        script.uModel.valueM4(matrix);
        script.lighting(
                rm, gm, bm, am,
                ra, ga, ba, aa);

        script.camera(camera);

        script.drawQuadSet(buffer, size, 0);
        Texture.activate(0);
    }

    protected NoosaScript script() {
        return NoosaScriptFlex.get();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (buffer != null)
            buffer.delete();
    }

    protected boolean needsRender(int pos) {
        return data[pos] >= 0;
    }

    //public int getBlankTile
}
