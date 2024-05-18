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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CrimsonFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CrimsonBurning extends Buff implements Hero.Doom {

    //for tracking burning of hero items
    private int burnIncrement = 0;

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        Buff.detach( target, Chill.class);

        return super.attachTo(target);
    }

    @Override
    public boolean act() {

        if (target.isAlive() && !target.isImmune(getClass())) {

            int damage = Random.NormalIntRange( 1, 3 + Dungeon.scalingDepth()/4 );
            Buff.detach( target, Chill.class);
            Buff.detach(target, Burning.class);

            if (target instanceof Hero && target.buff(TimekeepersHourglass.timeStasis.class) == null) {

                Hero hero = (Hero)target;

                hero.damage( damage, this );
                burnIncrement++;

                //at 4+ turns, there is a (turns-3)/3 chance an item burns
                if (Random.Int(3) < (burnIncrement - 3)){
                    burnIncrement = 0;

                    ArrayList<Item> burnable = new ArrayList<>();
                    //does not reach inside of containers
                    if (hero.buff(LostInventory.class) == null) {
                        for (Item i : hero.belongings.backpack.items) {
                            if (!i.unique) {
                                burnable.add(i);
                            }
                        }
                    }

                    if (!burnable.isEmpty()){
                        Item toBurn = Random.element(burnable).detach(hero.belongings.backpack);
                        GLog.w( Messages.capitalize(Messages.get(this, "burnsup", toBurn.title())) );
                        Heap.burnFX( hero.pos );
                    }
                }

            } else {
                target.damage( damage, this );
            }

            if (target instanceof Thief && ((Thief) target).item != null) {

                Item item = ((Thief) target).item;

                if (!item.unique) {
                    target.sprite.emitter().burst( ElmoParticle.FACTORY, 6 );
                    ((Thief)target).item = null;
                }

            }

        } else {

            detach();
        }

        if (Dungeon.level.flamable[target.pos] && Blob.volumeAt(target.pos, CrimsonFire.class) == 0) {
            GameScene.add( Blob.seed( target.pos, 4, CrimsonFire.class ) );
        }

        spend( TICK );
        return true;
    }

    public void reignite( Char ch, float duration ) {
        if (ch.isImmune(CrimsonBurning.class)){
            //TODO this only works for the hero, not others who can have brimstone+arcana effect
            // e.g. prismatic image, shadow clone
            if (ch instanceof Hero
                    && ((Hero) ch).belongings.armor() != null
                    && ((Hero) ch).belongings.armor().hasGlyph(Brimstone.class, ch)){
                //has a 2*boost/50% chance to generate 1 shield per turn, to a max of 4x boost
                float shieldChance = 2*(Armor.Glyph.genericProcChanceMultiplier(ch) - 1f);
                Armor heroArmor = ((Hero) ch).belongings.armor();
                float burnChance = (float) Math.pow(0.9f, heroArmor.level());
                if(Random.Float()<burnChance)
                {
                    ((Hero) ch).belongings.armor = null;
                    GLog.w( Messages.capitalize(Messages.get(this, "explodes", ((Hero) ch).belongings.armor.title())) );
                    new Bomb().explode(ch.pos);
                }
                else
                {
                    int shieldCap = Math.round(shieldChance*4f);
                    if (shieldCap > 0 && Random.Float() < shieldChance){
                        Barrier barrier = Buff.affect(ch, Barrier.class);
                        if (barrier.shielding() < shieldCap){
                            barrier.incShield(1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.CRIMSON_FIRE;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.CRIMSON_BURNING);
        else target.sprite.remove(CharSprite.State.CRIMSON_BURNING);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public void onDeath() {

        Badges.validateDeathFromFire();

        Dungeon.fail( this );
        GLog.n( Messages.get(this, "ondeath") );
    }
}
