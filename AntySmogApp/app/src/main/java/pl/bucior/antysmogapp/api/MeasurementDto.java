package pl.bucior.antysmogapp.api;

import java.util.List;

public class MeasurementDto {
    private String fromDateTime;
    private String tillDateTime;
    private List<DataDto> values;
    private List<IndexDto> indexes;
    private List<StandardDto> standards;

    @Override
    public String toString() {
        return "MeasurementDto{" +
                "fromDateTime='" + fromDateTime + '\'' +
                ", tillDateTime='" + tillDateTime + '\'' +
                ", values=" + values +
                ", indexes=" + indexes +
                ", standards=" + standards +
                '}';
    }

    public MeasurementDto() {
    }

    public MeasurementDto(String fromDateTime, String tillDateTime, List<DataDto> values, List<IndexDto> indexes, List<StandardDto> standards) {
        this.fromDateTime = fromDateTime;
        this.tillDateTime = tillDateTime;
        this.values = values;
        this.indexes = indexes;
        this.standards = standards;
    }

    public String getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(String fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public String getTillDateTime() {
        return tillDateTime;
    }

    public void setTillDateTime(String tillDateTime) {
        this.tillDateTime = tillDateTime;
    }

    public List<DataDto> getValues() {
        return values;
    }

    public void setValues(List<DataDto> values) {
        this.values = values;
    }

    public List<IndexDto> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<IndexDto> indexes) {
        this.indexes = indexes;
    }

    public List<StandardDto> getStandards() {
        return standards;
    }

    public void setStandards(List<StandardDto> standards) {
        this.standards = standards;
    }
}
