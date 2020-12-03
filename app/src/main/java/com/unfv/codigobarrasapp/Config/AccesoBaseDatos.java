package com.unfv.codigobarrasapp.Config;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unfv.codigobarrasapp.model.CodigoBarras;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccesoBaseDatos extends SQLiteOpenHelper {

    public static final String NOMBRE_BD="developerbd.bd";
    public static int VERSION_BD=1;
    public static final String TABLA="CREATE TABLE CODIGOBARRAS(UID TEXT, VCODIGO TEXT, DFECREGISTRO TEXT,DFECENVIO TEXT)";


    public AccesoBaseDatos(Context context){
        super(context,NOMBRE_BD,null,VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CODIGOBARRAS");
        db.execSQL(TABLA);
    }

    public void agregarCodigoBarras(String uid, String vCodigo, String dFecRegistro){
        SQLiteDatabase bd=getWritableDatabase();
        if(bd!=null){
            bd.execSQL("INSERT INTO CODIGOBARRAS(uid,vcodigo,dfecregistro) VALUES('"+uid+"','"+vCodigo+"','"+dFecRegistro+"')");
            bd.close();
        }
    }


    public void actualizarFechaEnvio(String uid, String dFecEnvio){
        SQLiteDatabase bd=getWritableDatabase();
        if(bd!=null){
            bd.execSQL("UPDATE CODIGOBARRAS SET dFecEnvio='"+dFecEnvio+"' where UID='"+uid+"' ");
            bd.close();
        }
    }

    public List<CodigoBarras> listarUltimosCodigos() throws ParseException {
        SQLiteDatabase bd=getReadableDatabase();
            Cursor cursor=bd.rawQuery("SELECT * FROM CODIGOBARRAS ORDER BY dFecRegistro DESC",null);
            List<CodigoBarras> listado=new ArrayList<>();
            if (cursor.moveToFirst()){
                do {
                    listado.add(new CodigoBarras(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
                }while (cursor.moveToNext());
            }
            return  listado;

    }

    public List<CodigoBarras> listarPendientesEnvio() throws ParseException {
        SQLiteDatabase bd=getReadableDatabase();
        Cursor cursor=bd.rawQuery("SELECT * FROM CODIGOBARRAS where DFECENVIO IS NULL",null);
        List<CodigoBarras> listado=new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                listado.add(new CodigoBarras(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
            }while (cursor.moveToNext());
        }
        return  listado;

    }


}
