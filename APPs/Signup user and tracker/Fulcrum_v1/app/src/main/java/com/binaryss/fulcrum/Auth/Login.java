package com.binaryss.fulcrum.Auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.binaryss.fulcrum.Utils.SharedHelper;
import com.binaryss.fulcrum.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login_Activity";
    ActivityLoginBinding binding;
    Context context = Login.this;
    SharedHelper sharedHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnNext.setOnClickListener(view -> {
            AuthMethods.signIn(Login.this,
                    binding.etEmail.getText().toString(),
                    binding.etPassword.getText().toString());
        });

        binding.txtSignUp.setOnClickListener(view -> {
            startActivity(new Intent(context, Signup.class));
            finish();
        });
    }
}
