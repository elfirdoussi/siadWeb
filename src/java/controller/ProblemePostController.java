package controller;

import com.dashoptimization.XPRMCompileException;
import controller.util.ErrorUtil;
import controller.util.FileGenerator;
import entities.ProblemePost;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import controller.util.SolutionGenerator;
import java.io.FileNotFoundException;
import java.io.IOException;
import service.ProblemePostFacade;

import java.io.Serializable;
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
import service.ConfigFacade;
import service.ProblemePreFacade;

@ManagedBean(name = "problemePostController")
@SessionScoped
public class ProblemePostController implements Serializable {

    @EJB
    private service.ProblemePostFacade ejbFacade;
    @EJB
    private service.NomenclatureItemFacade nomenclatureItemFacade;

    private List<ProblemePost> items = null;

    private ProblemePost selected;
    private int model = 1;

    @EJB
    private FileGenerator fileGenerator;
    @EJB
    private SolutionGenerator solutionGenerator;
    @EJB
    private ConfigFacade configFacade;
    @EJB
    private ProblemePreFacade problemePreFacade;
  

    public String saveProblemePost() throws FileNotFoundException, XPRMCompileException {
//        int code = ejbFacade.createProblemePost(selected, configFacade.getDataFilePath());
//        if (code > 0) {
//            System.out.println("aaaaaa " + selected.getId());
//            selected = ejbFacade.findByProblemePre(selected.getProblemePre());
//                fileGenerator.generate(selected.getProblemePre(), selected);
//        }
//        showProblemePostErrors(code);
        return "/faces/problemePost/Results.xhtml?faces-redirect=true";
    }

    private void showProblemePostErrors(int code) {
        switch (code) {
            case 1:
                return;
            case -1:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMEPOST_CREATION_PROBLEMEPRE_NOT_SELECTED);
                break;
            case -2:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMEPOST_CREATION_CRITEREOPTIMISATION_NOT_SELECTED);
                break;
            default:;
        }
    }

    public ProblemePostController() {
    }

    public ProblemePost getSelected() {
        if (selected == null) {
            selected = new ProblemePost();
            selected.setProblemePre(problemePreFacade.find(8000l));
        }
        return selected;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public void setSelected(ProblemePost selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProblemePostFacade getFacade() {
        return ejbFacade;
    }

    public ProblemePost prepareCreate() {
        selected = new ProblemePost();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ProblemePostCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ProblemePostUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ProblemePostDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ProblemePost> getItems() {
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

    public ProblemePost getProblemePost(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<ProblemePost> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ProblemePost> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ProblemePost.class)
    public static class ProblemePostControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProblemePostController controller = (ProblemePostController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "problemePostController");
            return controller.getProblemePost(getKey(value));
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
            if (object instanceof ProblemePost) {
                ProblemePost o = (ProblemePost) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ProblemePost.class.getName()});
                return null;
            }
        }

    }

}
