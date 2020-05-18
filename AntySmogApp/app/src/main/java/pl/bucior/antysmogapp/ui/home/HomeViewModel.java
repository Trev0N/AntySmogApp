package pl.bucior.antysmogapp.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import pl.bucior.antysmogapp.api.MeasurementDto;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<MeasurementDto>> mutableLiveData;

    public HomeViewModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<MeasurementDto>> getMutableLiveData() {
        return mutableLiveData;
    }

    void setMutableLiveData(List<MeasurementDto> list) {
        Collections.reverse(list);
        this.mutableLiveData.setValue(list);
    }
}