package com.ugb.controlesbasicos20;

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
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ActivityMain extends AppCompatActivity {
    TabHost tbhMain;
    Button btnAbrirProductos, btnAbrirVentas, btnChatSupport;
    TextView lblNameUser, lblGanancia, lblVentas, lblProductos, lblProdStock, lblGananciaM, lblVentasM, lblProductosM, lblProdStockM;
    ImageView imgFotoUser;
    LinearLayout btnActUser;
    ClassVerifyNet classVerifyNet;
    ClassLoadBalance classGanancia;
    FirebaseAuth auth;
    FirebaseUser userEmailAuth;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    DBSqlite dbSqlite;
    SQLiteDatabase dbRead;
    FirebaseFirestore databaseFirebase;
    FirebaseStorage storageFirebase;
    StorageReference storageRef;
    public static String userEmailLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cargarObjetos();
        cargarGoogleSignIn();
        cargarSqlite();

        userLogin();

        btnActUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityUser.class);
                startActivity(intent);
                finish();
            }
        });

        btnAbrirProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityProductos.class);
                startActivity(intent);
            }
        });

        btnAbrirVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityVentas.class);
                startActivity(intent);
            }
        });

        btnChatSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityChat.class);
                startActivity(intent);
            }
        });
    }

    private void cargarGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Add this line to request an ID token.
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
    }

    private void cargarObjetos() {
        tbhMain = findViewById(R.id.tbhMain);
        tbhMain.setup();
        tbhMain.addTab(tbhMain.newTabSpec("INV").setContent(R.id.tabInventario).setIndicator("", getResources().getDrawable(R.drawable.ic_manage360_box1)));
        tbhMain.addTab(tbhMain.newTabSpec("INI").setContent(R.id.tabInicio).setIndicator("", getResources().getDrawable(R.drawable.ic_manage360_home1)));
        tbhMain.addTab(tbhMain.newTabSpec("FIN").setContent(R.id.tabFinanzas).setIndicator("", getResources().getDrawable(R.drawable.ic_manage360_wallet1)));
        tbhMain.setCurrentTab(1);

        classVerifyNet = new ClassVerifyNet(ActivityMain.this);
        classVerifyNet.isOnlineNet();

        btnAbrirProductos = findViewById(R.id.btnVistaProductos);
        btnAbrirVentas = findViewById(R.id.btnVistaFinanzas);
        btnChatSupport = findViewById(R.id.btnChatSupport);

        lblNameUser = findViewById(R.id.lblNameUser);

        lblGanancia = findViewById(R.id.lblCountGanancia);
        lblVentas = findViewById(R.id.lblCountVent);
        lblProductos = findViewById(R.id.lblCountProd);
        lblProdStock = findViewById(R.id.lblCountStock);

        lblGananciaM = findViewById(R.id.lblCountGananciaM);
        lblVentasM = findViewById(R.id.lblCountVentM);
        lblProductosM = findViewById(R.id.lblCountProdM);
        lblProdStockM = findViewById(R.id.lblCountStockM);

        imgFotoUser = findViewById(R.id.imgFotoUser);
        btnActUser = findViewById(R.id.btnActUser);

        auth = FirebaseAuth.getInstance();
        userEmailAuth = auth.getCurrentUser();
        databaseFirebase = FirebaseFirestore.getInstance();
        storageFirebase = FirebaseStorage.getInstance();
        storageRef = storageFirebase.getReference();
    }

    private void userLogin() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String userEmailGoogle = acct.getEmail();
            userEmailLogin = userEmailGoogle;
            showDataSqlite(userEmailGoogle);
            loadDataBalanceSqlite(userEmailGoogle);
        } else {
            if (userEmailAuth == null) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finish();
            } else {
                String userEmailCorreo = userEmailAuth.getEmail();
                userEmailLogin = userEmailCorreo;
                showDataSqlite(userEmailCorreo);
                loadDataBalanceSqlite(userEmailCorreo);
            }
        }
    }

    private void cargarSqlite() {
        dbSqlite = new DBSqlite(this);
        dbRead = dbSqlite.getReadableDatabase();
    }

    private void showDataSqlite(String userEmail) {
        String[] projection = {
                DBSqlite.TableUser.COLUMN_NOMBRE,
                DBSqlite.TableUser.COLUMN_CORREO,
                DBSqlite.TableUser.COLUMN_TYPE,
                DBSqlite.TableUser.COLUMN_FOTO
        };

        String selection = DBSqlite.TableUser.COLUMN_CORREO + " = ?";
        String[] selectionArgs = {userEmail};

        try (Cursor cursor = dbRead.query(
                DBSqlite.TableUser.TABLE_USER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int nombreIndex = cursor.getColumnIndex(DBSqlite.TableUser.COLUMN_NOMBRE);
                    int correoIndex = cursor.getColumnIndex(DBSqlite.TableUser.COLUMN_CORREO);
                    int typeIndex = cursor.getColumnIndex(DBSqlite.TableUser.COLUMN_TYPE);
                    int fotoIndex = cursor.getColumnIndex(DBSqlite.TableUser.COLUMN_FOTO);

                    String nombre = cursor.getString(nombreIndex);
                    String correo = cursor.getString(correoIndex);
                    String type = cursor.getString(typeIndex);
                    String foto = cursor.getString(fotoIndex);

                    ClassUser usuario = new ClassUser(foto, nombre, correo, type);
                    lblNameUser.setText(usuario.getNombre());

                    Bitmap imagenBitmap = BitmapFactory.decodeFile(usuario.getFoto());
                    imgFotoUser.setImageBitmap(imagenBitmap);
                }
            }
        } catch (Exception e) {
            Log.d("ActivityMain", "Error al extraer de SQLite: " + e.getMessage());
        }
    }

    private void loadDataBalanceSqlite(String userEmail) {
        int countVent = dbSqlite.getCountVent();
        lblVentas.setText("Ventas hechas: " + countVent);
        lblVentasM.setText("Ventas hechas: " + countVent);

        int countProd = dbSqlite.getCountProd();
        lblProductos.setText("Productos: " + countProd);
        lblProductosM.setText("Productos: " + countProd);

        classGanancia = new ClassLoadBalance(ActivityMain.this);

        classGanancia.getGananciaSqlite();
        classGanancia.getStockSqlite();

        if (classGanancia.userGanancia == null) {
            lblGanancia.setText("Ganancia Total: $0.0");
        }
        lblGanancia.setText("Ganancia Total: $" + classGanancia.userGanancia);
        lblGananciaM.setText("Ganancia Total: $" + classGanancia.userGanancia);

        if (classGanancia.userStock == null) {
            lblProdStock.setText("Productos en Stock: 0");
        }
        lblProdStock.setText("Productos en Stock: " + classGanancia.userStock);
        lblProdStockM.setText("Productos en Stock: " + classGanancia.userStock);
    }
}