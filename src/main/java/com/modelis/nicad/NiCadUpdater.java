/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.modelis.nicad;

import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author irief
 */
public class NiCadUpdater {
    private final Logger logger=Logger.getLogger(this.getClass().getPackage().getName());
    
    public static void main(String[] args) throws SQLException {
        NiCadUpdater n = new NiCadUpdater();
        n.loadData();
    }
    
    public void loadData() throws SQLException{
        DataLoader d = new DataLoader(); 
        d.compareData();
        ArrayList<OneDataLine> datasLigne = d.getAllResultRecords();
        
        int code10=0;
        int code11=0;
        int code12=0;
        int code13=0;
        int code14=0;
        int code15=0;
//        int code16=0;
        int code17=0;
        int code18=0;
        int code19=0;
        
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 10")){
                code10++;
            }else if (oneDataLine.getMsg_traitement().contains("OK")){
                code11++;
            }else if (oneDataLine.getMsg_traitement().contains("Code 12")){
                code12++;
            }else if (oneDataLine.getMsg_traitement().contains("Code 13")){
                code13++;
            }else if (oneDataLine.getMsg_traitement().contains("Code 14")){
                code14++;
            }else if (oneDataLine.getMsg_traitement().contains("Code 15")){
                code15++;
//            }else if (oneDataLine.getMsg_traitement().contains("Code 16")){
//                code16++;
            }else if (oneDataLine.getMsg_traitement().contains("Code 17")){
                code17++;
            }else if (oneDataLine.getMsg_traitement().contains("Code 18")){
                code18++;
            }else if (oneDataLine.getMsg_traitement().contains("Code 19")){
                code19++;
            }
        }
        // reporting 
        // Creation du fichier de reporting
        //ligne OK
        logger.info("==== Resumé des traitements ====" );
        logger.info("Lignes totales : " + datasLigne.size() );
        logger.info("Lignes traitées avec succès : " + code11 );
        logger.info("Lignes avec erreur lors de l'insertion de la parcelle_nicad dans la BD du NICAD : " + code12 );
        logger.info("Lignes avec erreur lors de l'insertion de la parcelle dans la BD du NICAD : " + code13 );
        logger.info("Lignes avec impossibilité de construire le NICAD de la BD du SIF : " + code14 );
        logger.info("Lignes avec différence entre nicad de la BD du SIF et nicad calculé : " + code15 );
        logger.info("Lignes avec longueur du NICAD de la BD du SIF incorrecte : " + code17 );
        logger.info("Lignes avec doublons de NICAD : " + code18 );
        logger.info("Lignes avec longueur du NICAD calculée incorrecte : " + code19 );
        
        logger.info("==== Detail des traitements ====" );
        logger.info("OK : Code 11 : Enregistrements OK" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("OK")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() +  " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad  Calculé : " + oneDataLine.getNicadCalcule());
            }
        }
        
        logger.info("");
        //ligne KO
        logger.info("KO : Code 10 : Nicad absent dans la BD SIF, Erreur lors de sa mise à jour" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 10")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() +  " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad Calculé : " + oneDataLine.getNicadCalcule() );
            }
        }
        
        logger.info("");
        //ligne KO
        logger.info("KO : Code 12 : Erreur lors de l'insertion de la parcelle_nicad dans la BD du NICAD" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 12")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() +  " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad Calculé : " + oneDataLine.getNicadCalcule() );
            }
        }
        
        logger.info("");
        //ligne KO
        logger.info("KO : Code 13 : Erreur lors de l'insertion de la parcelle dans la BD du NICAD" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 13")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() +  " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad Calculé : " + oneDataLine.getNicadCalcule() );
            }
        }
        
        logger.info("");
        //ligne KO
        logger.info("KO : Code 14 : Impossible de construire le NICAD de la BD du SIF" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 14")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() +  " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad Calculé : " + oneDataLine.getNicadCalcule() );
            }
        }
        
        logger.info("");
        //ligne KO
        logger.info("KO : Code 15 : Le nicad de la BD du SIF et celui qui est calculé sont différents" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 15")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() +  " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad Calculé : " + oneDataLine.getNicadCalcule() );
            }
        }
        
        logger.info("");
        //ligne KO
        logger.info("KO : Code 17 : La longueur du NICAD de la BD du SIF est incorrecte" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 17")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() +  " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad Calculé : " + oneDataLine.getNicadCalcule() );
            }
        }
        
        logger.info("");
        //ligne KO
        logger.info("KO : Code 18 : Doublons de NICAD" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 18")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() + " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad Calculé : " + oneDataLine.getNicadCalcule() );
            }
        }
        
        logger.info("");
        //ligne KO
        logger.info("KO : Code 19 : Longueur du NICAD calculé est incorrecte" );
        logger.info("====================");
        for (OneDataLine oneDataLine : datasLigne ){
            if (oneDataLine.getMsg_traitement().contains("Code 19")){
                logger.info("Region : " + oneDataLine.getNomRegion() + " / Departement : " + oneDataLine.getNomDepartement() 
                        + " / Cav : " + oneDataLine.getCodeCav() + " / Commune : " + oneDataLine.getNomCommune() + " / Section " + oneDataLine.getNomSection()
                        + " / Id Parcelle SIF : " + oneDataLine.getIdParcelle() +  " / Parcelle : " + oneDataLine.getParcelle() + " / Nicad SIF : " + oneDataLine.getNicadSif() + " / Nicad Calculé : " + oneDataLine.getNicadCalcule() );
            }
        }
    }
}
