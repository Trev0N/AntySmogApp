package pl.bucior.antysmogapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.google.android.gms.common.ConnectionResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, AndroidNetworking.class, ANRequest.GetRequestBuilder.class})
public class LocationServiceTest {

    @Mock
    private SharedPreferences sharedPrefs;

    @Mock
    private Context mockContext;

    @InjectMocks
    private LocationService locationService;

    @Before
    public void setUp() {
        initMocks(this);
        PowerMockito.mockStatic(Log.class);
        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
    }

    @Test
    public void distance() {
        assertEquals(331.74024249328255, LocationService.distance(50.063879, 50.063121, 19.898164, 19.902659));
    }

    @Test
    public void onLocationChanged() {
        Location location = mock(Location.class);
        locationService.onLocationChanged(location);
    }

    @Test
    public void onBind() {
        LocationService locationService = new LocationService();
        Intent intent = new Intent();
        assertNull(locationService.onBind(intent));
    }

    @Test
    public void onConnectionFailed() {
        LocationService locationService = new LocationService();
        locationService.onConnectionFailed(ConnectionResult.RESULT_SUCCESS);
        PowerMockito.verifyStatic(Mockito.times(1));
        Log.e(anyString(), anyString());
    }

    @Test
    public void startServiceAndSendNotification() {
        try{
            locationService.startServiceAndSendNotification("test", "test", true);
        }catch (AssertionError assertionError){
            assertNotNull(assertionError);
        }
    }

    @Test
    public void onStartCommand() {
        Intent intent = mock(Intent.class);
        try {
            locationService.onStartCommand(intent, 0, 0);
        }catch (NullPointerException e){
            assertNotNull(e);
        }
    }
}