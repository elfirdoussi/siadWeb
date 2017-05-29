package controller;

import entities.CarnetCommandeOf;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.CarnetCommandeOfFacade;

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

@ManagedBean(name="carnetCommandeOfController")
@SessionScoped
public class CarnetCommandeOfController implements Serializable {

    @EJB
    private service.CarnetCommandeOfFacade ejbFacade;
    private List<CarnetCommandeOf> items = null;
    private CarnetCommandeOf selected;

    public CarnetCommandeOfController() {
    }

    public CarnetCommandeOf getSelected() {
        return selected;
    }

    public void setSelected(CarnetCommandeOf selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CarnetCommandeOfFacade getFacade() {
        return ejbFacade;
    }

    public CarnetCommandeOf prepareCreate() {
        selected = new CarnetCommandeOf();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CarnetCommandeOfCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CarnetCommandeOfUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CarnetCommandeOfDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<CarnetCommandeOf> getItems() {
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

    public CarnetCommandeOf getCarnetCommandeOf(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<CarnetCommandeOf> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<CarnetCommandeOf> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = CarnetCommandeOf.class)
    public static class CarnetCommandeOfControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CarnetCommandeOfController controller = (CarnetCommandeOfController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "carnetCommandeOfController");
            return controller.getCarnetCommandeOf(getKey(value));
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
            if (object instanceof CarnetCommandeOf) {
                CarnetCommandeOf o = (CarnetCommandeOf) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), CarnetCommandeOf.class.getName()});
                return null;
            }
        }

    }

}
