package com.hotel.hotelroomreservation.utils.parsers;

import com.hotel.hotelroomreservation.constants.Addresses;
import com.hotel.hotelroomreservation.constants.Constants;
import com.hotel.hotelroomreservation.model.Reservation;
import com.hotel.hotelroomreservation.model.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    public List<Room> parseRoomsInfo(String roomsInfo) {
        List<Room> rooms = new ArrayList<>();
        Room room;

        try {
            JSONObject json = new JSONObject(roomsInfo);
            JSONArray array = json.getJSONArray(Addresses.ROOMS);

            for (int i = 0; i < array.length(); i++) {
                JSONObject roomObj = array.getJSONObject(i);

                room = new Room(roomObj.getString(Constants.NAME), roomObj.getInt(Constants.NUMBER),
                        roomObj.getInt(Constants.RATING), roomObj.getInt(Constants.VISITORS),
                        roomObj.getInt(Constants.PRICE), roomObj.getString(Constants.URL));
                rooms.add(room);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public List<Reservation> parseBookingsInfo(String bookingsInfo) {
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation;

        try {
            JSONObject json = new JSONObject(bookingsInfo);
            JSONArray array = json.getJSONArray(Addresses.BOOKINGS);

            for (int i = 0; i < array.length(); i++) {
                JSONObject bookingObj = array.getJSONObject(i);

                reservation = new Reservation(bookingObj.getString(Constants.ARRIVAL), bookingObj.getString(Constants.DEPARTURE),
                        bookingObj.getString(Constants.EMAIL), bookingObj.getInt(Constants.ID), bookingObj.getString(Constants.NAME),
                        bookingObj.getString(Constants.NUMBER), bookingObj.getString(Constants.SURNAME));
                reservations.add(reservation);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public List<String> parsePhotoUrls(String photoUrlsJson) {
        List<String> urls = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(photoUrlsJson);
            JSONArray array = json.getJSONArray(Addresses.PHOTOS);

            for (int i = 0; i < array.length(); i++) {
                urls.add(array.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return urls;
    }
}
