package controller;

import entities.TypeLigne;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.TypeLigneFacade;

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

@ManagedBean(name = "typeLigneController")
@SessionScoped
public class TypeLigneController implements Serializable {

    @EJB
    private service.TypeLigneFacade ejbFacade;
    @EJB
    private service.EngraisFacade engraisFacade;

    private List<TypeLigne> items = null;

    private TypeLigne selected;
    private TypeLigne searchTypeLigne;

    public void search() {
        items = ejbFacade.findByCriteres(searchTypeLigne.getCode(), searchTypeLigne.getLibelle());
    }

    public void ajouterTypeLigne() {
        int code = ejbFacade.addTypeLigne(searchTypeLigne);
        if (code > 0) {

        }
    }

    public TypeLigneController() {
    }

    public TypeLigne getSearchTypeLigne() {
        return searchTypeLigne;
    }

    public void setSearchTypeLigne(TypeLigne searchTypeLigne) {
        this.searchTypeLigne = searchTypeLigne;
    }

    public TypeLigne getSelected() {
        return selected;
    }

    public void setSelected(TypeLigne selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TypeLigneFacade getFacade() {
        return ejbFacade;
    }

    public TypeLigne prepareCreate() {
        selected = new TypeLigne();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TypeLigneCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TypeLigneUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TypeLigneDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TypeLigne> getItems() {
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

    public TypeLigne getTypeLigne(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TypeLigne> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TypeLigne> getItemsAvailableSelectOne() {
        return getFacade().findAllOrderByCode();
    }

    @FacesConverter(forClass = TypeLigne.class)
    public static class TypeLigneControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TypeLigneController controller = (TypeLigneController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "typeLigneController");
            return controller.getTypeLigne(getKey(value));
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
            if (object instanceof TypeLigne) {
                TypeLigne o = (TypeLigne) object;
                return getStringKey(o.getCode());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TypeLigne.class.getName()});
                return null;
            }
        }

    }

}
