package pl.bucior.antysmogapp.api;

import java.io.Serializable;

public class StandardDto implements Serializable {
    private String name;
    private String pollutant;
    private Integer limit;
    private Double percent;


    public StandardDto() {
    }

    public StandardDto(String name, String pollutant, Integer limit, Double percent) {
        this.name = name;
        this.pollutant = pollutant;
        this.limit = limit;
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPollutant() {
        return pollutant;
    }

    public void setPollutant(String pollutant) {
        this.pollutant = pollutant;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}
