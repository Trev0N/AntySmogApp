package pl.bucior.antysmogapp.util;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import pl.bucior.antysmogapp.api.DataDto;
import pl.bucior.antysmogapp.api.MeasurementDto;

public class MeasurementTextParser {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String parseToScreen(MeasurementDto measurementDto){
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        ZonedDateTime zonedDateTimeFrom = ZonedDateTime.parse(measurementDto.getTillDateTime()).withZoneSameLocal(ZoneId.systemDefault());
//
//
//        String dateFrom = zonedDateTimeFrom.format(formatter);
        @SuppressLint("DefaultLocale") String response = String.format("Stężenie PM1: %s µg/m³,\n stężenie PM2.5: %s µg/m³, \n stężenie PM10: %s µg/m³," +
                        " \n ciśnienie: %s hPa, \n wilgotność: %s %%, \n temperatura: %s °C \n \n Ogólna ocena: %s \n %s",
                measurementDto.getValues().stream().filter(m -> m.getName().equals("PM1")).findAny().orElse(new DataDto("PM1",0.0)).getValue(),
                measurementDto.getValues().stream().filter(m -> m.getName().equals("PM25")).findAny().orElse(new DataDto("PM25",0.0)).getValue(),
                measurementDto.getValues().stream().filter(m -> m.getName().equals("PM10")).findAny().orElse(new DataDto("PM10",0.0)).getValue(),
                measurementDto.getValues().stream().filter(m -> m.getName().equals("PRESSURE")).findAny().orElse(new DataDto("PRESSURE",0.0)).getValue(),
                measurementDto.getValues().stream().filter(m -> m.getName().equals("HUMIDITY")).findAny().orElse(new DataDto("HUMIDITY",0.0)).getValue(),
                measurementDto.getValues().stream().filter(m -> m.getName().equals("TEMPERATURE")).findAny().orElse(new DataDto("TEMPERATURE",0.0)).getValue(),
                measurementDto.getIndexes().size()>0?measurementDto.getIndexes().get(0).getDescription():"",
                measurementDto.getIndexes().size()>0?measurementDto.getIndexes().get(0).getAdvice():"");
        return response;
    }
}
