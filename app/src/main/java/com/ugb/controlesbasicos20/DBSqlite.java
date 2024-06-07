package com.ugb.controlesbasicos20;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBSqlite extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "manage360";

    public static class TableUser implements BaseColumns {
        public static final String TABLE_USER = "Users";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_CORREO = "Correo";
        public static final String COLUMN_TYPE = "Tipo";
        public static final String COLUMN_FOTO = "Foto";
    }

    public static class TableBalance implements BaseColumns {
        public static final String TABLE_BALANCE = "Balance";
        public static final String COLUMN_USER = "User";
        public static final String COLUMN_VENT = "Total_Venta";
        public static final String COLUMN_PROD = "Total_Stock";
    }

    public static class TableProd implements BaseColumns {
        public static final String TABLE_PROD = "Productos";
        public static final String COLUMN_USER = "User";
        public static final String COLUMN_CODIGO = "Codigo";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_MARCA = "Marca";
        public static final String COLUMN_DESCRIPCION = "Descripcion";
        public static final String COLUMN_PRECIO = "Precio";
        public static final String COLUMN_COSTO = "Costo";
        public static final String COLUMN_STOCK = "Stock";
        public static final String COLUMN_FOTO = "Foto";
        public static final String COLUMN_FOTO_URL = "FotoURL";
    }

    public static class TableVent implements BaseColumns {
        public static final String TABLE_VENT = "Ventas";
        public static final String COLUMN_USER = "User";
        public static final String COLUMN_FECHA = "Fecha";
        public static final String COLUMN_FOTO_PROD = "Foto_Producto";
        public static final String COLUMN_FOTO_Url = "Foto_Url";
        public static final String COLUMN_ID_PROD = "ID_Producto";
        public static final String COLUMN_NOMBRE_PROD = "Nombre_Producto";
        public static final String COLUMN_MARCA_PROD = "Marca_Producto";
        public static final String COLUMN_CANTIDAD = "Cantidad";
        public static final String COLUMN_PRECIO_UNITARIO = "Precio_Unitario";
        public static final String COLUMN_CLIENTE = "Cliente";
        public static final String COLUMN_TOTAL_VENTA = "Total_Venta";
        public static final String COLUMN_GANANCIA = "Ganancia";
        public static final String COLUMN_LONGITUD = "GPS_LONGITUD";
        public static final String COLUMN_LATITUD = "GPS_LATITUD";
    }

    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + TableUser.TABLE_USER + " (" +
                    TableUser._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableUser.COLUMN_NOMBRE + " TEXT," +
                    TableUser.COLUMN_CORREO + " TEXT," +
                    TableUser.COLUMN_TYPE + " TEXT," +
                    TableUser.COLUMN_FOTO + " TEXT)";
    private static final String SQL_CREATE_TABLE_BALANCE =
            "CREATE TABLE " + TableBalance.TABLE_BALANCE + " (" +
                    TableBalance._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableBalance.COLUMN_USER + " TEXT," +
                    TableBalance.COLUMN_VENT + " TEXT," +
                    TableBalance.COLUMN_PROD + " TEXT)";

    private static final String SQL_CREATE_TABLE_PROD =
            "CREATE TABLE " + TableProd.TABLE_PROD + " (" +
                    TableProd._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableProd.COLUMN_USER + " TEXT," +
                    TableProd.COLUMN_CODIGO + " TEXT," +
                    TableProd.COLUMN_NOMBRE + " TEXT," +
                    TableProd.COLUMN_MARCA + " TEXT," +
                    TableProd.COLUMN_DESCRIPCION + " TEXT," +
                    TableProd.COLUMN_PRECIO + " TEXT," +
                    TableProd.COLUMN_COSTO + " TEXT," +
                    TableProd.COLUMN_STOCK + " TEXT," +
                    TableProd.COLUMN_FOTO + " TEXT," +
                    TableProd.COLUMN_FOTO_URL + " TEXT)";

    private static final String SQL_CREATE_TABLE_VENT =
            "CREATE TABLE " + TableVent.TABLE_VENT + " (" +
                    TableVent._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableVent.COLUMN_USER + " TEXT," +
                    TableVent.COLUMN_FECHA + " TEXT," +
                    TableVent.COLUMN_FOTO_PROD + " TEXT," +
                    TableVent.COLUMN_FOTO_Url + " TEXT," +
                    TableVent.COLUMN_ID_PROD + " TEXT," +
                    TableVent.COLUMN_NOMBRE_PROD + " TEXT," +
                    TableVent.COLUMN_MARCA_PROD + " TEXT," +
                    TableVent.COLUMN_CANTIDAD + " TEXT," +
                    TableVent.COLUMN_PRECIO_UNITARIO + " TEXT," +
                    TableVent.COLUMN_CLIENTE + " TEXT," +
                    TableVent.COLUMN_GANANCIA + " TEXT," +
                    TableVent.COLUMN_LONGITUD + " TEXT," +
                    TableVent.COLUMN_LATITUD + " TEXT," +
                    TableVent.COLUMN_TOTAL_VENTA + " TEXT)";

    private static final String SQL_DELETE_ENTRIES_USER =
            "DROP TABLE IF EXISTS " + TableUser.TABLE_USER;
    private static final String SQL_DELETE_ENTRIES_PROD =
            "DROP TABLE IF EXISTS " + TableProd.TABLE_PROD;
    private static final String SQL_DELETE_ENTRIES_VENT =
            "DROP TABLE IF EXISTS " + TableVent.TABLE_VENT;
    private static final String SQL_DELETE_ENTRIES_BALANCE =
            "DROP TABLE IF EXISTS " + TableBalance.TABLE_BALANCE;

    public DBSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USER);
        db.execSQL(SQL_CREATE_TABLE_BALANCE);
        db.execSQL(SQL_CREATE_TABLE_PROD);
        db.execSQL(SQL_CREATE_TABLE_VENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_USER);
        db.execSQL(SQL_DELETE_ENTRIES_BALANCE);
        db.execSQL(SQL_DELETE_ENTRIES_PROD);
        db.execSQL(SQL_DELETE_ENTRIES_VENT);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int getCountVent() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TableVent.TABLE_VENT;
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }

    public int getCountProd() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TableProd.TABLE_PROD;
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }
}
