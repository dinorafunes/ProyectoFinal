package com.ugb.controlesbasicos20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdapterVentas extends ArrayAdapter<ClassVenta> {

    public AdapterVentas(Context context, List<ClassVenta> ventas) {
        super(context, 0, ventas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_ventas, parent, false);
        }

        ClassVenta venta = getItem(position);

        TextView tvCodigo = convertView.findViewById(R.id.lblCod);
        TextView tvCliente = convertView.findViewById(R.id.lblCliente);
        TextView tvNom = convertView.findViewById(R.id.lblNom);
        TextView tvTot = convertView.findViewById(R.id.lblTot);
        ImageView imgVent = convertView.findViewById(R.id.imgVent);

        if (venta != null) {
            tvCodigo.setText(venta.getCodigo());
            tvCliente.setText(venta.getCliente());
            tvNom.setText(venta.getNombre());
            tvTot.setText("$ " + venta.getTotalVent());

            String urlCompletaFoto = venta.getFoto();

            if (urlCompletaFoto != null && !urlCompletaFoto.isEmpty()) {
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                if (imagenBitmap != null) {
                    imgVent.setImageBitmap(imagenBitmap);
                } else {
                    imgVent.setImageResource(R.drawable.ic_launcher_foreground);
                }
            } else {
                imgVent.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }

        return convertView;
    }
}
