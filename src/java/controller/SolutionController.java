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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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

    @EJB
    private LigneFacade ligneFacade;
    @EJB
    private ProblemePreFacade problemePreFacade;
    @EJB
    private ConfigFacade configFacade;

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

    }

    public void onSelectTimeLineEvent(TimelineSelectEvent e) {
//        eventMap.get(e.getTimelineEvent().getData() + "").updateTempsAbsoluParAtelier(e.getTimelineEvent());
    }

    @PostConstruct
    public void initTimeLine() {
        dataUtil.loadData(configFacade.getDataFilePath());
        Date[] dates = problemePreFacade.findDatesByNump(8000l);
        dateStart = dates[0];
        dateEnd = dates[1];
        timelineUtil = new TimelineUtil(model);
        lignes = ligneFacade.getLignesWithCarnetCommandeOfTxt(8000l, dateStart, dateEnd, eventMap);
        arrets = ligneFacade.getWithCarnetCommandeOfTxtArrets(8000l, dateStart, dateEnd, eventMap);
        timelineUtil.initialize(lignes, arrets);
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
