package org.dev4u.hv.guia7;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import modelo.AdaptadorCategoria;
import modelo.AdaptadorProducto;
import modelo.Categoria;
import modelo.DB;
import modelo.Producto;

public class Secundaria extends AppCompatActivity {

    private DB db;
    private AdaptadorProducto adaptadorProducto;
    private ListView listView;
    private TextView lblId_Pro;
    private EditText txtNombre_Pro,txtDescripcion,txtIdCategoria;
    private Button btnGuardar,btnEliminar;
    //lista de datos (categoria)
    private ArrayList<Producto> lstProducto;
    //sirve para manejar la eliminacion
    private Producto producto_temp=null;
    Intent intent=getIntent();
    //String IdCategoria=intent.getStringExtra("parametro");

    public static String id_categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secundaria);

        lblId_Pro=(TextView) findViewById(R.id.lblId_pro_main);
        txtNombre_Pro           = (EditText) findViewById(R.id.txtProducto);
        txtDescripcion=(EditText)findViewById(R.id.txtDescripcion);
        txtIdCategoria=(EditText)findViewById(R.id.txtIdCat);
        btnGuardar              = (Button)   findViewById(R.id.btnGuardarProducto);
        btnEliminar             = (Button)   findViewById(R.id.btnEliminarProducto);
        listView                = (ListView) findViewById(R.id.lstProductos);

        txtIdCategoria.setText(id_categoria);

        db                      = new DB(this);
        lstProducto=db.getArrayProducto(db.getCursorProducto(id_categoria));
        if(lstProducto==null)//si no hay datos
            lstProducto = new ArrayList<>();
        adaptadorProducto      = new AdaptadorProducto(this,lstProducto);
        listView.setAdapter(adaptadorProducto);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seleccionar(lstProducto.get(position));
            }
        });
        limpiar();
    }

    private void guardar(){
        Producto categoria = new Producto(lblId_Pro.getText().toString(),txtNombre_Pro.getText().toString(),
                txtDescripcion.getText().toString(),txtIdCategoria.getText().toString());
        producto_temp=null;
        if(db.guardar_O_ActualizarProducto(categoria)){
            Toast.makeText(this,"Se guardo producto",Toast.LENGTH_SHORT).show();
            //TODO limpiar los que existen y agregar los nuevos
            lstProducto.clear();
            lstProducto.addAll(db.getArrayProducto(
                    db.getCursorProducto(id_categoria)
            ));

            adaptadorProducto.notifyDataSetChanged();
            limpiar();
        }else{
            Toast.makeText(this,"Ocurrio un error al guardar",Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar(){
        if(producto_temp!=null){
            db.borrarProducto(producto_temp.getId_producto());
            lstProducto.remove(producto_temp);
            adaptadorProducto.notifyDataSetChanged();
            producto_temp=null;
            Toast.makeText(this,"Se elimino producto",Toast.LENGTH_SHORT).show();
            limpiar();
        }
    }

    private void limpiar(){
        lblId_Pro.setText(null);
        txtNombre_Pro.setText(null);
        txtDescripcion.setText(null);
    }
    private void seleccionar(Producto categoria){
        producto_temp = categoria;
        lblId_Pro.setText(producto_temp.getId_producto());
        txtNombre_Pro.setText(producto_temp.getNombre());
        txtDescripcion.setText(producto_temp.getDescripcion());
    }
}
