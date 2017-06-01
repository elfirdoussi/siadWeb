package controller;

import entities.Ligne;
import entities.ProblemePre;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.timeline.TimelineModificationEvent;
import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.timeline.TimelineModel;
import service.ConfigFacade;
import service.LigneFacade;
import service.ProblemePreFacade;
import controller.util.*;
import entities.CarnetCommandeOf;
import entities.DoodleGamme;
import entities.Engrais;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import org.primefaces.model.timeline.TimelineEvent;
import service.CarnetCommandeOfFacade;
import service.DoodleGammeFacade;

@SessionScoped
@ManagedBean(name = "solutionController")
public class SolutionController implements Serializable {

    private DataUtil dataUtil = new DataUtil();
    private TimelineUtil timelineUtil;

    private TimelineModel model = new TimelineModel();
    private LineChartModel lineModel1;
    private LineChartModel lineModel2;
    private LineChartModel lineModel3;
    private LineChartModel lineModel4;
    private Date dateStart;
    private Date dateEnd;
    private HashMap<String, Event> eventMap = new HashMap<>();
    private HashMap<String, LineChartSeries> seriesModelMap2 = new HashMap<>();
    private HashMap<String, LineChartSeries> seriesModelMap3 = new HashMap<>();

    private ProblemePre selectedProblemePre;
    private List<Ligne> lignes;
    private List<Ligne> arrets;

    private Event selectedEvent;
    private Date[][] selectedDates;
    private HashMap<String, Date[][]> timeLineDatesMap = new HashMap<>();

    @EJB
    private LigneFacade ligneFacade;
    @EJB
    private ProblemePreFacade problemePreFacade;
    @EJB
    private ConfigFacade configFacade;
    @EJB
    private DoodleGammeFacade doodleGammeFacade;
    @EJB
    private CarnetCommandeOfFacade carnetCommandeOfFacade;

    public void onChangeTimeLineEvent(TimelineModificationEvent e) {
        Event event = eventMap.get(e.getTimelineEvent().getData() + "");

        event.updateTempsAbsoluParAtelier(e.getTimelineEvent());
        createLineModels();
//        lineModel2.getSeries().
//                set(lineModel2.getSeries().
//                indexOf(seriesModelMap2.get(event.getCarnetCommandeOf().getEngrais().getLibelle())),
//                initLinerModel2SeriesByLigne(
//                 ConsommationProductionUtil.getLoadData(event.getCarnetCommandeOf().getEngrais().getLibelle()),
//                        event.getCarnetCommandeOf().getEngrais().getLibelle()));
//        System.out.println("ZXW -> " + event.getLigne());
//        correctDates(event, event.getLigne());
    }

    public void onSelectTimeLineEvent(TimelineSelectEvent e) {
//        eventMap.get(e.getTimelineEvent().getData() + "").updateTempsAbsoluParAtelier(e.getTimelineEvent());
        System.out.println("haaaaaaaaa " + e.getTimelineEvent().getStartDate());
        System.out.println("haaaaaaaaa " + e.getTimelineEvent().getEndDate());
        System.out.println("haaaaaaaaa " + e.getTimelineEvent().getData());
        System.out.println("kaaaaaaa " + eventMap.get(e.getTimelineEvent().getData() + "").getCarnetCommandeOf().getId());
        Event event = eventMap.get(e.getTimelineEvent().getData() + "");
        selectedEvent = new Event();
        selectedEvent.setCarnetCommandeOf(event.getCarnetCommandeOf());
        selectedEvent.setDateStart(event.getDateStart());
        selectedEvent.setDateEnd(event.getDateEnd());
        selectedEvent.setLigne(event.getLigne());
        selectedDates = timeLineDatesMap.get(selectedEvent.getLigne().getNomLigne());

        System.out.println("selectedEvent.getLigne() " + selectedEvent.getLigne());

        Date[][] dates = timeLineDatesMap.get(selectedEvent.getLigne().getNomLigne());
        for (int i = 0; i < dates[0].length; i++) {
//            if (event.getDateStart().before(dates[1][i]) && event.getDateEnd().after(dates[0][i])) {
            System.out.println("cccccc n dates : " + dates[0][i]);
            System.out.println("cccccc n dates : " + dates[1][i]);
//            }
        }

//        System.out.println("selectedEvent " + event.getLigne());
//        System.out.println("******************************************* ");
//        timeLineDatesMap.forEach((k, v) -> {
//            System.out.println("key " + k + " value " + v);
//        });
//        System.out.println("******************************************* ");
    }

    private void correctDates(Event event, Ligne ligne) {
        Date[][] dates = timeLineDatesMap.get(ligne.getNomLigne());
        for (int i = 0; i < dates.length; i++) {
//            if (event.getDateStart().before(dates[1][i]) && event.getDateEnd().after(dates[0][i])) {
            System.out.println("cccccc n dates : " + dates[0][i]);
            System.out.println("cccccc n dates : " + dates[1][i]);
//            }
        }
    }

    public void verifyEventsPosition(TimelineModificationEvent e) {
        TimelineEvent timelineEvent = e.getTimelineEvent();
        Event event = eventMap.get(e.getTimelineEvent().getData() + "");
        boolean chauv = false;
        boolean oper = false;
        int indice = -1;

        CarnetCommandeOf carnetCommandeOf = carnetCommandeOfFacade.find(event.getCarnetCommandeOf().getId());

        if (!verifyRouting(carnetCommandeOf.getEngrais(), timelineEvent.getGroup())) {
            System.out.println("haaaaa mmm " + carnetCommandeOf.getLigne());
            JsfUtil.addWrningMessage("attention...!");
        } else {
            event.getTimelineEvent().setGroup(timelineEvent.getGroup());
            event.setLigne(ligneFacade.findByExactLibelle(timelineEvent.getGroup()));
        }

        Date[][] dates = timeLineDatesMap.get(event.getLigne().getNomLigne());
        for (int i = 0; i < dates[0].length; i++) {
//            System.out.println("haaaaa dates[0] : " + dates[0][i]);
//            System.out.println("cccccc n dates[1] : " + dates[1][i]);
            if (event.getDateStart() == dates[0][i] && event.getDateEnd() == dates[1][i]) {
                indice = i;
            }
            if (dates[1][i] != null && dates[0][i] != null && event.getDateStart() != dates[0][i] && event.getDateEnd() != dates[1][i]) {
                oper = true;
                if (timelineEvent.getStartDate().before(dates[1][i]) && timelineEvent.getStartDate().after(dates[0][i])) {
                    System.out.println("HANA AO");
                    chauv = true;
                }
                if (!chauv && timelineEvent.getEndDate().before(dates[1][i]) && timelineEvent.getEndDate().after(dates[0][i])) {
                    System.out.println("HANA RO");
                    chauv = true;
                }
            }

        }
        if (!chauv && oper) {
            event.getTimelineEvent().setStartDate(timelineEvent.getStartDate());
            event.getTimelineEvent().setEndDate(timelineEvent.getEndDate());
            event.setDateStart(timelineEvent.getStartDate());
            event.setDateEnd(timelineEvent.getEndDate());
            if (indice != -1) {
                dates[0][indice] = event.getDateStart();
                dates[1][indice] = event.getDateEnd();
                timeLineDatesMap.put(event.getLigne().getNomLigne(), dates);
            }
        }
    }

    public void verifyEvents(TimelineModificationEvent e) {
        Event event = eventMap.get(e.getTimelineEvent().getData() + "");
        System.out.println("event " + event.getDateStart());
        System.out.println("event 2" + e.getTimelineEvent().getStartDate());
        System.out.println("haaaaaaaaaaaaaaaa 00");
        for (int i = 0; i < selectedDates[0].length; i++) {
            if (selectedDates[0][i] != null && selectedDates[1][i] != null && selectedDates[0][i] != event.getDateStart() && selectedDates[1][i] != event.getDateEnd()) {

                if ((e.getTimelineEvent().getStartDate().before(selectedDates[1][i])
                        && e.getTimelineEvent().getStartDate().after(selectedDates[0][i]))
                        || (e.getTimelineEvent().getEndDate().before(selectedDates[1][i])
                        && e.getTimelineEvent().getEndDate().after(selectedDates[0][i]))) {
//                    System.out.println("daaaaaaaates 0:" + selectedDates[0][i]);
//                    System.out.println("daaaaaaaates 1:" + selectedDates[1][i]);

//                    e.getTimelineEvent().setStartDate(event.getDateStart());
//                    e.getTimelineEvent().setEndDate(event.getDateEnd());
                    event.getTimelineEvent().setStartDate(event.getDateStart());
                    event.getTimelineEvent().setEndDate(event.getDateEnd());
                    JsfUtil.addWrningMessage("attention...!");

                } else {
                    event.setDateStart(e.getTimelineEvent().getStartDate());
                    event.setDateEnd(e.getTimelineEvent().getEndDate());
                    event.getTimelineEvent().setStartDate(e.getTimelineEvent().getStartDate());
                    event.getTimelineEvent().setEndDate(e.getTimelineEvent().getEndDate());
                }
            } else {
                event.setDateStart(e.getTimelineEvent().getStartDate());
                event.setDateEnd(e.getTimelineEvent().getEndDate());
                event.getTimelineEvent().setStartDate(e.getTimelineEvent().getStartDate());
                event.getTimelineEvent().setEndDate(e.getTimelineEvent().getEndDate());
            }

        }

        CarnetCommandeOf carnetCommandeOf = carnetCommandeOfFacade.find(event.getCarnetCommandeOf().getId());

        System.out.println("haaaaa mmm ss 1 : " + event.getCarnetCommandeOf().getId());
        System.out.println("haaaaa mmm ss" + carnetCommandeOf.getLigne());

        if (!verifyRouting(carnetCommandeOf.getEngrais(), e.getTimelineEvent().getGroup())) {
            System.out.println("haaaaa mmm " + carnetCommandeOf.getLigne());
            JsfUtil.addWrningMessage("attention...!");
        }
        e.getTimelineEvent().setGroup(carnetCommandeOf.getLigne().getNomLigne());
    }

    private boolean verifyRouting(Engrais engrais, String strLigne) {
        Ligne ligne = ligneFacade.findByExactLibelle(strLigne);
        DoodleGamme doodleGamme = doodleGammeFacade.findByEngraisAndTypeLigne(engrais, ligne.getTypeLigne());
        if (doodleGamme != null && doodleGamme.isValeur()) {
            return true;
        }
        return false;
    }

    @PostConstruct
    public void initTimeLine() {
        dataUtil.loadData(configFacade.getDataFilePath());
        Date[] dates = problemePreFacade.findDatesByNump(8000l);
        dateStart = dates[0];
        dateEnd = dates[1];
        timelineUtil = new TimelineUtil(model);
        lignes = ligneFacade.getLignesWithCarnetCommandeOfTxt(8000l, dateStart, dateEnd, eventMap, timeLineDatesMap);
        arrets = ligneFacade.getWithCarnetCommandeOfTxtArrets(8000l, dateStart, dateEnd, eventMap);
        timelineUtil.initialize(lignes, arrets, eventMap);
        createLineModels();
    }

    private void createLineModels() {
        double[][] accumulationOfConsumption = new double[2][Data.getInstance().getNbrePeriodes()];
        createLineModel1(accumulationOfConsumption);
        createLineModel2();
        createLineModel3();
        createLineModel4(accumulationOfConsumption);
    }

    private void createLineModel1(double[][] accumulationOfConsumption) {
        lineModel1 = initLinearModel(accumulationOfConsumption);
        lineModel1.setTitle("Acide consumption");
        lineModel1.setLegendPosition("e");
        Axis yAxis = lineModel1.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(200);
    }

    private void createLineModel2() {
        lineModel2 = initLinerModel2();
        lineModel2.setTitle("Production of phosphate fertilier");
        lineModel2.setLegendPosition("e");
        Axis yAxis2 = lineModel2.getAxis(AxisType.Y);
        yAxis2.setMin(0);
        yAxis2.setMax(260);
    }

    private void createLineModel3() {
        lineModel3 = initLinerModel3();
        lineModel3.setTitle("Accumulation of production");
        lineModel3.setLegendPosition("e");
        Axis yAxis2 = lineModel3.getAxis(AxisType.Y);
        yAxis2.setMin(0);
        yAxis2.setMax(14000);
    }

    private void createLineModel4(double[][] accumulationOfConsumption) {
        lineModel4 = initLinearModel4(accumulationOfConsumption);
        lineModel4.setTitle("Accumulation of consumption");
        lineModel4.setLegendPosition("e");
        Axis yAxis = lineModel4.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(27000);
    }

    private LineChartModel initLinearModel(double[][] accumulationOfConsumption) {
        LineChartModel model = new LineChartModel();

        LineChartSeries series = new LineChartSeries();
        series.setLabel("Series 1");

        double[][] data = ConsommationProductionUtil.getData2FromTxt(accumulationOfConsumption);

        for (int i = 0; i < data[0].length; i++) {
            series.set(data[0][i], data[1][i]);
        }
//        model.setExtender("myLineChartExtender");
        model.addSeries(series);
        return model;
    }

    private LineChartModel initLinearModel4(double[][] accumulationOfConsumption) {
        LineChartModel model = new LineChartModel();
        LineChartSeries series = new LineChartSeries();
        series.setLabel("Series 1");
        for (int i = 0; i < accumulationOfConsumption[0].length; i++) {
            series.set(accumulationOfConsumption[0][i], accumulationOfConsumption[1][i]);
        }
//        model.setExtender("myLineChartExtender");
        model.addSeries(series);
        return model;
    }

    private LineChartModel initLinerModel2() {
        LineChartModel model = new LineChartModel();
        List<double[][]> cumulProductionsParQualite = new ArrayList<>();
        List<double[][]> datas = ConsommationProductionUtil.getData1FromTxt(Data.getInstance().getDistinctQualite(), cumulProductionsParQualite);

        for (int i = 0; i < Data.getInstance().getDistinctQualite().size(); i++) {
            LineChartSeries lineChartSeries = initLinerModel2SeriesByLigne(datas.get(i), Data.getInstance().getDistinctQualite().get(i));
            LineChartSeries lineChartCumulSeries = initLinerModel2SeriesByLigne(cumulProductionsParQualite.get(i), Data.getInstance().getDistinctQualite().get(i));
            model.addSeries(lineChartSeries);
            seriesModelMap2.put(Data.getInstance().getDistinctQualite().get(i), lineChartSeries);
            seriesModelMap3.put(Data.getInstance().getDistinctQualite().get(i), lineChartCumulSeries);
        }

//        model.setExtender("myLineChartExtender");
        return model;
    }

    private LineChartModel initLinerModel3() {
        LineChartModel model = new LineChartModel();
        for (int i = 0; i < Data.getInstance().getDistinctQualite().size(); i++) {
            model.addSeries(seriesModelMap3.get(Data.getInstance().getDistinctQualite().get(i)));
        }
//        model.setExtender("myLineChartExtender");
        return model;
    }

    private LineChartSeries initLinerModel2SeriesByLigne(double[][] data, String qualite) {
        LineChartSeries series = new LineChartSeries();
        series.setLabel(qualite);
        for (int j = 0; j < data[0].length; j++) {
            series.set(data[0][j], data[1][j]);
        }
        return series;
    }

    public SolutionController() {
        super();
        // TODO Auto-generated constructor stub
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
    }

    public ProblemePre getSelectedProblemePre() {
        return selectedProblemePre;
    }

    public void setSelectedProblemePre(ProblemePre selectedProblemePre) {
        this.selectedProblemePre = selectedProblemePre;
    }

    public List<Ligne> getLignes() {
        return lignes;
    }

    public void setLignes(List<Ligne> lignes) {
        this.lignes = lignes;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public LineChartModel getLineModel3() {
        return lineModel3;
    }

    public LineChartModel getLineModel4() {
        return lineModel4;
    }

    public void setLineModel4(LineChartModel lineModel4) {
        this.lineModel4 = lineModel4;
    }

    public void setLineModel3(LineChartModel lineModel3) {
        this.lineModel3 = lineModel3;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public TimelineUtil getTimelineUtil() {
        return timelineUtil;
    }

    public void setTimelineUtil(TimelineUtil timelineUtil) {
        this.timelineUtil = timelineUtil;
    }

    public DataUtil getDataUtil() {
        return dataUtil;
    }

    public void setDataUtil(DataUtil dataUtil) {
        this.dataUtil = dataUtil;
    }

    public LineChartModel getLineModel1() {
        return lineModel1;
    }

    public void setLineModel1(LineChartModel lineModel1) {
        this.lineModel1 = lineModel1;
    }

    public List<Ligne> getArrets() {
        return arrets;
    }

    public void setArrets(List<Ligne> arrets) {
        this.arrets = arrets;
    }

    public LigneFacade getLigneFacade() {
        return ligneFacade;
    }

    public void setLigneFacade(LigneFacade ligneFacade) {
        this.ligneFacade = ligneFacade;
    }

    public ConfigFacade getConfigFacade() {
        return configFacade;
    }

    public void setConfigFacade(ConfigFacade configFacade) {
        this.configFacade = configFacade;
    }

    public LineChartModel getLineModel2() {
        return lineModel2;
    }

    public void setLineModel2(LineChartModel lineModel2) {
        this.lineModel2 = lineModel2;
    }

}
