package pjurado.com.viajes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    Button btnAcceder;
    Button btnRegistrar;
    EditText edtemail;
    EditText edtpass;
    FirebaseAuth mAuth;
    FirebaseUser user;

    Button btnGoogle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnAcceder = findViewById(R.id.botonAcceder);
        btnRegistrar = findViewById(R.id.botonRegistrar);
        edtemail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        edtpass = (EditText) findViewById(R.id.editTextTextPassword);
        btnGoogle = findViewById(R.id.buttonGoogle);



        mAuth = FirebaseAuth.getInstance();

        //**********************************
        //Provisional para pruebas
        //edtemail.setText("pjolid@gmail.com");
        //edtpass.setText("olid1901");
        //************
        setup();
        sesion();
    }

    public void sesion(){
        SharedPreferences mFilePreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        if (mFilePreferences.getString("email", null) != null){
            showMain(mFilePreferences.getString("email", null));
        }
    }
    public void setup(){
        setTitle("Acceder");
        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edtemail.getText().toString();
                String pass = edtpass.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()){
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Auth", "Petición cp");
                                    if (task.isSuccessful()){
                                       showMain(email);
                                    }
                                    else{
                                        Toast.makeText(Login.this, R.string.acceso_incorrecto, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edtemail.getText().toString();
                String pass = edtpass.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()){
                    Log.d("Auth", "No vacios");
                    mAuth.createUserWithEmailAndPassword(email, pass)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        showMain(email);
                                    }
                                    else{
                                        try {
                                            throw task.getException();
                                        } catch(FirebaseAuthWeakPasswordException e) {
                                            edtpass.setError(getString(R.string.contraseña_incorrecta));
                                            edtpass.requestFocus();
                                        } catch(FirebaseAuthInvalidCredentialsException e) {
                                            edtemail.setError(getString(R.string.email_incorrecto));
                                            edtemail.requestFocus();
                                        } catch(FirebaseAuthUserCollisionException e) {
                                            edtemail.setError(getString(R.string.ya_resgistrado));
                                            edtemail.requestFocus();
                                        } catch(Exception e) {
                                            Log.e("Auth", e.getMessage());
                                        }
                                        user = null;
                                    }
                                }
                            });
                }
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions googleConf = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient googleClient = GoogleSignIn.getClient(getBaseContext(), googleConf);
                googleClient.signOut();

                startActivityForResult(googleClient.getSignInIntent(), 100);


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null){
                    AuthCredential credencial = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(credencial).
                            addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                //Log.d("Grabo","Usuario actual" + user.getEmail());
                                Log.d("Grabo", "Usuario seleccionado" + account.getEmail());
                                showMain(account.getEmail());
                            }
                            else{
                                Toast.makeText(Login.this, R.string.acceso_incorrecto, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }


                
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    void showMain(String email){
        user = mAuth.getCurrentUser();
        //updateUI(AplicacionViajes.user);
        Intent i = new Intent(Login.this, MainActivity.class);
        i.putExtra("email", email);

        startActivity(i);
    }

}