package com.binaryss.fulcrum.Utils;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.binaryss.fulcrum.R;

public class LoadingDialog {
    Context context;
    Dialog dialogView;
    public LoadingDialog(Context activity) {
        this.context = activity;
    }

    public void show() {
        dialogView = new Dialog(context, R.style.CustomAlertDialog);
        dialogView.setCanceledOnTouchOutside(false);
        dialogView.setContentView(R.layout.progress_dialog);
        try {
            dialogView.show();
        }
        catch (Exception e) {
            Log.d("LoadingDialog", "WindowManager.BadTokenException: " + e.toString());
        }
    }

    public void dismiss() {
        try {
            if (dialogView != null && dialogView.isShowing()) {
                dialogView.dismiss();
            }
        }
        catch (Exception e) {
            Log.d("LoadingDialog", "dismiss Exception: " + e.toString());
        }

    }
}
