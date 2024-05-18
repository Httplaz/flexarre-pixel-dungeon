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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;

public class ExplosionReady extends Buff implements ActionIndicator.Action {

    public static final float DURATION = 20f;

    public int pos;

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    public ExplosionReady() {
        super();
        ActionIndicator.setAction(this);
    }


    public void hit(Char enemy) {
        if (enemy.buff(WalkingBomb.class) == null) {
            Buff.affect(enemy, WalkingBomb.class);
            pos = enemy.pos;
        }
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
    }

    @Override
    public int icon() {
        return BuffIndicator.FIRE;
    }

    @Override
    public float iconFadePercent() {
        return 1.f;
    }

    @Override
    public String iconTextDisplay() {
        return "";
    }

    @Override
    public String desc() {
        return "Killer queen is ready to explode it's bobm.";
    }

    @Override
    public boolean act() {
        spend(TICK);
        return true;
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.ELEMENTAL_BLAST;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        //txt.text(Integer.toString(victims.size()));
        txt.hardlight(CharSprite.POSITIVE);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        return 0xDFDFDF;
    }

    @Override
    public void doAction() {

        Char mob = Dungeon.level.findMob(pos);
        if (mob != null)
            if (mob.buff(WalkingBomb.class) != null) {
                if (mob.isAlive()) {
                    mob.die(ExplosionReady.class);
                    mob.sprite.killAndErase();
                    explode(pos);
                    return;
                }
            }

        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap != null)
            for (Item item : heap.items) {
                if (item.bomb) {
                    explode(pos);
                    return;
                }
            }

        for (Heap heap1 : Dungeon.level.heaps.values())
            if (heap1 != null)
                for (Item item : heap1.items) {
                    if (item.bomb) {
                        explode(heap1.pos);
                        return;
                    }
                }


        //search in hero items
        for (Item item : Dungeon.hero.belongings) {
            if (item.bomb) {
                item.detach(Dungeon.hero.belongings.backpack);
                if (item == Dungeon.hero.belongings.armor)
                    Dungeon.hero.belongings.armor = null;
                if (item == Dungeon.hero.belongings.weapon)
                    Dungeon.hero.belongings.weapon = null;
                if (item == Dungeon.hero.belongings.artifact)
                    Dungeon.hero.belongings.artifact = null;
                if (item == Dungeon.hero.belongings.misc)
                    Dungeon.hero.belongings.misc = null;
                if (item == Dungeon.hero.belongings.ring)
                    Dungeon.hero.belongings.ring = null;
                explode(Dungeon.hero.pos);
                return;
            }
        }

        if (target.buff(WalkingBomb.class) != null) {
            Dungeon.hero.belongings.backpack.clear();
            Dungeon.hero.damage(99999, WalkingBomb.class);
            explode(target.pos);
        }


    }

    public void explode(int pos) {
        CellEmitter.center(pos).burst(BlastParticle.FACTORY, 60);
        new Bomb().explode(pos);
        detach();
    }
}
