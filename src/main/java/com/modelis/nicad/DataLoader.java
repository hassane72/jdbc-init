/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.modelis.nicad;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;
/**
 *
 * @author irief
 */
public class DataLoader {
    
    private final Logger logger=Logger.getLogger(this.getClass().getPackage().getName());

    
    // recuperation des connexions
    private Connection conn_sif=null;
    private Connection conn_nicad = null;
    private HashMap<String, Integer> numeroParcelleMap= null;
    private ArrayList<OneDataLine> allResultRecords= null;
    public DataLoader() {
        
        numeroParcelleMap = new HashMap<String, Integer>();
        
        //Load Configuration 
        Configuration configuration = new Configuration();
        
        if (configuration.initialize()){
            DBManager dbManager = new DBManager(configuration);
            dbManager.initialize();
            
            // recuperation des connexions
            conn_sif = dbManager.getConnSif();
            conn_nicad = dbManager.getConnNicad();
        }
    }
    private String getNumParcelle(PGgeometry geometry){
        try{
            Statement stmt_sif = conn_sif.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql="SELECT code,key_commune,geom FROM sif.section s where ST_Intersects(s.geom,'" + geometry.getValue() +  "')" ;
            ResultSet result_section = stmt_sif.executeQuery(sql);
            String codeCommune = "";
            String codeSection = "";
            Integer lastNumber;
            String s_numeroParcelle="";
            String s_nicad="";
            Integer numeroParcelle=null;
            PGgeometry geomSection=null;
            NumberFormat formatter = new DecimalFormat("00000");     
            if (isLeaf(result_section)){ 
                while (result_section.next()){
                    codeCommune = result_section.getString("key_commune");
                    codeSection = result_section.getString("code");
                    geomSection = (PGgeometry) result_section.getObject("geom");
                    
                    if (numeroParcelleMap.containsKey(codeCommune + "_" + codeSection )){
                        // recuperation du dernier numéro
                        lastNumber  = numeroParcelleMap.get(codeCommune + "_" + codeSection);
                        numeroParcelleMap.replace(codeCommune + "_" + codeSection, lastNumber+1);
                        return formatter.format(lastNumber+1);
                    }else {
                        // requête des parcelles de la section
                        sql="SELECT numeroparcelle, nicad FROM sif.parcelle c where ST_Intersects(c.geom,'" + geomSection.getValue() +  "')" ;
                        ResultSet result_parcelle = stmt_sif.executeQuery(sql);
                        lastNumber=0;
                        while(result_parcelle.next()){
                            s_numeroParcelle = result_parcelle.getString("numeroparcelle");
                            if (s_numeroParcelle!=null){
                                s_numeroParcelle = s_numeroParcelle.trim();
                                if ( (s_numeroParcelle !=null)&& (!"".equals(s_numeroParcelle.trim()))){
                                    if (s_numeroParcelle.contains("null")){
                                        s_numeroParcelle="0";
                                    }
                                    numeroParcelle = Integer.parseInt(s_numeroParcelle);
                                    if (numeroParcelle> lastNumber)
                                        lastNumber = numeroParcelle;
                                }else {
                                    s_nicad = result_parcelle.getString("nicad");
                                    if ((s_nicad !=null)&&(!"".equals(s_nicad.trim()))){
                                        if (s_nicad.trim().length()==16){
                                            numeroParcelle = Integer.parseInt( s_nicad.trim().substring(11, 16));
                                            if (numeroParcelle> lastNumber)
                                                lastNumber = numeroParcelle;
                                        }

                                    }
                                }
                            }
                        }
                        if (lastNumber!=0){
                            numeroParcelleMap.put(codeCommune + "_" + codeSection, lastNumber+1);
                            return  formatter.format(lastNumber+1);
                        }else {
                            numeroParcelleMap.put(codeCommune + "_" + codeSection, 1);
                            return formatter.format(1);
                        }
                    }
                }
            }
            return "";
        }
        catch(NumberFormatException | SQLException exp){
            return null;
        }
    }
    
    private boolean isLeaf(ResultSet result) throws SQLException{
        try{
            result.last();
            if (result.getRow()==1){
                return true;
            }else {
               return false; 
            }
        }catch(SQLException exp){
            return false;
        }finally{
           result.beforeFirst();
        }
            
    }
    
    private int getResultSize(ResultSet result) throws SQLException{
        int count=0;
        try{
            result.last();
            count = result.getRow();
            return count;
        }catch(SQLException exp){
            return count;
        }finally{
           result.beforeFirst();
        }
            
    }
    
    private String buildNicadFromSif(PGgeometry geometry, OneDataLine dataLine, String oldNumeroParcelle, String old_nicad_sif){
        try{
            // recherche de la region 
            Statement stmt_sif = conn_sif.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql="SELECT  id_region,nomregion, code_syscol FROM sif.region  r where ST_Intersects(r.geom,'" + geometry.getValue() + "')";
            ResultSet result_region_sif = stmt_sif.executeQuery(sql);
           
            String codeSyscolRegion="";
            String codeSyscolDepartement="";
            String codeSyscolCommune =""; 
            String codeSyscolCav="";
            
            Integer idSection;
            String codeSyscolSection ="";       
            String nomSection ="";      
            
            Integer idRegion=null;
            if (isLeaf(result_region_sif)){
                while(result_region_sif .next()){
                    idRegion = result_region_sif.getInt("id_region");
                    codeSyscolRegion = result_region_sif.getString("code_syscol");
                    dataLine.setNomRegion(result_region_sif.getString("nomregion"));
                    dataLine.setCodeRegion(codeSyscolRegion);
                }
            }
            // Si plus d'une region trouvée, abandonner
            // SInon rechercher le departement
            sql="SELECT id_departement,nomdepartement,  code_syscol FROM sif.departement d where ST_Intersects(d.geom,'" + geometry.getValue() +  "')";
            ResultSet result_dep = stmt_sif.executeQuery(sql);
            Integer idDepartement=null;
            if (isLeaf(result_dep)){
                while(result_dep.next()){
                    idDepartement = result_dep.getInt("id_departement");
                    codeSyscolDepartement= result_dep.getString("code_syscol");
                    dataLine.setNomDepartement(result_dep.getString("nomdepartement"));
                    dataLine.setCodeDepartement(codeSyscolDepartement);
                }
            }
            
            // Si plus d'un arrondissement trouvé, abandonner
            // SInon rechercher la commune
            sql="SELECT id_commune,nomcommune, code_syscol, code_cav_syscol FROM sif.commune c where ST_Intersects(c.geom,'" + geometry.getValue() +  "')" ;
            ResultSet result_commune = stmt_sif.executeQuery(sql);
            Integer idCommune = null; 
            if (isLeaf(result_commune)){
                while (result_commune.next()){
                    idCommune = result_commune.getInt("id_commune");
                    codeSyscolCommune =  result_commune.getString("code_syscol");
                    codeSyscolCav = result_commune.getString("code_cav_syscol");
                    dataLine.setCodeCav(codeSyscolCav);
                    dataLine.setNomCommune(result_commune.getString("nomcommune"));
                    dataLine.setCodeCommune(codeSyscolCommune);
                }
            }
            
            // recuperation de la section 
            sql="SELECT id_section, code,nomsection, geom FROM sif.section s where ST_Intersects(s.geom,'" + geometry.getValue() +  "')" ;
            ResultSet result_section = stmt_sif.executeQuery(sql);
            if (isLeaf(result_section)){
                while (result_section.next()){
                    
                    idSection=result_section.getInt("id_section");
                    codeSyscolSection =result_section.getString("code");       
                    nomSection =result_section.getString("nomsection"); 
                    
                    dataLine.setIdSection(idSection);
                    dataLine.setCodeSyscolSection(codeSyscolSection);
                    dataLine.setNomSection(nomSection);
                }
            }
            
            // recuperaton du numéro de la parcelle
            String numeroParcelle=null;
            if ((oldNumeroParcelle==null)|| ("null".equals(oldNumeroParcelle.trim())) || ("".equals(oldNumeroParcelle.trim()))){
                if ((old_nicad_sif==null)|| ("".equals(old_nicad_sif.trim()))){
                    numeroParcelle = getNumParcelle(geometry);
                }else {
                    //extraction du numero de parcelle dans le nicad 
                    if (old_nicad_sif.trim().length()==16){
                        numeroParcelle = old_nicad_sif.trim().substring(11, 16);
                    }
                    if ((numeroParcelle==null) ||(numeroParcelle.contains("null"))){
                        numeroParcelle = getNumParcelle(geometry);
                    }

                }
            }else {
                    numeroParcelle =  oldNumeroParcelle;
            }
            if ((numeroParcelle!=null)&& (!"".equals(numeroParcelle.trim()))){
                dataLine.setParcelle(numeroParcelle.toString());
                // constrcution du nicad
                if ((codeSyscolRegion!=null) && (codeSyscolDepartement!=null) && (codeSyscolCommune!=null) && (nomSection!=null) && (codeSyscolCav!=null)){
                    String nicad = codeSyscolRegion + codeSyscolDepartement + codeSyscolCav + codeSyscolCommune + nomSection+ numeroParcelle;
                    dataLine.setNicadCalcule(nicad);
                    return nicad;
                }else {
                    return "";
                }
            }
            return "";
        }
        catch(Exception exp){
//            if (exp.getMessage().contains("TopologyException")){
//                return "";
//            }
//            logger.error("Erreur when build Nicad From Sif Feature, message " + exp.getMessage());
            return "";
        }
    }
    private String getGeometryPolygon(PGgeometry geometry){
        org.postgis.Polygon polygon = null;
        if (geometry.getGeoType() == org.postgis.Geometry.MULTIPOLYGON){
            org.postgis.MultiPolygon multipolygon = (org.postgis.MultiPolygon) geometry.getGeometry();
            polygon = multipolygon.getPolygon(0);
        }
        if (geometry.getGeoType() == org.postgis.Geometry.POLYGON){
            polygon = (org.postgis.Polygon) geometry.getGeometry();
        }
        if (polygon!=null){
            return polygon.toString();
        }
        return "";
    }
    private double getPgeomSurface(PGgeometry geometry){
        try{
            org.postgis.Polygon polygon = null;
            if (geometry.getGeoType() == org.postgis.Geometry.MULTIPOLYGON){
                org.postgis.MultiPolygon multipolygon = (org.postgis.MultiPolygon) geometry.getGeometry();
                polygon = multipolygon.getPolygon(0);
            }
            if (geometry.getGeoType() == org.postgis.Geometry.POLYGON){
                polygon = (org.postgis.Polygon) geometry.getGeometry();
            }
            if (polygon!=null){
                java.awt.Polygon p = new java.awt.Polygon();
                
                GeometryFactory geometryFactory = new GeometryFactory();
                Coordinate[] coords = new Coordinate[polygon.numPoints()];
                Point pt= null; 
                for (int i=0; i< polygon.numPoints();i++){
                    pt = polygon.getPoint(i);
                    coords[i] =new Coordinate(pt.x, pt.y); 
                }
                    
                // creation du contour ÃƒÂ  partir des sommets
                LinearRing ring = geometryFactory.createLinearRing(coords);
                // construction du polygone ÃƒÂ  partir du contour
                LinearRing holes[]=null;
                org.locationtech.jts.geom.Polygon pGon = geometryFactory.createPolygon(ring, holes );
                return pGon.getArea();
            }
        }catch(Exception exp){
            return 0;
        }
        return 0;
    }
    
    public boolean compareData() throws SQLException{
       String sql=""; 
        
        try {
            Statement stmt_sif = conn_sif.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Statement stmt_nicad = conn_nicad.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            DecimalFormat df2 = new DecimalFormat("##0.##");
            
            allResultRecords = new ArrayList<OneDataLine>();
            
            // recuperation du code de la nature juridique
            sql="SELECT id FROM public.nature_juridique where libelle='Domaine National'";
            ResultSet result_natureJuridique = stmt_nicad.executeQuery(sql);
            Integer id_natureJuridique=null;
            if (getResultSize(result_natureJuridique)==1){
                while(result_natureJuridique.next()){
                    id_natureJuridique = result_natureJuridique.getInt("id");
                }
            }

            // selection de la demande AUTOMATE dans la base de données
            sql="SELECT id FROM public.demande_nicad where code='AUTOMATE';";
            ResultSet result_demande = stmt_nicad.executeQuery(sql);
            Integer id_demande=null;
            if (getResultSize(result_demande)==1){
                while(result_demande.next()){
                    id_demande = result_demande.getInt("id");
                }
                if (id_demande!=null){
                    
                    // Mise à jour de tous les numéro de parcelle dans la table Parcelle du SIF
//                    sql="SELECT id_parcelle, numeroparcelle, nicad FROM sif.parcelle";
//                    ResultSet result_parcelles_sif = stmt_sif.executeQuery(sql);
//                    String numeroParcelle=null;
//                    String numeroNicad=null;
//                    Integer idParcelle=null;
//                    while(result_parcelles_sif.next()){
//                        numeroParcelle = result_parcelles_sif.getString("numeroparcelle"); 
//                        numeroNicad = result_parcelles_sif.getString("nicad"); 
//                        idParcelle =  result_parcelles_sif.getInt("id_parcelle"); 
//                        if (("".equals(numeroParcelle.trim())) || ("null".equals(numeroParcelle.trim())) || (numeroParcelle==null)){
//                            if ((!"".equals(numeroNicad.trim())) && (!"null".equals(numeroNicad.trim())) && (numeroNicad!=null)){
//                                if (numeroNicad.trim().length()==16){
//                                    numeroParcelle = numeroNicad.trim().substring(11, 16);
//                                    sql = "UPDATE sif.parcelle SET numeroparcelle='" + numeroParcelle + "' WHERE id_parcelle=" + idParcelle;
//                                    int rstUpdate = stmt_sif.executeUpdate(sql);
//                                    if (rstUpdate<1){
//                                        //dataLine.setMsg_traitement("KO : Code 10 : Nicad absent dans la BD SIF, Erreur lors de sa mise à jour");
//                                    }
//                                }
//                            }
//                        }
//                        
//                    }
                    ResultSet result_parcelles_sif = null;
                    // SQL Parcelle 
                    sql="SELECT id_parcelle, key_section, naturejuridique, lienreglementcopropriete, numeroparcelle, numerotitrefoncier, nicad, geom FROM sif.parcelle";
                    result_parcelles_sif = stmt_sif.executeQuery(sql);

                    OneDataLine dataLine = null;
                    
                    // 1. Recuperation des parcelles du SIF
                    PGgeometry geomParcelle= null;
                    Integer idParcelleSIF= null;
                    Integer idParcelleNICAD= null;
                    String oldNumeroParcelle=null;
                    while(result_parcelles_sif.next()){

                        // Creation de la ligne de resultat
                        dataLine = new OneDataLine();

                        // 2. Vérification du Nicad
                        // Est ce que le numéro de nicad existe sur la parcelle du SIF ?
                        String nicad_sif = result_parcelles_sif.getString("nicad");
                        geomParcelle = (PGgeometry) result_parcelles_sif.getObject("geom");
                        idParcelleSIF =  result_parcelles_sif.getInt("id_parcelle"); 
                        oldNumeroParcelle = result_parcelles_sif.getString("numeroparcelle"); 
                        
                        String nicadValue = buildNicadFromSif(geomParcelle, dataLine, oldNumeroParcelle, nicad_sif);
                        dataLine.setIdParcelle(idParcelleSIF);
                        dataLine.setNicadSif(nicad_sif);
                        
                        // Mise à jour du numéro de parcelles
                        if ((oldNumeroParcelle==null)|| ("null".equals(oldNumeroParcelle.trim()))|| ("".equals(oldNumeroParcelle.trim()))) {
                            if ((dataLine.getParcelle()!=null) && (!"".equals(dataLine.getParcelle()))){
                                sql = "UPDATE sif.parcelle SET numeroparcelle='" + dataLine.getParcelle() + "' WHERE id_parcelle=" + idParcelleSIF;
                                int rstUpdate = stmt_sif.executeUpdate(sql);
                                if (rstUpdate<1){
                                    //dataLine.setMsg_traitement("KO : Code 10 : Nicad absent dans la BD SIF, Erreur lors de sa mise à jour");
                                }
                            }
                        }
                        if (!"".equals(nicadValue) && (nicadValue.trim().length()> 19)){
                            dataLine.setMsg_traitement("KO : Code 19 : Longueur de NICAD calculé incorrecte");
                        }
                        else if ((nicadValue==null) || ("".equals(nicadValue.trim())) ){
                            dataLine.setMsg_traitement("KO : Code 14 : Impossible de construire le NICAD de la BD du SIF");
                        }else {
                            if ((nicad_sif==null)|| ("".equals(nicad_sif.trim())) || ("null".equals(nicad_sif.trim())) ){
                                // NON
                                // Recherche de doublon du NICAD en base du SIG 
                                sql = "SELECT nicad from  sif.parcelle where nicad='" + nicadValue + "'";
                                ResultSet result_sif = stmt_sif.executeQuery(sql);
                                if (getResultSize(result_sif)>0){
                                    dataLine.setMsg_traitement("KO : Code 18 : Nicad existant dans la base de données su SIF, Nicad : " + nicadValue);
                                }else{
                                    // Sauvegarde le numreo de nicad dans la base de données du SIF 
                                    sql = "UPDATE sif.parcelle SET nicad='" + nicadValue + "' WHERE id_parcelle=" + idParcelleSIF;
                                    int rstUpdate = stmt_sif.executeUpdate(sql);
                                    if (rstUpdate<1){
                                        dataLine.setMsg_traitement("KO : Code 10 : Nicad absent dans la BD SIF, Erreur lors de sa mise à jour");
                                    }

                                    // Recherche du nicad dans la table parcelle
                                    sql="SELECT id, nicad from public.parcelle where nicad='" + dataLine.getNicadCalcule() + "'";
                                    ResultSet result_nicad = stmt_nicad .executeQuery(sql);

                                    if (getResultSize(result_nicad)>0){
                                        while(result_nicad.next()){
                                            idParcelleNICAD = result_nicad.getInt("id");
                                        }
                                    }else{
                                        String surface = df2.format(getPgeomSurface(geomParcelle));
                                        surface = surface.replace(",",".");
                                        // Sauvegarder le numéro de nicad et la parcelle dans la base de données du Nicad
                                        sql="INSERT INTO public.parcelle(fuseau, geom28, lien_reglement_copropriete, nature_id, nicad, "
                                                + "numero_parcelle,statut_parcelle, superficie,demande_nicad_id, id_section, key_section)\n" 
                                                + "VALUES (28,'" + getGeometryPolygon(geomParcelle) + "','non'," +  id_natureJuridique + ",'" + dataLine.getNicadCalcule() + "','"
                                                + dataLine.getParcelle() +  "','ACTIF'," + surface +","+ id_demande 
                                                + ","+ dataLine.getIdSection()+ ",'" + dataLine.getNomSection() + "')";
                                        int comptResult_insert_nicad = stmt_nicad.executeUpdate(sql);
                                        if (comptResult_insert_nicad>0){
                                            sql="SELECT id, nicad from public.parcelle where nicad='" + dataLine.getNicadCalcule() + "'";
                                            result_nicad = stmt_nicad .executeQuery(sql);

                                            while(result_nicad.next()){
                                                idParcelleNICAD = result_nicad.getInt("id");
                                            }
                                        }
                                        else{
                                            dataLine.setMsg_traitement("KO : Code 13 : Erreur lors de l'insertion de la parcelle dans la BD du NICAD");
                                        }
                                    }    

                                    // Recherche du nicad dans la table nicad_parcelle
                                    sql="SELECT nicad from public.nicad_parcelle where nicad='" + dataLine.getNicadCalcule() + "'";
                                    result_nicad = stmt_nicad .executeQuery(sql);
                                    if (getResultSize(result_nicad)==0){
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
                                        sql = "INSERT INTO public.nicad_parcelle(date_creation, id_parcelle_geo_portail, nicad, statut_nicad, parcelle_id,demande_nicad_id)\n" +
                                        " VALUES ('" + formatter.format(new java.util.Date()) + "'," + idParcelleSIF + ",'" + dataLine.getNicadCalcule() + "','ACTIF'," 
                                        + idParcelleNICAD + ","+id_demande + ")";
                                        int comptResult_insert_nicad_parcelle = stmt_nicad.executeUpdate(sql);
                                        if (comptResult_insert_nicad_parcelle>0){
                                            dataLine.setMsg_traitement("OK : Code 11 : Mise à jour du NICAD de la BD Nicad effectué avec succès");
                                        }else {
                                            dataLine.setMsg_traitement("KO : Code 12 : Erreur lors de l'insertion de la parcelle_nicad dans la BD du NICAD");
                                        }
                                    }
                                }
                            }else{
                                //OUI
                                if (nicad_sif.trim().length()<16){
                                    dataLine.setMsg_traitement("KO : Code 17 : La longueur du NICAD de la BD du SIF est incorrecte NICAD SIF : " + nicad_sif );
                                }else{
                                    // Comparer la valeur du NICAD du SIF avec la valeur generée
                                    if (!nicad_sif.trim().equals(dataLine.getNicadCalcule().trim())){
                                        // Pas de sauvegarde en base de données du NICAD
                                        dataLine.setMsg_traitement("KO : Code 15 : Le nicad de la BD du SIF et celui qui est calculé sont différents, nicad BD SIF : " + nicad_sif + ", nicad calculé : " + nicadValue);

                                    }else {

                                        //Est ce que la parcelle existe dans la base de données du nicad? 
                                        sql= "SELECT t1.nicad FROM public.nicad_parcelle t1 inner join public.parcelle t2 on  t1.parcelle_id=t2.id where t1.id_parcelle_geo_portail=" + idParcelleSIF;
                                        ResultSet result_nicad_parcelle = stmt_nicad.executeQuery(sql);
                                        if (getResultSize(result_nicad_parcelle)>0){
                                            // OUI
                                            //le Nicad existe dans la base dd données du nicad
                                            // comparer
                                            String nicad_nicad="";
                                            while(result_nicad_parcelle.next()){
                                                nicad_nicad = result_nicad_parcelle.getString("nicad");
                                            }
                                            if (nicad_nicad.trim().toLowerCase().equals(nicad_sif.trim().toLowerCase())){
                                                dataLine.setMsg_traitement("OK : Code 16 : Le nicad de la BD SIF et celui de la BD NICAD sont égaux, nicad BD SIF : " + nicad_sif + ", nicad BD NICAD  : " + nicad_nicad);
                                            }else{
                                                dataLine.setMsg_traitement("KO : Code 15 : Le nicad de la BD SIF et celui de la BD NICAD sont différents, nicad BD SIF : " + nicad_sif + ", nicad BD NICAD  : " + nicad_nicad);
                                            }
                                        }else {
                                           // Recherche du nicad dans la table parcelle
                                            sql="SELECT id, nicad from public.parcelle where nicad='" + dataLine.getNicadCalcule() + "'";
                                            ResultSet result_nicad = stmt_nicad .executeQuery(sql);

                                            if (getResultSize(result_nicad)>0){
                                                while(result_nicad.next()){
                                                    idParcelleNICAD = result_nicad.getInt("id");
                                                }
                                            }else{
                                                String surface = df2.format(getPgeomSurface(geomParcelle));
                                                surface = surface.replace(",",".");
                                                // Sauvegarder le numéro de nicad et la parcelle dans la base de données du Nicad
                                                
                                                sql="INSERT INTO public.parcelle(fuseau, geom28, lien_reglement_copropriete, nature_id, nicad, "
                                                        + "numero_parcelle,statut_parcelle, superficie,demande_nicad_id, id_section, key_section)\n" 
                                                        + "VALUES (28,'" + getGeometryPolygon(geomParcelle) + "','non'," +  id_natureJuridique + ",'" + dataLine.getNicadCalcule() + "','"
                                                        + dataLine.getParcelle() +  "','ACTIF'," + surface +","+ id_demande 
                                                        + ","+ dataLine.getIdSection()+ ",'" + dataLine.getNomSection() + "')";
                                                int comptResult_insert_nicad = stmt_nicad.executeUpdate(sql);
                                                if (comptResult_insert_nicad>0){
                                                    sql="SELECT id, nicad from public.parcelle where nicad='" + dataLine.getNicadCalcule() + "'";
                                                    result_nicad = stmt_nicad .executeQuery(sql);

                                                    while(result_nicad.next()){
                                                        idParcelleNICAD = result_nicad.getInt("id");
                                                    }
                                                }
                                                else{
                                                    dataLine.setMsg_traitement("KO : Code 13 : Erreur lors de l'insertion de la parcelle dans la BD du NICAD");
                                                }
                                            }    

                                            // Recherche du nicad dans la table nicad_parcelle
                                            sql="SELECT nicad from public.nicad_parcelle where nicad='" + dataLine.getNicadCalcule() + "'";
                                            result_nicad = stmt_nicad .executeQuery(sql);
                                            if (getResultSize(result_nicad)==0){
                                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
                                                sql = "INSERT INTO public.nicad_parcelle(date_creation, id_parcelle_geo_portail, nicad, statut_nicad, parcelle_id,demande_nicad_id)\n" +
                                                " VALUES ('" + formatter.format(new java.util.Date()) + "'," + idParcelleSIF + ",'" + dataLine.getNicadCalcule() + "','ACTIF'," 
                                                + idParcelleNICAD + ","+id_demande + ")";
                                                int comptResult_insert_nicad_parcelle = stmt_nicad.executeUpdate(sql);
                                                if (comptResult_insert_nicad_parcelle>0){
                                                    dataLine.setMsg_traitement("OK : Code 11 : Mise à jour du NICAD de la BD Nicad effectué avec succès");
                                                }else {
                                                    dataLine.setMsg_traitement("KO : Code 12 : Erreur lors de l'insertion de la parcelle_nicad dans la BD du NICAD");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        System.out.println("Ajout de la ligne " + String.valueOf(allResultRecords.size()+1));
                        allResultRecords.add(dataLine);
                    }
                }
            }
            return true;
        }catch(Exception exp){
            //logger.error("Erreur when compare data, message " + exp.getMessage());
            return false;
        }finally{
            if (!conn_nicad.isClosed())
                conn_nicad.close();
            
            if (!conn_sif.isClosed())
                conn_sif.close();
        }                  
    }

    public ArrayList<OneDataLine> getAllResultRecords() {
        return allResultRecords;
    }

    public void setAllResultRecords(ArrayList<OneDataLine> allResultRecords) {
        this.allResultRecords = allResultRecords;
    }
    
}


