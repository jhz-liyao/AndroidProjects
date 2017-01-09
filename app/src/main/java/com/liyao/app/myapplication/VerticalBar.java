package com.liyao.app.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.InputStream;

/**
 * Created by liyao on 2017/1/8.
 */

public class VerticalBar extends View {
    private static final String  TAG = "VerticalBar";
    private static final int BG_IMAGE = 175;
    private static final int BTN_IMAGE = 36;
    private Paint p;
    private Resources rec;
    private InputStream in;
    private Bitmap bitmap_bg;
    private Bitmap bitmap_btn;
    Context context_;

    int max = 0;
    float curLocation;
    float step;
    int width;
    int height;
    float btn_Scale;
    float btn_Offset;
    public VerticalBar(Context context) {
        super(context);
        ControlInit();
    }

    public VerticalBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalBar);
        max = typedArray.getInt(R.styleable.VerticalBar_max, 100);
        ControlInit();
        context_ = context;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        float y = event.getY();
                        if(y > height) y = height;
                       else if(y < 0) y = 0;
                        curLocation = y;
                        Log.i(">>>>>>>>>>","return " + (int)((height - curLocation - height / 2) * step ));
                        break;
                    case MotionEvent.ACTION_UP:
                        curLocation = height / 2;//回到原点
                        break;
                }
                invalidate();
                return true;
            }
        });
    }

    public VerticalBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ControlInit();
    }



    public void ControlInit(){
        rec = getResources();
        in = rec.openRawResource(R.raw.verticalbar_bg);
        bitmap_bg = BitmapFactory.decodeStream(in);
        in = rec.openRawResource(R.raw.verticalbar_btn);
        bitmap_btn = BitmapFactory.decodeStream(in);
        p = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect src = new Rect();
        src.left = 0;
        src.right = width;
        src.top = 0;
        src.bottom = height;
        canvas.drawBitmap(bitmap_bg, null, src, p);
        canvas.save();

        canvas.scale(btn_Scale, btn_Scale);
        canvas.translate(btn_Offset, btn_Offset);//移动中点
        canvas.drawBitmap(bitmap_btn, width / 2 / btn_Scale, curLocation / btn_Scale, p);
        canvas.restore();
        //invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        step = max * 2.0f / height;
        btn_Scale = width / BG_IMAGE;
        btn_Offset = -(bitmap_btn.getWidth() / 2);
        curLocation = height / 2;
    }

    public void setProgress(int i){
        curLocation = height - (i / step + height / 2);
        invalidate();
    }

    public int getProgress(){
        return (int)((height - curLocation - height / 2)/step);
    }
}
