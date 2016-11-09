package com.liyao.app.myapplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liyao on 2016/5/23.
 */
public class VirtualJoystick extends View {
    public static final int CONTROL_WIDTH = 377;
    public static final int CONTROL_HEIGHT = 377;
    public static final float OFFSETX = CONTROL_WIDTH/2;//原点坐标
    public static final float OFFSETY = CONTROL_HEIGHT/2;//原点坐标
    float currentX = 0;
    float currentY = 0;
    public static final int DIAMETER = 25;
    //必须重写一个构造方法
    public VirtualJoystick(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public VirtualJoystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentX = OFFSETX;
        currentY = OFFSETY;
    }

    public void setLocation(float x, float y){
        currentX = x;
        currentY = y;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean flag = true;

        float posX = currentX - OFFSETX;    //相对坐标
        float posY = currentY - OFFSETY;    //相对坐标
        double xiebian = Math.sqrt(Math.abs(Math.pow(posX, 2)) + Math.abs(Math.pow(posY, 2)));
        if (xiebian > CONTROL_WIDTH/2-DIAMETER/2) {//勾股定理
            double y = posY/xiebian * (CONTROL_WIDTH/2-DIAMETER/2);
            double x = y/(posY/posX);
            currentY = (float) (y+OFFSETY);
            currentX = (float)(x+OFFSETX);

        }
        /*do{//用于响应边界之外的移动并且保持移动球在框内
            float posX = currentX - OFFSETX;    //相对坐标
            float posY = currentY - OFFSETY;    //相对坐标
            if (Math.sqrt(Math.abs(Math.pow(posX, 2)) + Math.abs(Math.pow(posY, 2)))
                    > CONTROL_WIDTH/2-DIAMETER/2) {//勾股定理
                if(posX > 0) {
                    currentX--;
                }else if(posX <0) {
                    currentX++;
                }
                if(posY > 0) {
                    currentY--;
                }else if(posY <0){
                    currentY++;
                }
            }else{
                flag = false;
            }
        }while(flag);*/
        //创建画笔
        Paint paint=new Paint();
        InputStream is = getResources().openRawResource(R.raw.arrow_bg);
        Bitmap mBitmap = BitmapFactory.decodeStream(is);
        //绘制背景图片
        canvas.drawBitmap(mBitmap, DIAMETER/2, DIAMETER/2, paint);
        //绘制一个小圆
        paint.setColor(Color.RED);
        canvas.drawCircle(currentX, currentY, DIAMETER, paint);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
