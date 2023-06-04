package com.example.seccion7_ejercicio.Application;

import android.app.Application;

import com.example.seccion7_ejercicio.Models.City;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApp extends Application {
  public static AtomicInteger CityID = new AtomicInteger();

  @Override
  public void onCreate() {
    super.onCreate();
    setUpRealmConfig();

    Realm realm = Realm.getDefaultInstance();
    CityID = getIdByTable(realm, City.class);
    realm.close();
  }

  private void setUpRealmConfig() {
    Realm.init(this);
    RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
    Realm.setDefaultConfiguration(config);
  }

  private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
    RealmResults<T> results = realm.where(anyClass).findAll();
    return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
  }
}
