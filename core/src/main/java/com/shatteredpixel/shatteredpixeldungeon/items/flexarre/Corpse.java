package com.shatteredpixel.shatteredpixeldungeon.items.flexarre;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.watabou.utils.Bundle;
import com.watabou.utils.RectF;

public class Corpse extends Food {

    private static final String FRAME = "frame";
    private static final String TEXTURE = "texture";

    public RectF frame;
    public String texturePath = Assets.Sprites.ITEMS;

    {
        unique = true;
        stackable = false;
        image = -1; //we can not use sprite index, only frame
        energy = Hunger.HUNGRY / 3f; //100 food value

        bones = true;
    }

    //only for loading instantiation
    public Corpse() {
    }

    public Corpse(Char ch) {
        texturePath = ch.sprite.texturePath;
        frame = ch.sprite.getDeathFrame();
        String b = name();
        String c = trueName();
        String d;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FRAME, frame);
        bundle.put(TEXTURE, texturePath);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        frame = new RectF();
        frame.restoreFromBundle(bundle.getBundle(FRAME));
        texturePath = bundle.getString(TEXTURE);
    }

    @Override
    protected float eatingTime() {
        if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH)
                || Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL)
                || Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL)
                || Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL)
                || Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL)) {
            return 5;
        } else {
            return 10;
        }
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        effect(hero);
    }

    void effect(Hero hero) {
        Buff.affect(hero, Poison.class).set(3.f);
        Buff.affect(hero, Slow.class, Slow.DURATION + eatingTime());
        Buff.affect(hero, Weakness.class, Weakness.DURATION + eatingTime());
    }

    @Override
    public int value() {
        return -50 * quantity;
    }

}
