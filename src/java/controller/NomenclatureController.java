package controller;

import entities.Nomenclature;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import entities.NomenclatureItem;
import service.NomenclatureFacade;

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

@ManagedBean(name = "nomenclatureController")
@SessionScoped
public class NomenclatureController implements Serializable {

    @EJB
    private service.NomenclatureFacade ejbFacade;
    @EJB
    private service.NomenclatureItemFacade nomenclatureItemFacade;

    private List<Nomenclature> items = null;
    private List<NomenclatureItem> nomenclatureItems = null;

    private Nomenclature selected;
    private NomenclatureItem selectedNomenclatureItem;
    
    public void updateNomenclatureItem(NomenclatureItem nomenclatureItem, int index){
        System.out.println("haaaaa yyyyyyy ooo");
        nomenclatureItemFacade.edit(nomenclatureItem);
        nomenclatureItems.set(index, nomenclatureItem);
    }

    public void search() {
        nomenclatureItems = nomenclatureItemFacade.findByEngraisAndTypeLigne(getSelected().getEngrais(), getSelected().getTypeLigne());
    }

    public NomenclatureItem getSelectedNomenclatureItem() {
        if(selectedNomenclatureItem == null){
            selectedNomenclatureItem = new NomenclatureItem();
        }
        return selectedNomenclatureItem;
    }

    public void setSelectedNomenclatureItem(NomenclatureItem selectedNomenclatureItem) {
        this.selectedNomenclatureItem = selectedNomenclatureItem;
    }

    public List<NomenclatureItem> getNomenclatureItems() {
        if (nomenclatureItems == null) {
            nomenclatureItems = new ArrayList<>();
        }
        return nomenclatureItems;
    }

    public void setNomenclatureItems(List<NomenclatureItem> nomenclatureItems) {
        this.nomenclatureItems = nomenclatureItems;
    }

    public NomenclatureController() {
    }

    public Nomenclature getSelected() {
        if(selected == null){
            selected = new Nomenclature();
        }
        return selected;
    }

    public void setSelected(Nomenclature selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private NomenclatureFacade getFacade() {
        return ejbFacade;
    }

    public Nomenclature prepareCreate() {
        selected = new Nomenclature();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("NomenclatureCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("NomenclatureUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("NomenclatureDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Nomenclature> getItems() {
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

    public Nomenclature getNomenclature(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Nomenclature> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Nomenclature> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Nomenclature.class)
    public static class NomenclatureControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            NomenclatureController controller = (NomenclatureController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "nomenclatureController");
            return controller.getNomenclature(getKey(value));
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
            if (object instanceof Nomenclature) {
                Nomenclature o = (Nomenclature) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Nomenclature.class.getName()});
                return null;
            }
        }

    }

}
