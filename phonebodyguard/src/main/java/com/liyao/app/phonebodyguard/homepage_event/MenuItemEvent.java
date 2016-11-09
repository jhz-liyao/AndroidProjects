package com.liyao.app.phonebodyguard.homepage_event;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.liyao.app.phonebodyguard.R;
import com.liyao.app.phonebodyguard.contentprovider.SmsOperation;


/**
 * Created by liyao on 2016/5/16.
 */
public class MenuItemEvent implements View.OnTouchListener {
    private Activity activity;
    private SmsOperation smsOperation;
    public MenuItemEvent(Activity _activity){
        activity = _activity;
        smsOperation = new SmsOperation(activity);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        RelativeLayout rl = (RelativeLayout)v;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                rl.setBackgroundColor(Color.parseColor("#DDDDDD"));
                break;

            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.homepage_rl_menu_left_1 : smsOperation.left_1_onClick(rl); break;
                    case R.id.homepage_rl_menu_right_1: smsOperation.right_1_onClick(rl);break;
//                    case R.id.homepage_rl_menu_left_2 : left_2_onClick(rl); break;
                    case R.id.homepage_rl_menu_right_2: callSelf_onClick(rl);break;
//                    case R.id.homepage_rl_menu_left_3 : left_3_onClick(rl); break;
//                    case R.id.homepage_rl_menu_right_3: right_3_onClick(rl);break;
                }
                Toast.makeText(activity, "被点击", Toast.LENGTH_SHORT).show();
            case MotionEvent.ACTION_OUTSIDE:
                rl.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
        }
        return true;
    }

    public void callSelf_onClick(RelativeLayout rl){
        Uri uri = Uri.parse("tel:18610346164");
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);//拨号界面
        //Intent intent = new Intent(Intent.ACTION_CALL, uri);//直接拨打
        activity.startActivity(intent);
    }
}
