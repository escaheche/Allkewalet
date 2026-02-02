package cl.alke.wallet;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class SendActivity extends AppCompatActivity {

    private Spinner spinnerRecipients;
    private LinearLayout llSelectedRecipient;
    private ShapeableImageView imgRecipient;
    private TextView tvRecipientName, tvRecipientEmail;
    private ImageView imgBack;

    private LinearLayout btnSendMoney;
    private TextInputEditText etAmount, etNotes;

    private SharedPreferences prefs;

    private int savedPhotoRes = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // Views
        spinnerRecipients = findViewById(R.id.spinnerRecipients);
        llSelectedRecipient = findViewById(R.id.llSelectedRecipient);
        imgRecipient = findViewById(R.id.imgRecipient);
        tvRecipientName = findViewById(R.id.tvRecipientName);
        tvRecipientEmail = findViewById(R.id.tvRecipientEmail);
        imgBack = findViewById(R.id.imgBackHome1);

        btnSendMoney = findViewById(R.id.btnSendMoney);
        etAmount = findViewById(R.id.etAmount);
        etNotes = findViewById(R.id.etNotes);

        prefs = getSharedPreferences("send_money", MODE_PRIVATE);

        // Lista de destinatarios
        List<Recipient> recipients = Arrays.asList(
                new Recipient("Felipe Perez", "felipe@mail.com", R.drawable.fotoperfil1),
                new Recipient("Maria Lopez", "maria@mail.com", R.drawable.perfil2)
        );

        // Adaptador Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                recipients.stream().map(Recipient::getName).toArray(String[]::new));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecipients.setAdapter(adapter);

        // Cargar destinatario guardado
        String savedName = prefs.getString("recipient_name", null);
        String savedEmail = prefs.getString("recipient_email", null);
        savedPhotoRes = prefs.getInt("recipient_photo", -1);

        if (savedName != null && savedEmail != null && savedPhotoRes != -1) {
            tvRecipientName.setText(savedName);
            tvRecipientEmail.setText(savedEmail);
            imgRecipient.setImageResource(savedPhotoRes);
            llSelectedRecipient.setVisibility(View.VISIBLE);
        }

        // Spinner listener
        spinnerRecipients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Recipient selected = recipients.get(position);

                llSelectedRecipient.setVisibility(View.VISIBLE);

                tvRecipientName.setText(selected.getName());
                tvRecipientEmail.setText(selected.getEmail());
                imgRecipient.setImageResource(selected.getPhotoRes());
                savedPhotoRes = selected.getPhotoRes();

                prefs.edit()
                        .putString("recipient_name", selected.getName())
                        .putString("recipient_email", selected.getEmail())
                        .putInt("recipient_photo", selected.getPhotoRes())
                        .apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                llSelectedRecipient.setVisibility(View.GONE);
            }
        });

        // Botón enviar dinero
        btnSendMoney.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) {
                etAmount.setError("Ingresa un monto");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                etAmount.setError("Monto inválido");
                return;
            }

            String notes = etNotes.getText().toString();

            SendActivity.Transaction transaction = new SendActivity.Transaction(
                    tvRecipientName.getText().toString(),
                    tvRecipientEmail.getText().toString(),
                    savedPhotoRes,
                    amount,
                    notes,
                    System.currentTimeMillis(),
                    "envio"
            );

            saveTransaction(transaction);

            // Volver al HomeActivity
            finish();
        });

        // Botón flecha atrás
        imgBack.setOnClickListener(v -> finish());
    }

    // Guardar transacción
    private void saveTransaction(Transaction transaction) {
        SharedPreferences prefs = getSharedPreferences("transactions", MODE_PRIVATE);
        String existing = prefs.getString("transactions_list", "");
        String newEntry = transaction.toJson();
        String updated = existing.isEmpty() ? newEntry : existing + "|" + newEntry;

        prefs.edit().putString("transactions_list", updated).apply();
        Toast.makeText(this, "Transacción enviada!", Toast.LENGTH_SHORT).show();
    }

    // Clase destinatario
    public static class Recipient {
        private String name;
        private String email;
        private int photoRes;

        public Recipient(String name, String email, int photoRes) {
            this.name = name;
            this.email = email;
            this.photoRes = photoRes;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getPhotoRes() { return photoRes; }
    }

    // Clase transacción (type: "ingreso" | "envio")
    public static class Transaction {
        public String recipientName;
        public String recipientEmail;
        public int recipientPhoto;
        public double amount;
        public String notes;
        public long timestamp;
        public String type;

        public Transaction(String recipientName, String recipientEmail, int recipientPhoto,
                           double amount, String notes, long timestamp) {
            this(recipientName, recipientEmail, recipientPhoto, amount, notes, timestamp, "envio");
        }

        public Transaction(String recipientName, String recipientEmail, int recipientPhoto,
                           double amount, String notes, long timestamp, String type) {
            this.recipientName = recipientName;
            this.recipientEmail = recipientEmail;
            this.recipientPhoto = recipientPhoto;
            this.amount = amount;
            this.notes = notes;
            this.timestamp = timestamp;
            this.type = type != null ? type : "envio";
        }

        public String toJson() {
            return recipientName + ";" + recipientEmail + ";" + recipientPhoto + ";" + amount + ";" + notes + ";" + timestamp + ";" + type;
        }

        public static Transaction fromJson(String json) {
            String[] parts = json.split(";");
            String type = parts.length >= 7 ? parts[6] : "envio";
            return new Transaction(
                    parts[0],
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Double.parseDouble(parts[3]),
                    parts[4],
                    Long.parseLong(parts[5]),
                    type
            );
        }
    }
}