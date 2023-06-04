package com.example.seccion7_ejercicio.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.seccion7_ejercicio.Adapters.CityAdapter;
import com.example.seccion7_ejercicio.Models.City;
import com.example.seccion7_ejercicio.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CityActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<City>> {
  private Realm realm;
  private FloatingActionButton fab;

  private RealmResults<City> cities;

  private RecyclerView recycler;
  private CityAdapter adapter;
  private RecyclerView.LayoutManager mLayoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_city);

    realm = Realm.getDefaultInstance();
    cities = realm.where(City.class).findAll();
    cities.addChangeListener(this);

    recycler = (RecyclerView) findViewById(R.id.recyclerView);
    recycler.setHasFixedSize(true);
    recycler.setItemAnimator(new DefaultItemAnimator());
    mLayoutManager = new LinearLayoutManager(this);
    recycler.setLayoutManager(mLayoutManager);


    fab = (FloatingActionButton) findViewById(R.id.FABAddCity);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(CityActivity.this, AddEditCityActivity.class);
        startActivity(intent);
      }
    });

    setHideShowFAB();

    adapter = new CityAdapter(cities, R.layout.recycler_view_item, new CityAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(City city, int position) {
        Intent intent = new Intent(CityActivity.this, AddEditCityActivity.class);
        intent.putExtra("id", city.getId());
        startActivity(intent);
      }
    }, new CityAdapter.OnButtonClickListener() {
      @Override
      public void onButtonClick(City city, int position) {
        showAlertForRemovingCity("Delete city", "Are you sure you want to delete " + city.getName() + "?", position);
      }
    });

    recycler.setAdapter(adapter);
    cities.addChangeListener(this);
  }

  private void setHideShowFAB() {
    recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0)
          fab.hide();
        else if (dy < 0)
          fab.show();
      }
    });
  }

  private void showAlertForRemovingCity(String title, String message, final int position) {

    AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {

              public void onClick(DialogInterface dialog, int whichButton) {
                deleteCity(position);
                Toast.makeText(CityActivity.this, "It has been deleted successfully", Toast.LENGTH_SHORT).show();
              }
            })
            .setNegativeButton("Cancel", null).show();
  }

  private void deleteCity(int position) {
    realm.beginTransaction();
    cities.get(position).deleteFromRealm();
    realm.commitTransaction();
  }

  @Override
  public void onChange(RealmResults<City> cities) {
    adapter.notifyDataSetChanged();
  }
}