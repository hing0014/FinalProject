package com.example.finalProject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalProject.R;

public class Navigation extends AppCompatActivity
{
    private final Intent goHome = new Intent(Navigation.this, MainActivity.class);
    private final Intent goToCovid = new Intent(Navigation.this, Covid.class);
    private final Intent goToAudio = new Intent(Navigation.this, Audio.class);
    private final Intent goToRecipes = new Intent(Navigation.this, Recipes.class);
    private final Intent goToTicketMaster = new Intent(Navigation.this, TicketMaster.class);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Spinner navSpinner = findViewById(R.id.navSpinner);
        navSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals(getResources().getString(R.string.HomeNavButton)))
                {
                    startActivity(goHome);
                }
                else if(selectedItem.equals(getResources().getString(R.string.CovidNavButton)))
                {
                    startActivity(goToCovid);
                }
                else if(selectedItem.equals(getResources().getString(R.string.AudioNavButton)))
                {
                    startActivity(goToAudio);
                }
                else if(selectedItem.equals(getResources().getString(R.string.RecipeNavButton)))
                {
                    startActivity(goToRecipes);
                }
                else if(selectedItem.equals(getResources().getString(R.string.TicketMasterNavButton)))
                {
                    startActivity(goToTicketMaster);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

}
