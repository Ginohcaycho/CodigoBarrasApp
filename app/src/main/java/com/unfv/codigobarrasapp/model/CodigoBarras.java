package com.unfv.codigobarrasapp.model;

import java.util.Date;

public class CodigoBarras {

    private String uid;
    private String vCodigo;
    private String dFecRegistro;
    private String dFecEnvio;

    public CodigoBarras(){

    }
    public CodigoBarras(String uid, String vCodigo, String dFecRegistro, String dFecEnvio){
        this.uid=uid;
        this.vCodigo=vCodigo;
        this.dFecRegistro=dFecRegistro;
        this.dFecEnvio=dFecEnvio;

    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getvCodigo() {
        return vCodigo;
    }

    public void setvCodigo(String vCodigo) {
        this.vCodigo = vCodigo;
    }


    public String getdFecRegistro() {
        return dFecRegistro;
    }

    public void setdFecRegistro(String dFecRegistro) {
        this.dFecRegistro = dFecRegistro;
    }

    public String getdFecEnvio() {
        return dFecEnvio;
    }

    public void setdFecEnvio(String dFecEnvio) {
        this.dFecEnvio = dFecEnvio;
    }
}
