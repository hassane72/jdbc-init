/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.modelis.nicad;

/**
 *
 * @author irief
 */
public class OneDataLine {
    private String codeRegion; 
    private String nomRegion; 
    private String codeDepartement;
    private String nomDepartement;
    private String codeCommune;
    private String nomCommune;
    private String codeCav;
    private Integer idSection;
    private String codeSyscolSection;
    private String nomSection;
    private String parcelle;
    private Integer idParcelle;
    private String nicadSif;
    private String nicadCalcule;
    private String msg_traitement;

    public Integer getIdParcelle() {
        return idParcelle;
    }

    public void setIdParcelle(Integer idParcelle) {
        this.idParcelle = idParcelle;
    }

    
    public String getNicadSif() {
        return nicadSif;
    }

    public void setNicadSif(String nicadSif) {
        this.nicadSif = nicadSif;
    }

    public String getNicadCalcule() {
        return nicadCalcule;
    }

    public void setNicadCalcule(String nicadCalcule) {
        this.nicadCalcule = nicadCalcule;
    }

    public String getCodeRegion() {
        return codeRegion;
    }

    public void setCodeRegion(String codeRegion) {
        this.codeRegion = codeRegion;
    }

    public String getNomRegion() {
        return nomRegion;
    }

    public void setNomRegion(String nomRegion) {
        this.nomRegion = nomRegion;
    }

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public String getNomDepartement() {
        return nomDepartement;
    }

    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }

    public String getCodeCommune() {
        return codeCommune;
    }

    public void setCodeCommune(String codeCommune) {
        this.codeCommune = codeCommune;
    }

    public String getNomCommune() {
        return nomCommune;
    }

    public void setNomCommune(String nomCommune) {
        this.nomCommune = nomCommune;
    }

    public String getCodeCav() {
        return codeCav;
    }

    public void setCodeCav(String codeCav) {
        this.codeCav = codeCav;
    }

    public Integer getIdSection() {
        return idSection;
    }

    public void setIdSection(Integer idSection) {
        this.idSection = idSection;
    }

    public String getCodeSyscolSection() {
        return codeSyscolSection;
    }

    public void setCodeSyscolSection(String codeSyscolSection) {
        this.codeSyscolSection = codeSyscolSection;
    }

    public String getNomSection() {
        return nomSection;
    }

    public void setNomSection(String nomSection) {
        this.nomSection = nomSection;
    }
    
    public String getParcelle() {
        return parcelle;
    }

    public void setParcelle(String parcelle) {
        this.parcelle = parcelle;
    }

    public String getMsg_traitement() {
        return msg_traitement;
    }

    public void setMsg_traitement(String msg_traitement) {
        this.msg_traitement = msg_traitement;
    }
    
    
}
