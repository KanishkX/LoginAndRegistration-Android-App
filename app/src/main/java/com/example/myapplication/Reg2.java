package com.example.myapplication;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class Reg2 extends AppCompatActivity {
    TextInputEditText emailtext, passwordtxt,passwordtxt2;
    Button ButtonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TableLayout table;
    DatabaseReference mDatabase;
    FirebaseDatabase database;
    User user;
    Boolean paswrd;
    Boolean mailError;
    private static final String USER = "user";
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg2);
        mAuth = FirebaseAuth.getInstance();
        emailtext = findViewById(R.id.mail);
        passwordtxt = findViewById(R.id.pswrd);
        progressBar = findViewById(R.id.progbar);
//        textView = findViewById(R.id.RegNow);
        ButtonReg = findViewById(R.id.RegButton);
        passwordtxt2 = findViewById(R.id.pswrd1);
        table = findViewById(R.id.table);
        TextInputLayout mailinput = findViewById(R.id.mailinpt);
        TextInputLayout textInputLayout1 = findViewById(R.id.paswrdInp);
        TextInputLayout textInputLayout2 = findViewById(R.id.paswrdInp1);
        table.setVisibility(View.INVISIBLE);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USER);

        emailtext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    if (validatemail(emailtext)) {
                        mailinput.setError("Incorrect Email");
                        mailError = true;
                    }else{
                        mailError = false;
                    }
                }
                else{
                    mailinput.setErrorEnabled(false);
                }

            }
        });

        passwordtxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                table.setVisibility(View.VISIBLE);
                String password = String.valueOf(passwordtxt.getText());
                if(!hasFocus){
                    table.setVisibility(View.INVISIBLE);
                    if (TextUtils.isEmpty(password)) {
                        textInputLayout1.setError("Incorrect Password");
                    }else{
                        if(password.length()<8 &&!isValidPassword(password)){
                            textInputLayout1.setError("Incorrect Password");
                            paswrd = true;
                        }else{
                            paswrd = false;
                        }
                    }
                }
                else{
                    textInputLayout1.setErrorEnabled(false);
                }
            }
        });

        passwordtxt2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                table.setVisibility(View.VISIBLE);
                String password2 = String.valueOf(passwordtxt2.getText());
                if(!hasFocus){
                    table.setVisibility(View.INVISIBLE);
                    if (TextUtils.isEmpty(password2)) {
                        textInputLayout2.setError("Incorrect Confirm Password ");
                    }
                }
                else{
                    textInputLayout2.setErrorEnabled(false);
                }
            }
        });

        passwordtxt2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    ButtonReg.performClick();
                }
                return false;
            }
        });
        ButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String password, email,password2;
                password = String.valueOf(passwordtxt.getText());
                email = String.valueOf(emailtext.getText());
                password2 = String.valueOf(passwordtxt2.getText());
                if(TextUtils.isEmpty(email) || mailError){
                    mailinput.setError("Incorrect Email");
                    progressBar.setVisibility(View.GONE);
                }
                else if (TextUtils.isEmpty(password) || paswrd){
                    textInputLayout1.setError("Incorrect Password");
                    progressBar.setVisibility(View.GONE);
                }else if(!password.equals(password2)){
                    textInputLayout2.setError("Invalid Confirm Password");
                    progressBar.setVisibility(View.GONE);
                }else{
                        user = new User(email,password);
                        registerUser(email,password);
                }
            }
        });
    }

    public void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Reg2.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
                                    UpdateUI(user);
                                }
                            });
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            checkemail();
                        }
                    }
                });
    }

    void checkemail(){
        mAuth = FirebaseAuth.getInstance();
        String email = String.valueOf(emailtext.getText());
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size()==0){
                    // email not existed
                    Toast.makeText(Reg2.this, "Failed :/",
                            Toast.LENGTH_SHORT).show();
                }else {
                    // email existed
                    Toast.makeText(Reg2.this, "Account Already Registered :)",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    void UpdateUI(FirebaseUser Currentuser){
        String keyId = mDatabase.push().getKey();
        mDatabase.child(keyId).setValue(user);
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);

    }

    private boolean validatemail(EditText text){
        String email = String.valueOf(text.getText());
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if(pat.matcher(email).matches()){
            return false;
        } else{return true;}
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}