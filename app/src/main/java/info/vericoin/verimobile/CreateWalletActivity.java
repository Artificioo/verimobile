package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import org.bitcoinj.kits.WalletAppKit;

public class CreateWalletActivity extends VeriActivity {

    private Button createWalletButton;

    private ProgressBar progressBar;

    private TextInputLayout passwordLayout;

    private TextInputLayout rePasswordLayout;

    private CheckBox encryptWallet;

    private CheckBox noPasswordBox;

    private BitcoinApplication bitcoinApplication;

    public static Intent createIntent(Context context){
        return new Intent(context, CreateWalletActivity.class);
    }

    public void disableNewPassword(){
        passwordLayout.setEnabled(false);
        rePasswordLayout.setEnabled(false);
        encryptWallet.setEnabled(false);
    }

    public void enableNewPassword(){
        passwordLayout.setEnabled(true);
        rePasswordLayout.setEnabled(true);
        encryptWallet.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        bitcoinApplication = (BitcoinApplication) getApplication();

        createWalletButton = findViewById(R.id.createWalletButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        passwordLayout = findViewById(R.id.passwordInputLayout);
        rePasswordLayout = findViewById(R.id.rePasswordInputLayout);
        encryptWallet = findViewById(R.id.encryptWallet);

        noPasswordBox = findViewById(R.id.noPasswordBox);

        noPasswordBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    disableNewPassword();
                }else{
                    enableNewPassword();
                }
            }
        });

        createWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!arePasswordsEqual() && !noPasswordBox.isChecked()) {
                    rePasswordLayout.setError("Passwords do not match");
                }else if(passwordsAreEmpty() && !noPasswordBox.isChecked()) {
                    passwordLayout.setError("Password can not be empty");
                }else{
                    passwordLayout.setErrorEnabled(false);
                    rePasswordLayout.setErrorEnabled(false);

                    createWalletButton.setEnabled(false);
                    createWalletButton.setText("");
                    progressBar.setVisibility(View.VISIBLE);

                    if(noPasswordBox.isChecked()) {
                        WalletConnection.startAsync(CreateWalletActivity.this);
                    }else if(shouldEncryptWallet()){
                        savePassword(getPassword());
                        WalletConnection.startAsync(CreateWalletActivity.this, getPassword());
                    }else{
                        savePassword(getPassword());
                        WalletConnection.startAsync(CreateWalletActivity.this);
                    }

                    WalletConnection.connect(new WalletConnection.OnConnectListener() {

                        @Override
                        public void OnSetUpComplete(WalletAppKit kit) {
                            startActivity(MainActivity.createIntent(CreateWalletActivity.this));
                            ActivityCompat.finishAffinity(CreateWalletActivity.this); //Prevent app from going back to previous activities.
                        }

                        @Override
                        public void OnSyncComplete() {

                        }
                    });
                }
            }
        });
    }

    public void savePassword(String password){
        bitcoinApplication.newPassword(password);
    }

    public String getPassword(){
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean shouldEncryptWallet(){
        return encryptWallet.isChecked();
    }

    public boolean passwordsAreEmpty(){
        String password = passwordLayout.getEditText().getText().toString();
        String rePassword = rePasswordLayout.getEditText().getText().toString();
        return (password.isEmpty() || rePassword.isEmpty());
    }

    public boolean arePasswordsEqual(){
        String password = passwordLayout.getEditText().getText().toString();
        String rePassword = rePasswordLayout.getEditText().getText().toString();
        return(password.equals(rePassword));
    }

}
