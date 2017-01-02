package com.liyao.app.bluetoothcontrolapp.wifi_activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.liyao.app.bluetoothcontrolapp.R;
import com.liyao.app.bluetoothcontrolapp.services.WifiControlService;
import com.liyao.app.bluetoothcontrolapp.vo.WifiImageVO;
import com.liyao.app.bluetoothcontrolapp.wifi_interface.WifiCommunication;

public class Fpv_Activity extends Activity {
    static final String TAG = "@Fpv_Activity@";
    static final int IMG_WIDTH = 640;
    static final int IMG_HEIGHT = 480;

    String IP = "";
    int PORT = 0;

    float image_scale = -1;//图片缩放比例
    float image_offset = -1;
    SurfaceView sv_image = null;
    private SurfaceHolder sfh;   //surfaceView的 控制器
    WifiControlService wifiControlService = null;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            wifiControlService = ((WifiControlService.MBinder)service).getService(new FlushImage());
            wifiControlService.IP = IP;
            wifiControlService.PORT = PORT;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fpv_activity);
        //获取ip和port参数
        Intent intent = getIntent();
        IP = intent.getStringExtra("IP");
        PORT = intent.getIntExtra("PORT",0);
        Toast.makeText(this, "connect:"+IP+":"+PORT,Toast.LENGTH_SHORT).show();

        sv_image = (SurfaceView)findViewById(R.id.sv_image);
        sfh = sv_image.getHolder();

        //绑定service
        intent = new Intent(this, WifiControlService.class);
        bindService(intent, conn, Service.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    public void huitu_onClick(View v){
        wifiControlService.call();

        //1.这里就是核心了， 得到画布 ，然后在你的画布上画出要显示的内容
        Canvas c = sfh.lockCanvas(new Rect(0, 0, 200, 200));
        //2.开画
        Paint  p =new Paint();
        p.setColor(Color.rgb( (int)(Math.random() * 255),
                (int)(Math.random() * 255) ,  (int)(Math.random() * 255)));
        Rect aa  =  new Rect( (int)(Math.random() * 100) ,
                (int)(Math.random() * 100)
                ,(int)(Math.random() * 500)
                ,(int)(Math.random() * 500) );
        c.drawRect(aa, p);
        //3. 解锁画布   更新提交屏幕显示内容
        sfh.unlockCanvasAndPost(c);
    }
    void calculateImageScale()
    {
        //计算图片比例
        int sv_image_width = sv_image.getWidth();
        int sv_image_height = sv_image.getHeight();
        Log.i(TAG,"w:"+ sv_image_width+"  h:" + sv_image_height);
        float width_scale = sv_image_width * 1.0f / IMG_WIDTH;
        float height_scale = sv_image_height * 1.0f / IMG_HEIGHT;
        if(width_scale < height_scale){
            image_scale = width_scale;
        }else{
            image_scale = height_scale;
        }
        image_offset = (sv_image_width - (IMG_WIDTH * image_scale))/2;
    }

    int dir = 0;
    public class FlushImage implements WifiCommunication{
        @Override
        public void receiveCallback(WifiImageVO vo) {
            byte[] data = vo.getStream().toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.i(TAG,"receiveCallback:"+(dir++)+"\tlen:"+ data.length);
            Canvas c = null;
            try {
                if(bitmap == null)
                    return;

                if(image_scale == -1)
                    calculateImageScale();
                c = sfh.lockCanvas( );
                c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                Paint paint = new Paint();
                Matrix matrix = new Matrix();
                matrix.postScale(image_scale , image_scale);
                matrix.postTranslate(image_offset,0);
                c.drawBitmap(bitmap, matrix, paint);
                sfh.unlockCanvasAndPost(c);
            }catch(Exception ex){
                Log.e(TAG, ex.getMessage());
            }
            //Log.i(TAG,"receiveCallbackEND");
//            canvas.drawBitmap();
            //YuvImage image = new YuvImage(vo.getStream().toByteArray(), ImageFormat.JPEG, 640, 480, null);
        }
    }
}
