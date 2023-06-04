package project.babysitting.babysiting.configs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

public class Permissions {
    public  void storagePermisionsForLessThanAndroid11(Activity activity,int code){
        ActivityCompat.requestPermissions(activity,new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},code);
    }
    public  void storagePermisionsForGreaterThanAndroid11(Context context,int code){
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s",context.getApplicationContext().getPackageName())));
            ((Activity) context).startActivityForResult(intent, code);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            ((Activity) context).startActivityForResult(intent, code);
        }
    }
    public void notificationPermisionsForAndroid11(Activity activity,int code){
        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.POST_NOTIFICATIONS},code);
    }
}
