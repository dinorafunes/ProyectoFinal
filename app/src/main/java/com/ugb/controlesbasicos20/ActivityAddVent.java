package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAddVent extends AppCompatActivity implements ClassLocationCallback {

    ImageView imgFoto;
    TextView lblCodigo, lblNombre, lblMarca, lblPrecio, lblGpsUbi;
    EditText txtFecha, txtCantidad, txtCliente;
    String urlCompletaFoto, fotoURL, IDVent;
    Double gananciaVent;
    Button btnVender;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite, dbRead;
    FirebaseFirestore databaseFirebase;
    ActivityMain activityMain;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vent);

        cargarObjetos();
        mostrarDatos();

        activityMain = new ActivityMain();

        ClassGps classGps = new ClassGps(ActivityAddVent.this, ActivityAddVent.this);
        classGps.registerLauncher();
        getLocationAndUpdateTextView();

        btnVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = activityMain.userEmailLogin;

                classGps.startGpsLocation();
            }
        });
    }

    private void cargarObjetos() {
        imgFoto = findViewById(R.id.imgProdVent);
        lblCodigo = findViewById(R.id.lblIdProd);
        lblNombre = findViewById(R.id.lblNombre);
        lblMarca = findViewById(R.id.lblMarca);
        lblPrecio = findViewById(R.id.lblPrecio);
        lblGpsUbi = findViewById(R.id.lblGpsUbi);
        txtFecha = findViewById(R.id.txtFec);
        txtCantidad = findViewById(R.id.txtCant);
        txtCliente = findViewById(R.id.txtClient);
        btnVender = findViewById(R.id.btnVender);

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        dbRead = dbSqlite.getReadableDatabase();
        databaseFirebase = FirebaseFirestore.getInstance();
    }

    private void mostrarDatos() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("producto")) {
            ClassProductos producto = (ClassProductos) intent.getSerializableExtra("producto");

            if (producto != null) {
                lblCodigo.setText(producto.getCodigo());
                lblNombre.setText(producto.getNombre());
                lblMarca.setText(producto.getMarca());
                lblPrecio.setText(producto.getPrecio().toString());

                urlCompletaFoto = producto.getFoto();
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                imgFoto.setImageBitmap(imagenBitmap);

                fotoURL = producto.getFotoUrl();
                gananciaVent = producto.getPrecio() - producto.getCosto();
            }
        }
    }

    private void getLocationAndUpdateTextView() {
        ClassGps classGps = new ClassGps(ActivityAddVent.this, new ClassLocationCallback() {
            @Override
            public void onLocationResult(double latitude, double longitude) {
                ActivityAddVent.this.latitude = latitude;
                ActivityAddVent.this.longitude = longitude;

                lblGpsUbi.setText(String.format("%s // %s", latitude, longitude));
            }
        });
        classGps.startGpsLocation();
    }

    @Override
    public void onLocationResult(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

        String userEmail = activityMain.userEmailLogin;
        insertDataToSqlite(userEmail);
    }

    private void insertDataToSqlite(String userEmail) {
        try {
            String codigo = lblCodigo.getText().toString();
            String nombre = lblNombre.getText().toString();
            String marca = lblMarca.getText().toString();
            double precio = Double.parseDouble(lblPrecio.getText().toString());
            String imgProd = urlCompletaFoto;
            String fecha = txtFecha.getText().toString();
            double cantidad = Double.parseDouble(txtCantidad.getText().toString());
            String cliente = txtCliente.getText().toString();
            String fotoUrl = fotoURL;
            double ganancia = gananciaVent * cantidad;
            double total = precio * cantidad;

            if (fecha == null || cantidad == -1 || cliente == null) {
                Toast.makeText(ActivityAddVent.this, "Ingrese datos en los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put(DBSqlite.TableVent.COLUMN_USER, userEmail);
            values.put(DBSqlite.TableVent.COLUMN_ID_PROD, codigo);
            values.put(DBSqlite.TableVent.COLUMN_NOMBRE_PROD, nombre);
            values.put(DBSqlite.TableVent.COLUMN_MARCA_PROD, marca);
            values.put(DBSqlite.TableVent.COLUMN_PRECIO_UNITARIO, String.valueOf(precio));
            values.put(DBSqlite.TableVent.COLUMN_FOTO_PROD, imgProd);
            values.put(DBSqlite.TableVent.COLUMN_FOTO_Url, fotoUrl);
            values.put(DBSqlite.TableVent.COLUMN_FECHA, fecha);
            values.put(DBSqlite.TableVent.COLUMN_CANTIDAD, String.valueOf(cantidad));
            values.put(DBSqlite.TableVent.COLUMN_CLIENTE, cliente);
            values.put(DBSqlite.TableVent.COLUMN_TOTAL_VENTA, String.valueOf(total));
            values.put(DBSqlite.TableVent.COLUMN_GANANCIA, ganancia);
            values.put(DBSqlite.TableVent.COLUMN_LATITUD, String.valueOf(latitude));  // Agregar latitud
            values.put(DBSqlite.TableVent.COLUMN_LONGITUD, String.valueOf(longitude));  // Agregar longitud

            long newRowId = dbWrite.insert(DBSqlite.TableVent.TABLE_VENT, null, values);

            cargarIdVenta(userEmail);

            if (newRowId != -1) {
                insertDataToFirebase(IDVent, userEmail, codigo, nombre, marca, String.valueOf(precio), imgProd, fotoUrl, fecha, String.valueOf(cantidad), cliente, String.valueOf(total), ganancia);
                updateDataToBalance(userEmail, ganancia, 0.0);
                startActivity(new Intent(getApplicationContext(), ActivityVentas.class));
                finish();
            } else {
                Log.d("ActivityAddVent", "No se pudieron insertar los datos");
            }
        } catch (Exception ex) {
            Log.d("ActivityAddVent", "Error al insertar datos: " + ex.getMessage());
        }
    }

    private void insertDataToFirebase(String IDVent, String userEmail, String codigo, String nombre, String marca, String precio, String foto, String fotoUrl, String fecha, String cantidad, String cliente, String total, Double ganancia) {
        Map<String, Object> prodData = new HashMap<>();
        prodData.put("ID", IDVent);
        prodData.put("user", userEmail);
        prodData.put("codigo", codigo);
        prodData.put("nombre", nombre);
        prodData.put("marca", marca);
        prodData.put("fecha", fecha);
        prodData.put("precio", precio);
        prodData.put("foto", foto);
        prodData.put("fotoUrl", fotoUrl);
        prodData.put("cantidad", cantidad);
        prodData.put("cliente", cliente);
        prodData.put("total", total);
        prodData.put("ganancia", ganancia);
        prodData.put("latitud", latitude);  // Agregar latitud
        prodData.put("longitud", longitude);  // Agregar longitud

        databaseFirebase.collection(userEmail).document("tableVentas").collection(IDVent).document(codigo).set(prodData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Exito
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ActivityAddVent", "Error al insertar los datos en Firebase: " + e.getMessage());
                    }
                });
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
                valuesUser.put(DBSqlite.TableBalance.COLUMN_VENT, ventas);

                long newRowId = dbWrite.insert(DBSqlite.TableBalance.TABLE_BALANCE, null, valuesUser);

                if (newRowId != -1) {
                    Log.d("ActivityAddVent", "Usuario agregado correctamente con ID: " + newRowId);
                } else {
                    Log.d("ActivityAddVent", "Error al agregar nuevo usuario");
                }
                return;
            }

            // Si el cursor no es null y tiene filas, actualiza los datos existentes
            int ventIndex = cursor.getColumnIndex(DBSqlite.TableBalance.COLUMN_VENT);

            if (ventIndex != -1) {
                Double currentVent = cursor.getDouble(ventIndex);

                Double totVent = currentVent + ventas;

                ContentValues values = new ContentValues();
                values.put(DBSqlite.TableBalance.COLUMN_VENT, totVent);

                String updateSelection = DBSqlite.TableBalance.COLUMN_USER + " = ?";
                String[] updateSelectionArgs = {userEmail};

                int rowsUpdated = dbWrite.update(DBSqlite.TableBalance.TABLE_BALANCE, values, updateSelection, updateSelectionArgs);

                if (rowsUpdated > 0) {
                    Log.d("ActivityAddVent", "Datos actualizados correctamente en el balance");
                } else {
                    Log.d("ActivityAddVent", "No se actualizaron los datos en el balance");
                }
            } else {
                Log.d("ActivityAddVent", "Columnas de ventas o compra no encontradas");
            }
        } catch (Exception e) {
            Log.d("ActivityAddVent", "Error al actualizar Balance: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void cargarIdVenta(String userEmail) {
        List<ClassVenta> ventas = new ArrayList<>();

        String[] projection = {
                DBSqlite.TableVent._ID,
                DBSqlite.TableVent.COLUMN_USER
        };

        String selection = DBSqlite.TableVent.COLUMN_USER + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = null;
        try {
            cursor = dbRead.query(
                    DBSqlite.TableVent.TABLE_VENT,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int correoIndex = cursor.getColumnIndex(DBSqlite.TableUser.COLUMN_CORREO);
                int IdIndex = cursor.getColumnIndex(DBSqlite.TableUser._ID);

                IDVent = cursor.getString(IdIndex);
                String correo = cursor.getString(correoIndex);

            } else {
                Log.d("ActivityAddVent", "Error al extraer ID: ");
            }
        } catch (Exception e) {
            Log.d("ActivityAddVent", "Error al extraer ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
