/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.modelis.nicad;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 *
 * @author irief
 */
public class CommuneSifCleaner {
    public static final String SYSCOL_FILE = "C:/MODELIS/2020/MODELIS SENEGAL/PROJETS/NICAD/01-ENTREES/DCAD/CODIFICATION.csv";

   

    public static void main(String[] args) throws SQLException {
        Connection connSif=null;
        Statement stmt_sif=null;
        try{
            
            // Etape 1 : Recuperation des données dans le fichier excel
            HashMap<String,String> keyMap = new HashMap<String,String>();
            String line = "";
            String cvsSplitBy = ";";
            try (BufferedReader br = new BufferedReader(new FileReader(SYSCOL_FILE))) {
                String keyCommune="";
                String valCommmune="";
                int index =0;
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] communeValues = line.split(cvsSplitBy);
                    if ((index>0)&& (communeValues.length==11)) {
                        // code sysColREG
                        String nomCommune= communeValues[4];
                        if (nomCommune.contains("GOR")){
                            System.out.println("stop");
                        }
                        if ("".equals(nomCommune))
                            nomCommune =  communeValues[2];
                        
                        nomCommune = nomCommune.replace("AR. ", "");
                        nomCommune = nomCommune.replace("VILLE DE ", "");
                        nomCommune = nomCommune.replace("COM. ", "");
                        nomCommune = nomCommune.replace("CA. ", "");
                        nomCommune = nomCommune.replace("’", "");
                        nomCommune = nomCommune.trim();
                        String s_cellREG =nomCommune + "_" + communeValues[5];
                        if ("".equals(s_cellREG)){
                        }else{
                            keyCommune = s_cellREG.trim().toUpperCase();

                            // code DEP
                            String s_cellDEP = communeValues[6]; 
                            if ("".equals(s_cellDEP)){
                                keyCommune = keyCommune + "_00";                               
                            }else{
                                keyCommune =keyCommune +"_" +   s_cellDEP.trim().toUpperCase();
                            }

                            // code C_A_V
                            String s_cellC_A_V = communeValues[7]; 
                            //if ("".equals(s_cellC_A_V)){
                            //    keyCommune = keyCommune + "_00";                    
                            //}else{
                            //    keyCommune =keyCommune  +"_" +  s_cellC_A_V.trim().toUpperCase();
                            //}
                            
                            // code syscol commune 
                            String s_cellSYSCOL = communeValues[8];
                            if ("".equals(s_cellSYSCOL)){
                                // si le syscol est 0 mettre le nom de la commune dans la clez
                                
                                keyCommune =keyCommune + "_00"; 
                            }else {
                                keyCommune= keyCommune  +"_" +  s_cellSYSCOL.trim().toUpperCase();
                            }

                            //String key = communeValues[9];
                            if (!keyMap.containsKey(keyCommune)){
                                keyMap.put(keyCommune,s_cellC_A_V );
                            }else{
                                System.out.println("key commune : " + keyCommune +  " value commune : " +  s_cellC_A_V);
                            }
                        }
                    }
                    index++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // 2. recuperation des données de la base de donnée
             Class.forName("org.postgresql.Driver"); 
            String urlSifDB = "jdbc:postgresql://localhost:5432/sif"; 
            connSif = DriverManager.getConnection(urlSifDB, "fabrice", "fabrice"); 
            stmt_sif = connSif.createStatement();

            //lecture de la table
            String sql="SELECT nomcommune, code_region_syscol, code_departement_syscol,code_syscol,code_cav_syscol FROM sif.commune" ;
            ResultSet result_commune_select = stmt_sif.executeQuery(sql);
            String codeReg="";
            String codeDep="";
            String codeCom="";
            String codeCAV="";
            String nomCommune="";
            String key="";
            int result_commune_update;
            while (result_commune_select.next()){
                codeReg = result_commune_select.getString("code_region_syscol");
                codeDep = result_commune_select.getString("code_departement_syscol");
                codeCom = result_commune_select.getString("code_syscol");
                codeCAV = result_commune_select.getString("code_cav_syscol");
                nomCommune = result_commune_select.getString("nomcommune");
                key = nomCommune + "_" + codeReg.trim().toUpperCase() + "_" + codeDep.trim().toUpperCase() + "_" + codeCom.trim().toUpperCase();
                if (keyMap.containsKey(key)){
                    //sauvegarde de la valeur
                    sql = "UPDATE sif.commune SET code_cav_syscol=" + keyMap.get(key) + " WHERE  code_region_syscol='" + codeReg + "' and code_departement_syscol='"
                            + codeDep + "' and code_syscol='" + codeCom + "'";
                    result_commune_update = stmt_sif.executeUpdate(sql);
                }
            }
        }catch(Exception exp){
            System.out.println("erreur : " + exp.getMessage());
        }finally{
            if (stmt_sif!=null) {
                if (!stmt_sif.isClosed())
                    stmt_sif.close();
            }
            if (connSif!=null){
                if (!connSif.isClosed())
                    connSif.close();
            }
        }
        
    }
}
