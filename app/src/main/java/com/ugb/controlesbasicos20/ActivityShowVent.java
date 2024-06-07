package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityShowVent extends AppCompatActivity {

    TextView lblFec, lblClient, lblCod, lblNom, lblMar, lblPre, lblCant, lblTot, lblGan, lblGpsUbi;
    Button btnEliminar;
    ImageView imgFotoVent;
    FloatingActionButton fabHome, fabInv, fabFin;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite, dbRead;
    FirebaseFirestore databaseFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vent);

        imgFotoVent = findViewById(R.id.imgProd);
        lblFec = findViewById(R.id.lblFec);
        lblClient = findViewById(R.id.lblClient);
        lblCod = findViewById(R.id.lblCod);
        lblNom = findViewById(R.id.lblNom);
        lblCant = findViewById(R.id.lblCant);
        lblGan = findViewById(R.id.lblGan);
        lblMar = findViewById(R.id.lblMar);
        lblPre = findViewById(R.id.lblPre);
        lblTot = findViewById(R.id.lblTot);
        lblGpsUbi = findViewById(R.id.lblGpsUbi);
        btnEliminar = findViewById(R.id.btnEliminar);

        fabHome = findViewById(R.id.fabHome);
        fabInv = findViewById(R.id.fabInvent);
        fabFin = findViewById(R.id.fabFinance);

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        dbRead = dbSqlite.getReadableDatabase();
        databaseFirebase = FirebaseFirestore.getInstance();

        ClassVenta venta = (ClassVenta) getIntent().getSerializableExtra("venta");

        if (venta != null) {
            lblCod.setText("Codigo: " + venta.getCodigo());
            lblNom.setText("Nombre: " + venta.getNombre());
            lblMar.setText("Marca: " + venta.getMarca());
            lblPre.setText("Precio: $" + venta.getPrecio().toString());
            lblTot.setText("Total venta: $" + venta.getTotalVent().toString());
            lblGan.setText("Ganancia: $" + venta.getGanancia().toString());
            lblCant.setText("Cantidad: " + venta.getCantidad().toString());
            lblClient.setText("Cliente: " + venta.getCliente());
            lblFec.setText("Fecha: " + venta.getFecha());
            lblGpsUbi.setText("Ubicacion: " + venta.getLatitud() + " // " + venta.getLongitud());

            String urlCompletaFoto = venta.getFoto();
            Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
            imgFotoVent.setImageBitmap(imagenBitmap);
        }

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                startActivity(intent);
                finish();
            }
        });

        fabInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityProductos.class);
                startActivity(intent);
                finish();
            }
        });

        fabFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityVentas.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta venta?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarVenta();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarVenta() {
        ClassVenta venta = (ClassVenta) getIntent().getSerializableExtra("venta");

        if (venta != null) {
            String selection = DBSqlite.TableVent._ID + " = ?";
            String[] selectionArgs = {venta.getID()};

            // Eliminar producto de SQLite
            int deletedRows = dbWrite.delete(DBSqlite.TableVent.TABLE_VENT, selection, selectionArgs);

            // Actualizar Balance
            String[] projection = {
                    DBSqlite.TableBalance.COLUMN_USER,
                    DBSqlite.TableBalance.COLUMN_PROD,
                    DBSqlite.TableBalance.COLUMN_VENT
            };

            String userEmail = ActivityMain.userEmailLogin;
            String selection1 = DBSqlite.TableBalance.COLUMN_USER + " = ?";
            String[] selectionArgs1 = {userEmail};

            Cursor cursor = null;
            try {
                cursor = dbRead.query(
                        DBSqlite.TableBalance.TABLE_BALANCE,
                        projection,
                        selection1,
                        selectionArgs1,
                        null,
                        null,
                        null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    int ventIndex = cursor.getColumnIndex(DBSqlite.TableBalance.COLUMN_VENT);

                    if (ventIndex != -1) {
                        Double currentVent = cursor.getDouble(ventIndex);
                        Double ventas = Double.valueOf(venta.getGanancia().toString());
                        Double totVent = currentVent - ventas;

                        ContentValues values = new ContentValues();
                        values.put(DBSqlite.TableBalance.COLUMN_VENT, totVent);

                        String updateSelection = DBSqlite.TableBalance.COLUMN_USER + " = ?";
                        String[] updateSelectionArgs = {userEmail};

                        int rowsUpdated = dbWrite.update(DBSqlite.TableBalance.TABLE_BALANCE, values, updateSelection, updateSelectionArgs);

                        if (rowsUpdated > 0) {
                            Log.d("ActivityShowVent", "Datos actualizados correctamente en el balance");
                        } else {
                            Log.d("ActivityShowVent", "No se actualizaron los datos en el balance");
                        }
                    } else {
                        Log.d("ActivityShowVent", "Columnas de ventas o stock no encontradas");
                    }
                }
            } catch (Exception ex) {
                Log.e("ActivityShowVent", "Error al actualizar el balance", ex);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            // Eliminar venta de Firebase
            databaseFirebase.collection(userEmail)
                    .document("tableVentas")
                    .collection(venta.getID())
                    .document(venta.getCodigo())
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("ActivityShowVent", "Venta eliminada correctamente de Firebase"))
                    .addOnFailureListener(e -> Log.e("ActivityShowVent", "Error al eliminar venta de Firebase", e));

            if (deletedRows > 0) {
                Intent intent = new Intent(getApplicationContext(), ActivityVentas.class);
                startActivity(intent);
                finish();
            } else {
                Log.e("ActivityShowVent", "Error al eliminar la venta de SQLite");
            }
        }
    }
}
