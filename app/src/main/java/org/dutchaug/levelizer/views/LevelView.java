package org.dutchaug.levelizer.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import org.dutchaug.levelizer.R;


public class LevelView extends View {

    private Paint mPaintGood, mPaintBad, mPaintBadThin;
    private Paint mPaintGoodBg, mPaintBadBg;

    private float mOrientation, mTolerance;

    public LevelView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public LevelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LevelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mPaintGood = new Paint();
        mPaintGood.setAntiAlias(true);
        mPaintGood.setColor(ContextCompat.getColor(context, R.color.indicator_good));
        mPaintGood.setStrokeWidth(8);
        mPaintGood.setStrokeCap(Paint.Cap.ROUND);
        mPaintGoodBg = new Paint(mPaintGood);
        mPaintGoodBg.setColor(ContextCompat.getColor(context, R.color.indicator_outline));
        mPaintGoodBg.setStrokeWidth(16);

        mPaintBad = new Paint(mPaintGood);
        mPaintBad.setColor(ContextCompat.getColor(context, R.color.indicator_bad));
        mPaintBad.setStrokeWidth(20);
        mPaintBadThin = new Paint(mPaintBad);
        mPaintBadThin.setStrokeWidth(4);
        mPaintBadBg = new Paint(mPaintGoodBg);
        mPaintBadBg.setStrokeWidth(28);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowView, defStyleAttr, defStyleRes);
        {
            // Read attributes
            mOrientation = a.getFloat(R.styleable.ArrowView_orientation, 0);
            mTolerance = a.getFloat(R.styleable.ArrowView_tolerance, 5);
        }
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int width = getWidth();
        int height = getHeight();
        canvas.rotate(mOrientation, width * .5f, height * .5f);
        Paint paint = mPaintGood, paintBg = mPaintGoodBg;
        if (mOrientation > mTolerance) {
            paint = mPaintBad;
            paintBg = mPaintBadBg;
            canvas.drawLine(width * .2f, height * .5f, width * .8f, height * .5f, mPaintBadThin);
        }
        canvas.drawLine(width * .1f, height * .5f, width * .2f, height * .5f, paintBg);
        canvas.drawLine(width * .1f, height * .5f, width * .2f, height * .5f, paint);
        canvas.drawLine(width * .8f, height * .5f, width * .9f, height * .5f, paintBg);
        canvas.drawLine(width * .8f, height * .5f, width * .9f, height * .5f, paint);
        canvas.restore();
    }

    @SuppressWarnings("unused")
    public void setOrientation(float orientation) {
        mOrientation = orientation;
        invalidate();
    }

    @SuppressWarnings("unused")
    public void setTolerance(float tolerance) {
        mTolerance = tolerance;
        invalidate();
    }

}
