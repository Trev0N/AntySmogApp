package pl.bucior.antysmogapp.ui.history;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import pl.bucior.antysmogapp.R;
import pl.bucior.antysmogapp.api.DataDto;
import pl.bucior.antysmogapp.api.MeasurementDto;
import pl.bucior.antysmogapp.api.StandardDto;
import pl.bucior.antysmogapp.ui.home.HomeViewModel;

public class HistoryFragment extends Fragment {

    private HomeViewModel historyViewModel;
    private RecyclerView mRecyclerView;
    private ListAdapter mListadapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        historyViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        historyViewModel.getMutableLiveData().observe(getViewLifecycleOwner(),
                measurementDtos -> {
                    mListadapter = new ListAdapter(measurementDtos);
                    mRecyclerView.setAdapter(mListadapter);
                });

        return root;
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private List<MeasurementDto> dataList;

        ListAdapter(List<MeasurementDto> data) {
            this.dataList = data;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textToDate;
            TextView textToPM1;
            LinearLayout linearDate;
            TextView textPM1;
            TextView textToPM25;
            TextView textPM25;
            TextView textPM10;
            TextView textToPM10;
            TextView textPressure;
            TextView textToPressure;
            TextView textToHumidity;
            TextView textHumidity;
            TextView textTemp;
            TextView textToTemp;


            ViewHolder(View itemView) {
                super(itemView);
                this.textToDate = itemView.findViewById(R.id.textToDate);
                this.textToPM1 = itemView.findViewById(R.id.textToPM1);
                this.textPM1 = itemView.findViewById(R.id.textPM1);
                this.textHumidity = itemView.findViewById(R.id.textHumidity);
                this.textToHumidity = itemView.findViewById(R.id.textToHumidity);
                this.textToPM25 = itemView.findViewById(R.id.textToPM25);
                this.textPM25 = itemView.findViewById(R.id.textPM25);
                this.textToPM10 = itemView.findViewById(R.id.textToPM10);
                this.textPM10 = itemView.findViewById(R.id.textPM10);
                this.textPressure = itemView.findViewById(R.id.textPressure);
                this.textToPressure = itemView.findViewById(R.id.textToPressure);
                this.textToTemp = itemView.findViewById(R.id.textToTemp);
                this.textTemp = itemView.findViewById(R.id.textTemp);
                this.linearDate = itemView.findViewById(R.id.linearDate);

            }
        }

        @NonNull
        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_history, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            ZonedDateTime zonedDateTimeFrom = ZonedDateTime.parse(dataList.get(position).getTillDateTime()).withZoneSameInstant(ZoneId.of("Poland"));
            String dateFrom = zonedDateTimeFrom.format(formatter);

            holder.textPM1.setText(requireContext().getText(R.string.textPM1));
            holder.textTemp.setText(requireContext().getText(R.string.textTemp));
            holder.textHumidity.setText(requireContext().getText(R.string.textHumidity));
            holder.textPressure.setText(requireContext().getText(R.string.textPressure));
            holder.textPM10.setText(requireContext().getText(R.string.textPM10));
            holder.textPM25.setText(requireContext().getText(R.string.textPM25));

            holder.textToDate.setText(dateFrom);
            holder.textToPM1.setText(dataList.get(position).getValues().stream().filter(v -> v.getName().equals("PM1"))
                    .findAny().orElse(new DataDto("PM1", 0.0)).getValue().toString() + " µg/m³");
            holder.textToPM25.setText(dataList.get(position).getValues().stream().filter(v -> v.getName().equals("PM25"))
                    .findAny().orElse(new DataDto("PM25", 0.0)).getValue().toString() + " µg/m³" + " | " +
                    dataList.get(position).getStandards().stream().filter(s -> s.getPollutant().equals("PM25")).findAny().orElse(new StandardDto("WHO", "PM25", 25, 0.0)).getPercent() + "%");
            holder.textToPM10.setText(dataList.get(position).getValues().stream().filter(v -> v.getName().equals("PM10"))
                    .findAny().orElse(new DataDto("PM10", 0.0)).getValue().toString() + " µg/m³" + " | " +
                    dataList.get(position).getStandards().stream().filter(s -> s.getPollutant().equals("PM10")).findAny().orElse(new StandardDto("WHO", "PM10", 50, 0.0)).getPercent() + "%");
            holder.textToTemp.setText(dataList.get(position).getValues().stream().filter(v -> v.getName().equals("TEMPERATURE"))
                    .findAny().orElse(new DataDto("TEMPERATURE", 0.0)).getValue().toString() + " °C");
            holder.textToHumidity.setText(dataList.get(position).getValues().stream().filter(v -> v.getName().equals("HUMIDITY"))
                    .findAny().orElse(new DataDto("HUMIDITY", 0.0)).getValue().toString() + "%");
            holder.textToPressure.setText(dataList.get(position).getValues().stream().filter(v -> v.getName().equals("PRESSURE"))
                    .findAny().orElse(new DataDto("PRESSURE", 0.0)).getValue().toString() + " hPa");
            holder.linearDate.setBackgroundColor(Color.parseColor(dataList.get(position).getIndexes().size()>0?
                    dataList.get(position).getIndexes().get(0).getColor():"#FFFFFF"));
            holder.linearDate.setAlpha(0.70f);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}