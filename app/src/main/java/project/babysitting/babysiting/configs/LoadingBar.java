package project.babysitting.babysiting.configs;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.widget.TextView;

import project.babysitting.babysiting.R;

public class LoadingBar {
    Context context;
    Dialog dialog;

    public LoadingBar(Context context) {
        this.context = context;
    }

    public void showDialog(String title){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtT = dialog.findViewById(R.id.txtTitle);
        txtT.setText(title);

        dialog.create();
        dialog.show();
    }

    public void dissmissDialog(){
        dialog.dismiss();
    }

}
