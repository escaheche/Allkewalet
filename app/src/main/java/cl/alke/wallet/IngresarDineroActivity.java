package cl.alke.wallet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class IngresarDineroActivity extends AppCompatActivity {

    private ImageView imgBack;
    private LinearLayout btnIngresarDinero;
    private TextInputEditText etAmount, etNotes;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar);

        // Views
        imgBack = findViewById(R.id.imgBack);
        btnIngresarDinero = findViewById(R.id.btnIngresarDinero);
        etAmount = findViewById(R.id.etAmount);
        etNotes = findViewById(R.id.etNotes);

        prefs = getSharedPreferences("transactions", MODE_PRIVATE);

        // Botón ingresar dinero
        btnIngresarDinero.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) {
                etAmount.setError("Ingresa un monto");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    etAmount.setError("El monto debe ser mayor a 0");
                    return;
                }
            } catch (NumberFormatException e) {
                etAmount.setError("Monto inválido");
                return;
            }

            String notes = etNotes.getText().toString();
            if (notes.isEmpty()) {
                notes = "Ingreso de dinero";
            }

            // Crear transacción de ingreso usando el mismo formato que SendActivity.Transaction
            SendActivity.Transaction transaction = new SendActivity.Transaction(
                    "Ingreso",
                    "ingreso@wallet.com",
                    R.drawable.icono_send,
                    amount,
                    notes,
                    System.currentTimeMillis(),
                    "ingreso"
            );

            saveTransaction(transaction);

            // Volver al HomeActivity
            finish();
        });

        // Botón flecha atrás
        imgBack.setOnClickListener(v -> finish());
    }

    // Guardar transacción
    private void saveTransaction(SendActivity.Transaction transaction) {
        SharedPreferences prefs = getSharedPreferences("transactions", MODE_PRIVATE);
        String existing = prefs.getString("transactions_list", "");
        String newEntry = transaction.toJson();
        String updated = existing.isEmpty() ? newEntry : existing + "|" + newEntry;

        prefs.edit().putString("transactions_list", updated).apply();
        Toast.makeText(this, "Dinero ingresado exitosamente!", Toast.LENGTH_SHORT).show();
    }
}
