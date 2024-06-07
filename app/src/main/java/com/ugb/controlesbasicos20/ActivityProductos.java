package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ActivityProductos extends AppCompatActivity {

    EditText txtBuscarProd;
    ListView listProd;
    Button btnAddProd;
    FloatingActionButton fabHome, fabInv, fabFin;
    DBSqlite dbSqlite;
    SQLiteDatabase dbRead;
    AdapterProductos adapter;
    List<ClassProductos> productos;
    List<ClassProductos> productosFiltrados;
    ActivityMain activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        activityMain = new ActivityMain();
        String userEmailLog = activityMain.userEmailLogin;

        cargarSqlite();
        cargarObjetos();
        loadDataFromSqlite(userEmailLog);

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

        btnAddProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityAddProd.class);
                startActivity(intent);
            }
        });

        listProd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassProductos productoSeleccionado = productosFiltrados.get(position);

                Intent intent = new Intent(getApplicationContext(), ActivityShowProd.class);
                intent.putExtra("producto", productoSeleccionado);
                startActivity(intent);
            }
        });

        txtBuscarProd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita implementar
            }
        });
    }

    private void filtrarProductos(String query) {
        productosFiltrados.clear();
        if (query.isEmpty()) {
            productosFiltrados.addAll(productos);
        } else {
            for (ClassProductos producto : productos) {
                if (producto.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                        producto.getMarca().toLowerCase().contains(query.toLowerCase()) ||
                        producto.getCodigo().toLowerCase().contains(query.toLowerCase())) {
                    productosFiltrados.add(producto);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void cargarObjetos() {
        txtBuscarProd = findViewById(R.id.txtBuscarProd);
        listProd = findViewById(R.id.listProd);
        btnAddProd = findViewById(R.id.btnAddProd);
        fabHome = findViewById(R.id.fabHome);
        fabInv = findViewById(R.id.fabInvent);
        fabFin = findViewById(R.id.fabFinance);
    }

    private void cargarSqlite() {
        dbSqlite = new DBSqlite(this);
        dbRead = dbSqlite.getReadableDatabase();
    }

    private void loadDataFromSqlite(String userEmail) {
        productos = new ArrayList<>();

        String[] projection = {
                DBSqlite.TableProd.COLUMN_USER,
                DBSqlite.TableProd.COLUMN_CODIGO,
                DBSqlite.TableProd.COLUMN_NOMBRE,
                DBSqlite.TableProd.COLUMN_MARCA,
                DBSqlite.TableProd.COLUMN_DESCRIPCION,
                DBSqlite.TableProd.COLUMN_PRECIO,
                DBSqlite.TableProd.COLUMN_COSTO,
                DBSqlite.TableProd.COLUMN_STOCK,
                DBSqlite.TableProd.COLUMN_FOTO,
                DBSqlite.TableProd.COLUMN_FOTO_URL
        };

        String selection = DBSqlite.TableProd.COLUMN_USER + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = null;
        try {
            cursor = dbRead.query(
                    DBSqlite.TableProd.TABLE_PROD,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int userIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_USER);
                    int codigoIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_CODIGO);
                    int nombreIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_NOMBRE);
                    int marcaIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_MARCA);
                    int descripcionIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_DESCRIPCION);
                    int precioIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_PRECIO);
                    int costoIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_COSTO);
                    int stockIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_STOCK);
                    int fotoIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_FOTO);
                    int fotoUrlIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_FOTO_URL);

                    if (codigoIndex != -1 && nombreIndex != -1 && precioIndex != -1) {
                        String user = cursor.getString(userIndex);
                        String codigo = cursor.getString(codigoIndex);
                        String nombre = cursor.getString(nombreIndex);
                        String marca = cursor.getString(marcaIndex);
                        String descripcion = cursor.getString(descripcionIndex);
                        Double precio = Double.valueOf(cursor.getString(precioIndex));
                        Double costo = Double.valueOf(cursor.getString(costoIndex));
                        int stock = Integer.parseInt(cursor.getString(stockIndex));
                        String foto = cursor.getString(fotoIndex);
                        String fotoUrl = cursor.getString(fotoUrlIndex);

                        productos.add(new ClassProductos(user, codigo, nombre, marca, descripcion, precio, costo, stock, foto, fotoUrl));
                    } else {
                        Toast.makeText(this, "No se pudieron mostrar los productos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (productos.isEmpty()) {
                Toast.makeText(this, "Aún no hay productos. ¡Agrega uno!", Toast.LENGTH_SHORT).show();
            } else {
                productosFiltrados = new ArrayList<>(productos); // Inicializa la lista de filtrados
                adapter = new AdapterProductos(this, productosFiltrados);
                listProd.setAdapter(adapter);
            }
        } catch (Exception e) {
            Log.d("ActivityProductos", "Error al extraer de SQLite: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
