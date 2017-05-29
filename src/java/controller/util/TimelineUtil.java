package controller.util;

import entities.CarnetCommandeOf;
import entities.Ligne;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;

public class TimelineUtil {

    private TimelineModel model;
    private HashMap<String, TimelineGroup> timeLineMap = new HashMap<>();
    private Date start;
    private Date end;

    public TimelineUtil() {
        super();
    }

    public TimelineUtil(TimelineModel model) {
        super();
        this.model = model;
    }

    public void initialize(List<Ligne> lignes, List<Ligne> arrets, HashMap<String, Event> eventMap) {
        model.addAllGroups(constructGroups(lignes));
        model.addAll(constructEvents(lignes, eventMap));
        model.addAll(constructArrets(arrets));
    }

    private List<TimelineGroup> constructGroups(List<Ligne> lignes) {
        List<TimelineGroup> timelineGroups = new ArrayList<>();
        lignes.forEach((l) -> {
            TimelineGroup timelineGroup = new TimelineGroup();
            timelineGroup.setId(l.getNomLigne());
            timeLineMap.put(l.getNomLigne(), timelineGroup);
            timelineGroups.add(timelineGroup);
        });
        return timelineGroups;
    }

    private List<TimelineEvent> constructEvents(List<Ligne> lignes, HashMap<String, Event> eventMap) {
        List<TimelineEvent> timelineEvents = new ArrayList<>();
        lignes.forEach((l) -> {
            timelineEvents.addAll(buildEventsByLine(l,eventMap));
        });
        return timelineEvents;
    }

    private List<TimelineEvent> buildEventsByLine(Ligne ligne, HashMap<String, Event> eventMap) {
        List<TimelineEvent> timelineEvents = new ArrayList<>();
        int i =0;
        for (CarnetCommandeOf c : ligne.getCarnetCommandeOfs()) {
            TimelineEvent timelineEvent = new TimelineEvent();
            timelineEvent.setData(c.getTitle());
            timelineEvent.setGroup(ligne.getNomLigne());
            timelineEvent.setStartDate(c.getDateLiveTot());
            timelineEvent.setEndDate(c.getDateLiveTard());
            timelineEvent.setEditable(true);
            timelineEvent.setStyleClass("color_" + i);
            timelineEvents.add(timelineEvent);
            eventMap.get(c.getTitle()).setTimelineEvent(timelineEvent);
            i++;
        }
        return timelineEvents;
    }

    private List<TimelineEvent> constructArrets(List<Ligne> lignes) {
        List<TimelineEvent> timelineEvents = new ArrayList<>();
        lignes.forEach((l) -> {
            timelineEvents.addAll(buildArretsByLine(l));
        });
        return timelineEvents;
    }

    private List<TimelineEvent> buildArretsByLine(Ligne ligne) {
        List<TimelineEvent> timelineEvents = new ArrayList<>();
        ligne.getCarnetCommandeOfs().forEach((c) -> {
            TimelineEvent timelineEvent = new TimelineEvent();
            timelineEvent.setData(c.getTitle());
            timelineEvent.setGroup(ligne.getNomLigne());
            timelineEvent.setStartDate(c.getDateLiveTot());
            timelineEvent.setEndDate(c.getDateLiveTard());
            timelineEvent.setEditable(true);
            timelineEvent.setStyleClass("arret");
            timelineEvents.add(timelineEvent);
        });
        return timelineEvents;
    }

}
