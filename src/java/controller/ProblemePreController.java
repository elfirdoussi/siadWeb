package controller;

import controller.util.DateUtil;
import controller.util.ErrorUtil;
import controller.util.Item;
import entities.ProblemePre;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import entities.CalendrierMaintenance;
import entities.CarnetCommande;
import entities.CarnetCommandeOf;
import entities.Ligne;
import entities.ProblemeLigneOuverte;
import entities.RegimeMarche;
import entities.Site;
import entities.StockInput;
import entities.StockOutput;
import entities.TypeLigne;
import service.ProblemePreFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

@ManagedBean(name = "problemePreController")
@SessionScoped
public class ProblemePreController implements Serializable {

    @EJB
    private service.ProblemePreFacade ejbFacade;
    @EJB
    private service.LigneFacade ligneFacade;
    @EJB
    private service.ProblemeLigneOuverteFacade problemeLigneOuverteFacade;
    @EJB
    private service.RegimeMarcheFacade regimeMarcheFacade;
    @EJB
    private service.CalendrierMaintenanceFacade calendrierMaintenanceFacade;
    @EJB
    private service.CarnetCommandeFacade carnetCommandeFacade;
    @EJB
    private service.StockOutputFacade stockOutputFacade;
    @EJB
    private service.StockInputFacade stockInputFacade;
    @EJB
    private service.CarnetCommandeOfFacade carnetCommandeOfFacade;

    private List<ProblemePre> items = null;
    private List<ProblemePre> itemsCreation = null;
    private List<ProblemeLigneOuverte> problemeLigneOuvertes;
    private List<Ligne> lignes;
    private List<RegimeMarche> regimeMarches;
    private List<CalendrierMaintenance> calendrierMaintenances;
    private List<CalendrierMaintenance> subListcalendrierMaintenances;
    private List<Item> calendrierMaintenanceDates = new ArrayList<>();
    private List<StockOutput> stockOutputs;
    private List<StockInput> stockInputs;
    private List<CarnetCommande> carnetCommandes;
    private List<CarnetCommandeOf> carnetCommandeOfs;

    private HashMap<Integer, Boolean> verificationMap;
    private HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap;

    private ProblemePre selected;
    private ProblemeLigneOuverte selectedProblemeLigneOuverte;
    private Site selectedSite;
    private TypeLigne selectedTypeLigne;
    private int selectedDate;
    private StockOutput selectedStockOutput;
    private StockInput selectedStockInput;
    private Ligne selectedLigne;
    private int indexTabView;

    private CarnetCommande carnetCommande;
    private Long nump;

    private LineChartModel lineModel;

    private String msg1;
    private String msg2;
    private boolean isNotFaisable;
    private int periods;
    private int operationalLines;
    private int order;

    private boolean prolemPreCreated = false;

    public void changerDateProblemPre() {
        System.out.println("haaaaa "+selected);
        calendrierMaintenanceFacade.correctionDates(getItemsCreation().get(0), problemeLigneOuvertes, calendrierMaintenanceMap);
        prolemPreCreated = true;
    }

    public String initProblemes() {
        items = ejbFacade.findAll();
        return "/faces/problemePre/List.xhtml?faces-redirect=true";
    }

    public String newProblemDemo() {
        Long nump = 8000l;
        selected = ejbFacade.find(nump);
        return prepareEditProblemPre();
    }

    public String newProblem() {
        initCreationProblem();
        return "/faces/problemePre/ProblemCreation.xhtml?faces-redirect=true";
    }

    public String prepareEditProblemPre() {
        prolemPreCreated = false;
        getItemsCreation().clear();
        getItemsCreation().add(selected);
        problemeLigneOuvertes = problemeLigneOuverteFacade.findByProblemePre(selected);
        regimeMarches = regimeMarcheFacade.findByProblemePre(selected);
        calendrierMaintenanceFacade.prepareCalendrierMaintenanceByLigne(selected, getCalendrierMaintenanceMap(), problemeLigneOuvertes, calendrierMaintenanceDates);
        stockOutputs = stockOutputFacade.findByProblemePre(selected);
        stockInputs = stockInputFacade.findByProblemePre(selected);
        return "/faces/problemePre/ProblemCreation.xhtml?faces-redirect=true";
    }

    private LineChartModel initAnalyserGraph(List<Object[]> objects, List<Object[]> objects2) {
        LineChartModel model = new LineChartModel();

        LineChartSeries series = new LineChartSeries();
        series.setLabel((String) objects.get(0)[1]);

        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel((String) objects2.get(0)[1]);

        for (int i = 0; i < objects.size(); i++) {
            series.set(objects.get(i)[2] + "", (float) objects.get(i)[0]);
        }
        for (int i = 0; i < objects2.size(); i++) {
            series2.set((String) objects2.get(i)[2], (float) objects2.get(i)[0]);
        }

        model.addSeries(series);
        model.addSeries(series2);
        return model;
    }

    private void createLineModel1(List<Object[]> objects, List<Object[]> objects2) {
        lineModel = initAnalyserGraph(objects, objects2);
        lineModel.setTitle("Cumulative production per period");
        lineModel.setLegendPosition("e");
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        Axis xAxis = lineModel.getAxis(AxisType.X);
        yAxis.setMin(0);
        yAxis.setMax(1000);
        xAxis.setTickAngle(-90);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("periods"));
    }

    public String prepareAnalyserProbleme() {
        getItemsCreation().clear();
        getItemsCreation().add(selected);
        problemeLigneOuvertes = problemeLigneOuverteFacade.findByProblemePre(selected);

        Object[] res = ejbFacade.analyserProbleme(selected, problemeLigneOuvertes);
        Object[] plusTard = (Object[]) res[0];
        Object[] plusTot = (Object[]) res[1];
        isNotFaisable = (boolean) res[2];
        msg1 = (String) res[3];
        msg2 = (String) res[4];
        operationalLines = problemeLigneOuvertes.size();
        periods = DateUtil.getNumberOfPeriod(selected.getDateDebut(), selected.getDateFin());
        order = (int) res[5];

        if (plusTot != null && plusTard != null) {
            List<Object[]> objectsPlusTot = (List<Object[]>) plusTot[1];
//        objectsPlusTot.forEach((o) -> {
//            System.out.println("  " + o[0]);
//            System.out.println("  " + o[1]);
//            System.out.println("  " + o[2]);
//        });
            List<Object[]> objectsPlusTard = (List<Object[]>) plusTard[1];
//        objectsPlusTard.forEach((o) -> {
//            System.out.println("  " + o[0]);
//            System.out.println("  " + o[1]);
//            System.out.println("  " + o[2]);
//        }); 
            createLineModel1(objectsPlusTot, objectsPlusTard);
        }

        JsfUtil.addSuccessMessage(msg2);
        return "/faces/problemePre/Analyser.xhtml";
    }

    public void deleteAll() {
        int code = ejbFacade.deleteAll(selected);
        if (code > 0) {
            items.remove(selected);
            selected = null;
        }
    }

    public void initCreationProblem() {
        getItemsCreation().clear();
        getProblemeLigneOuvertes().clear();
        getRegimeMarches().clear();
        getCalendrierMaintenanceMap().clear();
        getStockOutputs().clear();
        getStockInputs().clear();
        getSubListcalendrierMaintenances().clear();
    }

    public void saveAll() {
        int code = ejbFacade.saveAllProblem(itemsCreation.get(0), problemeLigneOuvertes, regimeMarches, calendrierMaintenanceMap, stockOutputs, stockInputs);
        if (code > 0) {
            itemsCreation.clear();
            problemeLigneOuvertes.clear();
            regimeMarches.clear();
            calendrierMaintenanceMap.clear();
            stockOutputs.clear();
            stockInputs.clear();
            subListcalendrierMaintenances.clear();
        }
    }

    public void btnNextTab() {
//        if (!problemeLigneOuvertes.isEmpty() && calendrierMaintenanceDates.isEmpty() && getCalendrierMaintenanceMap().isEmpty()) {
        prepareDatesCalendrierMaintenance();
//        }else{

//        }
    }

    public void ajouterStockInput() {
        selectedStockInput.setProblemePre(itemsCreation.isEmpty() ? null : itemsCreation.get(0));
        int code = stockInputFacade.createStockInput(selectedStockInput);
        if (code > 0) {
            getStockInputs().add(selectedStockInput);
            selectedStockInput = new StockInput();
        }
    }

    public void modifierStockInput() {
        int code = stockInputFacade.modifierStockInput(selectedStockInput, getStockInputs());
        if (code > 0) {
            selectedStockInput = new StockInput();
        }
    }

    public void supprimerStockInput() {
        int code = stockInputFacade.supprimerStockInput(selectedStockInput, getStockInputs());
        if (code > 0) {
            selectedStockInput = new StockInput();
        }
    }

    public void resetCarnetStockInput() {
        selectedStockInput = new StockInput();
    }

    public void ajouterStockOutput() {
        selectedStockOutput.setProblemePre(itemsCreation.isEmpty() ? null : itemsCreation.get(0));
        int code = stockOutputFacade.createStockOutput(selectedStockOutput);
        if (code > 0) {
            getStockOutputs().add(selectedStockOutput);
            selectedStockOutput = new StockOutput();
        }
    }

    public void modifierStockOutput() {
        int code = stockOutputFacade.modifierStockOutput(selectedStockOutput, getStockOutputs());
        if (code > 0) {
            selectedStockOutput = new StockOutput();
        }
    }

    public void supprimerStockOutput() {
        int code = stockOutputFacade.supprimerStockOutput(selectedStockOutput, getStockOutputs());
        if (code > 0) {
            selectedStockOutput = new StockOutput();
        }
    }

    public void resetCarnetStockOutput() {
        selectedStockOutput = new StockOutput();
    }

    public void updateRegimeMarche(RegimeMarche regimeMarche, int index) {
        regimeMarches.set(index, regimeMarche);
    }

    public void ajouterProblemeLigneOuverte() {
        ProblemePre problemePre = itemsCreation.isEmpty() ? null : itemsCreation.get(0);
        int code = problemeLigneOuverteFacade.addProblemeLigneOuvert(problemePre, selectedProblemeLigneOuverte, getProblemeLigneOuvertes());
        if (code > 0) {
            regimeMarcheFacade.ajouterRegimeMarche(selectedProblemeLigneOuverte, regimeMarches);
            selectedProblemeLigneOuverte = new ProblemeLigneOuverte();
        }
        showAddProblemLigneOuverteErrors(code);
    }

    public void modifierProblemeLigneOuverte() {
        int code = problemeLigneOuverteFacade.modifierProblemeLigneOuverte(selectedProblemeLigneOuverte, problemeLigneOuvertes);
        if (code > 0) {
            selectedProblemeLigneOuverte = new ProblemeLigneOuverte();
        }
    }

    public void supprimerProblemeLigneOuverte() {
        int code = problemeLigneOuverteFacade.supprimerProblemeLigneOuverte(selectedProblemeLigneOuverte, problemeLigneOuvertes);
        if (code > 0) {
            regimeMarcheFacade.supprimerRegimeMarche(selectedProblemeLigneOuverte, regimeMarches);
            selectedProblemeLigneOuverte = new ProblemeLigneOuverte();
        }
    }

    public void resetProblemeLigneOuverte() {
        selectedProblemeLigneOuverte = new ProblemeLigneOuverte();
    }

    public void findBySite() {
        lignes = ligneFacade.findBySite(selectedSite);
    }

    public void prepareDatesCalendrierMaintenance() {
        calendrierMaintenanceFacade.prepareCalendrierMaintenance(itemsCreation.get(0), calendrierMaintenanceDates, problemeLigneOuvertes, getCalendrierMaintenanceMap());
    }

    public void searchCalendrierMaintenance() {
        subListcalendrierMaintenances = calendrierMaintenanceFacade.searchCalendrierMaintenance(selectedLigne, selectedDate, calendrierMaintenanceMap);
    }

    public void updateCalendrierMaintenance(CalendrierMaintenance calendrierMaintenance, int index) {
        System.out.println("updated !");
        calendrierMaintenanceMap.get(selectedLigne).set(index, calendrierMaintenance);
    }

    private void showAddProblemLigneOuverteErrors(int code) {
        switch (code) {
            case 1:
                return;
            case -1:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMELIGNEOUVERTE_CREATION_LIGNE_NOT_SELECTED);
                break;
            case -2:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMELIGNEOUVERTE_CREATION_EXIST);
                break;
            case -3:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMELIGNEOUVERTE_CREATION_PROBLEMEPRE_NOTSELECTED);
                break;
            default:;
        }
    }

    public ProblemePreController() {
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public int getOperationalLines() {
        return operationalLines;
    }

    public void setOperationalLines(int operationalLines) {
        this.operationalLines = operationalLines;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getMsg1() {
        return msg1;
    }

    public void setMsg1(String msg1) {
        this.msg1 = msg1;
    }

    public String getMsg2() {
        return msg2;
    }

    public void setMsg2(String msg2) {
        this.msg2 = msg2;
    }

    public HashMap<Ligne, List<CalendrierMaintenance>> getCalendrierMaintenanceMap() {
        if (calendrierMaintenanceMap == null) {
            calendrierMaintenanceMap = new HashMap<>();
        }
        return calendrierMaintenanceMap;
    }

    public void setCalendrierMaintenanceMap(HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap) {
        this.calendrierMaintenanceMap = calendrierMaintenanceMap;
    }

    public List<CalendrierMaintenance> getSubListcalendrierMaintenances() {
        if (subListcalendrierMaintenances == null) {
            subListcalendrierMaintenances = new ArrayList<>();
        }
        return subListcalendrierMaintenances;
    }

    public void setSubListcalendrierMaintenances(List<CalendrierMaintenance> subListcalendrierMaintenances) {
        this.subListcalendrierMaintenances = subListcalendrierMaintenances;
    }

    public List<ProblemePre> getItemsCreation() {
        if (itemsCreation == null) {
            itemsCreation = new ArrayList<>();
        }
        return itemsCreation;
    }

    public void setItemsCreation(List<ProblemePre> itemsCreation) {
        this.itemsCreation = itemsCreation;
    }

    public Long getNump() {
        return nump;
    }

    public void setNump(Long nump) {
        this.nump = nump;
    }

    public List<CarnetCommande> getCarnetCommandes() {
        if (carnetCommandes == null) {
            carnetCommandes = carnetCommandeFacade.findAll();
        }
        return carnetCommandes;
    }

    public void setCarnetCommandes(List<CarnetCommande> carnetCommandes) {
        this.carnetCommandes = carnetCommandes;
    }

    public CarnetCommande getCarnetCommande() {
        return carnetCommande;
    }

    public LineChartModel getLineModel() {
        if (lineModel == null) {
            lineModel = new LineChartModel();
        }
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public void setCarnetCommande(CarnetCommande carnetCommande) {
        this.carnetCommande = carnetCommande;
    }

    public Ligne getSelectedLigne() {
        return selectedLigne;
    }

    public void setSelectedLigne(Ligne selectedLigne) {
        this.selectedLigne = selectedLigne;
    }

    public int getIndexTabView() {
        return indexTabView;
    }

    public void setIndexTabView(int indexTabView) {
        this.indexTabView = indexTabView;
    }

    public StockInput getSelectedStockInput() {
        if (selectedStockInput == null) {
            selectedStockInput = new StockInput();
        }
        return selectedStockInput;
    }

    public void setSelectedStockInput(StockInput selectedStockInput) {
        this.selectedStockInput = selectedStockInput;
    }

    public List<StockOutput> getStockOutputs() {
        if (stockOutputs == null) {
            stockOutputs = new ArrayList<>();
        }
        return stockOutputs;
    }

    public void setStockOutputs(List<StockOutput> stockOutputs) {
        this.stockOutputs = stockOutputs;
    }

    public List<StockInput> getStockInputs() {
        if (stockInputs == null) {
            stockInputs = new ArrayList<>();
        }
        return stockInputs;
    }

    public void setStockInputs(List<StockInput> stockInputs) {
        this.stockInputs = stockInputs;
    }

    public StockOutput getSelectedStockOutput() {
        if (selectedStockOutput == null) {
            selectedStockOutput = new StockOutput();
        }
        return selectedStockOutput;
    }

    public void setSelectedStockOutput(StockOutput selectedStockOutput) {
        this.selectedStockOutput = selectedStockOutput;
    }

    public List<Item> getCalendrierMaintenanceDates() {
        if (calendrierMaintenanceDates == null) {
            calendrierMaintenanceDates = new ArrayList<>();
        }
        return calendrierMaintenanceDates;
    }

    public void setCalendrierMaintenanceDates(List<Item> calendrierMaintenanceDates) {
        this.calendrierMaintenanceDates = calendrierMaintenanceDates;
    }

    public int getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(int selectedDate) {
        this.selectedDate = selectedDate;
    }

    public TypeLigne getSelectedTypeLigne() {
        return selectedTypeLigne;
    }

    public void setSelectedTypeLigne(TypeLigne selectedTypeLigne) {
        this.selectedTypeLigne = selectedTypeLigne;
    }

    public List<CalendrierMaintenance> getCalendrierMaintenances() {
        if (calendrierMaintenances == null) {
            calendrierMaintenances = new ArrayList<>();
        }
        return calendrierMaintenances;
    }

    public void setCalendrierMaintenances(List<CalendrierMaintenance> calendrierMaintenances) {
        this.calendrierMaintenances = calendrierMaintenances;
    }

    public List<RegimeMarche> getRegimeMarches() {
        if (regimeMarches == null) {
            regimeMarches = new ArrayList<>();
        }
        return regimeMarches;
    }

    public void setRegimeMarches(List<RegimeMarche> regimeMarches) {
        this.regimeMarches = regimeMarches;
    }

    public List<ProblemeLigneOuverte> getProblemeLigneOuvertes() {
        System.out.println("haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + problemeLigneOuvertes);
        if (problemeLigneOuvertes == null) {
            problemeLigneOuvertes = new ArrayList<>();
        }
        return problemeLigneOuvertes;
    }

    public void setProblemeLigneOuvertes(List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        this.problemeLigneOuvertes = problemeLigneOuvertes;
    }

    public List<Ligne> getLignes() {
        if (lignes == null) {
            lignes = new ArrayList<>();
        }
        return lignes;
    }

    public void setLignes(List<Ligne> lignes) {
        this.lignes = lignes;
    }

    public Site getSelectedSite() {
        return selectedSite;
    }

    public void setSelectedSite(Site selectedSite) {
        this.selectedSite = selectedSite;
    }

    public ProblemeLigneOuverte getSelectedProblemeLigneOuverte() {
        if (selectedProblemeLigneOuverte == null) {
            selectedProblemeLigneOuverte = new ProblemeLigneOuverte();
        }
        return selectedProblemeLigneOuverte;
    }

    public void setSelectedProblemeLigneOuverte(ProblemeLigneOuverte selectedProblemeLigneOuverte) {
        this.selectedProblemeLigneOuverte = selectedProblemeLigneOuverte;
    }

    public ProblemePre getSelected() {
        if (selected == null) {
            selected = new ProblemePre();
        }
        return selected;
    }

    public void setSelected(ProblemePre selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProblemePreFacade getFacade() {
        return ejbFacade;
    }

    public ProblemePre prepareCreate() {
        selected = new ProblemePre();
        selected.setDateCreation(new Date());
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        int code = ejbFacade.verifyAddProblemPre(selected);
        System.out.println("haaaa " + code);
        if (code > 0) {
            itemsCreation.add(selected);
            prolemPreCreated = true;
        }
        showAddProblemeErrors(code);
    }

    private void showAddProblemeErrors(int code) {
        switch (code) {
            case 1:
                return;
            case -1:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMELIGNEOUVERTE_CREATION_LIGNE_NOT_SELECTED);
                break;
            case -2:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMELIGNEOUVERTE_CREATION_EXIST);
                break;
            case -3:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMELIGNEOUVERTE_CREATION_PROBLEMEPRE_NOTSELECTED);
                break;
            case -4:
                JsfUtil.addWrningMessage(ErrorUtil.PROBLEMELIGNEOUVERTE_CREATION_PROBLEMEPRE_NOTSELECTED);
                break;
            default:;
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ProblemePreUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ProblemePreDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public boolean isProlemPreCreated() {
        return prolemPreCreated;
    }

    public void setProlemPreCreated(boolean prolemPreCreated) {
        this.prolemPreCreated = prolemPreCreated;
    }

    public List<ProblemePre> getItems() {
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

    public ProblemePre getProblemePre(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<ProblemePre> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ProblemePre> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ProblemePre.class)
    public static class ProblemePreControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProblemePreController controller = (ProblemePreController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "problemePreController");
            return controller.getProblemePre(getKey(value));
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
            if (object instanceof ProblemePre) {
                ProblemePre o = (ProblemePre) object;
                return getStringKey(o.getNump());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ProblemePre.class.getName()});
                return null;
            }
        }

    }

}
