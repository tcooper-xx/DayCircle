package com.example.daycircle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.core.content.ContextCompat;

import org.redout.solunarlib.RiseSetTransit;
import org.redout.solunarlib.Solunar;
import org.redout.solunarlib.SolunarFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DayView extends View {
    Paint dayPaint = new Paint();
    Paint nightPaint = new Paint();
    Paint moonPaint = new Paint();
    Paint textPaint = new Paint();
    int circleSize = 300;
    RectF mOval = new RectF();
    double lat;
    double lon;
    int sunUp;
    int sunDown;
    int moonUp;
    int moonDown;
    int daylightMinutes;
    int moonlightMinutes;
    SimpleDateFormat sdf;
    Calendar cal;

    public DayView(Context context) {
        super(context);
        init();
    }
    private void init() {
        dayPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.colorDaySlice));
        dayPaint.setStrokeWidth(10);
        dayPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        nightPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.colorNightSlice));
        nightPaint.setStrokeWidth(10);
        nightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        moonPaint.setStrokeWidth(10);
        moonPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(60);
        textPaint.setColor(Color.RED);
        sdf = new SimpleDateFormat("YYYY-MM-dd");

    }
    private void getData() {
        SolunarFacade sf = SolunarFacade.getInstance();
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        Calendar calYesterday = (Calendar) cal.clone();
        calYesterday.add(Calendar.DATE, -1);
        Calendar calTomorrow = (Calendar) cal.clone();
        calTomorrow.add(Calendar.DATE, 1);
        Solunar sToday = sf.getForDate(cal, lat, lon );
        Solunar sYesterday = sf.getForDate(calYesterday,lat,lon);
        Solunar sTomorrow = sf.getForDate(calTomorrow,lat,lon);
        RiseSetTransit solRST = sToday.getSolRST();
        RiseSetTransit moonRST = sToday.getMoonRST();
        sunUp = Math.toIntExact(Math.round(solRST.getRise() * 60));
        sunDown = Math.toIntExact(Math.round(solRST.getSet() * 60));
        moonUp = Math.toIntExact(Math.round(moonRST.getRise()*60));
        moonDown = Math.toIntExact(Math.round(moonRST.getSet()*60));
        daylightMinutes = sunDown - sunUp;
        moonlightMinutes = moonDown - moonUp;
        if (moonlightMinutes < 0)
            moonlightMinutes = 60*24 -moonUp + moonDown;
        System.out.println("LAT: " + lat + "LON: " + lon);
        System.out.println("RISE: " + moonRST.getRise() + " SET: " + moonRST.getSet() + "\n " + moonRST.getNoState()+"/"+moonRST.getTransit());
    }
    private int minToDegrees(int mins) {
        int degrees=0;
        degrees = (mins/4) +90;
        if (degrees>360) {
            degrees = degrees -360;
        }
        return degrees;
    }

    private int translateAngle(int a) {
        int result = Math.abs(a - 360);
        return result;
    }

    @Override
    public void onDraw(Canvas canvas) {
        getData();
        int height = canvas.getHeight() / 2;
        int width = canvas.getWidth() / 2;

        RectF mOval = new RectF();

        mOval.set(width - circleSize, height - circleSize, width + circleSize, height + circleSize);
        canvas.drawArc(mOval,0,360,true,nightPaint);
        canvas.drawArc(mOval, minToDegrees(sunUp),daylightMinutes/4, true, dayPaint);
        canvas.drawArc(mOval, minToDegrees(moonUp), moonlightMinutes/4, true, moonPaint);
        int upX = Math.toIntExact(width + Math.round(circleSize * Math.sin(Math.toRadians(translateAngle(sunUp/4)))));
        int upY = Math.toIntExact(height + Math.round(circleSize * Math.cos(Math.toRadians(sunUp/4))));
        Rect rect = new Rect(upX-10, upY -10, upX+10, upY+10);
        canvas.drawText(sdf.format(cal.getTime()),upX,upY, textPaint);
        canvas.drawRect(rect, textPaint );
        System.out.println("X=" + width + " Y=" +height);
        System.out.println("X=" + mOval.centerX() + " Y=" +mOval.centerY());
    }

}

