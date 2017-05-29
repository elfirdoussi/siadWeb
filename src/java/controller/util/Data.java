/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author AIMAD
 */
public class Data {

    private static Data instance;

    private int nbrePeriodes;
    private int nbreAteliers;
    private int nbreCommandes;
    private int nbreQualites;

    private Double[] dateRelativeDebutEffectifProd;
    private Double[] dateRelativeDebutLancementProd;
    private Double[] dateRelativeFin;

    private Double[] ligneChoisi;
    private List<String> arretRelatif = new ArrayList<>();
    private HashMap<Integer, String> qualiteParCommande = new HashMap<>();
    private HashMap<Integer, List<String>> arretParLigne = new HashMap();
    private List<String> distinctQualite = new ArrayList<>();

    private Double[] productionParCommande;
    private Double[] consommationParCommande;
    
    private HashMap<Integer,Double[]> tempsAbsoluParAtelier;
    private List<String> ateliersList = new ArrayList<>();

    public int getNbrePeriodes() {
        return nbrePeriodes;
    }

    public void setNbrePeriodes(int nbrePeriodes) {
        this.nbrePeriodes = nbrePeriodes;
    }

    public int getNbreAteliers() {
        return nbreAteliers;
    }

    public void setNbreAteliers(int nbreAteliers) {
        this.nbreAteliers = nbreAteliers;
    }

    public int getNbreCommandes() {
        return nbreCommandes;
    }

    public void setNbreCommandes(int nbreCommandes) {
        this.nbreCommandes = nbreCommandes;
    }

    public int getNbreQualites() {
        return nbreQualites;
    }

    public void setNbreQualites(int nbreQualites) {
        this.nbreQualites = nbreQualites;
    }

    public Double[] getDateRelativeDebutEffectifProd() {
        if (dateRelativeDebutEffectifProd == null) {
            dateRelativeDebutEffectifProd = new Double[nbreCommandes];
        }
        return dateRelativeDebutEffectifProd;
    }

    public void setDateRelativeDebutEffectifProd(Double[] dateRelativeDebutEffectifProd) {
        this.dateRelativeDebutEffectifProd = dateRelativeDebutEffectifProd;
    }

    public Double[] getDateRelativeDebutLancementProd() {
        if(dateRelativeDebutLancementProd == null){
            dateRelativeDebutLancementProd = new Double[nbreCommandes];
        }
        return dateRelativeDebutLancementProd;
    }

    public void setDateRelativeDebutLancementProd(Double[] dateRelativeDebutLancementProd) {
        this.dateRelativeDebutLancementProd = dateRelativeDebutLancementProd;
    }

    public Double[] getDateRelativeFin() {
        if(dateRelativeFin == null){
            dateRelativeFin = new Double[nbreCommandes];
        }
        return dateRelativeFin;
    }

    public void setDateRelativeFin(Double[] dateRelativeFin) {
        this.dateRelativeFin = dateRelativeFin;
    }

    public Double[] getLigneChoisi() {
        if(ligneChoisi == null){
            ligneChoisi = new Double[nbreCommandes];
        }
        return ligneChoisi;
    }

    public void setLigneChoisi(Double[] ligneChoisi) {
        this.ligneChoisi = ligneChoisi;
    }

    public List<String> getArretRelatif() {
        return arretRelatif;
    }

    public void setArretRelatif(List<String> arretRelatif) {
        this.arretRelatif = arretRelatif;
    }

    public HashMap<Integer, String> getQualiteParCommande() {
        return qualiteParCommande;
    }

    public void setQualiteParCommande(HashMap<Integer, String> qualiteParCommande) {
        this.qualiteParCommande = qualiteParCommande;
    }

    public HashMap<Integer, List<String>> getArretParLigne() {
        return arretParLigne;
    }

    public void setArretParLigne(HashMap<Integer, List<String>> arretParLigne) {
        this.arretParLigne = arretParLigne;
    }

    public Double[] getProductionParCommande() {
        if(productionParCommande == null){
            productionParCommande = new Double[nbreCommandes];
        }
        return productionParCommande;
    }

    public void setProductionParCommande(Double[] productionParCommande) {
        this.productionParCommande = productionParCommande;
    }

    public List<String> getDistinctQualite() {
        return distinctQualite;
    }

    public Double[] getConsommationParCommande() {
        if(consommationParCommande == null){
            consommationParCommande = new Double[nbreCommandes];
        }
        return consommationParCommande;
    }

    public void setConsommationParCommande(Double[] consommationParCommande) {
        this.consommationParCommande = consommationParCommande;
    }

    public void setDistinctQualite(List<String> distinctQualite) {
        this.distinctQualite = distinctQualite;
    }

    public HashMap<Integer,Double[]> getTempsAbsoluParAtelier() {
        if(tempsAbsoluParAtelier == null){
            tempsAbsoluParAtelier = new HashMap<>();
        }
        return tempsAbsoluParAtelier;
    }

    public void setTempsAbsoluParAtelier(HashMap<Integer,Double[]> tempsAbsoluParAtelier) {
        this.tempsAbsoluParAtelier = tempsAbsoluParAtelier;
    }

    public List<String> getAteliersList() {
        return ateliersList;
    }

    public void setAteliersList(List<String> ateliersList) {
        this.ateliersList = ateliersList;
    }

    public static Data getInstance() {
        if (null == instance) {
            instance = new Data();
        }
        return instance;
    }

    private Data() {
    }

}
