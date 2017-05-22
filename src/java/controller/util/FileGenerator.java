/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.util;

import entities.CalendrierMaintenance;
import entities.CarnetCommande;
import entities.CarnetCommandeOf;
import entities.DoodleGamme;
import entities.Engrais;
import entities.Gamme;
import entities.Lancement;
import entities.Ligne;
import entities.NomenclatureItem;
import entities.ProblemeLigneOuverte;
import entities.ProblemePost;
import entities.ProblemePre;
import entities.RegimeMarche;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import service.CalendrierMaintenanceFacade;
import service.CarnetCommandeOfFacade;
import service.ConfigFacade;
import service.DoodleGammeFacade;
import service.EngraisFacade;
import service.GammeFacade;
import service.InputFacade;
import service.LancementFacade;
import service.LigneFacade;
import service.NomenclatureFacade;
import service.NomenclatureItemFacade;
import service.ProblemeLigneOuverteFacade;
import service.ProblemePostFacade;
import service.ProblemePreFacade;
import service.RegimeMarcheFacade;

/**
 *
 * @author Aimad.JAOUHAR
 */
@Stateless
public class FileGenerator {
    
    @EJB
    private ConfigFacade configFacade;
    @EJB
    private ProblemePreFacade problemePreFacade;
    @EJB
    private ProblemePostFacade problemePostFacade;
    @EJB
    private ProblemeLigneOuverteFacade problemeLigneOuverteFacade;
    @EJB
    private CarnetCommandeOfFacade carnetCommandeOfFacade;
    @EJB
    private DoodleGammeFacade doodleGammeFacade;
    @EJB
    private LigneFacade ligneFacade;
    @EJB
    private GammeFacade gammeFacade;
    @EJB
    private RegimeMarcheFacade regimeMarcheFacade;
    @EJB
    private NomenclatureFacade nomenclatureFacade;
    @EJB
    private NomenclatureItemFacade nomenclatureItemFacade;
    @EJB
    private InputFacade inputFacade;
    @EJB
    private CalendrierMaintenanceFacade calendrierMaintenanceFacade;
    @EJB
    private EngraisFacade engraisFacade;
    @EJB
    private LancementFacade lancementFacade;
    
    private int nbrprd = 0;
    private int nbLigne = 0;
    private int nbCommande = 0;
    
    FileUtil fileUtil = new FileUtil();
    
    public void generate(ProblemePre problemePre, ProblemePost problemePost) throws FileNotFoundException {
        try {
            String generationDirectory = configFacade.getDataFilePath();
            
            Path path = Paths.get(generationDirectory + "/fichier.dat");
            fileUtil.getData().clear();
            
            fileUtil.prepareFile(path, generationDirectory);
            List<String> listLignes = new ArrayList<>();
            List<String> listLignes2 = new ArrayList<>();
            List<Integer> listNumLignes = new ArrayList<>();
            List<Integer> listNumLignes2 = new ArrayList<>();
            
            Vector vectDebitProd = new Vector();
            Vector vectDebitAcide = new Vector();
//
            List<CarnetCommandeOf> carnetCommandeOfs = carnetCommandeOfFacade.findByAndOrderByCarnetCommande(problemePre.getCarnetCommande());
            List<CarnetCommandeOf> carnetCommandeOfEnCours = carnetCommandeOfFacade.findCarnetEnCours(problemePre.getCarnetCommande());
            List<ProblemeLigneOuverte> problemeLigneOuvertes = problemeLigneOuverteFacade.findByProblemePreOrderByOrder(problemePre);
//
            nbrprd = (int) DateUtil.dateDiffEnHeure(problemePre.getDateFin(), problemePre.getDateDebut());
            nbLigne = problemeLigneOuverteFacade.countProblemeLigneOuvert(problemePre);
            nbCommande = carnetCommandeOfs.size();
            
//            writeIdProblemePost(problemePost.getId().intValue());
            writeNbrePeriodes();
            writeNbreLigneOuverte();
            
            writeNbreCommande();
            
            writeNbreEngraisUtilise(carnetCommandeOfFacade.countEngraisUtilise(problemePre.getCarnetCommande()));
            writeCritereSoulition(problemePostFacade.findByProblemePre(problemePre));
            
            prepareListLignesOuverte(listLignes, listLignes2, listNumLignes, listNumLignes2, problemeLigneOuvertes);
            
            writeLigneOuverteUtilise(listLignes, listLignes2);
            
            writePonderation(carnetCommandeOfs);
            
            writeValeurDoodleGamme(problemePre.getCarnetCommande(), listNumLignes, carnetCommandeOfs);
            writeDebitAcideP(vectDebitAcide, problemePre, listNumLignes, listNumLignes2, carnetCommandeOfs);
            
            writeDebitProduction(vectDebitProd, carnetCommandeOfs, listNumLignes, listNumLignes2, problemePre);
            
            writeDateLiveTot(problemePre, carnetCommandeOfs, carnetCommandeOfEnCours, listNumLignes, listNumLignes2);
            
            writeDateLiveTard(problemePre, carnetCommandeOfs, carnetCommandeOfEnCours, listNumLignes, listNumLignes2);
            
            writeCommandeReference(carnetCommandeOfs);
            
            writeReferenceUtilisee(problemePre);
            
            writeTTAvecL(vectDebitProd, carnetCommandeOfs, listNumLignes, listNumLignes2, problemePre);
            
            writeTTSansL(vectDebitProd, carnetCommandeOfs, listNumLignes, listNumLignes2);
            
            writeCoutCommande(carnetCommandeOfs, listNumLignes, listNumLignes2);
            
            writeArret(problemePre, listNumLignes);
            
            fileUtil.save(path, generationDirectory);
        } catch (IOException ex) {
            Logger.getLogger(FileGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeIdProblemePost(int id) {
        fileUtil.printLine("Id_problem: " + id);
    }
    
    private void writeNbrePeriodes() {
//        fileUtil.writeLn("Nb_periodes: " + nbrprd);
        fileUtil.printLine("Nb_periodes: " + nbrprd);
    }
    
    private void writeNbreLigneOuverte() {
//        fileUtil.writeLn("Nombre_ateliers: " + nbLigne);
        fileUtil.printLine("Nombre_ateliers: " + nbLigne);
    }
    
    private void writeNbreCommande() {
//        fileUtil.writeLn("Nombre_commande: " + nbCommande);
        fileUtil.printLine("Nombre_commande: " + nbCommande);
    }
    
    private void writeNbreEngraisUtilise(int nbreEngrais) {
//        fileUtil.writeLn("Nb_ref_utilisee: " + nbreEngrais);
        fileUtil.printLine("Nb_ref_utilisee: " + nbreEngrais);
    }
    
    private void writeCritereSoulition(ProblemePost problemePost) {
//        fileUtil.writeLn("Critere_opt: " + problemePost.getCritereOptimisation().getNumCritere());
        fileUtil.printLine("Critere_opt: " + problemePost.getCritereOptimisation().getNumCritere());
    }
    
    private void prepareListLignesOuverte(List<String> listLignes, List<String> listLignes2, List<Integer> listNumLignes, List<Integer> listNumLignes2, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        problemeLigneOuvertes.forEach((p) -> {
            listLignes.add(p.getLigne().getNomLigne());
            listNumLignes.add(p.getLigne().getNumeroLigne().intValue());
            if (p.getLigne2() != null) {
                listLignes2.add(p.getLigne2().getNomLigne());
                listNumLignes2.add(p.getLigne2().getNumeroLigne().intValue());
            } else {
                listLignes2.add("NC");
                listNumLignes2.add(-1);
            }
        });
    }
    
    private void writeLigneOuverteUtilise(List<String> listLignes, List<String> listLignes2) {
//        fileUtil.write("Atelier: [ ");
        String line = "Atelier: [ ";
        for (int i = 0; i < listLignes.size(); i++) {
            if (!listLignes2.get(i).equals("NC")) {
//                fileUtil.write("  " + listLignes.get(i) + "&" + listLignes2.get(i) + "  ");
                line += ("  " + listLignes.get(i) + "&" + listLignes2.get(i) + "  ");
            } else {
                line += ("  " + listLignes.get(i) + "  ");
//                fileUtil.write("  " + listLignes.get(i) + "  ");
            }
        }
//        fileUtil.writeLn("] ");
        line += ("] ");
        fileUtil.printLine(line);
    }
    
    private void writePonderation(List<CarnetCommandeOf> carnetCommandeOfs) {
        String line = "";
//        fileUtil.write("Ponderation: [ ");
        line += ("Ponderation: [ ");
        
        for (CarnetCommandeOf carnetCommandeOf : carnetCommandeOfs) {
//            fileUtil.write(" " + carnetCommandeOf.getPonderation() + " ");
            line += (" " + carnetCommandeOf.getPonderation() + " ");
        }
//        fileUtil.writeLn("] ");
        line += ("] ");
        fileUtil.printLine(line);
    }
    
    private void writeValeurDoodleGamme(CarnetCommande carnetCommande, List<Integer> listNumLignes, List<CarnetCommandeOf> carnetCommandeOfs) {
        String line = "";
//        fileUtil.write("Bin_A_C: [");
        line += ("Bin_A_C: [");
        for (int i = 0; i < listNumLignes.size(); i++) {
            line += writeValeurDoodleGammeItem(i, carnetCommande, listNumLignes.get(i), carnetCommandeOfs);
            int j = 0;
            do {
                if (i == j) {
//                    fileUtil.write(" 1 ");
                    line += (" 1 ");
                } else {
//                    fileUtil.write(" 0 ");
                    line += (" 0 ");
                }
                j++;
            } while (j < nbLigne);
//            fileUtil.writeLn("  ");
            line += ("  ");
        }
//        fileUtil.writeLn("] ");
        line += ("] ");
        fileUtil.printLine(line);
    }
    
    private String writeValeurDoodleGammeItem(int index, CarnetCommande carnetCommande, int numLigne, List<CarnetCommandeOf> carnetCommandeOfs) {
        String strLigne = "";
        Ligne ligne = ligneFacade.find(new Long(numLigne));
        if (ligne != null) {
            for (int i = 0; i < carnetCommandeOfs.size(); i++) {
                List<DoodleGamme> doodleGammes = doodleGammeFacade.findByCommandeOfAndTypeLigne(carnetCommande, carnetCommandeOfs.get(i), ligne.getTypeLigne());
//                fileUtil.write(" " + (doodleGammes.get(0).isValeur() ? 1 : 0) + " ");
                strLigne += (" " + (doodleGammes.get(0).isValeur() ? 1 : 0) + " ");
            }
        }
        return strLigne;
    }
    
    private void writeDebitAcideP(Vector vectDebitAcide, ProblemePre problemePre, List<Integer> listNumLignes, List<Integer> listNumLignes2, List<CarnetCommandeOf> carnetCommandeOfs) {
        String line = "";
//        fileUtil.write("Debit_acide_P:  [");
        line += ("Debit_acide_P:  [");
        for (int i = 0; i < listNumLignes.size(); i++) {
            Integer numL = (Integer) listNumLignes.get(i);
            Integer numL2 = (Integer) listNumLignes2.get(i);
            Ligne ligne = ligneFacade.find(new Long(numL));
            Ligne ligne2 = ligneFacade.find(new Long(numL2));
            Vector vectDebitAcideparLigne = new Vector();
            line += writeDebitAcidePItem(vectDebitAcideparLigne, carnetCommandeOfs, ligne, ligne2, problemePre);
            vectDebitAcide.addElement(vectDebitAcideparLigne);
//            fileUtil.writeLn("   ");
            line += ("   ");
        }
//        fileUtil.writeLn("]");
        line += ("]");
        fileUtil.printLine(line);
    }
    
    private String writeDebitAcidePItem(Vector vectDebitAcideparLigne, List<CarnetCommandeOf> carnetCommandeOfs, Ligne ligne, Ligne ligne2, ProblemePre problemePre) {
        String strLigne = "";
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            Integer numOf = (Integer) carnetCommandeOfs.get(i).getNumeroOf().intValue();
            Integer numE = (Integer) carnetCommandeOfs.get(i).getEngrais().getNumero().intValue();
            Engrais engrais = carnetCommandeOfs.get(i).getEngrais();
            DoodleGamme doodleGamme = doodleGammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
            
            if (doodleGamme.isValeur()) {
                float a2 = 0;
                float a3 = 0;
                Gamme gamme = gammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
                
                if (gamme != null) {
                    a2 = gamme.getQtteProduite() * gamme.getRatio();
                }
                
                if (ligne2 != null) {
                    Gamme gamme2 = gammeFacade.findByEngraisAndTypeLigne(engrais, ligne2.getTypeLigne());
                    a2 = a2 + (gamme2.getQtteProduite() * gamme2.getRatio());
                }

                //pour le couplage on suppose le meme regime de marche
                RegimeMarche regimeMarche = regimeMarcheFacade.findByLigneAndProblemePre(problemePre, ligne);
                if (regimeMarche != null) {
                    a3 = regimeMarche.getValeur();
                }
                
                float s = 0;
                NomenclatureItem nomenclatureItem = nomenclatureItemFacade.findByEngraisAndTypeLigneAndInput(engrais, ligne.getTypeLigne(), inputFacade.find(2l));
                if (nomenclatureItem != null) {
                    s = nomenclatureItem.getQtteInput();
                }
                float a5 = a2 * a3 * s;
//                fileUtil.write("  " + new Float(a5).intValue() + "  ");
                strLigne += ("  " + new Float(a5).intValue() + "  ");
                vectDebitAcideparLigne.addElement(a5);
            } else {
//                fileUtil.write("  " + "0" + "  ");
                strLigne += ("  " + "0" + "  ");
                float val = 0;
                vectDebitAcideparLigne.addElement(val);
            }
        }
        return strLigne;
    }
    
    private void writeDebitProduction(Vector vectDebitProd, List<CarnetCommandeOf> carnetCommandeOfs, List<Integer> listNumLignes, List<Integer> listNumLignes2, ProblemePre problemePre) {
        String line = "";
//        fileUtil.write("Debit_Prod:[ ");
        line += ("Debit_Prod:[ ");
        for (int i = 0; i < listNumLignes.size(); i++) {
            Vector vectDebitProdparLigne = new Vector();
            Integer numL = (Integer) listNumLignes.get(i);
            Integer numL2 = (Integer) listNumLignes2.get(i);
            Ligne ligne = ligneFacade.find(new Long(numL));
            Ligne ligne2 = ligneFacade.find(new Long(numL2));
            line += writeDebitProductionItem(vectDebitProdparLigne, carnetCommandeOfs, ligne, ligne2, problemePre);
            vectDebitProd.addElement(vectDebitProdparLigne);
//            fileUtil.writeLn("   ");
            line += ("   ");
        }
//        fileUtil.writeLn("]");
        line += ("]");
        fileUtil.printLine(line);
    }
    
    private String writeDebitProductionItem(Vector vectDebitProdparLigne, List<CarnetCommandeOf> carnetCommandeOfs, Ligne ligne, Ligne ligne2, ProblemePre problemePre) {
        String strLine = "";
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            Engrais engrais = carnetCommandeOfs.get(i).getEngrais();
            DoodleGamme doodleGamme = doodleGammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
            if (doodleGamme != null && doodleGamme.isValeur()) {
                float a2 = 0;
                float a3 = 0;
                Gamme gamme = gammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
                if (gamme != null) {
                    a2 = gamme.getQtteProduite() * gamme.getRatio();
                }
                
                if (ligne2 != null) {
                    
                    Gamme gamme2 = gammeFacade.findByEngraisAndTypeLigne(engrais, ligne2.getTypeLigne());
                    a2 = a2 + (gamme2.getQtteProduite() * gamme2.getRatio());
                    
                }
                //pour le couplage on suppose le meme regime de marche
                RegimeMarche regimeMarche = regimeMarcheFacade.findByLigneAndProblemePre(problemePre, ligne);
                if (regimeMarche != null) {
                    a3 = regimeMarche.getValeur();
                }
                
                float a6 = a2 * a3;
//                fileUtil.write("  " + new Float(a6).intValue() + "  ");
                strLine += ("  " + new Float(a6).intValue() + "  ");
                vectDebitProdparLigne.addElement(a6);
                
            } else {
//                fileUtil.write("  " + "0" + "  ");
                strLine += ("  " + "0" + "  ");
                float val = 0;
                vectDebitProdparLigne.addElement(val);
            }
        }
        return strLine;
    }
    
    private void writeDateLiveTot(ProblemePre problemePre, List<CarnetCommandeOf> carnetCommandeOfs, List<CarnetCommandeOf> carnetCommandeOfEnCours, List<Integer> listNumLignes, List<Integer> listNumLignes2) {
        String line = "";
//        fileUtil.write("Date_liv_tot: [  ");
        line += ("Date_liv_tot: [  ");
        for (int i = 0; i < listNumLignes.size(); i++) {
            Integer numL = (Integer) listNumLignes.get(i);
            Integer numL2 = (Integer) listNumLignes2.get(i);
            Ligne ligne = ligneFacade.find(new Long(numL));
            line += writeDateLiveTotItem(problemePre, i, listNumLignes.size(), carnetCommandeOfEnCours, carnetCommandeOfs, ligne);
        }
//        fileUtil.writeLn("]");
        line += ("]");
        fileUtil.printLine(line);
    }
    
    private String writeDateLiveTotItem(ProblemePre problemePre, int index, int ligneSize, List<CarnetCommandeOf> carnetCommandeOfEnCours, List<CarnetCommandeOf> carnetCommandeOfs, Ligne ligne) {
        String strLine = "";
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            
            boolean enCours = false;
            Engrais engrais = carnetCommandeOfs.get(i).getEngrais();
            
            if (exist(carnetCommandeOfs.get(i), carnetCommandeOfEnCours)) {
                int indexEncours = i;
                enCours = true;
            }
            
            int valeurbin = 0;
            DoodleGamme doodleGamme = doodleGammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
            if (doodleGamme.isValeur() && (index == i || i >= ligneSize)) {
                int livalue = 0;
                int quantity = 0;
                //remplissage liac:
                Date dateTot = null;
                CarnetCommandeOf carnetCommandeOf = carnetCommandeOfFacade.findByCarnetCommandeAndNumOf(problemePre.getCarnetCommande(), carnetCommandeOfs.get(i).getNumeroOf());
                dateTot = carnetCommandeOf.getDateLiveTot();
                quantity = carnetCommandeOf.getQuantite().intValue();
                long datetot_int = dateTot.getTime();
                Date d = problemePre.getDateDebut();
                long dint = d.getTime();
                int npa = (int) DateUtil.dateDiffEnHeure(dateTot, problemePre.getDateDebut());
                
                Gamme gamme = gammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
                int delaiProd = (int) DateUtil.calculTempsProductionParLigne(quantity, (gamme == null ? -1 : gamme.getQtteProduite().intValue()));
                int npaMin = npa - delaiProd;

                // deduire la periode de maintenance
                livalue = npa - calendrierMaintenanceFacade.sumValeurPeriodeByProblemePreAndLigneAndPeriode(problemePre, ligne, npa);
//                fileUtil.write("  " + livalue + " ");
                strLine += ("  " + livalue + " ");
            } else {
//                fileUtil.write(" 0 ");
                strLine += (" 0 ");
            }
        }
        int i = 0;
        do {
            if (index == i) {
//                fileUtil.write(" 1 ");
                strLine += (" 1 ");
            } else {
//                fileUtil.write(" 0 ");
                strLine += (" 0 ");
            }
            i++;
        } while (i < nbLigne);
//        fileUtil.writeLn("   ");
        strLine += ("   ");
        return strLine;
    }
    
    private void writeDateLiveTard(ProblemePre problemePre, List<CarnetCommandeOf> carnetCommandeOfs, List<CarnetCommandeOf> carnetCommandeOfEnCours, List<Integer> listNumLignes, List<Integer> listNumLignes2) {
        String line = "";
//        fileUtil.write("Date_liv_tard: [  ");
        line += ("Date_liv_tard: [  ");
        for (int i = 0; i < listNumLignes.size(); i++) {
            Integer numL = (Integer) listNumLignes.get(i);
            Integer numL2 = (Integer) listNumLignes2.get(i);
            Ligne ligne = ligneFacade.find(new Long(numL));
            Ligne ligne2 = ligneFacade.find(new Long(numL2));
            line += writeDateLiveTardItem(problemePre, i, listNumLignes.size(), carnetCommandeOfEnCours, carnetCommandeOfs, ligne, ligne2);
        }
//        fileUtil.writeLn("]");
        line += ("]");
        fileUtil.printLine(line);
    }
    
    private String writeDateLiveTardItem(ProblemePre problemePre, int index, int ligneSize, List<CarnetCommandeOf> carnetCommandeOfEnCours, List<CarnetCommandeOf> carnetCommandeOfs, Ligne ligne, Ligne ligne2) {
        String strLigne = "";
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            Engrais engrais = carnetCommandeOfs.get(i).getEngrais();
            
            DoodleGamme doodleGamme = doodleGammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
            if (doodleGamme.isValeur() && (index == i || i >= ligneSize)) {
                int lsValue = 0;
                int quantity = 0;
                
                CarnetCommandeOf carnetCommandeOf = carnetCommandeOfFacade.findByCarnetCommandeAndNumOf(problemePre.getCarnetCommande(), carnetCommandeOfs.get(i).getNumeroOf());
                Date dateTard = carnetCommandeOf.getDateLiveTard();
                quantity = carnetCommandeOf.getQuantite().intValue();
                
                long datetardInt = dateTard.getTime();
                long dint = problemePre.getDateDebut().getTime();
                
                int npa = (int) DateUtil.dateDiffEnHeure(dateTard, problemePre.getDateDebut());
                
                Gamme gamme = gammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
                int delaiProd = (int) DateUtil.calculTempsProductionParLigne(quantity, (gamme == null ? -1 : gamme.getQtteProduite().intValue()));
                int npaMin = npa - delaiProd;

                // deduire la periode de maintenance
                lsValue = npa - calendrierMaintenanceFacade.sumValeurPeriodeByProblemePreAndLigneAndPeriode(problemePre, ligne, npa);
//                fileUtil.write("  " + lsValue + " ");
                if (ligne2 != null) {
                    lsValue = lsValue / 2;
                }
//                fileUtil.write("  " + lsValue + " ");
                strLigne += ("  " + lsValue + " ");
            } else {
//                fileUtil.write(" 0 ");
                strLigne += (" 0 ");
            }
        }
        int i = 0;
        do {
            if (index == i) {
//                fileUtil.write(" " + nbrprd + " ");
                strLigne += (" " + nbrprd + " ");
            } else {
//                fileUtil.write(" 0 ");
                strLigne += (" 0 ");
            }
            i++;
        } while (i < nbLigne);
//        fileUtil.writeLn("   ");
        strLigne += ("   ");
        return strLigne;
    }
    
    private void writeCommandeReference(List<CarnetCommandeOf> carnetCommandeOfs) {
        String line = "";
//        fileUtil.write("Commande_Reference:	[");
        line += ("Commande_Reference:	[");
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            Engrais engrais = carnetCommandeOfs.get(i).getEngrais();
//            fileUtil.write("  " + engrais.getLibelle() + "  ");
            line += ("  " + engrais.getLibelle() + "  ");
        }
//        fileUtil.writeLn(" ]");
        line += (" ]");
        fileUtil.printLine(line);
    }
    
    private void writeReferenceUtilisee(ProblemePre problemePre) {
        String line = "";
//        fileUtil.write("Ref_utilisee:   [");
        line += ("Ref_utilisee:   [");
        List<Engrais> engraises = carnetCommandeOfFacade.findEngraisUtilise(problemePre.getCarnetCommande());
        for (int i = 0; i < engraises.size(); i++) {
//            fileUtil.write("  " + engraises.get(i).getLibelle() + "  ");
            line += ("  " + engraises.get(i).getLibelle() + "  ");
        }
//        fileUtil.writeLn(" ]");
        line += (" ]");
        fileUtil.printLine(line);
    }
    
    private void writeTTAvecL(Vector vectDebitProd, List<CarnetCommandeOf> carnetCommandeOfs, List<Integer> listNumLignes, List<Integer> listNumLignes2, ProblemePre problemePre) {
        String line = "";
//        fileUtil.write("TT_avec_L:  [");
        line += ("TT_avec_L:  [");
        for (int i = 0; i < listNumLignes.size(); i++) {
            Integer numL = listNumLignes.get(i);
            Integer numL2 = listNumLignes2.get(i);
            Ligne ligne = ligneFacade.find(new Long(numL));
            line += writeTTAvecLItem(vectDebitProd, problemePre, i, listNumLignes.size(), carnetCommandeOfs, ligne);
        }
//        fileUtil.writeLn("]");
        line += ("]");
        fileUtil.printLine(line);
    }
    
    private String writeTTAvecLItem(Vector vectDebitProd, ProblemePre problemePre, int index, int ligneSize, List<CarnetCommandeOf> carnetCommandeOfs, Ligne ligne) {
        String strLine = "";
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            Engrais engrais = carnetCommandeOfs.get(i).getEngrais();
            
            DoodleGamme doodleGamme = doodleGammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
            for (int j = 0; j < carnetCommandeOfs.size(); j++) {
                Engrais engrais2 = carnetCommandeOfs.get(j).getEngrais();
                DoodleGamme doodleGamme2 = doodleGammeFacade.findByEngraisAndTypeLigne(engrais2, ligne.getTypeLigne());
                if (doodleGamme2.isValeur() && doodleGamme.isValeur() && (j == index || j >= ligneSize)) {
                    float tl = 0;
                    List<Lancement> lancement = lancementFacade.findByCritere(ligne.getTypeLigne(), carnetCommandeOfs.get(i).getEngrais(), carnetCommandeOfs.get(j).getEngrais());
                    if (!lancement.isEmpty()) {
                        if (lancement.get(0) == null) {
                        } else {
                            if (lancement.get(0).getTempsl() == null) {
                            } else {
                                tl = lancement.get(0).getTempsl();
                            }
                            
                        }
                    }
                    float dphac = 0;
                    Vector vecrDebitParLigne = (Vector) vectDebitProd.elementAt(index);
                    dphac = (Float) vecrDebitParLigne.elementAt(j);
                    float w = carnetCommandeOfs.get(j).getQuantite();
                    double h = tl + Math.ceil(w / dphac);
//                    fileUtil.write("  " + new Double(h).intValue() + "  ");
                    strLine += ("  " + new Double(h).intValue() + "  ");
                } else {
//                    fileUtil.write("  " + "0" + "  ");
                    strLine += ("  " + "0" + "  ");
                }
            }
            if (index == nbLigne - 1 && i == nbCommande - 1) {
//                fileUtil.write(" ");
                strLine += (" ");
            } else {
//                fileUtil.writeLn(" ");
                strLine += (" ");
            }
        }
        return strLine;
    }
    
    private void writeTTSansL(Vector vectDebitProd, List<CarnetCommandeOf> carnetCommandeOfs, List<Integer> listNumLignes, List<Integer> listNumLignes2) {
        String line = "";
//        fileUtil.write("TT_sans_L:  [");
        line += ("TT_sans_L:  [");
        for (int i = 0; i < listNumLignes.size(); i++) {
            Integer numL = (Integer) listNumLignes.get(i);
            Integer numL2 = (Integer) listNumLignes2.get(i);
            Ligne ligne = ligneFacade.find(new Long(numL));
            Ligne ligne2 = ligneFacade.find(new Long(numL2));
            Vector vecrDebitParLigne = (Vector) vectDebitProd.elementAt(i);
            line += writeTTSansLItem(vecrDebitParLigne, i, listNumLignes.size(), carnetCommandeOfs, ligne);
        }
//        fileUtil.writeLn("]");
        line += ("]");
        fileUtil.printLine(line);
    }
    
    private String writeTTSansLItem(Vector vecrDebitParLigne, int index, int ligneSize, List<CarnetCommandeOf> carnetCommandeOfs, Ligne ligne) {
        String strLine = "";
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            Engrais engrais = carnetCommandeOfs.get(i).getEngrais();
            Float debit = (Float) vecrDebitParLigne.elementAt(index); //   i

            float w = 0;
            w = carnetCommandeOfs.get(i).getQuantite();
            
            DoodleGamme doodleGamme = doodleGammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
            
            if (doodleGamme.isValeur() && (index == i || i >= ligneSize)) {
                double tte = Math.ceil(w / debit);
//                fileUtil.write("  " + new Float(tte).intValue() + "  ");
                strLine += ("  " + new Float(tte).intValue() + "  ");
            } else {
//                fileUtil.write("  " + "0" + "  ");
                strLine += ("  " + "0" + "  ");
            }
        }
//        fileUtil.writeLn("   ");
        strLine += ("   ");
        return strLine;
    }
    
    private void writeCoutCommande(List<CarnetCommandeOf> carnetCommandeOfs, List<Integer> listNumLignes, List<Integer> listNumLignes2) {
        String line = "";
//        fileUtil.write("Cout_commande:   [");
        line += ("Cout_commande:   [");
        for (int i = 0; i < listNumLignes.size(); i++) {
            Integer numL = (Integer) listNumLignes.get(i);
            Integer numL2 = (Integer) listNumLignes2.get(i);
            Ligne ligne = ligneFacade.find(new Long(numL));
            Ligne ligne2 = ligneFacade.find(new Long(numL2));
            line += writeCoutCommandeItem(i, ligne, ligne2, carnetCommandeOfs);
        }
//        fileUtil.writeLn("]");
        line += ("]");
        fileUtil.printLine(line);
    }
    
    private String writeCoutCommandeItem(int index, Ligne ligne, Ligne ligne2, List<CarnetCommandeOf> carnetCommandeOfs) {
        String strLine = "";
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            Engrais engrais = carnetCommandeOfs.get(i).getEngrais();
            
            DoodleGamme doodleGamme = doodleGammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
            
            for (int j = nbLigne; j < carnetCommandeOfs.size(); j++) {
                Engrais engrais2 = carnetCommandeOfs.get(j).getEngrais();
                DoodleGamme doodleGamme2 = doodleGammeFacade.findByEngraisAndTypeLigne(engrais2, ligne.getTypeLigne());
                
                if (doodleGamme.isValeur() && doodleGamme2.isValeur()) {
                    float cl = 0;
                    List<Lancement> lancements = lancementFacade.findByCritere(ligne.getTypeLigne(), engrais, engrais2);
                    if (!lancements.isEmpty()) {
                        if (lancements.get(0).getCoutl() != null) {
                            cl = lancements.get(0).getCoutl();
                        }
                    }
                    if (ligne2 != null) {
                        List<Lancement> lancements2 = lancementFacade.findByCritere(ligne2.getTypeLigne(), engrais, engrais2);
                        cl += lancements2.get(0).getCoutl();
                    }
                    float cv = 0;
                    Gamme gamme = gammeFacade.findByEngraisAndTypeLigne(engrais2, ligne.getTypeLigne());
                    if (gamme != null) {
                        cv = gamme.getCoutVariable();
                    }
                    if (ligne2 != null) {
                        Gamme gamme2 = gammeFacade.findByEngraisAndTypeLigne(engrais2, ligne2.getTypeLigne());
                        cv += gamme2.getCoutVariable();
                    }
                    
                    float ct = cl + cv;
                    //ajout commande en cours
//                    fileUtil.write("  " + new Float(ct).intValue() + "  ");
                    strLine += ("  " + new Float(ct).intValue() + "  ");
                } else {
//                    fileUtil.write("  " + "0" + "  ");
                    strLine += ("  " + "0" + "  ");
                }
            }
            if (index == nbLigne - 1 && i == nbCommande - 1) {
//                fileUtil.write(" ");
                strLine += (" ");
            } else {
//                fileUtil.writeLn(" ");
                strLine += (" ");
            }
        }
        return strLine;
    }
    
    private void writeArret(ProblemePre problemePre, List<Integer> listNumLignes) {
        //on prevoit les memes arrets pour les deux lignes
        String line = "";
//        fileUtil.write("Arret:   [");
        line += ("Arret:   [");
        for (int i = 0; i < listNumLignes.size(); i++) {
            Integer numL = (Integer) listNumLignes.get(i);
            Ligne ligne = ligneFacade.find(new Long(numL));
            int j = 1;
            do {
                List<CalendrierMaintenance> calendrierMaintenances = calendrierMaintenanceFacade.findByProblemePreAndLigneAndPeriode(problemePre, ligne, j);
                if (!calendrierMaintenances.isEmpty()) {
//                    calendrierMaintenances.forEach(c -> fileUtil.write(" " + (c.isValeur() ? 1 : 0) + " "));
                    if (!calendrierMaintenances.isEmpty()) {
                        line += (" " + (calendrierMaintenances.get(0).isValeur() ? 1 : 0) + " ");
                    }
                }
                j++;
            } while (j <= nbrprd);
//            fileUtil.write(" ");
            line += (" ");
        }
//        fileUtil.writeLn(" ]");
        line += (" ]");
        fileUtil.printLine(line);
    }
    
    private boolean exist(CarnetCommandeOf carnetCommandeOf, List<CarnetCommandeOf> carnetCommandeOfs) {
        return carnetCommandeOfs.stream().anyMatch((c) -> (c.getNumeroOf().equals(carnetCommandeOf.getNumeroOf())));
    }
}
