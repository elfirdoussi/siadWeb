/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Config;
import entities.DoodleGamme;
import entities.Engrais;
import entities.TypeLigne;
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
public class ConfigFacade extends AbstractFacade<Config> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    public String getDataFilePath() {
        Config config = find("generateFileDir");
        if (config != null) {
            return config.getValeur();
        }
        return "";
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigFacade() {
        super(Config.class);
    }

}
