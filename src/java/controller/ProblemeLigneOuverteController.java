package controller;

import entities.ProblemeLigneOuverte;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.ProblemeLigneOuverteFacade;

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

@ManagedBean(name="problemeLigneOuverteController")
@SessionScoped
public class ProblemeLigneOuverteController implements Serializable {

    @EJB
    private service.ProblemeLigneOuverteFacade ejbFacade;
    private List<ProblemeLigneOuverte> items = null;
    private ProblemeLigneOuverte selected;

    public ProblemeLigneOuverteController() {
    }

    public ProblemeLigneOuverte getSelected() {
        return selected;
    }

    public void setSelected(ProblemeLigneOuverte selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProblemeLigneOuverteFacade getFacade() {
        return ejbFacade;
    }

    public ProblemeLigneOuverte prepareCreate() {
        selected = new ProblemeLigneOuverte();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ProblemeLigneOuverteCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ProblemeLigneOuverteUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ProblemeLigneOuverteDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ProblemeLigneOuverte> getItems() {
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

    public ProblemeLigneOuverte getProblemeLigneOuverte(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<ProblemeLigneOuverte> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ProblemeLigneOuverte> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ProblemeLigneOuverte.class)
    public static class ProblemeLigneOuverteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProblemeLigneOuverteController controller = (ProblemeLigneOuverteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "problemeLigneOuverteController");
            return controller.getProblemeLigneOuverte(getKey(value));
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
            if (object instanceof ProblemeLigneOuverte) {
                ProblemeLigneOuverte o = (ProblemeLigneOuverte) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ProblemeLigneOuverte.class.getName()});
                return null;
            }
        }

    }

}
