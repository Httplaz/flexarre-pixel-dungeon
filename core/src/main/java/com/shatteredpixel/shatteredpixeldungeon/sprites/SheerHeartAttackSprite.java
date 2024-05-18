package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class SheerHeartAttackSprite extends MobSprite {

    public SheerHeartAttackSprite() {
        super();

        texture(Assets.Sprites.SHEER_HEART_ATTACK);

        TextureFilm frames = new TextureFilm(texture, 16, 16);

        idle = new MovieClip.Animation(1, true);
        idle.frames(frames, 0);

        run = new MovieClip.Animation(10, true);
        run.frames(frames, 0, 2);

        attack = new MovieClip.Animation(10, false);
        attack.frames(frames, 3, 4, 5, 6);

        die = new MovieClip.Animation(1, false);
        die.frames(frames, 0);

        play(idle);
    }

    @Override
    public void draw() {
        Blending.setNormalMode();
        super.draw();
        Blending.setNormalMode();
    }

    @Override
    public void die() {
        super.die();
        emitter().start(ShaftParticle.FACTORY, 0.3f, 4);
        emitter().start(Speck.factory(Speck.INFERNO), 0.2f, 3);
    }

    @Override
    public int blood() {
        return 0xFFFFFF;
    }
}

