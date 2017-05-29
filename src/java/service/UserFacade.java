/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.User;
import controller.util.JsfUtil;
import controller.util.SessionUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Younes
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }

    
    public User findByUserName(String username) {
        try {
            User user = (User) em.createQuery("select u from User u where u.username = '" + username + "'").getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Username incorrect");
        }
        return null;
    }

    public void seDeConnnecter() {
        SessionUtil.getSession().invalidate();

    }

    public int seConnnecter(User user) {
        if (user == null || user.getUsername() == null) {
            JsfUtil.addErrorMessage("Veuilliez saisir votre login");
            return -5;
        } else {
            User loadedUser = findByUserName(user.getUsername());
            if (loadedUser == null) {
                return -4;
            } else if (!loadedUser.getPassword().equals(user.getPassword())) {
                if (loadedUser.getNbreCnx()< 3) {
                    loadedUser.setNbreCnx(loadedUser.getNbreCnx()+ 1);
                } else if (loadedUser.getNbreCnx() >= 3) {
                    loadedUser.setBlocked(true);
                    // edit(loadedUser);
                }
                JsfUtil.addErrorMessage("Mot de passe incorrect");
                return -3;
            } else if (loadedUser.isBlocked()) {
                JsfUtil.addErrorMessage("Cet utilisateur est bloqué");
                return -2;
            } else {
                loadedUser.setNbreCnx(0);
                //edit(loadedUser);
                user = clone(loadedUser);
                user.setPassword(null);
                SessionUtil.registerUser(user);
                return 1;
            }
        }
    }

    public List<Boolean> getPrivileges() {
        User loadeUser = find(SessionUtil.getConnectedUser().getUsername());
        List<Boolean> privileges = new ArrayList();

        return privileges;
    }


    public User clone(User user) {
        User clone = new User();
        clone.setId(user.getId());
        clone.setMail(user.getMail());
        clone.setNbreCnx(user.getNbreCnx());
        clone.setNom(user.getNom());
        clone.setPassword(user.getPassword());
        clone.setPrenom(user.getPrenom());
        clone.setSuperAdmin(user.isSuperAdmin());
        clone.setUsername(user.getUsername());
        return clone;
    }

}
