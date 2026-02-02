package cl.alke.wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView txtBienvenida;
    private TextView txtMonto;
    private LinearLayout layoutEmpty;
    private RecyclerView recyclerTransacciones;
    private LinearLayout btnSendMoney;
    private LinearLayout btnIngresar;
    private ImageView imgProfile;
    private ImageView imgLogout;

    private List<SendActivity.Transaction> transactions = new ArrayList<>();
    private TransaccionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtBienvenida = findViewById(R.id.txtBienvenida);
        txtMonto = findViewById(R.id.txtMonto);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerTransacciones = findViewById(R.id.recyclerTransacciones);
        btnSendMoney = findViewById(R.id.btnSend);
        btnIngresar = findViewById(R.id.btnAdd);
        imgProfile = findViewById(R.id.imgProfile);
        imgLogout = findViewById(R.id.imgLogout);

        mostrarUsuario();

        // RecyclerView
        adapter = new TransaccionAdapter(transactions);
        recyclerTransacciones.setLayoutManager(new LinearLayoutManager(this));
        recyclerTransacciones.setAdapter(adapter);

        cargarTransacciones();
        actualizarBalance();

        // Click en "Enviar Dinero"
        btnSendMoney.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SendActivity.class));
        });

        // Click en "Ingresar"
        btnIngresar.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, IngresarDineroActivity.class));
        });

        // Click en perfil
        imgProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        // Cerrar sesión: borra todos los datos y vuelve a AuthSelectorActivity
        imgLogout.setOnClickListener(v -> {
            borrarDatosYVolverALogin();
        });
    }

    private void borrarDatosYVolverALogin() {
        getSharedPreferences("transactions", MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("usuarios", MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("send_money", MODE_PRIVATE).edit().clear().apply();

        Intent intent = new Intent(HomeActivity.this, AuthSelectorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTransacciones();
        actualizarBalance();
        adapter.notifyDataSetChanged();
    }

    private void mostrarUsuario() {
        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Usuario");
        txtBienvenida.setText("Hola, " + nombre + "!");
    }

    private void cargarTransacciones() {
        SharedPreferences prefs = getSharedPreferences("transactions", MODE_PRIVATE);
        String listJson = prefs.getString("transactions_list", "");

        transactions.clear();

        if (!listJson.isEmpty()) {
            String[] entries = listJson.split("\\|");
            for (String entry : entries) {
                transactions.add(SendActivity.Transaction.fromJson(entry));
            }
            layoutEmpty.setVisibility(View.GONE);
            recyclerTransacciones.setVisibility(View.VISIBLE);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerTransacciones.setVisibility(View.GONE);
        }
    }

    private void actualizarBalance() {
        double balance = 0;
        for (SendActivity.Transaction tx : transactions) {
            if ("ingreso".equals(tx.type)) {
                balance += tx.amount;
            } else {
                balance -= tx.amount;
            }
        }
        txtMonto.setText(String.format(Locale.US, "$%.2f", balance));
    }

    // Adapter para RecyclerView
    public static class TransaccionAdapter extends RecyclerView.Adapter<TransaccionAdapter.ViewHolder> {

        private final List<SendActivity.Transaction> transactionList;

        public TransaccionAdapter(List<SendActivity.Transaction> transactionList) {
            this.transactionList = transactionList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaccion, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SendActivity.Transaction tx = transactionList.get(position);
            holder.imgRecipient.setImageResource(tx.recipientPhoto);

            boolean esIngreso = "ingreso".equals(tx.type);
            if (esIngreso) {
                holder.tvNombre.setText(tx.recipientName);
                holder.tvMonto.setTextColor(holder.itemView.getContext().getColor(R.color.verde));
            } else {
                holder.tvNombre.setText("Enviado a: " + tx.recipientName);
                holder.tvMonto.setTextColor(holder.itemView.getContext().getColor(R.color.rojo));
            }
            holder.tvMonto.setText(String.format("$%.2f", tx.amount));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvFecha.setText(sdf.format(new Date(tx.timestamp)));
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgRecipient;
            TextView tvNombre, tvMonto, tvFecha;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgRecipient = itemView.findViewById(R.id.imgRecipientItem);
                tvNombre = itemView.findViewById(R.id.tvNombreItem);
                tvMonto = itemView.findViewById(R.id.tvMontoItem);
                tvFecha = itemView.findViewById(R.id.tvFechaItem);
            }
        }
    }
}