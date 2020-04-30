package pl.bucior.antysmogapp.ui.history;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pl.bucior.antysmogapp.api.MeasurementDto;

public class HistoryViewModel extends ViewModel {

    private MutableLiveData<List<MeasurementDto>> mMutableLiveData = new MutableLiveData<>();

    public HistoryViewModel() {
    }

    public MutableLiveData<List<MeasurementDto>> getmMutableLiveData() {
        if(mMutableLiveData==null){
            mMutableLiveData=new MutableLiveData<>();
        }
        return mMutableLiveData;
    }

    public void setmMutableLiveData(List<MeasurementDto> mMutableLiveData) {
        this.mMutableLiveData.setValue(mMutableLiveData);
    }
}