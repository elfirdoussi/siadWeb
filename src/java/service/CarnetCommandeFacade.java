/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import controller.util.PdfUtil;
import entities.CarnetCommande;
import entities.CarnetCommandeOf;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author AIMAD
 */
@Stateless
public class CarnetCommandeFacade extends AbstractFacade<CarnetCommande> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private CarnetCommandeOfFacade carnetCommandeOfFacade;

    public void supprimerCarnetCommande(CarnetCommande carnetCommande) {
        carnetCommandeOfFacade.deleteAll(carnetCommande);
        remove(carnetCommande);
    }

    public int createCarnetCommande(CarnetCommande carnetCommande) {
        int code = verifyCarnetCreation(carnetCommande);
        if (code > 0) {
            carnetCommande.setCarnetCommandeOfs(null);
            create(carnetCommande);
        }
        return code;
    }

    private int verifyCarnetCreation(CarnetCommande carnetCommande) {
        if (carnetCommande.getId() == null) {
            return -1;
        }
        if (find(carnetCommande.getId()) != null) {
            return -2;
        }
        return 1;
    }

    public void genratePdf(List<CarnetCommandeOf> carnetCommandeOfs, CarnetCommande carnetCommande) throws JRException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("idCarnet", carnetCommande.getId());
        PdfUtil.generatePdf(carnetCommandeOfs, params, "CarnetCommande-N"+carnetCommande.getId(), "/reports/carnetCommande.jasper");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CarnetCommandeFacade() {
        super(CarnetCommande.class);
    }

}
