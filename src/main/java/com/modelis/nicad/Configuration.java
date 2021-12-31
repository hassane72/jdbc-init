/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.modelis.nicad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author irief
 */
public class Configuration {
    private final Logger logger=Logger.getLogger(this.getClass().getPackage().getName());
    
    private String NICAD_HOST="";
    private String NICAD_PORT="";
    private String NICAD_DATABASE="";
    private String NICAD_SCHEMA="";
    private String NICAD_DATABASE_USER="";
    private String NICAD_DATABASE_PASSWD="";
    private String SIF_HOST="";
    private String SIF_PORT="";
    private String SIF_DATABASE="";
    private String SIF_SCHEMA="";
    private String SIF_DATABASE_USER="";
    private String SIF_DATABASE_PASSWD="";
    
    public boolean initialize(){
        try{
            String propFileName = "config.properties";
            String filePath = new File(Configuration.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() + "/" + propFileName ;
            String decodedPath = URLDecoder.decode(filePath, "UTF-8");
            InputStream inputStream = new FileInputStream(decodedPath);
            Properties prop = new Properties();
            if (inputStream != null) {
                    prop.load(inputStream);
            } else {
                    throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            NICAD_HOST = prop.getProperty("NICAD_HOST");
            NICAD_PORT = prop.getProperty("NICAD_PORT");
            NICAD_DATABASE = prop.getProperty("NICAD_DATABASE");
            NICAD_SCHEMA = prop.getProperty("NICAD_SCHEMA");
            NICAD_DATABASE_USER = prop.getProperty("NICAD_DATABASE_USER");
            NICAD_DATABASE_PASSWD = prop.getProperty("NICAD_DATABASE_PASSWD");
            SIF_HOST = prop.getProperty("SIF_HOST");
            SIF_PORT = prop.getProperty("SIF_PORT");
            SIF_DATABASE = prop.getProperty("SIF_DATABASE");
            SIF_SCHEMA = prop.getProperty("SIF_SCHEMA");
            SIF_DATABASE_USER = prop.getProperty("SIF_DATABASE_USER");
            SIF_DATABASE_PASSWD = prop.getProperty("SIF_DATABASE_PASSWD");
            return true;
        }
        catch (IOException | URISyntaxException exp){
            return false;
        }
    }

    public String getNICAD_SCHEMA() {
        return NICAD_SCHEMA;
    }

    public void setNICAD_SCHEMA(String NICAD_SCHEMA) {
        this.NICAD_SCHEMA = NICAD_SCHEMA;
    }

    public String getSIF_SCHEMA() {
        return SIF_SCHEMA;
    }

    public void setSIF_SCHEMA(String SIF_SCHEMA) {
        this.SIF_SCHEMA = SIF_SCHEMA;
    }

    
    
    public String getNICAD_HOST() {
        return NICAD_HOST;
    }

    public void setNICAD_HOST(String NICAD_HOST) {
        this.NICAD_HOST = NICAD_HOST;
    }

    public String getNICAD_PORT() {
        return NICAD_PORT;
    }

    public void setNICAD_PORT(String NICAD_PORT) {
        this.NICAD_PORT = NICAD_PORT;
    }

    public String getNICAD_DATABASE() {
        return NICAD_DATABASE;
    }

    public void setNICAD_DATABASE(String NICAD_DATABASE) {
        this.NICAD_DATABASE = NICAD_DATABASE;
    }

    public String getNICAD_DATABASE_USER() {
        return NICAD_DATABASE_USER;
    }

    public void setNICAD_DATABASE_USER(String NICAD_DATABASE_USER) {
        this.NICAD_DATABASE_USER = NICAD_DATABASE_USER;
    }

    public String getNICAD_DATABASE_PASSWD() {
        return NICAD_DATABASE_PASSWD;
    }

    public void setNICAD_DATABASE_PASSWD(String NICAD_DATABASE_PASSWD) {
        this.NICAD_DATABASE_PASSWD = NICAD_DATABASE_PASSWD;
    }

    public String getSIF_HOST() {
        return SIF_HOST;
    }

    public void setSIF_HOST(String SIF_HOST) {
        this.SIF_HOST = SIF_HOST;
    }

    public String getSIF_PORT() {
        return SIF_PORT;
    }

    public void setSIF_PORT(String SIF_PORT) {
        this.SIF_PORT = SIF_PORT;
    }

    public String getSIF_DATABASE() {
        return SIF_DATABASE;
    }

    public void setSIF_DATABASE(String SIF_DATABASE) {
        this.SIF_DATABASE = SIF_DATABASE;
    }

    public String getSIF_DATABASE_USER() {
        return SIF_DATABASE_USER;
    }

    public void setSIF_DATABASE_USER(String SIF_DATABASE_USER) {
        this.SIF_DATABASE_USER = SIF_DATABASE_USER;
    }

    public String getSIF_DATABASE_PASSWD() {
        return SIF_DATABASE_PASSWD;
    }

    public void setSIF_DATABASE_PASSWD(String SIF_DATABASE_PASSWD) {
        this.SIF_DATABASE_PASSWD = SIF_DATABASE_PASSWD;
    }
    
    
}
