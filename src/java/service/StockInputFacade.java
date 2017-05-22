/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Input;
import entities.ProblemePre;
import entities.Site;
import entities.StockInput;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class StockInputFacade extends AbstractFacade<StockInput> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    public void removeAll(List<StockInput> stockInputs) {
        stockInputs.forEach((s) -> {
            s.setProblemePre(null);
            remove(s);
        });
    }

    public void saveAll(List<StockInput> stockInputs) {
        stockInputs.forEach((s) -> {
            if (s.getId() == -1) {
                s.setId(null);
            }
            edit(s);
        });
    }

    public int modifierStockInput(StockInput stockInput, List<StockInput> stockInputs) {
        for (int i = 0; i < stockInputs.size(); i++) {
            if (stockInputs.get(i).getInput().equals(stockInput.getInput()) && stockInput.getSite().equals(stockInputs.get(i).getSite())) {
                stockInputs.set(i, stockInput);
                return 1;
            }
        }
        return -1;
    }

    public int createStockInput(StockInput stockInput) {
        int code = verifyStockInputCreationErrors(stockInput);
        if (code > 0) {
            stockInput.setId(-1l);
        }
        return code;
    }

    public int supprimerStockInput(StockInput stockInput, List<StockInput> stockInputs) {
        for (int i = 0; i < stockInputs.size(); i++) {
            if (stockInputs.get(i).getInput().equals(stockInput.getInput()) && stockInput.getSite().equals(stockInputs.get(i).getSite())) {
                stockInputs.remove(i);
                return 1;
            }
        }
        return -1;
    }

    private int verifyStockInputCreationErrors(StockInput stockInput) {
        if (stockInput.getProblemePre() == null) {
            return -5;
        }
        if (stockInput.getInput() == null) {
            return -1;
        }
        if (stockInput.getSite() == null) {
            return -2;
        }
        if (stockInput.getQuantite() <= 0) {
            return -3;
        }
        if (!findByCritere(stockInput.getInput(), stockInput.getSite(), stockInput.getProblemePre()).isEmpty()) {
            return -4;
        }
        return 1;
    }

    private List<StockInput> findByCritere(Input input, Site site, ProblemePre problemePre) {
        String query = "select s from StockInput s where 1=1 ";
        if (input != null) {
            query += " and s.input.numInput=" + input.getNumInput();
        }
        if (site != null) {
            query += " and s.site.id=" + site.getId();
        }
        if (problemePre != null) {
            query += " and s.problemePre.nump=" + problemePre.getNump();
        }
        return getEntityManager().createQuery(query).getResultList();
    }

    private List<StockInput> findByCritere(Input input, Site site) {
        String query = "select s from StockInput s where 1=1 ";
        if (input != null) {
            query += " and s.input.numInput=" + input.getNumInput();
        }
        if (site != null) {
            query += " and s.site.id=" + site.getId();
        }
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<StockInput> findByProblemePre(ProblemePre problemePre) {
        return em.createQuery("select s from StockInput s where s.problemePre.nump=" + problemePre.getNump()).getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StockInputFacade() {
        super(StockInput.class);
    }

}
