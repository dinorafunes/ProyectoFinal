package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ActivityRegister extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    ClassVerifyNet classVerifyNet;
    ClassFoto classFoto;
    Button btnRegister, btnLogin;
    ImageButton btnFotoUser;
    EditText txtName, txtEmail, txtPassword, txtPasswordConfirm;
    FirebaseAuth mAuth;
    FirebaseFirestore databaseFirebase;
    FirebaseUser userEmailAcc;
    FirebaseStorage storageUser;
    StorageReference storageUserRef;
    ProgressBar barProgress;
    String fotoUser;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            classFoto.tomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                classFoto.tomarFoto();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                String urlCompletaFoto = classFoto.urlCompletaFoto;
                fotoUser = urlCompletaFoto;
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                btnFotoUser.setImageBitmap(imagenBitmap);
            } else {
                Toast.makeText(this, "Se cancelo la captura de camara", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("ActivityRegister", "No se pudo tomar la foto: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
        setContentView(R.layout.activity_register);

        cargarObjetos();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });

        btnFotoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String passwordConfirm = txtPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ActivityRegister.this, "Ingrese el correo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(ActivityRegister.this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(ActivityRegister.this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(ActivityRegister.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!classVerifyNet.isOnlineNet()) {
            Toast.makeText(ActivityRegister.this, "No hay conexión a internet. Conéctese a una red Wifi", Toast.LENGTH_SHORT).show();
            return;
        }

        barProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        barProgress.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            try {
                                userEmailAcc = mAuth.getCurrentUser();
                                String type = "Email";
                                insertDataToSqlite(userEmailAcc.getEmail());
                                insertDataToFirebase(fotoUser, txtName.getText().toString(), userEmailAcc.getEmail(), type);
                                insertDataToStorage(userEmailAcc.getEmail());
                                Toast.makeText(ActivityRegister.this, "¡Usuario creado exitosamente!", Toast.LENGTH_SHORT).show();
                            } catch (Exception ex) {
                                Log.d("ActivityRegister", "No se pudo tomar la foto: " + ex.getMessage());
                            }

                            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                            intent.putExtra("USEREMAIL", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ActivityRegister.this, "No se pudo crear el usuario. Intente otra vez",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cargarObjetos() {
        classVerifyNet = new ClassVerifyNet(ActivityRegister.this);
        classFoto = new ClassFoto(ActivityRegister.this);

        txtName = findViewById(R.id.txtNombre);
        txtEmail = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);
        txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnFotoUser = findViewById(R.id.btnFotoUser);
        barProgress = findViewById(R.id.barProgress);

        storageUser = FirebaseStorage.getInstance();
        storageUserRef = storageUser.getReference();

        mAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseFirestore.getInstance();


        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        dbRead = dbSqlite.getReadableDatabase();
    }

    private void insertDataToSqlite(String userEmailAcc) {
        if (fotoUser == null) {
            fotoUser = null;
        }

        String foto = fotoUser;
        String nombre = txtName.getText().toString();
        String email = userEmailAcc;
        String type = "Email";

        try {
            ContentValues values = new ContentValues();
            values.put(DBSqlite.TableUser.COLUMN_FOTO, foto);
            values.put(DBSqlite.TableUser.COLUMN_NOMBRE, nombre);
            values.put(DBSqlite.TableUser.COLUMN_CORREO, email);
            values.put(DBSqlite.TableUser.COLUMN_TYPE, type);

            long newRowId = dbWrite.insert(DBSqlite.TableUser.TABLE_USER, null, values);

            //Exito//
        } catch (Exception ex) {
            Log.d("ActivityRegister", "Error al insertar los datos en SQLite: " + ex.getMessage());
        }
    }

    private void insertDataToFirebase(String foto, String nombre, String email, String tipo) {
        databaseFirebase = FirebaseFirestore.getInstance();

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
                        Log.d("ActivityRegister", "Error al insertar los datos en Firebase: " + e.getMessage());
                    }
                });
    }

    private void insertDataToStorage(String userEmail) {
        Uri file = Uri.fromFile(new File(fotoUser));
        StorageReference userRef = storageUserRef.child(userEmailAcc.getEmail().toString());
        StorageReference userFotosRef = userRef.child("fotosUser/" + file.getLastPathSegment());
        UploadTask uploadTask = userFotosRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("ActivityRegister", "Error al insertar los datos en FireStorage: " + exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Exito//
            }
        });

        //Obtener enlace a la foto de Storage
        uploadTask = userFotosRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return userFotosRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                try {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        updateDataToFirebase(userEmail, downloadUri.toString());
                    }
                } catch (Exception ex) {
                    Log.d("ActivityRegister_insertDataToStorage", "Error al extraer URL de Storage: " + ex.getMessage());
                }
            }
        });
    }

    private void updateDataToFirebase(String userEmail, String fotoUrl) {
        Map<String, Object> prodData = new HashMap<>();
        prodData.put("fotoUrl", fotoUrl);

        databaseFirebase.collection(userEmail).document("tableUser").set(prodData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Exito
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ActivityRegister_updateDataToFirebase", "Error al actualizar los datos en Firebase: " + e.getMessage());
            }
        });
    }
}