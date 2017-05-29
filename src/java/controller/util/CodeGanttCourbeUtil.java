/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.util;

/**
 *
 * @author AIMAD
 */
public class CodeGanttCourbeUtil {

    public static int debutEffectifaAbsolu(int commande) {
        int de = Data.getInstance().getDateRelativeDebutEffectifProd()[commande - 1].intValue();
        int nligne = Data.getInstance().getLigneChoisi()[commande-1].intValue();
        int debuteffectifabsolu = Data.getInstance().getTempsAbsoluParAtelier().get(nligne)[de-1].intValue();
        return debuteffectifabsolu;
    }
    
        public static int finAbsolue(int commande){
        int fin = Data.getInstance().getDateRelativeFin()[commande-1].intValue();
        int nligne = Data.getInstance().getLigneChoisi()[commande-1].intValue();
        int finabsolue = Data.getInstance().getTempsAbsoluParAtelier().get(nligne)[fin-1].intValue();
        return finabsolue;
    }
        
}
