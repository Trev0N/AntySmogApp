package pl.bucior.antysmogapp.api;

import java.io.Serializable;
import java.util.List;

public class MeasurementResponse implements Serializable {

    private MeasurementDto current;
    private List<MeasurementDto> history;
    private List<MeasurementDto> forecast;


    public MeasurementResponse() {
    }

    public MeasurementResponse(MeasurementDto current, List<MeasurementDto> history, List<MeasurementDto> forecast) {
        this.current = current;
        this.history = history;
        this.forecast = forecast;
    }

    public MeasurementDto getCurrent() {
        return current;
    }

    public void setCurrent(MeasurementDto current) {
        this.current = current;
    }

    public List<MeasurementDto> getHistory() {
        return history;
    }

    public void setHistory(List<MeasurementDto> history) {
        this.history = history;
    }

    public List<MeasurementDto> getForecast() {
        return forecast;
    }

    public void setForecast(List<MeasurementDto> forecast) {
        this.forecast = forecast;
    }
}
