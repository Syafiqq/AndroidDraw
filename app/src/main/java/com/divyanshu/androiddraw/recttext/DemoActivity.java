package com.divyanshu.androiddraw.recttext;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class DemoActivity
        extends Activity
        implements SurfaceHolder.Callback
{
    /** Activity is created */
    @Override
    public void onCreate( Bundle state )
    {
        super.onCreate( state );

        SurfaceView v = new SurfaceView( this );
        SurfaceHolder h = v.getHolder();
        h.addCallback( this );

        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( v );
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
    }

    /** Surface changed */
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height )
    {
    }

    /** Surface created */
    public void surfaceCreated( SurfaceHolder holder )
    {
        if( holder == null )
            return;

        Canvas c = null;

        try
        {
            if( (c = holder.lockCanvas()) != null )
                drawBubbles( c );
        }
        finally
        {
            if( c != null )
                holder.unlockCanvasAndPost( c );
        }
    }

    /** Surface destroyed */
    public void surfaceDestroyed( SurfaceHolder holder )
    {
    }

    /** Draw bubbles */
    private void drawBubbles( final Canvas c )
    {
        final String texts[] = new String[]{
                "And now for something compeletly different. According to en.wikipedia.org, the origin of this phrase \" is credited to Christopher Trace, founding presenter of the children's television programme Blue Peter, who used it (in all seriousness) as a link between segments\". Interesting, isn't it?",};
        final int cells = (int) Math.ceil( Math.sqrt( texts.length ) );
        final int margin = 8;
        final int totalMargin = (cells + 1) * margin;
        final int w = (c.getWidth() - totalMargin) / cells;
        final int h = (c.getHeight() - totalMargin) / cells;

        for( int n = 0, l = texts.length, x = margin, y = margin;
             n < l; )
        {
            drawBubble(
                    c,
                    x,
                    y,
                    w,
                    h,
                    texts[n] );

            if( ++n % cells == 0 )
            {
                x = margin;
                y += h + margin;
            }
            else
                x += w + margin;
        }
    }

    /** Draw a text bubble */
    private void drawBubble(
            final Canvas c,
            final int x,
            final int y,
            final int width,
            final int height,
            final String text )
    {
        final TextRect textRect;

        // set up font
        {
            final Paint fontPaint = new Paint();
            fontPaint.setColor( Color.WHITE );
            fontPaint.setAntiAlias( true );
            fontPaint.setTextSize( 24 );

            textRect = new TextRect( fontPaint );
        }

        final int h = textRect.prepare(
                text,
                width - 8,
                height - 8 );

        // draw bubble
        {
            final Paint p = new Paint();
            p.setColor( Color.BLUE );
            p.setStyle( Paint.Style.FILL );
            p.setAntiAlias( true );

            c.drawRoundRect(
                    new RectF( x, y, x + width, y + h + 8 ),
                    4,
                    4,
                    p );
        }

        textRect.draw( c, x + 4, y + 4 );
    }
}