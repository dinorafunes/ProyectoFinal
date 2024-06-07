package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity {

    Button btnRegister;
    Button btnLogin;
    Button btnGoogle;
    EditText txtEmail;
    EditText txtPassword;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;
    FirebaseAuth mAuth;
    FirebaseFirestore databaseFirebase;
    ProgressBar barProgress;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cargarGoogleSignIn();
        cargarObjetos();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            startActivity(intent);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityRegister.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ActivityLogin.this, "Ingrese su dirección de correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(ActivityLogin.this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                barProgress.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                barProgress.setVisibility(View.GONE);
                                try {
                                    if (task.isSuccessful()) {
                                        //Exito//
                                        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(ActivityLogin.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ex){
                                    Log.d("ActivityLogin", "Error al iniciar sesion con Email: " + ex.getMessage());
                                }

                            }
                        });
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });
    }

    private void cargarObjetos() {
        txtEmail = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnSiginGoogle);

        barProgress = findViewById(R.id.barProgress);

        mAuth = FirebaseAuth.getInstance();

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        dbRead = dbSqlite.getReadableDatabase();
    }

    private void cargarGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
    }

    void signInGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

                if (acct != null) {
                    String userNameGoogle = acct.getDisplayName();
                    String userEmailGoogle = acct.getEmail();

                    insertDataSqlite(userEmailGoogle, userNameGoogle);
                    insertDataFirebase(userEmailGoogle, userNameGoogle);

                    //Exito//

                    Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "No se pudo obtener la cuenta de Google", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Log.d("ActivityLogin", "Error al iniciar sesion con Google: " + e.getMessage());
            }
        }
    }

    private void insertDataSqlite(String userEmailAcc, String userNameAcc) {

        String foto = null;
        String nombre = userNameAcc;
        String email = userEmailAcc;
        String type = "Google";

        try {
            ContentValues values = new ContentValues();
            values.put(DBSqlite.TableUser.COLUMN_FOTO, foto);
            values.put(DBSqlite.TableUser.COLUMN_NOMBRE, nombre);
            values.put(DBSqlite.TableUser.COLUMN_CORREO, email);
            values.put(DBSqlite.TableUser.COLUMN_TYPE, type);

            long newRowId = dbWrite.insert(DBSqlite.TableUser.TABLE_USER, null, values);

            //Exito//
        } catch (Exception ex) {
            Log.d("ActivityLogin", "Error al insertar datos en SQLite: " + ex.getMessage());
        }
    }

    public void insertDataFirebase(String email, String nombre) {
        databaseFirebase = FirebaseFirestore.getInstance();
        String foto = null;
        String tipo = "Google";

        Map<String, Object> userData = new HashMap<>();

        userData.put("foto", foto);
        userData.put("nombre", nombre);
        userData.put("email", email);
        userData.put("tipoCuenta", tipo);

        databaseFirebase.collection(email).document("tableUser")
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Exito//
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ActivityLogin", "Error al insertar datos a Firebase: " + e.getMessage());
                    }
                });
    }
}