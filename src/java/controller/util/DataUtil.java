/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;



/**
 *
 * @author AIMAD
 */
public class DataUtil {

    private Data data;

    public void loadData(String filePath) {
        data = Data.getInstance();
        List<String> lines = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath + "\\result1.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Fichier non trouve");
        }

        String ligne[] = lines.get(1).split("\\ ");
        data.setNbrePeriodes(getNbrePeriodes(ligne));
        data.setNbreAteliers(getNbreAteliers(ligne));
        data.setNbreCommandes(getNbreCommandes(ligne));
        data.setNbreQualites(getNbreQualite(ligne));

        String ligne2[] = lines.get(19).split("\\ ");
        data.setDateRelativeDebutEffectifProd(getDoubles(ligne2, data.getDateRelativeDebutEffectifProd()));

        String ligne3[] = lines.get(21).split("\\ ");
        data.setDateRelativeDebutLancementProd(getDoubles(ligne3, data.getDateRelativeDebutLancementProd()));

        String ligne4[] = lines.get(17).split("\\ ");
        data.setDateRelativeFin(getDoubles(ligne4, data.getDateRelativeFin()));

        String ligne5[] = lines.get(12).split("\\ ");
        data.setLigneChoisi(getDoubles(ligne5, data.getLigneChoisi()));

        String ligne6[] = lines.get(9).split("\\ ");
        
        String ligne7[] = lines.get(14).split("\\ ");
        data.setAteliersList(initAtelierList(ligne7));
        
        initQualiteParCommandeMap(ligne6);
        data.setDistinctQualite(getDistinctQualite(ligne6));
        data.getDistinctQualite().forEach(e -> System.out.println("errrrrrrrr " + e));

        initTempsAbsoluParAteliers(lines);
        initConsommationParCommande(lines);
        initArretsRelatif(lines);
        initArretParLigne(lines);
        initProductionParCommande(lines);

    }

    private int getNbrePeriodes(String[] ligne) {
        return new Integer(ligne[0]);
    }

    private int getNbreAteliers(String[] ligne) {
        return new Integer(ligne[1]);
    }

    private int getNbreCommandes(String[] ligne) {
        return new Integer(ligne[2]);
    }

    private int getNbreQualite(String[] ligne) {
        return new Integer(ligne[3]);
    }

    private void initArretsRelatif(List<String> lines) {
        int s = 1;
        String ligne[] = lines.get(25 + data.getNbreAteliers()).split("\\ ");
        for (int i = 1; i <= ligne.length; i++) {
            String ss = String.valueOf(s);
            if (ligne[i - 1].equals("0")) {
                data.getArretRelatif().add(ss);
                s++;
            }
            if (ligne[i - 1].equals("1")) {
                data.getArretRelatif().add("0");
            }
        }
    }

    private void initQualiteParCommandeMap(String[] ligne) {
        for (int i = 1; i < ligne.length; i++) {
            data.getQualiteParCommande().put(i, ligne[i]);
        }
    }

    private void initProductionParCommande(List<String> lines) {
        for (int i = 0; i < data.getNbreCommandes(); i++) {
            String ligne[] = lines.get(27 + 2 * data.getNbreAteliers() + data.getLigneChoisi()[i].intValue()).split("\\ ");
            data.getProductionParCommande()[i] = Double.valueOf(ligne[i]);
        }
    }

    private void initConsommationParCommande(List<String> lines) {
        for (int i = 0; i < data.getNbreCommandes(); i++) {
            String ligne[] = lines.get(29 + 3 * data.getNbreAteliers() + data.getLigneChoisi()[i].intValue()).split("\\ ");
            data.getConsommationParCommande()[i] = Double.valueOf(ligne[i]);
        }
    }

    private void initArretParLigne(List<String> lines) {
        for (int i = 0; i < data.getNbreAteliers(); i++) {
            String ligne[] = lines.get(23 + (i+1)).split("\\ ");
//            String ligne[] = lines.get(23 + data.getLigneChoisi()[i].intValue()).split("\\ ");
            data.getArretParLigne().put(i + 1, initArretLigne(ligne));
        }
    }

    private List<String> initArretLigne(String[] line) {
        List<String> listArret = new ArrayList<>();
        for (int i = 1; i <= data.getNbrePeriodes(); i++) {
            String a = String.valueOf(i);
            boolean b = false;
            for (int j = 0; j < line.length; j++) {
                if (line[j].equals(a)) {
                    b = true;
                }
            }
            if (b == false) {
                listArret.add(a);
            }
        }
        return listArret;
    }

    private List<String> initAtelierList(String[] line) {
        List<String> ateliers = new ArrayList<>();
        for (int i = 0; i < data.getNbreAteliers(); i++) {
            ateliers.add(line[i]);
        }
        return ateliers;
    }

    private void initTempsAbsoluParAteliers(List<String> lignes) {
        for (int i = 1; i <= Data.getInstance().getNbreAteliers(); i++) {
            String[] ligne = lignes.get(23 + i).split("\\ ");
            Data.getInstance().getTempsAbsoluParAtelier().put(i, getDoubles(ligne, new Double[ligne.length]));
        }
    }

    private Double[] getDoubles(String[] ligne, Double[] tab) {
        for (int i = 0; i < tab.length; i++) {
            tab[i] = Double.valueOf(ligne[i]);
        }
        return tab;
    }

    private boolean[] getBooleans(String[] ligne) {
        boolean[] tab = new boolean[ligne.length];
        for (int i = 0; i < ligne.length; i++) {
            tab[i] = ligne[i].equals("1");
        }
        return tab;
    }

    private List<String> getDistinctQualite(String listqualite[]) {
        List<String> qualite = new ArrayList<String>();
        for (int i = 1; i < listqualite.length; i++) {
            boolean isdistinct = false;
            for (int j = 0; j < i; j++) {
                if (listqualite[j].equals(listqualite[i])) {
                    isdistinct = true;
                    break;
                }
            }
            if (!isdistinct) {
                qualite.add(listqualite[i]);
            }

        }
        return qualite;
    }
}
