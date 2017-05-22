/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import controller.util.DateUtil;
import entities.CalendrierMaintenance;
import entities.CarnetCommandeOf;
import entities.Engrais;
import entities.Gamme;
import entities.Ligne;
import entities.ProblemeLigneOuverte;
import entities.ProblemePre;
import entities.RegimeMarche;
import entities.StockInput;
import entities.StockOutput;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class ProblemePreFacade extends AbstractFacade<ProblemePre> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private CarnetCommandeOfFacade carnetCommandeOfFacade;
    @EJB
    private ProblemeLigneOuverteFacade problemeLigneOuverteFacade;
    @EJB
    private RegimeMarcheFacade regimeMarcheFacade;
    @EJB
    private CalendrierMaintenanceFacade calendrierMaintenanceFacade;
    @EJB
    private StockInputFacade stockInputFacade;
    @EJB
    private StockOutputFacade stockOutputFacade;
    @EJB
    private GammeFacade gammeFacade;
    @EJB
    private ProblemePostFacade problemePostFacade;

    public Object[] analyserProbleme(ProblemePre problemePre, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        Object[] object = null;
        Object[] object2 = null;
        List<CarnetCommandeOf> carnetCommandeOfs = loadDataPoblem(problemePre, false, false);
        List<Date> periodes = preparePeriodsAnalyse(problemePre);
        HashMap<Engrais, List<Float>> chargerEngraisMap = prepareChargeEngrais(carnetCommandeOfs, problemeLigneOuvertes);
        //verifier la faisabilité du probléme
        Object[] res = checkFaisability(problemePre, carnetCommandeOfs, periodes, chargerEngraisMap, problemeLigneOuvertes);
        boolean isNotFaisable = (boolean) res[0];
        String msg = (String) res[1];
        String msg2;
        if (isNotFaisable) {
            msg2 = "No solution for the problem selected.";
            System.out.println("haaaaa "+msg2);
        } else {
            object = verifyApprochePlustard(problemePre, problemeLigneOuvertes, periodes, chargerEngraisMap);
            object2 = verifyApprochePlutot(problemePre, problemeLigneOuvertes, periodes);
            boolean verifPlusTardNotPossible = (boolean) object[0];
            boolean verifPlusTotNotPossible = (boolean) object2[0];
            if (verifPlusTardNotPossible && verifPlusTotNotPossible) {
                msg2 = "It is possible that the problem selected has no solution.";
            } else {
                msg2 = "The problem selected seems feasible.";
            }
        }
        return new Object[]{object, object2, isNotFaisable, msg, msg2, carnetCommandeOfs.size()};
    }
    
//    public void updateCommande_Table(int idcde){
//        for (int i=0; i <problemeLigneOuvertes.size();i++) {
//            Vector rowEngrais = new Vector();
//            List<Item> 
//            rowEngrais.addElement(vectEnrgais.elementAt(i));
//            rowEngrais.addElement(vectQuantiteEngrais.elementAt(i));
//            Vector vectLigne = (Vector) vectChargeEngrais.elementAt(i);
//            for (int j=0; j <vectLigne.size();j++) {
//                rowEngrais.addElement(vectLigne.elementAt(j));
//            }
//            rowEngrais.addElement(vectProdEngraisPlutot.elementAt(i));
//            rowEngrais.addElement(vectProdEngraisPlusTard.elementAt(i));
//            rowData.addElement(rowEngrais);
//        }
//    }

    public Object[] checkFaisability(ProblemePre problemePre, List<CarnetCommandeOf> carnetCommandeOfs, List<Date> periodes, HashMap<Engrais, List<Float>> chargerEngraisMap, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        boolean isNotFaisable = false;
        String msg = "";
        if (checkCharge(problemePre, carnetCommandeOfs, periodes, chargerEngraisMap, problemeLigneOuvertes)) {
            //charge demandé est supérieur à la charge programmé
            isNotFaisable = true;
            msg = "The accumulation of hours requested exceeds the work dharges * open lines";
            /* sera traité aprés car pb de concordance
             }else if (chevochement()) {
                 //cas d'un chavechement de deux commande
                 isNotFaisable = true;  
                 jLabel3.setText("Il existe au moins un chevauchement entre deux commandes sur une même ligne");
             */
        } else if (engraisNotproduced(carnetCommandeOfs, chargerEngraisMap)) {
            //il existe au moins un engrais qui ne peut pas être traité par les lignes ouvertes
            isNotFaisable = true;
            msg = "There is at least one fertilizer that can not be treated by open lines.";
        }
        return new Object[]{isNotFaisable, msg};
    }

    public boolean engraisNotproduced(List<CarnetCommandeOf> carnetCommandeOfs, HashMap<Engrais, List<Float>> chargerEngraisMap) {
        boolean existEngraisNotProduced = false;
        for (int m = 0; m < carnetCommandeOfs.size(); m++) {
            List<Float> charges = chargerEngraisMap.get(carnetCommandeOfs.get(m).getEngrais());
            Float chargeSurCetteLigne = (Float) getMinChargeInVector(charges);
            if (chargeSurCetteLigne == null || chargeSurCetteLigne.floatValue() <= 0) {
                existEngraisNotProduced = true;
            }
        }
        return existEngraisNotProduced;
    }

    public boolean checkCharge(ProblemePre problemePre, List<CarnetCommandeOf> carnetCommandeOfs, List<Date> periodes, HashMap<Engrais, List<Float>> chargerEngraisMap, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        //Algorithme A : vérifier que le cumul des heures demandées ne dépasse pas 
        //le produit du nombre N de lignes ouvertes par la plus tardive des dates de fin au plus tard
        //nb de charge en heure demandé
        //date la plus tardive
        Timestamp maxDate = new Timestamp(maxValue(carnetCommandeOfs).getTime());
        Date debutPeriode = (Date) minValueInVector(periodes);
        long difEnHeure = DateUtil.hoursDifference(maxDate, debutPeriode);
        float minCharge = calculCumulChargeMin(problemePre, carnetCommandeOfs, chargerEngraisMap);
        long nbHours = difEnHeure * problemeLigneOuvertes.size();
        return (minCharge > nbHours);
    }

    private Date maxValue(List<CarnetCommandeOf> carnetCommandeOfs) {
        Date maxdate = null;
        for (int j = 0; j < carnetCommandeOfs.size(); j++) {
            Date val = (Date) carnetCommandeOfs.get(j).getDateLiveTard();
            if (j == 0) {
                maxdate = val;
            }
            if (maxdate.before(val)) {
                maxdate = val;
            }
        }
        return maxdate;
    }

    public float calculCumulChargeMin(ProblemePre problemePre, List<CarnetCommandeOf> carnetCommandeOfs, HashMap<Engrais, List<Float>> chargerEngraisMap) {
        //calculer les différents cas de charge pendant la période de production
        float totalChargeMin = 0;
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            List<Float> chargeEngrais = chargerEngraisMap.get(carnetCommandeOfs.get(i).getEngrais());
            float chargeEngraisMin = getMinChargeInVector(chargeEngrais);
            totalChargeMin = totalChargeMin + chargeEngraisMin;

        }
        return totalChargeMin;
    }

    private float getMinChargeInVector(List<Float> chargeEngrais) {
        float minCharge = 0;
        for (int j = 0; j < chargeEngrais.size(); j++) {
            Float val = (Float) chargeEngrais.get(j);
            if (j == 0) {
                minCharge = val;
            }
            if (val.floatValue() > 0) {
                if (minCharge > val.floatValue()) {
                    minCharge = val;
                } else {
                    minCharge = val;
                }
            }
        }
        return minCharge;
    }

    public Date minValueInVector(List<Date> dates) {
        Date mindate = null;
        for (int j = 0; j < dates.size(); j++) {
            Date val = (Date) dates.get(j);
            if (j == 0) {
                mindate = val;
            }
            if (mindate.after(val)) {
                mindate = val;
            }
        }
        return mindate;
    }

    private List<Date> preparePeriodsAnalyse(ProblemePre problemePre) {
        List<Date> dates = new ArrayList<>();
        //je crée un vecteur entre date début et date de fin de production
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(problemePre.getDateDebut());
        Calendar calFin = Calendar.getInstance(); // creates calendar
        calFin.setTime(problemePre.getDateFin());
        long numberOfHour = DateUtil.hoursDifference(calFin.getTime(), cal.getTime());
        for (int l = 0; l <= numberOfHour; l++) {
            Date dateperiode = (Date) cal.getTime();
            dates.add(dateperiode);
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }
        return dates;
    }

    public float calculTempsProdcutionParLigne(Engrais engrais, Ligne ligne, float quantite) {
        //si la ligne ne peut pas produire alors return -1
        float chargeProduction_Heure = 0;
        Gamme gamme = gammeFacade.findByLigneAndEngrais(engrais, ligne.getTypeLigne());
        if (gamme != null) {

            if (gamme.getQtteProduite() > 0) {
                chargeProduction_Heure = quantite / gamme.getQtteProduite();
            } else {
                chargeProduction_Heure = -1;
            }
        } else {
            chargeProduction_Heure = -1;
        }
        return chargeProduction_Heure;
    }

    private HashMap<Engrais, List<Float>> prepareChargeEngrais(List<CarnetCommandeOf> carnetCommandeOfs, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        HashMap<Engrais, List<Float>> map = new HashMap<>();
        carnetCommandeOfs.forEach((c) -> {
            List<Float> chargeLigneEngrais = new ArrayList<>();
            for (ProblemeLigneOuverte problemeLigneOuverte : problemeLigneOuvertes) {
                chargeLigneEngrais.add(calculTempsProdcutionParLigne(c.getEngrais(), problemeLigneOuverte.getLigne(), c.getQuantite()));
            }
            map.put(c.getEngrais(), chargeLigneEngrais);
        });
        return map;
    }

    //ordonnancement au plus tard
    public Object[] verifyApprochePlustard(ProblemePre problemePre, List<ProblemeLigneOuverte> problemeLigneOuvertes, List<Date> periodes, HashMap<Engrais, List<Float>> chargerEngraisMap) {
        boolean approchePlusTardNotPossible = false;
        String series2 = "Cumulation at the latest";
        //Exemple avec 2 lignes ouvertes de l’algorithme B
        int nbLigneOuverte = problemeLigneOuvertes.size();
        //je commence par mettre toutes les lignes en 0 pour dire qu'ils sont libre
        //je crée un vecteur vectLigneOccupation de même taille que ligne ouverte
        List<Integer> ligneOccupations = new ArrayList<>();
        List<String> engraisEnCourss = new ArrayList<>();
        for (int m = 0; m < problemeLigneOuvertes.size(); m++) {
            ligneOccupations.add(0);
            engraisEnCourss.add("No");
        }

        /*
            Classer les OF par date décroissante de fin au plus tard (=> 4, 1, 2,3)*/
        List<CarnetCommandeOf> carnetCommandeOfs = loadDataPoblem(problemePre, true, false);
        chargerEngraisMap = prepareChargeEngrais(carnetCommandeOfs, problemeLigneOuvertes);
        /*
            1.Prendre le premier de la liste et l’ordonnancer au plus tard // j=1
                                   placement de l’OF 4*/
        float chargeAuPlustard = 0;
        /*
        for (int a=0; a <vectEnrgais.size(); a++){
            String engrais = (String) vectEnrgais.elementAt(a);
            Timestamp dateplustard = (Timestamp) vectProdEngraisPlusTard.elementAt(a);
            Vector vectChargeLigne = (Vector) vectChargeEngrais.elementAt(a);
            float maxCharge = Utils.getMaxChargeInVector(vectChargeLigne);
            int IndexMaxCharge = vectChargeLigne.indexOf(maxCharge);
            String ligneToSchedule = (String) ligneOuverte.elementAt(IndexMaxCharge);
            Integer ligneOccupation = (Integer)vectLigneOccupation.elementAt(IndexMaxCharge);
            if ((ligneOccupation != null && ligneOccupation.intValue() == 0)) {
                vectLigneOccupation.add(IndexMaxCharge, new Integer(1));
                vectEngraisEnCours.add(IndexMaxCharge,engrais);
                float ChargeProdEngrais = (chargeProdEngraisInt != null)? chargeProdEngraisInt.floatValue() : 0;
                long dateDif = Utils.dateDifenHeure(dateplustard,datePeriode);
                //System.out.println("date diff is " + dateDif + " date pluto is "+ dateplutot+" date periode est " + datePeriode);
                if ((dateDif >= 0 && dateDif <= ChargeProdEngrais)) {
                       chargeAuPlustard = chargeAuPlustard +1;
                      // System.out.println("periode est " + p + " datedif "+dateDif + " chargeaplustard " + chargeAuPlustard);
                } else {
                    //j'ai fini je remets la ligne à 0
                    vectLigneOccupation.add(p, new Integer(0));
                }
                //ligne libre, on arrete la boucle
                p=vectChargeLigne.size();
            } else {
                //ligne occupée alors pas possible
                //on passe à la suite
                if (p == vectChargeLigne.size()-1) approchePlusTardNotPossible = true;
            }
            dataset.addValue(chargeAuPlustard, series2, date);
        }*/

        List<Object[]> objects = new ArrayList<>();

        for (int b = 0; b < periodes.size(); b++) {
            //pour chaque période on calcule la période
            Date datePeriode = (Date) periodes.get(b);
            DateFormat dateStandard = new SimpleDateFormat("dd-MM HH:MM");
            String date = dateStandard.format(datePeriode);
            for (int a = 0; a < carnetCommandeOfs.size(); a++) {
                //pour chaque engrais,on calcule la production d'une période
                String engrais = (String) carnetCommandeOfs.get(a).getEngrais().getLibelle();
                Timestamp dateplustard = new Timestamp(carnetCommandeOfs.get(a).getDateLiveTard().getTime());
                //on travaille sur la charge en heure sur la ligne la plus couteuse
                List<Float> chargeLigne = chargerEngraisMap.get(carnetCommandeOfs.get(a).getEngrais());
                //je parcours les charges pour voir si ligne libre pour placer l'OF
                for (int p = 0; p < chargeLigne.size(); p++) {
                    Float chargeProdEngraisInt = chargeLigne.get(p);
                    String ligneToSchedule = (String) problemeLigneOuvertes.get(p).getLigne().getNomLigne();
                    //voir si la ligne est inoccupée
                    Integer ligneOccupation = ligneOccupations.get(p);
                    String engraisEnCours = engraisEnCourss.get(p);
                    if ((ligneOccupation != null && ligneOccupation.intValue() == 0) || !engraisEnCours.equals(engrais)) {
                        ligneOccupations.add(p, new Integer(1));
                        engraisEnCourss.add(p, engrais);
                        float chargeProdEngrais = (chargeProdEngraisInt != null) ? chargeProdEngraisInt.floatValue() : 0;
                        long dateDif = DateUtil.hoursDifference(dateplustard, datePeriode);
                        //System.out.println("date diff is " + dateDif + " date pluto is "+ dateplutot+" date periode est " + datePeriode);
                        if ((dateDif >= 0 && dateDif <= chargeProdEngrais)) {
                            chargeAuPlustard = chargeAuPlustard + 1;
                            // System.out.println("periode est " + p + " datedif "+dateDif + " chargeaplustard " + chargeAuPlustard);
                        } else {
                            //j'ai fini je remets la ligne à 0
                            ligneOccupations.add(p, new Integer(0));
                        }
                        //ligne libre, on arrete la boucle
                        p = chargeLigne.size();
                    } else {
                        //ligne occupée alors pas possible
                        //on passe à la suite
                        if (p == chargeLigne.size() - 1) {
                            approchePlusTardNotPossible = true;
                        }
                    }
                }
            }
            objects.add(new Object[]{chargeAuPlustard, series2, date});
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("haaaaaaaaaaaaaaa " + chargeAuPlustard);
            System.out.println("haaaaaaaaaaaaaaa " + series2);
            System.out.println("haaaaaaaaaaaaaaa " + date);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }

        /*
            2. Essayer de placer au plus tard le suivant 
                si possible (nombre d’OF simultanés sur la période <= nombre de ligne ouverte sur la période) // j=j+1 // revenir en 2
                                   placement de l’OF 1, j =2
                                   placement de l’OF 2, j=3
                sinon, la date de fin au plus tard de l’OF j est la plus tardive des dates de début au plus tard des tâches en cours d’exécution
                                   on essaie de placer l’OF 4 au plus tard en période 8 => impossible car 2 lignes déjà occupées => la plus des dates de début au plus tard des OF en cours est Max(8,4)=4 => date de fin au plus tard de l’OF 4 
                                   devient 8-1=7 => on place cet OF /// si ce n’est pas possible (violation de la date de fin au plus tôt), on pourrait chercher à anticiper la programmation d’une tache déjà programmée mais là cela devient 
                                   explosif => on 
         */
        return new Object[]{approchePlusTardNotPossible, objects};

    }

    public List<CarnetCommandeOf> loadDataPoblem(ProblemePre problemePre, boolean dateplutard, boolean dateplutoto) {
        List<CarnetCommandeOf> carnetCommandeOfs = null;
        if (dateplutard) {
            carnetCommandeOfs = carnetCommandeOfFacade.findByProblemePreDateLiveTarDesc(problemePre);
        } else if (dateplutoto) {
            carnetCommandeOfs = carnetCommandeOfFacade.findByProblemePreDateLiveTotAsc(problemePre);
        } else {
            carnetCommandeOfs = carnetCommandeOfFacade.findByNump(problemePre.getNump());
        }
        return carnetCommandeOfs;
    }

    public Object[] verifyApprochePlutot(ProblemePre problemePre, List<ProblemeLigneOuverte> problemeLigneOuvertes, List<Date> periodes) {
        boolean approchePlutotNotPossible = false;
        String series1 = "Cumulative production at the early";
        //Exemple avec 2 lignes ouvertes de l’algorithme B
        int nbLigneOuverte = problemeLigneOuvertes.size();
        //je commence par mettre toutes les lignes en 0 pour dire qu'ils sont libre
        //je crée un vecteur vectLigneOccupation de même taille que ligne ouverte
        List<Integer> ligneOccupations = new ArrayList<>();
        List<String> engraisEnCourss = new ArrayList<>();
        for (int m = 0; m < problemeLigneOuvertes.size(); m++) {
            ligneOccupations.add(0);
            engraisEnCourss.add("No");
        }

        /*
            Classer les OF par date décroissante de fin au plus tard (=> 4, 1, 2,3)*/
        List<CarnetCommandeOf> carnetCommandeOfs = loadDataPoblem(problemePre, false, true);
        HashMap<Engrais, List<Float>> chargerEngraisMap = prepareChargeEngrais(carnetCommandeOfs, problemeLigneOuvertes);
        /*
            1.Prendre le premier de la liste et l’ordonnancer au plus tard // j=1
                                   placement de l’OF 4*/
        List<Object[]> objects = new ArrayList<>();
        float chargeAuPlustot = 0;
        for (int b = 0; b < periodes.size(); b++) {
            //pour chaque période on calcule la période
            Date datePeriode = (Date) periodes.get(b);
            DateFormat dateStandard = new SimpleDateFormat("dd-MM HH:MM");
            String date = dateStandard.format(datePeriode);

            for (int a = 0; a < carnetCommandeOfs.size(); a++) {
                //pour chaque engrais,on calcule la production d'une période
                String engrais = (String) carnetCommandeOfs.get(a).getEngrais().getLibelle();
                Timestamp dateplutot = new Timestamp(carnetCommandeOfs.get(a).getDateLiveTot().getTime());
                //on travaille sur la charge en heure sur la ligne la plus couteuse
                List<Float> chargeLignes = chargerEngraisMap.get(carnetCommandeOfs.get(a).getEngrais());
                //je parcours les charges pour voir si ligne libre pour placer l'OF
                for (int p = 0; p < chargeLignes.size(); p++) {
                    Float chargeProdEngraisInt = (Float) chargeLignes.get(p);
                    String ligneToSchedule = (String) problemeLigneOuvertes.get(p).getLigne().getNomLigne();
                    //voir si la ligne est inoccupée
                    Integer ligneOccupation = (Integer) ligneOccupations.get(p);
                    String engraisEnCours = (String) engraisEnCourss.get(p);
                    if ((ligneOccupation != null && ligneOccupation.intValue() == 0) || !engraisEnCours.equals(engrais)) {
                        ligneOccupations.set(p, new Integer(1));
                        float chargeProdEngrais = (chargeProdEngraisInt != null) ? chargeProdEngraisInt.floatValue() : 0;
                        long dateDif = DateUtil.hoursDifference(dateplutot, datePeriode);
                        //System.out.println("date diff is " + dateDif + " date pluto is "+ dateplutot+" date periode est " + datePeriode);
                        if ((dateDif >= 0 && (dateDif) <= chargeProdEngrais)) {
                            chargeAuPlustot = chargeAuPlustot + 1;
                        } else {
                            //j'ai fini je remets la ligne à 0
                            ligneOccupations.set(p, new Integer(0));
                        }
                        //ligne libre, on arrete la boucle
                        p = chargeLignes.size();
                    } else {
                        //ligne occupée alors pas possible
                        //on passe à la suite
                        if (p == chargeLignes.size() - 1) {
                            approchePlutotNotPossible = true;
                        }
                    }
                }
            }
            objects.add(new Object[]{chargeAuPlustot, series1, date});
        }

        return new Object[]{approchePlutotNotPossible, objects};
    }

    public int deleteAll(ProblemePre problemePre) {
        if (problemePre != null) {
            calendrierMaintenanceFacade.removeAll(calendrierMaintenanceFacade.findByProblemePre(problemePre));
            stockInputFacade.removeAll(stockInputFacade.findByProblemePre(problemePre));
            stockOutputFacade.removeAll(stockOutputFacade.findByProblemePre(problemePre));
            regimeMarcheFacade.removeAll(regimeMarcheFacade.findByProblemePre(problemePre));
            problemeLigneOuverteFacade.removeAll(problemeLigneOuverteFacade.findByProblemePre(problemePre));
            problemePostFacade.remove(problemePostFacade.findByProblemePre(problemePre));
            remove(problemePre);
            return 1;
        }
        return -1;
    }

    public int saveAllProblem(ProblemePre problemPre, List<ProblemeLigneOuverte> problemeLigneOuvertes, List<RegimeMarche> regimeMarches, HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap, List<StockOutput> stockOutputs, List<StockInput> stockInputs) {
        int code = verifySaveAllProblem(problemeLigneOuvertes, stockOutputs, stockInputs);
        if (code > 0) {
            edit(problemPre);
            problemeLigneOuverteFacade.saveAll(problemeLigneOuvertes);
            regimeMarcheFacade.saveAll(regimeMarches);
            calendrierMaintenanceFacade.saveAllByLigne(calendrierMaintenanceMap, problemeLigneOuvertes);
            stockOutputFacade.saveAll(stockOutputs);
            stockInputFacade.saveAll(stockInputs);
        }
        return code;
    }

    public int verifySaveAllProblem(List<ProblemeLigneOuverte> problemeLigneOuvertes, List<StockOutput> stockOutputs, List<StockInput> stockInputs) {
        if (problemeLigneOuvertes.isEmpty()) {
            return -1;
        }
        if (stockOutputs.isEmpty()) {
            return -2;
        }
        if (stockInputs.isEmpty()) {
            return -3;
        }
        return 1;
    }

    public int verifyAddProblemPre(ProblemePre problemePre) {
        if (problemePre.getNump() == null || problemePre.getNump() == 0) {
            return -1;
        }
        if (find(problemePre.getNump()) != null) {
            return -2;
        }
        if (!correctDate(problemePre.getDateDebut(), problemePre.getDateFin())) {
            return -4;
        }
//        if (problemePre.getProblemeLigneOuvertes().isEmpty()) {
//            return -5;
//        }

        return 1;
    }

    private boolean correctDate(Date start, Date end) {
        return start.before(end);
    }

    public Date[] findDatesByNump(Long nump) {
        ProblemePre problemePre = find(nump);
        CarnetCommandeOf carnetCommandeOf = carnetCommandeOfFacade.findMaxDateLiveTardByNump(nump);
        Date dateFin = problemePre.getDateFin();
        if (carnetCommandeOf != null) {
            dateFin = carnetCommandeOf.getDateLiveTard();
        }
        return new Date[]{problemePre.getDateDebut(), dateFin};
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProblemePreFacade() {
        super(ProblemePre.class);
    }

}
