package com.example.budapestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Firebase Auth instance
    private FirebaseAuth mAuth;

    // UI elemek
    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase Authentication inicializálása
        mAuth = FirebaseAuth.getInstance();

        // UI elemek összekapcsolása a layout elemeivel
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Bejelentkezés gomb eseménykezelése
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Adatok kiolvasása az EditText-ekből
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Egyszerű ellenőrzés: minden mező ki van-e töltve
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Kérjük, töltsd ki az összes mezőt!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase bejelentkezési metódus hívása
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if(task.isSuccessful()){
                                // Ha sikeres a bejelentkezés, megkapjuk a FirebaseUser-t
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();

                                // Átnavigálunk a MainActivity-re (vagy a megfelelő képernyőre)
                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Hiba esetén megjelenítjük a hibaüzenetet
                                Toast.makeText(LoginActivity.this, "Bejelentkezés sikertelen: "
                                        + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}