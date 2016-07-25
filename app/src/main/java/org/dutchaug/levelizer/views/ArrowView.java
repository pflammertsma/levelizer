package org.dutchaug.levelizer.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import org.dutchaug.levelizer.R;


public class ArrowView extends View {

    private Path mPath;
    private RectF mArc;
    private Paint mPaintPath, mPaintArc;

    private float mOrientation;

    public ArrowView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mPath = new Path();
        mPath.moveTo(0, -40);
        mPath.lineTo(50, 40);
        mPath.lineTo(-50, 40);
        mPath.close();

        mArc = new RectF(0, 0, 100, 100);

        mPaintPath = new Paint();
        mPaintPath.setAntiAlias(true);
        mPaintPath.setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        mPaintArc = new Paint(mPaintPath);
        mPaintArc.setStrokeWidth(20);
        mPaintArc.setStyle(Paint.Style.STROKE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowView, defStyleAttr, defStyleRes);
        {
            // Read attributes
            mOrientation = a.getFloat(R.styleable.ArrowView_orientation, 0);
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mPath.reset();
        mPath.moveTo(width * 0.5f, 0);
        mPath.lineTo(width, height * 0.5f);
        mPath.lineTo(width * 0.6f, height * 0.5f);
        mPath.lineTo(width * 0.6f, height);
        mPath.lineTo(width * 0.4f, height);
        mPath.lineTo(width * 0.4f, height * 0.5f);
        mPath.lineTo(0, height * 0.5f);
        mPath.close();
        */
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int width = getWidth();
        int height = getHeight();
        canvas.translate(100, height / 2);
        double radians = Math.toRadians(mOrientation);
        int x = width / 2 - 100;
        int y = height / 2 - 100;
        float size = Math.min(x, y) * (float) Math.sin(radians);
        canvas.translate(size, 0);
        mArc.top = -size;
        mArc.left = -size;
        mArc.bottom = size;
        mArc.right = size;
        canvas.drawArc(mArc, 180 - mOrientation, mOrientation * 2, false, mPaintArc);
        canvas.translate(0, -size);
        canvas.rotate(mOrientation);
        canvas.drawPath(mPath, mPaintPath);
        canvas.restore();
    }

}
