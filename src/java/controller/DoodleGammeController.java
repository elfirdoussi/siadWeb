package controller;

import entities.DoodleGamme;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import entities.TypeLigne;
import service.DoodleGammeFacade;

import java.io.Serializable;
import java.util.HashMap;
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

@ManagedBean(name = "doodleGammeController")
@SessionScoped
public class DoodleGammeController implements Serializable {

    @EJB
    private service.DoodleGammeFacade ejbFacade;

    private List<DoodleGamme> items = null;
    private HashMap<TypeLigne, List<DoodleGamme>> doodleGammeMap;

    private DoodleGamme selected;

    public void updateDoodleGammeByTypeLigne(TypeLigne typeLigne, int index) {
        getFacade().edit(getDoodleGammeMap().get(typeLigne).get(index));
        JsfUtil.addSuccessMessage("Updated !");
    }

    public DoodleGamme doodleGammeByTypeLigne(TypeLigne typeLigne, int index) {
        return getDoodleGammeMap().get(typeLigne).get(index);
    }

    public DoodleGammeController() {
    }

    public HashMap<TypeLigne, List<DoodleGamme>> getDoodleGammeMap() {
        if (doodleGammeMap == null) {
            doodleGammeMap = ejbFacade.findAllOrderByEngraisTypeLigne();
        }
        return doodleGammeMap;
    }

    public void setDoodleGammeMap(HashMap<TypeLigne, List<DoodleGamme>> doodleGammeMap) {
        this.doodleGammeMap = doodleGammeMap;
    }

    public DoodleGamme getSelected() {
        return selected;
    }

    public void setSelected(DoodleGamme selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DoodleGammeFacade getFacade() {
        return ejbFacade;
    }

    public DoodleGamme prepareCreate() {
        selected = new DoodleGamme();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DoodleGammeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DoodleGammeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DoodleGammeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<DoodleGamme> getItems() {
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

    public DoodleGamme getDoodleGamme(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<DoodleGamme> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<DoodleGamme> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = DoodleGamme.class)
    public static class DoodleGammeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DoodleGammeController controller = (DoodleGammeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "doodleGammeController");
            return controller.getDoodleGamme(getKey(value));
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
            if (object instanceof DoodleGamme) {
                DoodleGamme o = (DoodleGamme) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), DoodleGamme.class.getName()});
                return null;
            }
        }

    }

}
