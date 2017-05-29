/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.util;

import com.dashoptimization.XPRM;
import com.dashoptimization.XPRMCompileException;
import com.dashoptimization.XPRMModel;
import java.io.IOException;

/**
 *
 * @author YOUNESS
 */
public class ProblemData {

    public static int Id_problem;
    public static int Nb_periodes;
    public static int Nombre_ateliers;
    public static int Nombre_commande;
    public static int Nb_ref_utilisee;
    public static int Critere_opt;
    public static String[] Atelier;
    public static float[] Ponderation;
    public static int[] TT_avec_L;
    public static int[] TT_sans_L;
    public static String[] Commande_Reference;
    public static String[] Ref_utilisee;
    public static float[] Cout_commande;
    public static float[] Debit_acide_P;
    public static boolean[] Bin_A_C;
    public static int[] Date_liv_tot;
    public static int[] Date_liv_tard;
    public static float[] Debit_Prod;
    public static int[] Arret;

    public static void xpressCompile(String generationDirectory) throws XPRMCompileException, IOException {
        XPRM mosel;
        XPRMModel mod;
        System.loadLibrary("xprm_rtJ");
        System.out.println(System.getProperty("java.library.path"));

        mosel = new XPRM();
        mosel.compile(generationDirectory + "/Xpress_Modele1.mos");
        mod = mosel.loadModel(generationDirectory + "/Xpress_Modele1.bim");

        XPRM.bind("Id_problem", ProblemData.Id_problem);
        XPRM.bind("Nb_periodes", ProblemData.Nb_periodes);
        XPRM.bind("Nombre_ateliers", ProblemData.Nombre_ateliers);
        XPRM.bind("Nombre_commande", ProblemData.Nombre_commande);
        XPRM.bind("Nb_ref_utilisee", ProblemData.Nb_ref_utilisee);
        XPRM.bind("Critere_opt", ProblemData.Critere_opt);
        XPRM.bind("Atelier", ProblemData.Atelier);
        XPRM.bind("Ponderation", ProblemData.Ponderation);
        XPRM.bind("Bin_A_C", ProblemData.Bin_A_C);
        XPRM.bind("Debit_acide_P", ProblemData.Debit_acide_P);
        XPRM.bind("Debit_Prod", ProblemData.Debit_Prod);
        XPRM.bind("Date_liv_tot", ProblemData.Date_liv_tot);
        XPRM.bind("Date_liv_tard", ProblemData.Date_liv_tard);
        XPRM.bind("Commande_Reference", ProblemData.Commande_Reference);
        XPRM.bind("TT_sans_L", ProblemData.TT_sans_L);
        XPRM.bind("Arret", ProblemData.Arret);

        mod.execParams = 
                "Id_problem='noindex,Id_problem',"
                + "Nb_periodes='noindex,Nb_periodes',"
                + "Nombre_ateliers='noindex,Nombre_ateliers',"
                + "Nombre_commande='noindex,Nombre_commande',"
                + "Nb_ref_utilisee='noindex,Nb_ref_utilisee',"
                + "Critere_opt='noindex,Critere_opt',"
                + "Atelier='noindex,Atelier',"
                + "TT_avec_L='noindex,TT_avec_L',"
                + "TT_sans_L='noindex,TT_sans_L',"
                + "Commande_Reference='noindex,Commande_Reference',"
                + "Ref_utilisee='noindex,Ref_utilisee',"
                + "Cout_commande='noindex,Cout_commande',"
                + "Debit_acide_P='noindex,Debit_acide_P',"
                + "Bin_A_C='noindex,Bin_A_C',"
                + "Date_liv_tot='noindex,Date_liv_tot',"
                + "Date_liv_tard='noindex,Date_liv_tard',"
                + "Ponderation='noindex,Ponderation',"
                + "Debit_Prod='noindex,Debit_Prod',"
                + "Arret='noindex,Arret'";


        mod.run();

//        if (mod.getProblemStatus() != mod.PB_OPTIMAL) {
//            System.exit(1);
//        }
//
//        System.out.println("Objective value: " + mod.getObjectiveValue());
//        for (int i = 0; i < 8; i++) {
//            System.out.println(" take(" + (i + 1) + "): " + solution[i]);
//        }
        mod.reset();
    }

}
