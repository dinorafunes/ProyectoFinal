package com.ugb.controlesbasicos20;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MensajeViewHolder> {

    private List<ClassMensaje> classMensajes;
    private String userID;
    private String otherUserID;

    public AdapterChat(List<ClassMensaje> classMensajes, String userID, String otherUserID) {
        this.classMensajes = classMensajes;
        this.userID = userID;
        this.otherUserID = otherUserID;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        ClassMensaje classMensaje = classMensajes.get(position);

        if (classMensaje.getEmisor() != null && classMensaje.getEmisor().equals(userID)) {
            // Si el mensaje es del usuario actual, establecer la gravedad a la derecha
            holder.layout1.setGravity(Gravity.RIGHT);
            holder.layout2.setVisibility(View.GONE); // Ocultar el contenedor del mensaje del receptor
            holder.textViewMensajeEmisor.setText(classMensaje.getContenido());
        } else if (classMensaje.getEmisor() != null && classMensaje.getEmisor().equals(otherUserID)) {
            // Si el mensaje es del otro usuario, establecer la gravedad a la izquierda
            holder.layout1.setGravity(Gravity.LEFT);
            holder.layout2.setVisibility(View.VISIBLE); // Mostrar el contenedor del mensaje del receptor
            holder.layout1.setVisibility(View.GONE);
            holder.textViewMensajeReceptor.setText(classMensaje.getContenido());
        }
    }

    @Override
    public int getItemCount() {
        return classMensajes.size();
    }

    public void addMessage(String mensaje, String emisor) {
        classMensajes.add(new ClassMensaje(mensaje, emisor));
        notifyDataSetChanged();
    }

    public void clearMessages() {
        classMensajes.clear();
        notifyDataSetChanged();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout1, layout2;
        private TextView textViewMensajeEmisor, textViewMensajeReceptor;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            layout1 = itemView.findViewById(R.id.layout1);
            layout2 = itemView.findViewById(R.id.layout2);
            textViewMensajeEmisor = itemView.findViewById(R.id.textViewMensajeEmisor);
            textViewMensajeReceptor = itemView.findViewById(R.id.textViewMensajeReceptor);
        }
    }
}