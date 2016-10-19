package com.creditscanner.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.creditscanner.android.adapter.OperatorAdapter;
import com.creditscanner.android.model.Operator;

import java.util.ArrayList;
import java.util.Locale;

public class OperatorInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_input);

        setTitle("Network provider");

        final String voucherCode = getIntent().getStringExtra("code");
        TextView codeLabel = (TextView) findViewById(R.id.codeLabel);
        if (voucherCode != null) {
            codeLabel.setText(String.format(Locale.getDefault(),
                    getString(R.string.code_label), voucherCode));
        } else {
            codeLabel.setVisibility(View.GONE);
        }

        ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<Operator> operatorList = new ArrayList<>();
        operatorList.add(new Operator("Vodafone", 134, R.drawable.ic_vodafone));
        operatorList.add(new Operator("Glo", 123, R.drawable.ic_glo));
        operatorList.add(new Operator("Airtel", 134, R.drawable.ic_airtel));
        operatorList.add(new Operator("Tigo", 842, R.drawable.ic_tigo));
        operatorList.add(new Operator("MTN", 134, R.drawable.ic_mtn));
        listView.setAdapter(new OperatorAdapter(this, operatorList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Operator operator = operatorList.get(position);
                PreferenceManager.getDefaultSharedPreferences(OperatorInputActivity.this).edit().putInt("operator", operator.getCode()).apply();
                if (getIntent().hasExtra("code")) {
                    dial(operator.getCode(), voucherCode);
                } else {
                    Toast.makeText(OperatorInputActivity.this, operator.getName() + " set as a provider", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    public void dial(int operatorCode, String voucherCode) {
        String number = String.format(Locale.getDefault(), "*%d*%s", operatorCode, voucherCode);

        Intent call = new Intent(Intent.ACTION_DIAL);
        call.setData(Uri.parse("tel:" + number + Uri.encode("#")));
        startActivity(call);
    }
}
