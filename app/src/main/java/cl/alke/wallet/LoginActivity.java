package cl.alke.wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgot = findViewById(R.id.txtForgot);

        btnLogin.setOnClickListener(v -> validarLogin());
    }

    private void validarLogin() {

        String emailIngresado = edtEmail.getText().toString().trim();
        String passwordIngresada = edtPassword.getText().toString();

        if (emailIngresado.isEmpty() || passwordIngresada.isEmpty()) {
            Toast.makeText(this, "Ingresa email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);

        boolean registrado = prefs.getBoolean("registrado", false);

        if (!registrado) {
            Toast.makeText(this, "No existe un usuario registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailGuardado = prefs.getString("email", "");
        String passwordGuardada = prefs.getString("password", "");

        if (emailIngresado.equals(emailGuardado)
                && passwordIngresada.equals(passwordGuardada)) {

            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, HomeActivity.class));
            finish();

        } else {
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}