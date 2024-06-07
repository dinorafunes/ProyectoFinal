package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityChat extends AppCompatActivity {

    private static final String KEY_CONTENT = "contenido";
    private static final String KEY_SENDER = "emisor";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String CHATS_REF = "chats";

    private RecyclerView recyclerViewMensajes;
    private EditText editTextMensaje;
    private Button btnEnviar;
    private DatabaseReference databaseRef;
    private String userID = "user";
    private String otherUserID = "soporte";
    private AdapterChat adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewMensajes = findViewById(R.id.recyclerViewMensajes);
        editTextMensaje = findViewById(R.id.editTextMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMensajes.setLayoutManager(layoutManager);

        adapter = new AdapterChat(new ArrayList<>(), userID, otherUserID);
        recyclerViewMensajes.setAdapter(adapter);

        databaseRef = FirebaseDatabase.getInstance().getReference(CHATS_REF);

        btnEnviar.setOnClickListener(v -> sendMessage());

        loadMessages();
    }

    private void sendMessage() {
        String mensaje = editTextMensaje.getText().toString();

        if (!mensaje.isEmpty()) {
            String messageId = databaseRef.push().getKey();
            String timestamp = String.valueOf(System.currentTimeMillis());

            HashMap<String, Object> mensajeMap = new HashMap<>();
            mensajeMap.put(KEY_CONTENT, mensaje);
            mensajeMap.put(KEY_SENDER, userID);
            mensajeMap.put(KEY_TIMESTAMP, timestamp);

            databaseRef.child(messageId).setValue(mensajeMap);

            editTextMensaje.setText("");
        } else {
            Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        Query query = databaseRef.orderByChild(KEY_TIMESTAMP);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearMessages();

                for (DataSnapshot mensajeSnapshot : dataSnapshot.getChildren()) {
                    String contenido = mensajeSnapshot.child(KEY_CONTENT).getValue(String.class);
                    String emisor = mensajeSnapshot.child(KEY_SENDER).getValue(String.class);

                    adapter.addMessage(contenido, emisor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ActivityChat", "Error al leer mensajes de la base de datos", databaseError.toException());
            }
        });
    }
}
