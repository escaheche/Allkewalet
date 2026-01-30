package cl.alke.wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName;
    private ImageView imgEditName;
    private ShapeableImageView imgProfile;

    private SharedPreferences userPrefs;
    private String userEmail;

    // Launcher para seleccionar imagen desde galería
    private final ActivityResultLauncher<String> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imgProfile.setImageURI(uri);
                    // Guardar URI de la imagen en SharedPreferences del usuario
                    userPrefs.edit().putString("profile_image_uri", uri.toString()).apply();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializar views
        tvFullName = findViewById(R.id.tvFullName);
        imgEditName = findViewById(R.id.imgEditName);
        imgProfile = findViewById(R.id.imgProfile);

        // 🔹 Obtener email del usuario actual (de la sesión, por ejemplo)
        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);
        userEmail = prefs.getString("email", null);

        if (userEmail == null) {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 SharedPreferences exclusivo del usuario
        userPrefs = getSharedPreferences("user_" + userEmail, MODE_PRIVATE);

        // 🔹 Cargar nombre y apellido
        String nombre = userPrefs.getString("nombre", prefs.getString("nombre", "Nombre"));
        String apellido = userPrefs.getString("apellido", prefs.getString("apellido", "Apellido"));
        tvFullName.setText(nombre + " " + apellido);

        // 🔹 Cargar imagen de perfil si existe
        String imageUriStr = userPrefs.getString("profile_image_uri", null);
        if (imageUriStr != null) {
            imgProfile.setImageURI(Uri.parse(imageUriStr));
        }

        imgEditName.setOnClickListener(view -> {
            EditNameDialog dialog = new EditNameDialog(ProfileActivity.this, (newName, newLastName) -> {
                tvFullName.setText(newName + " " + newLastName);
            });
            dialog.show();
        });

        // Click en la foto de perfil para cambiarla
        imgProfile.setOnClickListener(view -> selectImageLauncher.launch("image/*"));

        ImageView btnBack = findViewById(R.id.imgBackHome);

        btnBack.setOnClickListener(v -> {
            // Volver al HomeActivity
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // opcional, si no quieres que Profile quede en el back stack
        });
    }
}