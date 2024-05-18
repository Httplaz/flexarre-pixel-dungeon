package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroStand;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseStand;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

import java.util.ArrayList;

public class StandArrow extends Item {

    private static final String AC_STAB = "STAB";

    {
        stackable = false;
        image = ItemSpriteSheet.ADRENALINE_DART;

        defaultAction = AC_STAB;

        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( AC_STAB );
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals( AC_STAB )) {

            curUser = hero;

            GameScene.show( new WndChooseStand( this, hero ) );

        }
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        //Badges.validateMastery();
        return super.doPickUp( hero, pos );
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public void choose( HeroStand stand ) {

        //TODO detach later
        //detach( curUser.belongings.backpack );

        curUser.spend( Actor.TICK );
        curUser.busy();

        curUser.stand = stand;

        curUser.sprite.operate( curUser.pos );
        Sample.INSTANCE.play( Assets.Sounds.MASTERY );

        Emitter e = curUser.sprite.centerEmitter();
        e.pos(e.x-2, e.y-6, 4, 4);
        e.start(Speck.factory(Speck.MASK), 0.05f, 20);
        GLog.p( Messages.get(this, "used"));

    }
}
