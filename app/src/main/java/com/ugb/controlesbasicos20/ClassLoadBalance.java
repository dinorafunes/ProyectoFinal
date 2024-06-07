package com.ugb.controlesbasicos20;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ClassLoadBalance {
    Activity activity;
    List<ClassBalance> balance;
    ActivityMain activityMain;
    DBSqlite dbSqlite;
    SQLiteDatabase dbRead;
    public static String userGanancia;
    public static String userStock;

    public ClassLoadBalance(Activity activity) {
        this.activity = activity;
    }

    public void getGananciaSqlite() {
        balance = new ArrayList<>();
        String userEmail = activityMain.userEmailLogin;

        dbSqlite = new DBSqlite(activity);
        dbRead = dbSqlite.getReadableDatabase();

        String[] projectionBalance = {
                DBSqlite.TableBalance.COLUMN_USER,
                DBSqlite.TableBalance.COLUMN_VENT,
                DBSqlite.TableBalance.COLUMN_PROD
        };

        String selectionBal = DBSqlite.TableBalance.COLUMN_USER + " = ?";
        String[] selectionArgsBal = {userEmail};

        try (
                Cursor cursorBal = dbRead.query(
                        DBSqlite.TableBalance.TABLE_BALANCE,
                        projectionBalance,
                        selectionBal,
                        selectionArgsBal,
                        null,
                        null,
                        null
                )) {
            if (cursorBal != null) {
                while (cursorBal.moveToNext()) {
                    String user = cursorBal.getString(cursorBal.getColumnIndexOrThrow(DBSqlite.TableBalance.COLUMN_USER));
                    String venta = cursorBal.getString(cursorBal.getColumnIndexOrThrow(DBSqlite.TableBalance.COLUMN_VENT));

                    balance.add(new ClassBalance(user, null, venta));
                }
                if (!balance.isEmpty()) {
                    ClassBalance classBalance = balance.get(0);
                    userGanancia = classBalance.getVenta();
                } else {
                    Log.d("ClassLoadBalance", "Datos stock: " + userGanancia);
                }
            }
        } catch (Exception e) {
            Log.e("ClassLoadBalance", "error: " + e.getMessage(), e);
        }
    }

    public void getStockSqlite() {
        balance = new ArrayList<>();
        String userEmail = activityMain.userEmailLogin;

        dbSqlite = new DBSqlite(activity);
        dbRead = dbSqlite.getReadableDatabase();

        String[] projectionBalance = {
                DBSqlite.TableBalance.COLUMN_USER,
                DBSqlite.TableBalance.COLUMN_VENT,
                DBSqlite.TableBalance.COLUMN_PROD
        };

        String selectionBal = DBSqlite.TableBalance.COLUMN_USER + " = ?";
        String[] selectionArgsBal = {userEmail};

        try (
                Cursor cursorBal = dbRead.query(
                        DBSqlite.TableBalance.TABLE_BALANCE,
                        projectionBalance,
                        selectionBal,
                        selectionArgsBal,
                        null,
                        null,
                        null
                )) {
            if (cursorBal != null) {
                while (cursorBal.moveToNext()) {
                    String user = cursorBal.getString(cursorBal.getColumnIndexOrThrow(DBSqlite.TableBalance.COLUMN_USER));
                    String stock = cursorBal.getString(cursorBal.getColumnIndexOrThrow(DBSqlite.TableBalance.COLUMN_PROD));

                    balance.add(new ClassBalance(user, stock, null));
                }
                if (!balance.isEmpty()) {
                    ClassBalance classBalance = balance.get(0);
                    userStock = classBalance.getStock();
                } else {
                    Log.d("ClassLoadBalance", "Datos stock: " + userStock);
                }
            }
        } catch (Exception e) {
            Log.e("ClassLoadBalance", "error: " + e.getMessage(), e);
        }
    }
}
