package controller;

import entities.Ligne;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.LigneFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@ManagedBean(name="ligneController")
@SessionScoped
public class LigneController implements Serializable {

    @EJB
    private service.LigneFacade ejbFacade;
    
    private List<Ligne> items = null;
    
    private Ligne selected;
    private Ligne searchLigne;
    
    public void search(){
        items = ejbFacade.findByCriteres(searchLigne.getNumeroLigne(), searchLigne.getNomLigne(), searchLigne.getSite(), searchLigne.getTypeLigne());
    }

    public LigneController() {
    }

    public Ligne getSearchLigne() {
        if(searchLigne == null){
            searchLigne = new Ligne();
        }
        return searchLigne;
    }

    public void setSearchLigne(Ligne searchLigne) {
        this.searchLigne = searchLigne;
    }
    
    public Ligne getSelected() {
        return selected;
    }

    public void setSelected(Ligne selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LigneFacade getFacade() {
        return ejbFacade;
    }

    public Ligne prepareCreate() {
        selected = new Ligne();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LigneCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LigneUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LigneDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Ligne> getItems() {
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
                    getFacade().edit(selected);
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

    public Ligne getLigne(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Ligne> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Ligne> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Ligne.class)
    public static class LigneControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LigneController controller = (LigneController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ligneController");
            return controller.getLigne(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Ligne) {
                Ligne o = (Ligne) object;
                return getStringKey(o.getNumeroLigne());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ligne.class.getName()});
                return null;
            }
        }

    }

}
