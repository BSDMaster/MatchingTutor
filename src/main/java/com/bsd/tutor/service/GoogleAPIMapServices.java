package com.bsd.tutor.service;

import com.bsd.tutor.dao.TravelTimeDao;
import com.bsd.tutor.model.TravelTime;
import com.bsd.tutor.utils.Constants;
import com.bsd.tutor.utils.GoogleMapUtils;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TransitRoutingPreference;
import com.google.maps.model.TravelMode;
import org.joda.time.DateTime;

/**
 * Created by kewalins on 2/17/2018 AD.
 */
public class GoogleAPIMapServices {

    TravelTimeDao travelTimeDao = new TravelTimeDao();

    public DistanceMatrix estimateRouteTimeForDriving(LatLng departure, LatLng arrivals) {
        try {
            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(GoogleMapUtils.getContext());

            DistanceMatrix trix = req.origins(departure)
                    .destinations(arrivals)
                    .mode(TravelMode.DRIVING)
                    .language("fr-FR")
                    .await();
            return trix;

        } catch (ApiException e) {

            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public DistanceMatrix estimateRouteTimeForTransit(LatLng departure, LatLng arrivals) {
        try {
            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(GoogleMapUtils.getContext());

            DistanceMatrix trix = req.origins(departure)
                    .destinations(arrivals)
                    .mode(TravelMode.TRANSIT)
                    .transitRoutingPreference(TransitRoutingPreference.FEWER_TRANSFERS)
                    .language("th-TH")
                    .await();
            return trix;

        } catch (ApiException e) {

            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Double calculateTravelTime(Double startLat, Double startLong, Double endLat, Double endLong, String isByDrive, String isByTaxi, String isByTrain, String isByBus, GoogleAPIMapServices googleMapService) {
        TravelTime travelTime = null;
        if (isByDrive.equals(Constants.FLAG_YES) || isByTaxi.equals(Constants.FLAG_YES)) {
            travelTime = travelTimeDao.findByLocation(startLat, startLong, endLat, endLong, Constants.TRAVELTYPE_DRIVE);
            if (travelTime != null) {
                return travelTime.getTmeTime();
            }
        } else {
            travelTime = travelTimeDao.findByLocation(startLat, startLong, endLat, endLong, Constants.TRAVELTYPE_TRANSIT);
            if (travelTime != null) {
                return travelTime.getTmeTime();
            }
        }

        DistanceMatrix matrix = null;
        String type = "";
		if (isByDrive.equals(Constants.FLAG_YES) || isByTaxi.equals(Constants.FLAG_YES)) {
            type = Constants.TRAVELTYPE_DRIVE;
			matrix = googleMapService.estimateRouteTimeForDriving(new LatLng(startLat, startLong), new LatLng(endLat, endLong));
			System.out.println("Driving Duration : " + matrix.rows[0].elements[0].duration.inSeconds + " s");

		} else {
            type = Constants.TRAVELTYPE_TRANSIT;
			matrix = googleMapService.estimateRouteTimeForTransit(new LatLng(startLat, startLong), new LatLng(endLat, endLong));
			System.out.println("Transit Duration : " + matrix.rows[0].elements[0].duration.inSeconds + " s");

		}
		try {
			Thread.sleep(Constants.GOOGLEMAP_API_DELAYTIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Double seconds = Double.valueOf(matrix.rows[0].elements[0].duration.inSeconds);
        Double secondsRoundUp = seconds / (Constants.ROUNDUP_TRAVELTIME * 60);
        if (seconds >= secondsRoundUp) {
            secondsRoundUp +=  (Constants.ROUNDUP_TRAVELTIME * 60);
        }

		travelTime = new TravelTime(startLat, startLong, endLat, endLong, type, secondsRoundUp);
        travelTime = travelTimeDao.create(travelTime);

		return travelTime.getTmeTime();
        //return 0D;
    }


}
