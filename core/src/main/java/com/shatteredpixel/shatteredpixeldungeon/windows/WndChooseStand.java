package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroStand;
import com.shatteredpixel.shatteredpixeldungeon.items.StandArrow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;

public class WndChooseStand extends Window {

    private static final int WIDTH		= 130;
    private static final float GAP		= 2;

    public WndChooseStand(final StandArrow arrow, final Hero hero ) {

        super();

        IconTitle titlebar = new IconTitle();
        titlebar.icon( new ItemSprite( arrow.image(), null ) );
        titlebar.label( arrow.name() );
        titlebar.setRect( 0, 0, WIDTH, 0 );
        add( titlebar );

        RenderedTextBlock message = PixelScene.renderTextBlock( 6 );
        message.text( Messages.get(this, "message"), WIDTH );
        message.setPos( titlebar.left(), titlebar.bottom() + GAP );
        add( message );

        float pos = message.bottom() + 3*GAP;

        for (HeroStand stand : HeroStand.values()){
            if(stand == HeroStand.NONE)
                continue;
            RedButton standBtn = new RedButton( stand.shortDesc(), 6 ) {
                @Override
                protected void onClick() {
                    GameScene.show(new WndOptions(
                            Messages.titleCase(stand.title()),
                            Messages.get(WndChooseStand.this, "are_you_sure"),
                            Messages.get(WndChooseStand.this, "yes"),
                            Messages.get(WndChooseStand.this, "no")){
                        @Override
                        protected void onSelect(int index) {
                            hide();
                            if (index == 0 && WndChooseStand.this.parent != null){
                                WndChooseStand.this.hide();
                                arrow.choose( stand );
                            }
                        }
                    });
                }
            };
            standBtn.leftJustify = true;
            standBtn.multiline = true;
            standBtn.setSize(WIDTH-20, standBtn.reqHeight()+2);
            standBtn.setRect( 0, pos, WIDTH-20, standBtn.reqHeight()+2);
            add( standBtn );

            IconButton clsInfo = new IconButton(Icons.get(Icons.INFO)){
                @Override
                protected void onClick() {
                    GameScene.show(new WndInfoStand(stand));
                }
            };
            clsInfo.setRect(WIDTH-20, standBtn.top() + (standBtn.height()-20)/2, 20, 20);
            add(clsInfo);

            pos = standBtn.bottom() + GAP;
        }

        RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
            @Override
            protected void onClick() {
                hide();
            }
        };
        btnCancel.setRect( 0, pos, WIDTH, 18 );
        add( btnCancel );

        resize( WIDTH, (int)btnCancel.bottom() );
    }
}

