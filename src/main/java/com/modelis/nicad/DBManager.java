/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.modelis.nicad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author irief
 */
public class DBManager {
    private final Logger logger=Logger.getLogger(this.getClass().getPackage().getName());
    private Configuration configuration;
    java.sql.Connection connSif;
    java.sql.Connection connNicad;

    public DBManager(Configuration configuration) {
        this.configuration = configuration;
    }
    
    public boolean initialize(){
        // initialize databaseConextion
        try{
            Class.forName("org.postgresql.Driver"); 
            String urlSifDB = "jdbc:postgresql://" + configuration.getSIF_HOST() + ":" + configuration.getSIF_PORT() + "/" + configuration.getSIF_DATABASE(); 
            connSif = DriverManager.getConnection(urlSifDB, configuration.getSIF_DATABASE_USER(), configuration.getSIF_DATABASE_PASSWD()); 
            
            String urlNicadDB = "jdbc:postgresql://" + configuration.getNICAD_HOST() + ":" + configuration.getNICAD_PORT() + "/" + configuration.getNICAD_DATABASE(); 
            connNicad = DriverManager.getConnection(urlNicadDB, configuration.getNICAD_DATABASE_USER(), configuration.getNICAD_DATABASE_PASSWD()); 
            return true;
        }
        catch(ClassNotFoundException | SQLException exp){
            return false;
        }
    }
    
    
    
    public boolean release(){
        try {
            if (!connNicad.isClosed())
                connNicad.close();
            if (!connSif.isClosed())
                connSif.close();
            return true;
        }catch (SQLException exp){
            return false;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Connection getConnSif() {
        return connSif;
    }

    public void setConnSif(Connection connSif) {
        this.connSif = connSif;
    }

    public Connection getConnNicad() {
        return connNicad;
    }

    public void setConnNicad(Connection connNicad) {
        this.connNicad = connNicad;
    }
    
    
   
    
    
}
