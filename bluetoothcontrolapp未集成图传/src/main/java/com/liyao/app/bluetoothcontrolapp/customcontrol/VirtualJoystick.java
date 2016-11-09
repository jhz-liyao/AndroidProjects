package com.liyao.app.bluetoothcontrolapp.customcontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.liyao.app.bluetoothcontrolapp.R;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liyao on 2016/5/23.
 */
public class VirtualJoystick extends View {

    public static final int DIAMETER = 40;      //摇杆红点直径
    public static final int CONTROL_WIDTH = 420;//控件宽
    public static final int CONTROL_HEIGHT = 420;//控件高
    public static final int BG_WIDTH = 352;     //背景宽
    public static final int BG_HEIGHT = 352;    //背景高
    public static final int ADSORB_ANGLE = 10;   //吸附角度
    public static final float RADIUS = BG_WIDTH/2;    //半径

    public JoystickInfoVO joystickInfoVO = new JoystickInfoVO();
    public static final float OFFSETX = CONTROL_WIDTH/2;//原点坐标
    public static final float OFFSETY = CONTROL_HEIGHT/2;//原点坐标
    float currentX = 0;
    float currentY = 0;
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
        float posX = currentX - OFFSETX;    //相对坐标
        float posY = OFFSETY - currentY;    //相对坐标
        double angle = 0;
        double hypotenuse = Math.sqrt(Math.abs(Math.pow(posX, 2)) + Math.abs(Math.pow(posY, 2)));//深度


        if (hypotenuse > RADIUS) {//比例求长
            double y = posY/hypotenuse * (RADIUS);//得到边缘x轴坐标
            double x = y/(posY/posX);//得到边缘Y轴坐标
            currentX = (float)(x+OFFSETX);//设置相对坐标转换为绝对坐标
            currentY = (float)(OFFSETY-y);//设置相对坐标转换为绝对坐标
        }

        posX = currentX - OFFSETX;    //相对坐标
        posY = OFFSETY - currentY;    //相对坐标
        angle = (Math.atan2(posX,posY)*180/Math.PI);
        //吸附策略
        if(hypotenuse > (RADIUS*0.3)) {//如果深度超过半径的30%
            if (angle > 0 - ADSORB_ANGLE && angle < 0 + ADSORB_ANGLE){
                angle = 0;
                currentX = OFFSETX;
            }else if(angle > 90-ADSORB_ANGLE && angle < 90 + ADSORB_ANGLE) {
                angle = 90;
                currentY = OFFSETY;
            }else if(angle > 180-ADSORB_ANGLE || angle < -180 + ADSORB_ANGLE) {
                angle = -180;
                currentX = OFFSETX;
            }else if(angle > -90 -ADSORB_ANGLE && angle < -90 + ADSORB_ANGLE) {
                angle = -90;
                currentY = OFFSETY;
            }

        }

        joystickInfoVO.setDeepness(hypotenuse > (BG_WIDTH/2)?(BG_WIDTH/2):hypotenuse);//深度不超过半径
        joystickInfoVO.setAngle(angle);
        joystickInfoVO.setPosX((int)posX);
        joystickInfoVO.setPosY((int)posY);

        //创建画笔
        Paint paint=new Paint();
        InputStream is = getResources().openRawResource(R.raw.arrow_bg);
        Bitmap mBitmap = BitmapFactory.decodeStream(is);
        //绘制背景图片
        canvas.drawBitmap(mBitmap,(CONTROL_WIDTH - BG_WIDTH)/2 ,(CONTROL_HEIGHT - BG_HEIGHT)/2 , paint);
        //绘制一个小圆
        paint.setColor(Color.RED);
        canvas.drawCircle(currentX, currentY, DIAMETER, paint);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class JoystickInfoVO{
        private double deepness;
        private double angle;
        private int posX;
        private int posY;

        public int getPosX() {
            return posX;
        }

        public void setPosX(int posX) {
            this.posX = posX;
        }
        public int getPosY() {
            return posY;
        }

        public void setPosY(int posY) {
            this.posY = posY;
        }
        public double getDeepness() {
            return deepness;
        }

        public void setDeepness(double deepness) {
            this.deepness = deepness;
        }

        public double getAngle() {
            return angle;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }
    }
}
