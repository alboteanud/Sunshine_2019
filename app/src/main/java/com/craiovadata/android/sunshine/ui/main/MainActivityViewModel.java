package com.craiovadata.android.sunshine.ui.main;

import com.craiovadata.android.sunshine.data.RepositoryWeather;
import com.craiovadata.android.sunshine.data.database.ListWeatherEntry;
import com.craiovadata.android.sunshine.data.database.WeatherEntry;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private final LiveData<List<ListWeatherEntry>> nextHoursWeather;
    private final LiveData<List<ListWeatherEntry>> midDayWeather ;
    private final LiveData<List<WeatherEntry>> allForecast;
    private final LiveData<List<WeatherEntry>> currentWeather;


    MainActivityViewModel(RepositoryWeather repository) {
        nextHoursWeather = repository.getNextHoursWeather();
        midDayWeather = repository.getMidDayWeatherEntries();
        allForecast = repository.getAllWeatherEntries();
        currentWeather = repository.getCurrentWeather();
    }



    public LiveData<List<ListWeatherEntry>> getNextHoursWeather() {
        return nextHoursWeather;
    }

    public LiveData<List<ListWeatherEntry>> getMidDayWeather() {


//        Transformations.map(midDayWeather.getValue()){
//
//        }

        return midDayWeather;
    }


//    @MainThread
//    fun getGroupLiveData(groupId: String): LiveData<GroupDisplayOrException> {
//
//        var liveData = groupLiveData
//        if (liveData == null) {
//            val ld = repository.getGroupLiveData(groupId)
//            liveData = Transformations.map(ld) {
//                GroupDisplayOrException(it.data?.toGroupDisplay(), it.exception)
//            }
//            groupLiveData = liveData
//        }
//        return liveData!!
//    }

    public LiveData<List<WeatherEntry>> getAllForecast() {
        return allForecast;
    }

    public LiveData<List<WeatherEntry>> getCurrentWeather() {
        return currentWeather;
    }

}
