package com.binaryss.fulcrum.Auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.binaryss.fulcrum.R;
import com.binaryss.fulcrum.Utils.SharedHelper;
import com.binaryss.fulcrum.databinding.ActivityConfirmSignupBinding;

public class ConfirmSignUpActivity extends AppCompatActivity {
    ActivityConfirmSignupBinding binding;
    Context context = ConfirmSignUpActivity.this;
    String UserID = "";
    CountDownTimer countDownTimer;
    String pinCode = "";
    SharedHelper sharedHelper;
    int codeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InitViews();
        if(getIntent().getStringExtra("sendEmail")!= null && getIntent().getStringExtra("sendEmail").equals("true")){
            SendCodeAgain();
        }

        binding.confirmSignUpBtnSendCodeAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendCodeAgain();
            }
        });

        binding.confirmSignUpBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckFields();
            }
        });
        binding.confirmSignUpConfirmCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                pinCode = s.toString();
            }
        });
    }

    private void CheckFields() {
        if (pinCode.length() < 6) {
            Toast.makeText(context, "Please Enter Correct Code", Toast.LENGTH_SHORT).show();
            return;
        }
        ConfirmUser();
    }
    private void ConfirmUser() {
        AuthMethods.confirmSignUp(ConfirmSignUpActivity.this, UserID, pinCode);
    }

    //Send Code Again
    private void SendCodeAgain() {
        binding.confirmSignUpBtnSendCodeAgain.setTextColor(context.getColor(R.color.gray_900));

        AuthMethods.resendCode(ConfirmSignUpActivity.this);
        switch (codeCount){
            case 0:
                countDownTimer(7500);
                break;
            case 1:
            case 2:
                countDownTimer(15000);
                break;
            default:
                countDownTimer(30000);
        }
        codeCount++;
    }

    @SuppressLint("SetTextI18n")
    private void InitViews() {
        UserID = getIntent().getStringExtra("name");
        binding.confirmSignUpUserName.setText(UserID);
        binding.confirmSignUpUserName.setEnabled(false);
        sharedHelper = new SharedHelper(context);
        binding.confirmSignUpConfirmationCodeStatement.setText("Enter the confirmation code we sent to " + getIntent().getStringExtra("name") + " below.");
        countDownTimer = new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.confirmSignUpTimer.setText("Resend in " + millisUntilFinished / 1000 + " S");
            }
            @Override
            public void onFinish() {
                binding.confirmSignUpTimer.setText("");
                binding.confirmSignUpBtnSendCodeAgain.setEnabled(true);
                binding.confirmSignUpBtnSendCodeAgain.setTextColor(context.getColor(R.color.light_blue_400));

            }
        };
        countDownTimer.start();
    }

    private void countDownTimer(int time){
        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.confirmSignUpTimer.setText("Resend in " + millisUntilFinished / 1000 + " S");
            }
            @Override
            public void onFinish() {
                binding.confirmSignUpTimer.setText("00:00 S");
                binding.confirmSignUpBtnSendCodeAgain.setEnabled(true);
                binding.confirmSignUpBtnSendCodeAgain.setTextColor(context.getColor(R.color.light_blue_400));
            }
        };
        countDownTimer.start();
    }
}