package com.liyao.app.phonebodyguard.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.liyao.app.phonebodyguard.MainActivity;

/**
 * Created by liyao on 2016/5/17.
 */
public class SmsContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SMS_QUERY = 0;
    static {
        uriMatcher.addURI("phonebodyguard.contentprovider.SmsContentProvider","query",SMS_QUERY);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(SMS_QUERY == uriMatcher.match(uri)){
            //Log.v("$$$$", "query被调用");
            Uri _uri = Uri.parse("content://sms/");
            return getContext().getContentResolver().query(_uri, new String[]{"_id", "address", "body", "date", "type"}, null, null, null);
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
