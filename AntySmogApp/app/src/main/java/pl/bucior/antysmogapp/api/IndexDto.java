package pl.bucior.antysmogapp.api;

import java.io.Serializable;

public class IndexDto implements Serializable {
    private String name;
    private Double value;
    private String level;
    private String description;
    private String advice;
    private String color;

    public IndexDto() {
    }

    public IndexDto(String name, Double value, String level, String description, String advice, String color) {
        this.name = name;
        this.value = value;
        this.level = level;
        this.description = description;
        this.advice = advice;
        this.color = color;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
