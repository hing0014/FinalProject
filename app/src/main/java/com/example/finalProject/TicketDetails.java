package com.example.finalProject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
/**
 * Shows the values of the selected event.
 *  <p>
 * Course Name: CST8288_010
 * Class name: TicketDetails
 * Date: November 19, 2020
 *
 * @version 1.0
 * @author Chris HIng
 */
public class TicketDetails extends AppCompatActivity
{
    public final static String ITEM_CITY = "CITY";
    public final static String ITEM_NAME = "EVENT NAME";
    public final static String ITEM_START_DATE = "START DATE";
    public final static String ITEM_MIN_PRICE = "MIN PRICE";
    public final static String ITEM_MAX_PRICE = "MAX PRICE";
    public final static String ITEM_URL = "URL";
    public final static String ITEM_IMAGE_STRING = "IMAGE";
    Bitmap image;
    String city;
    String eventName;
    String startDate;
    double ticketPriceMin;
    double ticketPriceMax;
    String eventUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_details);

        Bundle dataToSet = getIntent().getExtras();

        ImageView imagev = findViewById(R.id.eventImage);
        TextView cityv = findViewById(R.id.city);
        TextView namev = findViewById(R.id.name);
        TextView startDatev = findViewById(R.id.startDate);
        TextView ticketPriceMinv = findViewById(R.id.ticketPriceMin);
        TextView ticketPriceMaxv = findViewById(R.id.ticketPriceMax);
        TextView urlv = findViewById(R.id.url);

        image = TicketMaster.decodeBase64(dataToSet.getString(ITEM_IMAGE_STRING));
        city = dataToSet.getString(ITEM_CITY);
        eventName = dataToSet.getString(ITEM_NAME);
        startDate = dataToSet.getString(ITEM_START_DATE);
        ticketPriceMin = dataToSet.getDouble(ITEM_MIN_PRICE);
        ticketPriceMax = dataToSet.getDouble(ITEM_MAX_PRICE);
        eventUrl = dataToSet.getString(ITEM_URL);

        imagev.setImageBitmap(image);
        cityv.setText(city);
        namev.setText(eventName);
        startDatev.setText(startDate);
        ticketPriceMinv.setText(String.valueOf(ticketPriceMin));
        ticketPriceMaxv.setText(String.valueOf(ticketPriceMax));

        urlv.setText(eventUrl);
        urlv.setMovementMethod(LinkMovementMethod.getInstance());

        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener( clk -> onBackPressed());
    }
}
