package cl.alke.wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class EditNameDialog extends Dialog {

    private EditText edtNombre, edtApellido;
    private Button btnGuardar, btnCancelar;
    private SharedPreferences prefs;

    public interface OnNameUpdatedListener {
        void onNameUpdated(String nombre, String apellido);
    }

    private OnNameUpdatedListener listener;

    public EditNameDialog(@NonNull Context context, OnNameUpdatedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_name);

        // Inicializar SharedPreferences
        prefs = getContext().getSharedPreferences("usuarios", Context.MODE_PRIVATE);

        edtNombre = findViewById(R.id.edtNombreDialog);
        edtApellido = findViewById(R.id.edtApellidoDialog);
        btnGuardar = findViewById(R.id.btnGuardarDialog);
        btnCancelar = findViewById(R.id.btnCancelarDialog);

        // Cargar valores actuales
        String nombreActual = prefs.getString("nombre", "");
        String apellidoActual = prefs.getString("apellido", "");
        edtNombre.setText(nombreActual);
        edtApellido.setText(apellidoActual);

        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = edtNombre.getText().toString().trim();
            String nuevoApellido = edtApellido.getText().toString().trim();

            if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty()) {
                Toast.makeText(getContext(), "Completa ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar en SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nombre", nuevoNombre);
            editor.putString("apellido", nuevoApellido);
            editor.apply();

            // Notificar al listener
            if (listener != null) {
                listener.onNameUpdated(nuevoNombre, nuevoApellido);
            }

            dismiss();
        });

        btnCancelar.setOnClickListener(v -> dismiss());
    }
}
