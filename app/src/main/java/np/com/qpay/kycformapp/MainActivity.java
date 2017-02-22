package np.com.qpay.kycformapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import np.com.qpay.kycformapp.kyc.KycFormFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setTitle("KYC Form");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        addFilterOptionFragment();
    }

    public void addFilterOptionFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        KycFormFragment kycFormFragment = new KycFormFragment();
//        filterOptionFragment.setOnFilterOptionCloseListener(this);
        Bundle bundle = new Bundle();
        kycFormFragment.setArguments(bundle);
        transaction.add(R.id.kyc_frag_container, kycFormFragment);
        transaction.commit();
    }
}
