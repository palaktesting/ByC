package com.nap.bycab.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nap.bycab.models.Driver;
import com.nap.bycab.models.NotificationList;
import com.nap.bycab.models.Order;
import com.nap.bycab.models.RideResponse;
import com.nap.bycab.models.Ticket;

public class PrefUtils {

    public static void setCurrentDriver(Driver currentDriver, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "Driver_prefs", 0);
        complexPreferences.putObject("current_Driver_value", currentDriver);
        complexPreferences.commit();
    }

    public static void clearCurrentDriver( Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "Driver_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static Driver getCurrentDriver(Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "Driver_prefs", 0);
        Driver currentDriver = complexPreferences.getObject("current_Driver_value", Driver.class);
        return currentDriver;
    }

//    public static void setForm(GetAssignedForm1 currentDriver, Context ctx){
//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "form1_prefs", 0);
//        complexPreferences.putObject("from1_value", currentDriver);
//        complexPreferences.commit();
//    }
//
//
//    public static GetAssignedForm1 getForm(Context ctx){
//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "form1_prefs", 0);
//        GetAssignedForm1 currentDriver = complexPreferences.getObject("from1_value", GetAssignedForm1.class);
//        return currentDriver;
//    }

    public static void setCurrentRideList(RideResponse currentDriver, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "current_ride_prefs", 0);
        complexPreferences.putObject("current_ride_value", currentDriver);
        complexPreferences.commit();
    }



    public static RideResponse getCurrentRideList(Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "current_ride_prefs", 0);
        RideResponse currentDriver = complexPreferences.getObject("current_ride_value", RideResponse.class);
        return currentDriver;
    }



    public static void setNotificationId(String login, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("notification", login);
        editor.apply();
    }

    public static String getNotificationId(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString("notification", "");

    }

    public static void setUpcomingNotificationIdList(NotificationList notificationList, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "upcoming_notificationList_prefs", 0);
        complexPreferences.putObject("UpcomingNotificationIdList", notificationList);
        complexPreferences.commit();
    }

    public static void clearUpcomingNotificationIdList( Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "upcoming_notificationList_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static NotificationList getUpcomingNotificationIdList(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "upcoming_notificationList_prefs", 0);
        NotificationList notificationList = complexPreferences.getObject("UpcomingNotificationIdList", NotificationList.class);
        return notificationList;
    }

    public static void setCurrentNotificationIdList(NotificationList notificationList, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "current_notificationList_prefs", 0);
        complexPreferences.putObject("CurrentNotificationIdList", notificationList);
        complexPreferences.commit();
    }

    public static void clearCurrentNotificationIdList( Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "current_notificationList_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static NotificationList getCurrentNotificationIdList(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "current_notificationList_prefs", 0);
        NotificationList notificationList = complexPreferences.getObject("CurrentNotificationIdList", NotificationList.class);
        return notificationList;
    }


    public static void setServiceRunningInBackground(boolean serviceRunningInBackground, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("serviceRunningInBackground", serviceRunningInBackground);
        editor.apply();
    }

    public static boolean isServiceRunningInBackground(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean("serviceRunningInBackground", false);

    }





    public static void setTicketInfo(Ticket notificationList, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "ticket_pref", 0);
        complexPreferences.putObject("ticket_value_pref", notificationList);
        complexPreferences.commit();
    }

    public static void clearTicketInfo( Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "ticket_pref", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static Ticket getTicketInfo(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "ticket_pref", 0);
        Ticket notificationList = complexPreferences.getObject("ticket_value_pref", Ticket.class);
        return notificationList;
    }

    public static void setRunningRide(Order order, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "order_pref", 0);
        complexPreferences.putObject("order_value_pref", order);
        complexPreferences.commit();
    }

    public static void clearRunningRide(Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "order_pref", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static Order getRunningRide(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "order_pref", 0);
        Order order = complexPreferences.getObject("order_value_pref", Order.class);
        return order;
    }
}
