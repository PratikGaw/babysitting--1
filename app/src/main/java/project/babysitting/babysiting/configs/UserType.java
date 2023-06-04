package project.babysitting.babysiting.configs;

import android.content.Context;
import android.content.SharedPreferences;

public class UserType  {
    public void setType(Context context,String type){
        SharedPreferences sharedPreferences = context.getSharedPreferences("userType",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("type",type);
        editor.commit();
        editor.apply();
    }

    public String getType(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("userType",Context.MODE_PRIVATE);
        return sharedPreferences.getString("type","");
    }
}
