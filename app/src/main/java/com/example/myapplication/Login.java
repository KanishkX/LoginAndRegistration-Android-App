package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    TextInputEditText emailtext, passwordtxt;
    Button ButtonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    TextView textView;


//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailtext = findViewById(R.id.mail);
        passwordtxt = findViewById(R.id.pswrd);
        progressBar = findViewById(R.id.progbar);
        textView = findViewById(R.id.RegNow);
        ButtonReg = findViewById(R.id.loginBut);
        TextInputLayout mailinput = findViewById(R.id.mailinpt);
        TextInputLayout textInputLayout = findViewById(R.id.pswrd1);



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Reg2.class);
                startActivity(intent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        emailtext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if (validatemail(emailtext)) {
                        mailinput.setError("Incorrect Email");
                    }
                }else{
                    mailinput.setErrorEnabled(false);
                }
            }
        });


        ButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String password, email;
                password = String.valueOf(passwordtxt.getText());
                email = String.valueOf(emailtext.getText());

                if (TextUtils.isEmpty(password)){
                    textInputLayout.setError("Incorrect Password");
                    progressBar.setVisibility(View.GONE);
                }else if(TextUtils.isEmpty(email)){
                    mailinput.setError("Incorrect Email");
                }else{
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    progressBar.setVisibility(View.GONE);
                                    if(user != null){
                                        boolean isVerified = user.isEmailVerified();
                                        if(!isVerified){
                                            Toast.makeText(Login.this, "Account Not Verified", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }else if(!task.isSuccessful()){
                                        Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


            }
        });


    }

    private boolean validatemail(EditText text){
        String email = String.valueOf(text.getText());
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if(pat.matcher(email).matches()){
            return false;
        }else{return true;}
    }
}