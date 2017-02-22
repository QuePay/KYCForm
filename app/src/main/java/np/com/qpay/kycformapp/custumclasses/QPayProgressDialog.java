package np.com.qpay.kycformapp.custumclasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import np.com.qpay.kycformapp.R;

/**
 * Created by qpay on 2/17/17.
 */

public class QPayProgressDialog {
    private Context context;
    private Dialog dialog;

    public QPayProgressDialog(Context context) {
        this.context = context;
        init();
    }

    public void init(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.progresslayout, null);

        builder.setView(v);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void show(){
        if(dialog != null) {
            dialog.show();
        }
    }

    public void dismiss(){
        if(dialog != null) {
            dialog.dismiss();
        }
    }
}
