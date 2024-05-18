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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.flexarre;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SheerHeartAttackSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class KillerQueen extends Artifact {

    public static final String AC_SUMMON = "SUMMON";
    private int standId = -1;

    {
        image = ItemSpriteSheet.ARTIFACT_ROSE1;

        levelCap = 10;

        charge = 100;
        chargeCap = 100;

        defaultAction = AC_SUMMON;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (!Ghost.Quest.completed()) {
            return actions;
        }
        if (isEquipped(hero)) {
            actions.add(AC_SUMMON);
        }

        return actions;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new chaliceRegen();
    }

    @Override
    public String defaultAction() {
        return AC_SUMMON;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SUMMON)) {

            ArrayList<Integer> spawnPoints = new ArrayList<>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                    spawnPoints.add(p);
                }
            }

            if (spawnPoints.size() > 0) {
                SheerHeartAttack stand = new SheerHeartAttack();
                standId = stand.id();
                stand.pos = Random.element(spawnPoints);

                GameScene.add(stand, 1f);
                Dungeon.level.occupyCell(stand);


                CellEmitter.get(stand.pos).start(ShaftParticle.FACTORY, 0.3f, 4);
                CellEmitter.get(stand.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

                hero.spend(1f);
                hero.busy();
                hero.sprite.operate(hero.pos);

                Invisibility.dispel(hero);
                Talent.onArtifactUsed(hero);
                charge = 0;
                partialCharge = 0;
                updateQuickslot();

            } else
                GLog.i(Messages.get(this, "no_space"));

        }
    }

    @Override
    public String desc() {
        return "1111";
    }

    @Override
    public int value() {
        return super.value();
    }

    @Override
    public String status() {
        return super.status();
    }

    @Override
    public void charge(Hero target, float amount) {

    }

    @Override
    public Item upgrade() {
        return super.upgrade();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
    }

    public static class SheerHeartAttack extends NPC {

        private int yellCounter = 5;

        {
            spriteClass = SheerHeartAttackSprite.class;

            state = WANDERING;
            alignment = Alignment.ALLY;
            properties.add(Property.INORGANIC);
            HP = 9000;
            HT = 9000;
            defenseSkill = 9000;
        }

        {
            immunities.add(CorrosiveGas.class);
            immunities.add(Burning.class);
            immunities.add(ScrollOfRetribution.class);
            immunities.add(ScrollOfPsionicBlast.class);
            immunities.add(AllyBuff.class);
        }

        public SheerHeartAttack() {
            super();
        }

        @Override
        protected boolean act() {
            yellCounter--;
            state = WANDERING;
            super.act();
            Char enemy = chooseEnemy();
            if (enemy != null) {
                Point a = Dungeon.level.cellToPoint(pos);
                Point b = Dungeon.level.cellToPoint(enemy.pos);
                if (new Point(a.x - b.x, a.y - b.y).length() <= 2) {
                    explode(pos);
                    sprite.attack(pos);
                } else {
                    if (yellCounter > 0) {
                    } else {
                        yellCounter = 5;
                        CellEmitter.center(pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3);
                        Sample.INSTANCE.play(Assets.Sounds.ALERT);

                        yell("OVER HERE");

                        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                            mob.beckon(pos);
                            mob.aggro(this);
                        }
                    }
                }
                spend(TICK);
            }
            return true;
        }

        private void explode(int pos) {
            CellEmitter.center(pos).burst(BlastParticle.FACTORY, 60);
            new Bomb().explode(pos);
        }

    }

    public class chaliceRegen extends ArtifactBuff {
        //see Regeneration.class for effect
    }
}
