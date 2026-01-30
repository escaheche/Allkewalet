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
    private LinearLayout layoutEmpty;
    private RecyclerView recyclerTransacciones;
    private LinearLayout btnSendMoney;
    private ImageView imgProfile;

    private List<SendActivity.Transaction> transactions = new ArrayList<>();
    private TransaccionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtBienvenida = findViewById(R.id.txtBienvenida);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerTransacciones = findViewById(R.id.recyclerTransacciones);
        btnSendMoney = findViewById(R.id.btnSend);
        imgProfile = findViewById(R.id.imgProfile);

        mostrarUsuario();

        // RecyclerView
        adapter = new TransaccionAdapter(transactions);
        recyclerTransacciones.setLayoutManager(new LinearLayoutManager(this));
        recyclerTransacciones.setAdapter(adapter);

        cargarTransacciones();

        // Click en "Enviar Dinero"
        btnSendMoney.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SendActivity.class));
        });

        // Click en perfil
        imgProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar transacciones cada vez que regresas a Home
        cargarTransacciones();
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
            holder.tvNombre.setText(tx.recipientName);
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