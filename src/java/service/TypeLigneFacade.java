/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.TypeLigne;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class TypeLigneFacade extends AbstractFacade<TypeLigne> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    public void deleteTypeLigne(TypeLigne typeLigne) {
        System.out.println("delete !!");
    }

    public int addTypeLigne(TypeLigne typeLigne) {
        int code = verifyTypeLigneCreation(typeLigne);
        if (code > 0) {
            create(typeLigne);
        }
        return code;
    }

    private int verifyTypeLigneCreation(TypeLigne typeLigne) {
        if (typeLigne.getCode() == null || typeLigne.getCode() == 0) {
            return -1;
        }
        if (typeLigne.getLibelle() == null || typeLigne.getLibelle().equals("")) {
            return -2;
        }
        if (typeLigneExist(typeLigne.getCode())) {
            return -3;
        }
        if (findByExactLibelle(typeLigne.getLibelle()) != null) {
            return -4;
        }
        return 1;
    }

    public int verifyTypeLigneDelete(TypeLigne typeLigne) {
        if (typeLigne.getCode() == null || typeLigne.getCode() == 0) {
            return -1;
        }
        if (typeLigne.getLibelle() == null || typeLigne.getLibelle().equals("")) {
            return -2;
        }
        return 1;
    }

    private boolean typeLigneExist(Long numero) {
        TypeLigne typeLigne = find(numero);
        return typeLigne != null;
    }

    public TypeLigne findByExactLibelle(String libelle) {
        TypeLigne typeLigne = null;
        if (libelle != null && !libelle.equals("")) {
            try {
                typeLigne = (TypeLigne) getEntityManager().createQuery("select t from TypeLigne t where t.libelle ='" + libelle + "'").getSingleResult();
            } catch (NoResultException e) {
            }
        }
        return typeLigne;
    }

    public List<TypeLigne> findByCriteres(Long numero, String libelle) {
        String query = "select t from TypeLigne t where 1=1 ";
        if (numero != null && numero != 0) {
            query += " and t.code=" + numero;
        }
        if (libelle != null && !libelle.equals("")) {
            query += " and t.libelle like '%" + libelle + "%'";
        }
        return getEntityManager().createQuery(query).getResultList();
    }
    
    public List<TypeLigne> findAllOrderByCode(){
        return em.createQuery("select t from TypeLigne t order by t.code").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TypeLigneFacade() {
        super(TypeLigne.class);
    }

}
