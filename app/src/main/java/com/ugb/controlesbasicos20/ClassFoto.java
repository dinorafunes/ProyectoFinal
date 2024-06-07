package com.ugb.controlesbasicos20;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClassFoto {
    private Activity activity;
    public String urlCompletaFoto;

    public ClassFoto(Activity activity) {
        this.activity = activity;
    }

    public void tomarFoto() {
        Intent tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoUser = null;
        try {
            fotoUser = crearImagen();
            if (fotoUser != null) {
                Uri urifotoAmigo = FileProvider.getUriForFile(activity,
                        "com.ugb.controlesbasicos20.fileprovider", fotoUser);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, urifotoAmigo);
                activity.startActivityForResult(tomarFotoIntent, 1);
            } else {
                Toast.makeText(activity, "No se pudo iniciar la camara", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("ClassFoto", "Error al iniciar la camara: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private File crearImagen() throws Exception {
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "imagen_" + fechaHoraMs + "_";
        File dirAlmacenamiento = activity.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (dirAlmacenamiento != null && !dirAlmacenamiento.exists()) {
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaFoto = image.getAbsolutePath();

        return image;
    }
}
