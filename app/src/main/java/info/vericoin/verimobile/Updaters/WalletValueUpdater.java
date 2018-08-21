package info.vericoin.verimobile.Updaters;

import android.widget.TextView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;

import info.vericoin.verimobile.WalletConnection;

public class WalletValueUpdater implements WalletChangeEventListener {

    private TextView confirmedTextView;

    private TextView unconfirmedTextView;

    private Wallet wallet;

    public WalletValueUpdater(Wallet wallet, TextView confirmedTextView, TextView unconfirmedTextView) {
        this.wallet = wallet;
        this.confirmedTextView = confirmedTextView;
        this.unconfirmedTextView = unconfirmedTextView;
    }

    public void listenForBalanceChanges(){
        wallet.addChangeEventListener(WalletConnection.getRunInUIThread(), this);
    }

    public void updateWalletView(){
        Coin confirmed = wallet.getBalance(Wallet.BalanceType.AVAILABLE);
        Coin unconfirmed = wallet.getBalance(Wallet.BalanceType.ESTIMATED).subtract(confirmed);
        confirmedTextView.setText(confirmed.toFriendlyString());
        unconfirmedTextView.setText(unconfirmed.toFriendlyString());
    }

    @Override
    public void onWalletChanged(Wallet wallet) {
        updateWalletView();
    }

    public void stopListening(){
        wallet.removeChangeEventListener(this);
    }
}