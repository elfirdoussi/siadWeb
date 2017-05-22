/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AIMAD
 */
public class ConsommationProductionUtil {

    public static double[][] getData2FromTxt(double[][] accumulationOfConsumption) {
        double[][] data = new double[2][Data.getInstance().getNbrePeriodes()];
        int cumulConso = 0;
        for (int i = 0; i < Data.getInstance().getNbrePeriodes(); i++) {
            data[1][i] = consommationPeriode(i);
            data[0][i] = i;
            cumulConso += data[1][i];
            accumulationOfConsumption[1][i] = cumulConso;
            accumulationOfConsumption[0][i] = i;
        }
        return data;
    }

    public static List<double[][]> getData1FromTxt(List<String> distinctQualite, List<double[][]> cumulProductionsParQualite) {
        List<double[][]> datas = new ArrayList<>();
        for (int i = 0; i < distinctQualite.size(); i++) {
            double[][] cumulProductions = new double[2][Data.getInstance().getNbrePeriodes()];
            datas.add(getLoadData(distinctQualite.get(i), cumulProductions));
            cumulProductionsParQualite.add(cumulProductions);
        }
        return datas;
    }

    public static double[][] getLoadData(String qualite, double[][] cumulProductions) {
        double[][] data = new double[2][Data.getInstance().getNbrePeriodes()];
        int cumulProd = 0;
        for (int i = 1; i <= Data.getInstance().getNbrePeriodes(); i++) {
            data[1][i - 1] = productionQualitePeriode(qualite, i);
            data[0][i - 1] = i;
            cumulProd += data[1][i - 1];
            cumulProductions[1][i - 1] = cumulProd;
            cumulProductions[0][i - 1] = i;
        }
        return data;
    }

//    public static double[][] getUpdateLoadData(Event event) {
//        double[][] data = new double[2][Data.getInstance().getNbrePeriodes()];
//        int[] dates = getDebutEffAbsAndFinabsFromEvent(event);
//        System.out.println("kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa date 0 "+dates[0]+" ---- date 1"+dates[1]);
//        for (int i = 1; i <= Data.getInstance().getNbrePeriodes(); i++) {
//            data[1][i - 1] = updateProductionQualitePeriode(event.getCarnetCommandeOf().getEngrais().getLibelle(), i, dates[0], dates[1]);
//            data[0][i - 1] = i;
//        }
//        return data;
//    }
////
//    private static int[] getDebutEffAbsAndFinabsFromEvent(Event event) {
//       return new int[]{DateUtil.getHoursBetweenTwoDates(event.getStartDate(), event.getCarnetCommandeOf().getDateLiveTot()), DateUtil.getHoursBetweenTwoDates(event.getEndDate(), event.getCarnetCommandeOf().getDateLiveTard())};
//    }
    public static int updateProductionQualitePeriode(String qulaite, int periode, int debutEffectifAbsolu, int finAbsolue) {
        int s = 0;
        int nbreCommandes = Data.getInstance().getNbreCommandes();
        for (int i = 1; i <= nbreCommandes; i++) {
            if (Data.getInstance().getQualiteParCommande().get(i).equals(qulaite)) {
                s += updateValeurProductionCommandePeriode(i, periode, debutEffectifAbsolu, finAbsolue);
            }
        }
        return s;
    }

    public static int updateValeurProductionCommandePeriode(int commande, int periode, int debutEffectifAbsolu, int finAbsolue) {
        int nLigne = Data.getInstance().getLigneChoisi()[commande - 1].intValue();
        List<String> listArret = Data.getInstance().getArretParLigne().get(nLigne);
        boolean b = false;
        int a = 0;
        int i = 0;
        while (i < listArret.size() && b == false) {
            a = Integer.valueOf(listArret.get(i));
            if (a == periode) {
                b = true;
            }
            i++;
        }
        if (periode < debutEffectifAbsolu || periode > finAbsolue || b == true) {
            return 0;
        } else {
            return Data.getInstance().getProductionParCommande()[commande - 1].intValue();
        }
    }

    public static int productionQualitePeriode(String qulaite, int periode) {
        int s = 0;
        int nbreCommandes = Data.getInstance().getNbreCommandes();
        for (int i = 1; i <= nbreCommandes; i++) {
            if (Data.getInstance().getQualiteParCommande().get(i).equals(qulaite)) {
                s += valeurProductionCommandePeriode(i, periode);
            }
        }
        return s;
    }

    public static int valeurProductionCommandePeriode(int commande, int periode) {
        int debutEffectifAbsolu = CodeGanttCourbeUtil.debutEffectifaAbsolu(commande);
        int finaAbsolue = CodeGanttCourbeUtil.finAbsolue(commande);
        int nLigne = Data.getInstance().getLigneChoisi()[commande - 1].intValue();
        List<String> listArret = Data.getInstance().getArretParLigne().get(nLigne);
        boolean b = false;
        int a = 0;
        int i = 0;
        while (i < listArret.size() && b == false) {
            a = Integer.valueOf(listArret.get(i));
            if (a == periode) {
                b = true;
            }
            i++;
        }
        if (periode < debutEffectifAbsolu || periode > finaAbsolue || b == true) {
            return 0;
        } else {
            return Data.getInstance().getProductionParCommande()[commande - 1].intValue();
        }
    }

    public static int valeurCommandePeriode(int commande, int periode) {
        int debuteffectifabsolu = CodeGanttCourbeUtil.debutEffectifaAbsolu(commande);
        int finabsolue = CodeGanttCourbeUtil.finAbsolue(commande);
        int nligne = Data.getInstance().getLigneChoisi()[commande - 1].intValue();
        List<String> listearret = Data.getInstance().getArretParLigne().get(nligne);
        boolean b = false;
        int a = 0;
        int i = 0;
        while (i < listearret.size() && b == false) {
            a = Integer.valueOf(listearret.get(i));
            if (a == periode) {
                b = true;
            }
            i++;
        }
        if (periode < debuteffectifabsolu || periode > finabsolue || b == true) {
            return 0;
        } else {
            return Data.getInstance().getConsommationParCommande()[commande - 1].intValue();
        }
    }

    public static int consommationPeriode(int periode) {
        int c = 0;
        int nbrecommandes = Data.getInstance().getNbreCommandes();
        for (int i = 1; i <= nbrecommandes; i++) {
            c += valeurCommandePeriode(i, periode);
        }
        return c;
    }

}
