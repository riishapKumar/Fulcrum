package com.binaryss.fulcrum.Auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.binaryss.fulcrum.MainActivity;
import com.binaryss.fulcrum.Utils.SharedHelper;
import com.binaryss.fulcrum.databinding.ActivityLoginBinding;
import com.binaryss.fulcrum.databinding.ActivitySignupBinding;

public class Signup extends AppCompatActivity {
    private static final String TAG = "Login_Activity";
    ActivitySignupBinding binding;
    Context context = Signup.this;
    SharedHelper sharedHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnNext.setOnClickListener(view -> {
            RegisterUser();
        });
        binding.txtLogin.setOnClickListener(view -> {
            startActivity(new Intent(context, Login.class));
            finish();
        });
    }
    private void RegisterUser() {
        AuthMethods.registerUser(Signup.this,
                binding.etFirstname.getText().toString().trim() + " " + binding.etLastname.getText().toString().trim(),
                binding.etEmail.getText().toString().toLowerCase().trim(),
                binding.etPassword.getText().toString());
    }
}
