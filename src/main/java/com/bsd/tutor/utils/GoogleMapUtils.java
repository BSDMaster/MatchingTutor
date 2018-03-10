package com.bsd.tutor.utils;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApiRequest;

import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;

/**
 * Created by kewalins on 2/17/2018 AD.
 */
public class GoogleMapUtils {
    private static final String API_KEY = "AIzaSyBQp1hM_ZqwiPp2-iBniE5-TtPp0KyMY18";
    private static GeoApiContext context;

    static {
        context = new GeoApiContext.Builder().apiKey(API_KEY)
                .build();
    }

    public static GeoApiContext getContext() {
        return context;
    }

    public static Double distFrom(Double lat1, Double lng1, Double lat2, Double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double dist = (Double) (earthRadius * c);

        return dist;
    }

}
