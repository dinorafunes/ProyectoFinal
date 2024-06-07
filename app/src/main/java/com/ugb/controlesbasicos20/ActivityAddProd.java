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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ActivityAddProd extends AppCompatActivity {

    EditText txtCod, txtNom, txtMar, txtDesc, txtPrec, txtCost, txtStock;
    ImageView imgProd;
    ClassFoto classFoto;
    Button btnGuardarProd;
    ActivityMain activityMain;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite, dbRead;
    FirebaseFirestore databaseFirebase;
    FirebaseStorage storageProd;
    StorageReference storageProdRef;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                String urlCompletaFoto = classFoto.urlCompletaFoto;
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                imgProd.setImageBitmap(imagenBitmap);
            } else {
                Toast.makeText(this, "Se cancelo la captura de camara", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("ActivityAddProd", "No se pudo tomar la foto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prod);

        classFoto = new ClassFoto(ActivityAddProd.this);
        activityMain = new ActivityMain();

        String userEmail = activityMain.userEmailLogin;

        cargarObjetos();

        imgProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("producto")) {
                ClassProductos producto = (ClassProductos) intent.getSerializableExtra("producto");

                if (producto != null) {
                    txtCod.setText(producto.getCodigo());
                    txtNom.setText(producto.getNombre());
                    txtMar.setText(producto.getMarca());
                    txtDesc.setText(producto.getDescripcion());
                    txtPrec.setText(producto.getPrecio().toString());
                    txtCost.setText(producto.getCosto().toString());
                    txtStock.setText(String.valueOf(producto.getStock()));

                    String urlCompletaFoto = producto.getFoto();
                    Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                    imgProd.setImageBitmap(imagenBitmap);
                }
            }
        }

        btnGuardarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = txtCod.getText().toString();
                String nombre = txtNom.getText().toString();
                String marca = txtMar.getText().toString();
                String descripcion = txtDesc.getText().toString();
                String precio = txtPrec.getText().toString();
                String costo = txtCost.getText().toString();
                String stock = txtStock.getText().toString();
                String foto = classFoto.urlCompletaFoto;

                if (codigo == null || nombre == null || marca == null || precio == null || foto == null || descripcion == null || costo == null || stock == null) {
                    Toast.makeText(ActivityAddProd.this, "Ingrese datos en los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (intent != null && intent.hasExtra("producto")) {
                    // Actualizar producto existente
                    ClassProductos producto = (ClassProductos) intent.getSerializableExtra("producto");

                    if (producto != null) {
                        String urlCompletaFoto = producto.getFoto();
                        Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                        imgProd.setImageBitmap(imagenBitmap);

                        insertDataToStorage(urlCompletaFoto, userEmail, codigo, nombre);

                        try {
                            ContentValues values = new ContentValues();
                            values.put(DBSqlite.TableProd.COLUMN_USER, userEmail);
                            values.put(DBSqlite.TableProd.COLUMN_CODIGO, codigo);
                            values.put(DBSqlite.TableProd.COLUMN_NOMBRE, nombre);
                            values.put(DBSqlite.TableProd.COLUMN_MARCA, marca);
                            values.put(DBSqlite.TableProd.COLUMN_DESCRIPCION, descripcion);
                            values.put(DBSqlite.TableProd.COLUMN_PRECIO, precio);
                            values.put(DBSqlite.TableProd.COLUMN_COSTO, costo);
                            values.put(DBSqlite.TableProd.COLUMN_STOCK, stock);
                            values.put(DBSqlite.TableProd.COLUMN_FOTO, urlCompletaFoto);

                            String selection = DBSqlite.TableProd.COLUMN_CODIGO + " = ?";
                            String[] selectionArgs = {codigo};

                            int rowsUpdated = dbWrite.update(
                                    DBSqlite.TableProd.TABLE_PROD,
                                    values,
                                    selection,
                                    selectionArgs
                            );

                            insertDataToFirebase(userEmail, codigo, nombre, marca, descripcion, precio, urlCompletaFoto);

                        } catch (Exception ex) {
                            Log.d("ActivityAddProd", "Error al actualizar los datos en SQLite: " + ex.getMessage());
                        }
                    }

                } else {
                    // Agregar nuevo producto
                    insertDataToStorage(foto, userEmail, codigo, nombre);

                    try {
                        ContentValues values = new ContentValues();
                        values.put(DBSqlite.TableProd.COLUMN_USER, userEmail);
                        values.put(DBSqlite.TableProd.COLUMN_CODIGO, codigo);
                        values.put(DBSqlite.TableProd.COLUMN_NOMBRE, nombre);
                        values.put(DBSqlite.TableProd.COLUMN_MARCA, marca);
                        values.put(DBSqlite.TableProd.COLUMN_DESCRIPCION, descripcion);
                        values.put(DBSqlite.TableProd.COLUMN_PRECIO, precio);
                        values.put(DBSqlite.TableProd.COLUMN_COSTO, costo);
                        values.put(DBSqlite.TableProd.COLUMN_STOCK, stock);
                        values.put(DBSqlite.TableProd.COLUMN_FOTO, foto);

                        long newRowId = dbWrite.insert(DBSqlite.TableProd.TABLE_PROD, null, values);

                        insertDataToFirebase(userEmail, codigo, nombre, marca, descripcion, precio, foto);
                        updateDataToBalance(userEmail, 0.0, Double.parseDouble(stock));

                    } catch (Exception ex) {
                        Log.d("ActivityAddProd", "Error al insertar los datos en SQLite: " + ex.getMessage());
                    }
                }

                Intent intent = new Intent(getApplicationContext(), ActivityProductos.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cargarObjetos() {
        txtCod = findViewById(R.id.txtCod);
        txtNom = findViewById(R.id.txtNom);
        txtMar = findViewById(R.id.txtMar);
        txtDesc = findViewById(R.id.txtDesc);
        txtPrec = findViewById(R.id.txtPrec);
        txtCost = findViewById(R.id.txtCost);
        txtStock = findViewById(R.id.txtStock);
        btnGuardarProd = findViewById(R.id.btnGuardarProd);
        imgProd = findViewById(R.id.btnImgProd);

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        dbRead = dbSqlite.getReadableDatabase();
        databaseFirebase = FirebaseFirestore.getInstance();

        storageProd = FirebaseStorage.getInstance();
        storageProdRef = storageProd.getReference();
    }

    private void insertDataToFirebase(String userEmail, String codigo, String nombre, String marca, String descripcion, String precio, String foto) {
        Map<String, Object> prodData = new HashMap<>();
        prodData.put("user", userEmail);
        prodData.put("codigo", codigo);
        prodData.put("nombre", nombre);
        prodData.put("marca", marca);
        prodData.put("descripcion", descripcion);
        prodData.put("precio", precio);
        prodData.put("foto", foto);

        databaseFirebase.collection(userEmail).document("tableProductos").collection(codigo).document(nombre).set(prodData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Exito//
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ActivityAddProd_insertDataToFirebase", "Error al insertar los datos en Firebase: " + e.getMessage());
            }
        });
    }

    private void insertDataToStorage(String foto, String userEmail, String codigo, String nombre) {
        Uri file = Uri.fromFile(new File(foto));
        StorageReference prodRef = storageProdRef.child(userEmail);
        StorageReference prodFotosRef = prodRef.child("fotosProd/" + file.getLastPathSegment());
        UploadTask uploadTask = prodFotosRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("ActivityAddProd_insertDataToStorage", "Error al insertar los datos en Storage: " + exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Exito//
            }
        });

        //Obtener enlace a la foto de Storage
        uploadTask = prodFotosRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return prodFotosRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                try {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        updateDataToFirebase(userEmail, codigo, nombre, downloadUri.toString());
                        updateDataToSqlite(codigo, downloadUri.toString());
                    }
                } catch (Exception ex) {
                    Log.d("ActivityAddProd_insertDataToStorage", "Error al extraer URL de Storage: " + ex.getMessage());
                }
            }
        });
    }

    private void updateDataToFirebase(String userEmail, String codigo, String nombre, String fotoUrl) {
        Map<String, Object> prodData = new HashMap<>();
        prodData.put("fotoUrl", fotoUrl);

        databaseFirebase.collection(userEmail).document("tableProductos").collection(codigo).document(nombre).set(prodData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Exito
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ActivityAddProd_updateDataToFirebase", "Error al actualizar los datos en Firebase: " + e.getMessage());
            }
        });
    }

    private void updateDataToSqlite(String codigo, String fotoUrl) {
        ContentValues values = new ContentValues();
        values.put(DBSqlite.TableProd.COLUMN_CODIGO, codigo);
        values.put(DBSqlite.TableProd.COLUMN_FOTO_URL, fotoUrl);

        String selection = DBSqlite.TableProd.COLUMN_CODIGO + " = ?";
        String[] selectionArgs = {codigo};

        int rowsUpdated = dbWrite.update(
                DBSqlite.TableProd.TABLE_PROD,
                values,
                selection,
                selectionArgs
        );
    }

    private void updateDataToBalance(String userEmail, Double ventas, Double stock) {
        String[] projection = {
                DBSqlite.TableBalance.COLUMN_USER,
                DBSqlite.TableBalance.COLUMN_PROD,
                DBSqlite.TableBalance.COLUMN_VENT
        };

        String selection = DBSqlite.TableBalance.COLUMN_USER + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = null;
        try {
            cursor = dbRead.query(
                    DBSqlite.TableBalance.TABLE_BALANCE,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor == null || !cursor.moveToFirst()) {
                // Si el cursor es null o no tiene filas, inserta un nuevo usuario
                ContentValues valuesUser = new ContentValues();
                valuesUser.put(DBSqlite.TableBalance.COLUMN_USER, userEmail);
                valuesUser.put(DBSqlite.TableBalance.COLUMN_VENT, 0.0);
                valuesUser.put(DBSqlite.TableBalance.COLUMN_PROD, stock);

                long newRowId = dbWrite.insert(DBSqlite.TableBalance.TABLE_BALANCE, null, valuesUser);

                if (newRowId != -1) {
                    Log.d("ActivityAddVent", "Usuario agregado correctamente con ID: " + newRowId);
                } else {
                    Log.d("ActivityAddVent", "Error al agregar nuevo usuario");
                }
                return;
            }

            // Si el cursor no es null y tiene filas, actualiza los datos existentes
            int stockIndex = cursor.getColumnIndex(DBSqlite.TableBalance.COLUMN_PROD);

            if (stockIndex != -1) {
                Double currentStock = cursor.getDouble(stockIndex);

                Double totStock = currentStock + stock;

                ContentValues values = new ContentValues();
                values.put(DBSqlite.TableBalance.COLUMN_PROD, totStock);

                String updateSelection = DBSqlite.TableBalance.COLUMN_USER + " = ?";
                String[] updateSelectionArgs = {userEmail};

                int rowsUpdated = dbWrite.update(DBSqlite.TableBalance.TABLE_BALANCE, values, updateSelection, updateSelectionArgs);

                if (rowsUpdated > 0) {
                    Log.d("ActivityAddProd", "Datos actualizados correctamente en el balance");
                } else {
                    Log.d("ActivityAddProd", "No se actualizaron los datos en el balance");
                }
            } else {
                Log.d("ActivityAddProd", "Columnas de ventas o compra no encontradas");
            }
        } catch (Exception e) {
            Log.d("ActivityAddProd", "Error al actualizar Balance: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}