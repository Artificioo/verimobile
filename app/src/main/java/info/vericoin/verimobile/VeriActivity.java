package info.vericoin.verimobile;

import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public abstract class VeriActivity extends AppCompatActivity {

    private VeriMobileApplication veriMobileApplication;

    @Override
    protected void onResume() {
        super.onResume();
        veriMobileApplication = (VeriMobileApplication) getApplication();

        if (veriMobileApplication.isSecureWindowEnabled()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        veriMobileApplication = (VeriMobileApplication) getApplication();

        if (veriMobileApplication.isSecureWindowEnabled()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

}
