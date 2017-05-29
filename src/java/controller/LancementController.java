package controller;

import entities.Lancement;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import entities.Engrais;
import entities.TypeLigne;
import service.LancementFacade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@ManagedBean(name = "lancementController")
@SessionScoped
public class LancementController implements Serializable {

    @EJB
    private service.LancementFacade ejbFacade;
    @EJB
    private service.TypeLigneFacade typeLigneFacade;

    private List<Lancement> items = null;
    HashMap<Engrais, List<Lancement>> lancementsMap;

    private Lancement selected;
    private TypeLigne selectedTypeLigne;
    private int selectedCout;

    @PostConstruct
    public void initialize() {
        selectedTypeLigne = typeLigneFacade.findAll().get(0);
    }

    public void search() {
        lancementsMap = getFacade().findAllLancementByTypeLigne(selectedTypeLigne);
    }

    public void updateLancement(Engrais engrais, int index) {
        getFacade().edit(getLancementsMap().get(engrais).get(index));
    }

    public Lancement findByEngraiss(Engrais engrais, int index) {
        return getLancementsMap().get(engrais).get(index);
    }

    public LancementController() {
    }

    public int getSelectedCout() {
        return selectedCout;
    }

    public void setSelectedCout(int selectedCout) {
        this.selectedCout = selectedCout;
    }

    public HashMap<Engrais, List<Lancement>> getLancementsMap() {
        if (lancementsMap == null) {
            lancementsMap = getFacade().findAllLancementByTypeLigne(selectedTypeLigne);
        }
        return lancementsMap;
    }

    public void setLancementsMap(HashMap<Engrais, List<Lancement>> lancementsMap) {
        this.lancementsMap = lancementsMap;
    }

    public TypeLigne getSelectedTypeLigne() {
        return selectedTypeLigne;
    }

    public void setSelectedTypeLigne(TypeLigne selectedTypeLigne) {
        this.selectedTypeLigne = selectedTypeLigne;
    }

    public Lancement getSelected() {
        return selected;
    }

    public void setSelected(Lancement selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LancementFacade getFacade() {
        return ejbFacade;
    }

    public Lancement prepareCreate() {
        selected = new Lancement();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LancementCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LancementUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LancementDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Lancement> getItems() {
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

    public Lancement getLancement(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Lancement> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Lancement> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Lancement.class)
    public static class LancementControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LancementController controller = (LancementController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "lancementController");
            return controller.getLancement(getKey(value));
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
            if (object instanceof Lancement) {
                Lancement o = (Lancement) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Lancement.class.getName()});
                return null;
            }
        }

    }

}
