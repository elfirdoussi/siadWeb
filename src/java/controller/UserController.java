package controller;

import entities.User;

import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import controller.util.SessionUtil;
import java.io.IOException;
import service.UserFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@ManagedBean(name = "userController")
@SessionScoped
public class UserController implements Serializable {

    @EJB
    private service.UserFacade ejbFacade;
    private List<User> items;
    private User selected;

    private String oldPassword;
    private String changePassword;
    private String changeRepetePassword;

    public UserController() {

    }

    private void showMessage(int res) {
        if (res == -1) {
            JsfUtil.addErrorMessage("la confirmation de votre mot de passe n'est pas correct");
        } else if (res == -2) {
            JsfUtil.addErrorMessage("l'ancient mot de passe ne correspond pas au mot de passe de la base de données");
        } else if (res == -3) {
            JsfUtil.addErrorMessage("le nouveau mot de passe ne doit pas etre l'ancient");
        } else {
            JsfUtil.addSuccessMessage("Modification avec succes ");
        }
    }

    public void init() {
        selected = new User();
    }

    public User getCurrentUser() {
        return SessionUtil.getConnectedUser();
    }

    public List<Boolean> getPrivileges() {
        return getFacade().getPrivileges();
    }

    private void validteConnexionForm(int res) {
        if (res == -1) {
            JsfUtil.addErrorMessage("Socie actuelement bloqué, Merci de contacter l'éditeur du logiciel");
        } else if (res == -2) {
            JsfUtil.addErrorMessage("Compte bloqué, Merci de contacter l'admin");
        } else if (res == -3) {
            JsfUtil.addErrorMessage("Erreur password, Réssayer SVP");
        } else if (res == -4) {
            JsfUtil.addErrorMessage("Erreur login, Réssayer SVP");
        }
    }

    public String seConnnecter() {
        System.out.println("hahowa user selected ::: " + selected.getUsername());
        int res = ejbFacade.seConnnecter(selected);
        if (res > 0) {
//            SessionUtil.attachUserToCommune(selected);
            return "/faces/menu.xhtml?faces-redirect=true";
            //communeFacade.initCommuneParams(selected.getCommune());
        }
        validteConnexionForm(res);
        return null;
    }

    public String seDeConnnecter() {
        //
        ejbFacade.seDeConnnecter();
        return "/index?faces-redirect=true";

    }

    public void isConnected() throws IOException {
        if (SessionUtil.getConnectedUser() != null) {
            SessionUtil.redirect("siadWeb/faces"+"/menu");
        }
    }

    public void isNotConnected() throws IOException {
        if (SessionUtil.getConnectedUser() == null) {
            SessionUtil.redirect("/index");
        }
    }

    public User getSelected() {
        if (selected == null) {
            selected = new User();
        }
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }

    public String getChangeRepetePassword() {
        return changeRepetePassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setChangeRepetePassword(String changeRepetePassword) {
        this.changeRepetePassword = changeRepetePassword;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public User prepareCreate() {
        selected = new User();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        SessionUtil.registerUser(selected);
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<User> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {

        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    System.out.println("hhaani f creat dial user" + selected.getUsername());
                    getFacade().edit(selected);
                    System.out.println("hhaani f creat dial user");
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<User> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<User> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public User getUser(java.lang.String id) {
        return getFacade().find(id);
    }

    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.getUser(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getId().toString());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), User.class.getName()});
                return null;
            }
        }

    }
}
