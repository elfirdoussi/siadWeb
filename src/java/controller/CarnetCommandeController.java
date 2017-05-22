package controller;

import controller.util.ErrorUtil;
import entities.CarnetCommande;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import entities.CarnetCommandeOf;
import java.io.IOException;
import service.CarnetCommandeFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import net.sf.jasperreports.engine.JRException;

@ManagedBean(name = "carnetCommandeController")
@SessionScoped
public class CarnetCommandeController implements Serializable {

    @EJB
    private service.CarnetCommandeFacade ejbFacade;
    @EJB
    private service.CarnetCommandeOfFacade carnetCommandeOfFacade;

    private List<CarnetCommande> items = null;
    private List<CarnetCommandeOf> carnetCommandeOfs;

    private CarnetCommande selected;
    private CarnetCommandeOf selectedCarnetCommandeOf;

    private boolean carnetCommandeAdded;

    public void genereatePdf(CarnetCommande carnetCommande) throws JRException, IOException {
        ejbFacade.genratePdf(carnetCommandeOfs, carnetCommande);
        FacesContext.getCurrentInstance().getResponseComplete();
    }

    public void deleteCarnetCommandeOf() {
        carnetCommandeOfs.remove(carnetCommandeOfs.indexOf(selectedCarnetCommandeOf));
        carnetCommandeOfFacade.remove(selectedCarnetCommandeOf);
    }

    public void updateCarnetCommandeOf() {
        carnetCommandeOfFacade.edit(selectedCarnetCommandeOf);
        carnetCommandeOfs.set(carnetCommandeOfs.indexOf(selectedCarnetCommandeOf), selectedCarnetCommandeOf);
    }

    public void creationCarnetCommandeOf() {
        carnetCommandeOfs.add(selectedCarnetCommandeOf);
        carnetCommandeOfFacade.create(selectedCarnetCommandeOf);
        selectedCarnetCommandeOf = new CarnetCommandeOf();
    }

    public void prepareCreationCarnetCommandeOf() {
        selectedCarnetCommandeOf = new CarnetCommandeOf();
        selectedCarnetCommandeOf.setCarnetCommande(selected);
    }

    public void supprimerCarnet() {
        if (selected != null) {
            ejbFacade.supprimerCarnetCommande(selected);
            carnetCommandeOfs.clear();
            items = ejbFacade.findAll();
        }
    }

    public void ajouterCarnetCommande() {
        int code = ejbFacade.createCarnetCommande(selected);
        if (code > 0) {
            getItems().add(selected);
            carnetCommandeAdded = true;
        }
        System.out.println("haaa code " + code);
        showCarnetCreationErrors(code);
    }

    public void saveCarnetCommandeOfs() {
        if (carnetCommandeAdded) {
            carnetCommandeOfFacade.createCarnetCommandeOfs(selected, carnetCommandeOfs);
            carnetCommandeAdded = false;
        }
    }

    public void resetCarnetCommande() {
        selectedCarnetCommandeOf = new CarnetCommandeOf();
    }

    public void supprimerCarnetCommande() {
        carnetCommandeOfs.remove(indexOf(carnetCommandeOfs, selectedCarnetCommandeOf));
        selectedCarnetCommandeOf = new CarnetCommandeOf();
    }

    public void modifierCarnetCommande() {
        carnetCommandeOfs.set(indexOf(carnetCommandeOfs, selectedCarnetCommandeOf), selectedCarnetCommandeOf);
        selectedCarnetCommandeOf = new CarnetCommandeOf();
    }

    public void ajouterCarnetCommandeOf() {
        if (carnetCommandeAdded) {
            getSelectedCarnetCommandeOf().setId(-1l);
            carnetCommandeOfs.add(selectedCarnetCommandeOf);
            System.out.println("haaaaaaaaaaaaaaaaaaaa " + selected);
            selectedCarnetCommandeOf = new CarnetCommandeOf();
        }
        else{
            JsfUtil.addWrningMessage("please Add order book !");
        }
    }

    public void findByCarnetCommande(CarnetCommande carnetCommande) {
        selected = carnetCommande;
        carnetCommandeOfs = carnetCommandeOfFacade.findByCarnetCommande(selected);
    }

    private int indexOf(List<CarnetCommandeOf> carnetCommandeOfs, CarnetCommandeOf carnetCommandeOf) {
        for (int i = 0; i < carnetCommandeOfs.size(); i++) {
            if (carnetCommandeOfs.get(i).getNumeroOf().equals(carnetCommandeOf.getNumeroOf())) {
                return i;
            }
        }
        return -1;
    }

    private void showCarnetCreationErrors(int code) {
        switch (code) {
            case 1:
                return;
            case -1:
                break;
            case -2:
                JsfUtil.addWrningMessage(ErrorUtil.CARNETCOMMANDE_CREATION_NUMEROCARNET_EXIST);
                break;
            default:;
        }
    }

    public CarnetCommandeController() {
    }

    public CarnetCommandeOf getSelectedCarnetCommandeOf() {
        if (selectedCarnetCommandeOf == null) {
            selectedCarnetCommandeOf = new CarnetCommandeOf();
        }
        return selectedCarnetCommandeOf;
    }

    public void setSelectedCarnetCommandeOf(CarnetCommandeOf selectedCarnetCommandeOf) {
        this.selectedCarnetCommandeOf = selectedCarnetCommandeOf;
    }

    public List<CarnetCommandeOf> getCarnetCommandeOfs() {
        if (carnetCommandeOfs == null) {
            carnetCommandeOfs = new ArrayList<>();
        }
        return carnetCommandeOfs;
    }

    public boolean isCarnetCommandeAdded() {
        return carnetCommandeAdded;
    }

    public void setCarnetCommandeAdded(boolean carnetCommandeAdded) {
        this.carnetCommandeAdded = carnetCommandeAdded;
    }

    public void setCarnetCommandeOfs(List<CarnetCommandeOf> carnetCommandeOfs) {
        this.carnetCommandeOfs = carnetCommandeOfs;
    }

    public CarnetCommande getSelected() {
        if (selected == null) {
            selected = new CarnetCommande();
        }
        return selected;
    }

    public void setSelected(CarnetCommande selected) {
        this.selected = selected;
    }

    private CarnetCommandeFacade getFacade() {
        return ejbFacade;
    }

    public void prepareCreate() {
        selected = new CarnetCommande();
        carnetCommandeAdded = false;
        carnetCommandeOfs.clear();
        selectedCarnetCommandeOf = new CarnetCommandeOf();
        selected.setDateCreation(new Date());
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CarnetCommandeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CarnetCommandeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CarnetCommandeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<CarnetCommande> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
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

    public CarnetCommande getCarnetCommande(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<CarnetCommande> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<CarnetCommande> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = CarnetCommande.class)
    public static class CarnetCommandeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CarnetCommandeController controller = (CarnetCommandeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "carnetCommandeController");
            return controller.getCarnetCommande(getKey(value));
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
            if (object instanceof CarnetCommande) {
                CarnetCommande o = (CarnetCommande) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), CarnetCommande.class.getName()});
                return null;
            }
        }

    }

}
