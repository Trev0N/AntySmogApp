package pl.bucior.antysmogapp.api;


import java.io.Serializable;

public class DataDto implements Serializable {

    private String name;
    private Double value;

    public DataDto() {
    }

    public DataDto(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}

