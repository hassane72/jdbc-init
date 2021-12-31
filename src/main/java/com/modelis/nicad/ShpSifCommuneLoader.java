/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.modelis.nicad;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.postgis.PGgeometry;

/**
 *
 * @author irief
 */
public class ShpSifCommuneLoader {
    private Connection conn_sif=null;
    
    public static void main(String[] args) throws SQLException {
        //Load Configuration 
        ShpSifCommuneLoader shp = new ShpSifCommuneLoader();
        shp.initializeConnection();
        shp.readShapefileContent();
        
    }
    
    public void initializeConnection(){
        Configuration configuration = new Configuration();
        
        if (configuration.initialize()){
            DBManager dbManager = new DBManager(configuration);
            dbManager.initialize();
            
            // recuperation des connexions
            conn_sif = dbManager.getConnSif();
        }
    }
    
      
    public boolean readShapefileContent(){
            
        FeatureIterator iterator= null;
        try {
            File file = new File("C:/MODELIS/2020/MODELIS SENEGAL/PROJETS/NICAD/02-RESULTATS/TEST_DATA/20112020/IMPACTES TOBAGO 20112020/IMPACTES TOBAGO 20112020/LIMITES_COMMUNES.shp");
            Map<String, String> connect = new HashMap();
            connect.put("url", file.toURI().toString());

            DataStore dataStore = DataStoreFinder.getDataStore(connect);
            String[] typeNames = dataStore.getTypeNames();
            String typeName = typeNames[0];

            System.out.println("Reading content " + typeName);

            FeatureSource featureSource = dataStore.getFeatureSource(typeName);
            FeatureCollection collection = featureSource.getFeatures();
            iterator = collection.features();
            
            //Geometry geom = null;
            
            
            Integer key_departement=null;
            String code_departement_syscol="";
            String code_region_syscol=null;
            Integer key_arrondissement=null;
            String code_cav_syscol="";
            String code_syscol=""; 
            String nomCommune= ""; 
            Integer key_ville=null;

            MultiPolygon multipolygo = null;
            String sql="";
            String s_polygon="";
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                // recuperation des champs 
                SimpleFeature f = (SimpleFeature) feature;
                Object geomf = f.getDefaultGeometry();
                if (geomf instanceof MultiPolygon){
                    multipolygo = (MultiPolygon) geomf;

                    s_polygon = multipolygo.toString();
                    s_polygon = s_polygon.replace("MULTIPOLYGON", "SRID=32628;MULTIPOLYGON");
                  
                // recherche du departement
                    sql= "SELECT id_departement, key_region, code_region_syscol, code_syscol, nomdepartement from sif.departement s where ST_Intersects(s.geom,'" + s_polygon +  "')" ;
                    Statement stmt_sif = conn_sif.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet result_commune = stmt_sif.executeQuery(sql);
                    while (result_commune.next()){
                        key_departement =  result_commune.getInt("id_departement");
                        code_departement_syscol =  result_commune.getString("code_syscol");
                        code_region_syscol =  result_commune.getString("code_region_syscol");
                    }
                   
                    key_arrondissement=null;
                    code_cav_syscol=(String) f.getAttribute("COD_CAV");
                    code_syscol=(String) f.getAttribute("COD_CCRCA");
                    nomCommune=(String) f.getAttribute("NOM_COMMUN");
                    
                    
                            // Insertion de la section
                    sql ="INSERT INTO sif.commune( key_departement, code_departement_syscol, code_region_syscol, "
                            + " code_cav_syscol, geom, code_syscol, nomcommune)" 
                            + "	VALUES (" + key_departement + ",'" + code_departement_syscol + "','" + code_region_syscol + "'," 
                            + "'" + code_cav_syscol + "','" + s_polygon + "','" + code_syscol + "','"+ nomCommune + "')";
                    
                    int comptResult_insert_nicad = stmt_sif.executeUpdate(sql);
                    if (comptResult_insert_nicad>0){

                    }
                }
            }            
            return true;
        }
        catch (Exception exp){
            return false;
        } 
        finally {
            iterator.close();
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
}
