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

public class AdapterProductos extends ArrayAdapter<ClassProductos> {

    public AdapterProductos(Context context, List<ClassProductos> productos) {
        super(context, 0, productos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_productos, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvCodigo = convertView.findViewById(R.id.lblCod);
            viewHolder.tvNombre = convertView.findViewById(R.id.lblNom);
            viewHolder.tvMarca = convertView.findViewById(R.id.lblMar);
            viewHolder.tvPrecio = convertView.findViewById(R.id.lblPrec);
            viewHolder.imgProd = convertView.findViewById(R.id.imgProd);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ClassProductos producto = getItem(position);

        if (producto != null) {
            viewHolder.tvCodigo.setText(producto.getCodigo());
            viewHolder.tvNombre.setText(producto.getNombre());
            viewHolder.tvMarca.setText(producto.getMarca());
            viewHolder.tvPrecio.setText("$ " + producto.getPrecio());

            String urlCompletaFoto = producto.getFoto();

            if (urlCompletaFoto != null && !urlCompletaFoto.isEmpty()) {
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                if (imagenBitmap != null) {
                    viewHolder.imgProd.setImageBitmap(imagenBitmap);
                } else {
                    viewHolder.imgProd.setImageResource(R.drawable.ic_launcher_foreground);
                }
            } else {
                viewHolder.imgProd.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvCodigo;
        TextView tvNombre;
        TextView tvMarca;
        TextView tvPrecio;
        ImageView imgProd;
    }
}