/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Engrais;
import entities.ProblemePre;
import entities.Site;
import entities.StockOutput;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class StockOutputFacade extends AbstractFacade<StockOutput> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    public void removeAll(List<StockOutput> stockOutputs) {
        stockOutputs.forEach((s) -> {
            s.setProblemePre(null);
            remove(s);
        });
    }

    public void saveAll(List<StockOutput> stockOutputs) {
        stockOutputs.forEach((s) -> {
            if(s.getId() == -1){
                s.setId(null);
            }
            edit(s);
        });
    }

    public int supprimerStockOutput(StockOutput stockOutput, List<StockOutput> stockOutputs) {
        for (int i = 0; i < stockOutputs.size(); i++) {
            if (stockOutputs.get(i).getEngrais().equals(stockOutput.getEngrais()) && stockOutput.getSite().equals(stockOutputs.get(i).getSite())) {
                stockOutputs.remove(i);
                return 1;
            }
        }
        return -1;
    }

    public int modifierStockOutput(StockOutput stockOutput, List<StockOutput> stockOutputs) {
        for (int i = 0; i < stockOutputs.size(); i++) {
            if (stockOutputs.get(i).getEngrais().equals(stockOutput.getEngrais()) && stockOutput.getSite().equals(stockOutputs.get(i).getSite())) {
                stockOutputs.set(i, stockOutput);
                return 1;
            }
        }
        return -1;
    }

    public int createStockOutput(StockOutput stockOutput) {
        int code = verifyStockOutputCreationErrors(stockOutput);
        if (code > 0) {
            stockOutput.setId(-1l);
        }
        return code;
    }

    private int verifyStockOutputCreationErrors(StockOutput stockOutput) {
        if(stockOutput.getProblemePre() == null){
            return -5;
        }
        if (stockOutput.getEngrais() == null) {
            return -1;
        }
        if (stockOutput.getSite() == null) {
            return -2;
        }
        if (stockOutput.getQuantite() <= 0) {
            return -3;
        }
        if (!findByCritere(stockOutput.getEngrais(), stockOutput.getSite(), stockOutput.getProblemePre()).isEmpty()) {
            return -4;
        }
        return 1;
    }
    
      private List<StockOutput> findByCritere(Engrais engrais, Site site, ProblemePre problemePre) {
        String query = "select s from StockOutput s where 1=1 ";
        if (engrais != null) {
            query += " and s.engrais.numero=" + engrais.getNumero();
        }
        if (site != null) {
            query += " and s.site.id=" + site.getId();
        }
        if(problemePre != null){
            query += " and s.problemePre.nump=" + problemePre.getNump();
        }
        return getEntityManager().createQuery(query).getResultList();
    }

    private List<StockOutput> findByCritere(Engrais engrais, Site site) {
        String query = "select s from StockOutput s where 1=1 ";
        if (engrais != null) {
            query += " and s.engrais.numero=" + engrais.getNumero();
        }
        if (site != null) {
            query += " and s.site.id=" + site.getId();
        }
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<StockOutput> findByProblemePre(ProblemePre problemePre) {
        return em.createQuery("select s from StockOutput s where s.problemePre.nump=" + problemePre.getNump()).getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StockOutputFacade() {
        super(StockOutput.class);
    }

}
