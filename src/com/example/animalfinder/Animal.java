package com.example.animalfinder;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class Animal {

	String[] nameArray;
	String name;
	String animKingdom;
	Boolean hasAnimal;



	public Animal(String[] names) throws IOException {
		Log.d("ANIMAL", "In animal constructor");
		this.nameArray = names;
	}

	public String getAnimalName() {
		return this.name;
	}

	public void setAnimalName(String aName) {
		Log.d("setName", aName);
		this.name = aName;
	}

}
