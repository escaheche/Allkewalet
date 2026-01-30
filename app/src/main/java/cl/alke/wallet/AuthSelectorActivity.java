package cl.alke.wallet;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AuthSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_selector);

        Button btnCreate = findViewById(R.id.btnCreateAccount);
        TextView txtLogin = findViewById(R.id.txtLogin);

        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));

        txtLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }
}
