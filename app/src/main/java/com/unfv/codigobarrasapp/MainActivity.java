package com.unfv.codigobarrasapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.unfv.codigobarrasapp.Config.AccesoBaseDatos;
import com.unfv.codigobarrasapp.model.CodigoBarras;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView codigo;
    private ZXingScannerView vistaEscaner;
    ListView listaCodigos;
    AccesoBaseDatos accesoBaseDatos;
    private ArrayAdapter<String> mAdapter;
    List<String> codigosBarra = new ArrayList<>();
    SimpleDateFormat dateFormat;
    // Yamil
    private Button leerQr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Yamil
        leerQr = findViewById(R.id.leerQr);
        listaCodigos=findViewById(R.id.lv_codigoBarras);
        inicializarFirebase();
        accesoBaseDatos=new AccesoBaseDatos(getApplicationContext());
        generarListadoInit();
        leerQr.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if (result.getContents() != null){
                codigo = (TextView) findViewById(R.id.edtCodigo);
                codigo.setText(result.getContents());
                actualizarListado();
            }
        }
    }
    public void escanearCodigo(View view){
        vistaEscaner = new ZXingScannerView(this);
        vistaEscaner.setResultHandler(new zxingScanner());
        setContentView(vistaEscaner);
        vistaEscaner.startCamera();
    }

    public void procesarCodigo(View view){
        CodigoBarras p = new CodigoBarras();
        dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        p.setdFecRegistro(dateFormat.format(new Date()));
        p.setUid(UUID.randomUUID().toString());
        p.setvCodigo(codigo.getText().toString());
        almacenarLocal(p);
        codigo = (TextView) findViewById(R.id.edtCodigo);
        codigo.setText("");
        Toast.makeText(this,"Procesado", Toast.LENGTH_LONG).show();
    }

    public void almacenarLocal(CodigoBarras codigoBarras){
        //databaseReference.child("CodigoBarras").child(codigoBarras.getUid()).setValue(codigoBarras);
        accesoBaseDatos.agregarCodigoBarras(codigoBarras.getUid(),codigoBarras.getvCodigo(),codigoBarras.getdFecRegistro());
        actualizarListado(codigoBarras.getvCodigo());

    }

    public void enviarNube(View view) throws ParseException {
        List<CodigoBarras> codigoBarras=accesoBaseDatos.listarPendientesEnvio();
        dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        if (codigoBarras.size()>=1){
        for(CodigoBarras codigoBarras1:codigoBarras){
            Map<String,Object> data=new HashMap<>();
            data.put("uid",codigoBarras1.getUid());
            data.put("vCodigo",codigoBarras1.getvCodigo());
            data.put("dFecRegistro",codigoBarras1.getdFecRegistro());
            data.put("dFecEnvio",dateFormat.format(new Date()));
            databaseReference.child("CodigoBarras").push().setValue(data);
            accesoBaseDatos.actualizarFechaEnvio(codigoBarras1.getUid(),codigoBarras1.getdFecEnvio());
        }
        }
        Toast.makeText(this,"Enviados al servidor", Toast.LENGTH_LONG).show();
        generarListadoInit();
    }

    class zxingScanner implements ZXingScannerView.ResultHandler{
        @SuppressLint("WrongViewCast")
        @Override
        public void handleResult(com.google.zxing.Result result) {
            String dato=result.getText();
            setContentView(R.layout.activity_main);
            vistaEscaner.stopCamera();
            codigo = (TextView) findViewById(R.id.edtCodigo);
            codigo.setText(dato);
            actualizarListado();
        }
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.leerQr:
                    new IntentIntegrator(MainActivity.this).initiateScan();
                    break;
            }
        }
    };

    private void generarListadoInit(){
        try {
            List<CodigoBarras> codigos=accesoBaseDatos.listarPendientesEnvio();

            if (codigos.size()>=1){
                for(CodigoBarras dataCodigo:codigos){
                    codigosBarra.add(dataCodigo.getvCodigo());
                }}else{
                codigosBarra.clear();
            }
                List<String> codReversed=codigosBarra;
                Collections.reverse(codReversed);
                mAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,codReversed);
                listaCodigos.setAdapter(mAdapter);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void actualizarListado(String dato){
        listaCodigos=findViewById(R.id.lv_codigoBarras);
        codigosBarra.add(dato);
        List<String> codReversed=codigosBarra;
        Collections.reverse(codReversed);
        mAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,codReversed);
        listaCodigos.setAdapter(mAdapter);

    }

    private void actualizarListado(){
        listaCodigos=findViewById(R.id.lv_codigoBarras);
        List<String> codReversed=codigosBarra;
        Collections.reverse(codReversed);
        mAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,codReversed);
        listaCodigos.setAdapter(mAdapter);

    }

}