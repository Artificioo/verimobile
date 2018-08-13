package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.bitcoinj.kits.WalletAppKit;

public class ChangePasswordActivity extends VeriActivity {

    private TextInputLayout currentPasswordLayout;
    private TextInputLayout newPasswordLayout;
    private TextInputLayout reNewPasswordLayout;
    private Button changePasswordButton;
    private CheckBox encryptWalletBox;

    private WalletAppKit kit;

    private ProgressBar progressBar;

    private CheckBox noPasswordBox;

    private BitcoinApplication bitcoinApplication;

    public static Intent createIntent(Context context){
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        kit = WalletConnection.getKit();
        bitcoinApplication = (BitcoinApplication) getApplication();

        currentPasswordLayout = findViewById(R.id.currentPasswordInputLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        reNewPasswordLayout = findViewById(R.id.reNewPasswordLayout);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        encryptWalletBox = findViewById(R.id.encryptWallet);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

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

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetErrors();

                if(!isCurrentPasswordCorrect(getCurrentPassword())){
                    currentPasswordLayout.setError("Current password is not correct");
                }else if(!doPasswordsMatch() && !noPasswordBox.isChecked()) {
                    newPasswordLayout.setError("Passwords do not match");
                }else if(isNewPasswordEmpty() && !noPasswordBox.isChecked()){
                    newPasswordLayout.setError("Password can not be empty");
                }else{
                    updatePassword(getNewPassword());
                }
            }
        });
    }

    public void disableNewPassword(){
        newPasswordLayout.setEnabled(false);
        reNewPasswordLayout.setEnabled(false);
        encryptWalletBox.setEnabled(false);
    }

    public void enableNewPassword(){
        newPasswordLayout.setEnabled(true);
        reNewPasswordLayout.setEnabled(true);
        encryptWalletBox.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(doesPasswordExist()){
            currentPasswordLayout.setEnabled(true);
        }else{
            currentPasswordLayout.setEnabled(false);
        }

    }

    public boolean doesPasswordExist(){
        return bitcoinApplication.doesPasswordExist();
    }

    public void updatePassword(String password){

        if(noPasswordBox.isChecked()){
            bitcoinApplication.removePassword();
        }else {
            bitcoinApplication.newPassword(password);
        }

        changePasswordButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        changePasswordButton.setText("");

        new Thread(new Runnable() {
            @Override
            public void run() {

                if(kit.wallet().isEncrypted()){
                    decryptWallet(getCurrentPassword());
                }

                if (encryptWalletBox.isChecked() && !noPasswordBox.isChecked()) {
                    encryptWallet(getNewPassword());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        changePasswordButton.setText("Update Password");
                        changePasswordButton.setEnabled(true);
                        Toast.makeText(ChangePasswordActivity.this, "Password updated!", Toast.LENGTH_LONG).show();
                        finish();
                        clearInputs();
                    }
                });
            }
        }).start();
    }

    public boolean isNewPasswordEmpty(){
        return newPasswordLayout.getEditText().getText().toString().isEmpty();
    }

    public String getCurrentPasswordHash(){
        return bitcoinApplication.getPasswordHash();
    }

    public void clearInputs(){
        currentPasswordLayout.getEditText().setText("");
        newPasswordLayout.getEditText().setText("");
        reNewPasswordLayout.getEditText().setText("");
    }

    public void decryptWallet(String password){
        kit.wallet().decrypt(password);
    }

    public void encryptWallet(String password){
        kit.wallet().encrypt(password);
    }

    public void resetErrors(){
        currentPasswordLayout.setErrorEnabled(false);
        newPasswordLayout.setErrorEnabled(false);
        reNewPasswordLayout.setErrorEnabled(false);
    }

    public String getCurrentPassword(){
        return currentPasswordLayout.getEditText().getText().toString();
    }

    public String getNewPassword(){
        return newPasswordLayout.getEditText().getText().toString();
    }

    public String getReNewPassword(){
        return reNewPasswordLayout.getEditText().getText().toString();
    }

    public boolean doPasswordsMatch(){
        return getNewPassword().equals(getReNewPassword());
    }

    public boolean isCurrentPasswordCorrect(String oldPassword){
        String passwordHash = getCurrentPasswordHash();
        return (passwordHash.isEmpty() || passwordHash.equals(Util.hashStringSHA256(oldPassword)));
    }

}
