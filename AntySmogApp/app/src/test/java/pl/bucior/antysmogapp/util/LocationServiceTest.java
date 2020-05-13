package pl.bucior.antysmogapp.util;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


public class LocationServiceTest {
    @Test
    public void distance() {
        assertEquals(331.74024249328255,LocationService.distance(50.063879,50.063121,19.898164,19.902659));
    }

}