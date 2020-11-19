package com.example.finalProject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.androidlabs.R;

public class AudioActivity extends AppCompatActivity {

    private Album album;

    private Fragment current;
    private AudioSearchFragment searchFragment;
    private AudioListFragment listFragment;
    private AudioItemFragment itemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        searchFragment = new AudioSearchFragment();
        listFragment = new AudioListFragment();
        itemFragment = new AudioItemFragment();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    /**
     *
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {

        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.frAudio, fragment).commit();
        }

        getSupportFragmentManager().beginTransaction().hide(current).show(fragment).commit();

        current = fragment;
    }

    public void openSearch() {
        changeFragment(searchFragment);
    }

    public void openList() {
        changeFragment(listFragment);
    }

    public void openItem() {
        changeFragment(itemFragment);
    }

    public void back() {
        if (current == searchFragment) ;
        else if (current == listFragment) openSearch();
        else if (current == itemFragment) openList();
    }

    /**
     *
     * @return
     */
    public Album getAlbum() {
        return album;
    }

    /**
     *
     * @param album
     */
    public void setAlbum(Album album) {
        this.album = album;
    }
}