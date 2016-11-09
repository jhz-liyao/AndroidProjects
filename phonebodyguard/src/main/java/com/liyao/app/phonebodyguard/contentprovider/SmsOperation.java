package com.liyao.app.phonebodyguard.contentprovider;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liyao.app.phonebodyguard.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liyao on 2016/5/17.
 */
public class SmsOperation {
    String filePath = "";
    private Activity activity;
    public SmsOperation(Activity _activity){
        activity = _activity;
        filePath = Environment.getExternalStorageDirectory().getPath() + "/sms_bak.xml";
    }
    public void right_1_onClick(RelativeLayout v){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
        Uri uri = Uri.parse("content://phonebodyguard.contentprovider.SmsContentProvider/query");
        Cursor cursor = activity.getContentResolver().query(uri,null,null,null,null);
        if(cursor != null && cursor.getCount()>0){
//            String filePath = activity.getFilesDir().getPath()+"/sms_bak.xml";
            XmlSerializer xmlSerializer = Xml.newSerializer();
            try {
                xmlSerializer.setOutput(new FileOutputStream(new File(filePath)),"UTF-8");
                xmlSerializer.startDocument("UTF-8", true);
                xmlSerializer.startTag(null,"smss");

                xmlSerializer.startTag(null,"info").attribute(null, "count", String.valueOf(cursor.getCount()));
                xmlSerializer.endTag(null,"info");
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(cursor.moveToNext())
            {
                try {
                    xmlSerializer.startTag(null, "sms")
                            .attribute(null,"address",cursor.getString(1))
                            .attribute(null,"date",cursor.getString(3))
                            .attribute(null,"type",cursor.getString(4));
                    xmlSerializer.text(cursor.getString(2));
                    xmlSerializer.endTag(null, "sms");

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            try {
                xmlSerializer.endTag(null, "smss");
                xmlSerializer.endDocument();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        flushSmsBakCount();
        Log.v("$$$$", "right_1_onClick");
    }
    public void flushSmsBakCount(){
        TextView v = ((TextView)activity.findViewById(R.id.tv_right_detail1));
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(new FileInputStream(new File(filePath)),"UTF-8");
            int type = xmlPullParser.getEventType();
            if(type == XmlPullParser.START_DOCUMENT){
                while(xmlPullParser.nextTag() != XmlPullParser.END_DOCUMENT){
                    if("info".equals(xmlPullParser.getName())){
                        v.setHint("目前备份：" + xmlPullParser.getAttributeValue(0));
                        return;
                    }
                }
            }
        } catch (Exception e) {
            //无此文件则说明没有备份
        }
        v.setHint("目前无备份");
    }

    public void left_1_onClick(RelativeLayout v){
        File file = new File(filePath);
        file.delete();
        flushSmsBakCount();
    }
}
