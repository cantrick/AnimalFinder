package com.example.animalfinder;

/*
 * TODO:
 * Make animal class with constructor to send
 * information to, to be used on previous animal activity.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class AnimalInfo extends Activity {

	public String name;
	public ProgressDialog dialog;
	TextView tv1;
	TextView tv6;
	TextView tv7;
	TextView tv2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animal_info);
		Intent intent = getIntent();
		String photoPath = intent.getStringExtra("com.cantrick.animalfinder.filePath");
		ImageView mImageView = (ImageView) findViewById(R.id.animThumb);
		tv1 = (TextView) findViewById(R.id.textView1);
		tv1.setGravity(Gravity.CENTER_HORIZONTAL);
		tv6 = (TextView) findViewById(R.id.textView6);
		tv7 = (TextView) findViewById(R.id.textView7);
		tv2 = (TextView) findViewById(R.id.textView2);
		//set tv2 to be scrollable
		tv2.setMovementMethod(new ScrollingMovementMethod());
		dialog = new ProgressDialog(AnimalInfo.this);
		dialog.setCancelable(false);
		
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mImageView.setImageBitmap(bitmap);
		Log.d("photopath", photoPath);
		new ImageRequest().execute(photoPath);

	}

	//Request image and get token to identify
	private class ImageRequest extends AsyncTask<String, Void, String> {
		String photoPath;

		protected void onPreExecute() {
			dialog.setMessage("Fetching animal information");
			dialog.show();

			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... strings) {
			photoPath = strings[0];
			//split 'file:' from filename to get usable photo path
			String[] photoPaths = photoPath.split("file:");
			photoPath = photoPaths[1];
			Log.d("photoPath", "IN DO BACKGROUND: "+ photoPath);

			try {
				File photoFile = new File(photoPath);

				// These code snippets use an open-source library. http://unirest.io/java
				HttpResponse<JsonNode> response = Unirest.post("https://camfind.p.mashape.com/image_requests")
						.header("X-Mashape-Key", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
						.field("focus[x]", "480")
						.field("focus[y]", "640")
						.field("image_request[altitude]", "27.912109375")
						.field("image_request[image]", photoFile)
						.field("image_request[language]", "en")
						.field("image_request[latitude]", "35.8714220766008")
						.field("image_request[locale]", "en_US")
						.field("image_request[longitude]", "14.3583203002251")
						.asJson();

				JSONObject obj = response.getBody().getObject();
				String token = obj.getString("token");
				String body = response.getBody().toString();
				Log.d("token", token);
				Log.d("Body Result", body);
				return token;
			} catch (UnirestException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String token) {
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new ImageResponse().execute(token);
		}
	}

	//Get response as to what data the token points at
	private class ImageResponse extends AsyncTask<String, Void, String> {
		String token;
		
		@Override
		protected String doInBackground(String... strings) {
			token = strings[0];
			Log.d("token Result", token);
			String status = "";
			JSONObject body = null;
			String requestString = "https://camfind.p.mashape.com/image_responses/" + token;
			while(!status.equals("completed")){
				Log.d("STATUS", status);
				// These code snippets use an open-source library. http://unirest.io/java
				try {
					Thread.sleep(500);
					HttpResponse<JsonNode> response = Unirest.get(requestString)
							.header("X-Mashape-Key", "6ISar8dyb5mshRmqD43wWI4w5COBp1ixIu6jsndfppaFvUxpR5")
							.header("Accept", "application/json")
							.asJson();

					//Return the name of the item that the picture was taken of
					body = response.getBody().getObject();
					status = body.getString("status");
				} catch (UnirestException | JSONException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String name;
			try {
				name = body.getString("name");
				Log.d("Body Result", body.toString());
				return name;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}


		}

		@Override
		protected void onPostExecute(String name) {
			Log.d("NAME", name);
			String[] words = name.split(" ");
			//Log.d("WORDS", words[0] + " :" + words[1]);
			new animalResponse().execute(words);
			return;
		}
	}

	public class Wrapper
	{
		public String name;
	    public String website;
	    public String kingdom;
	    public String phylum;
	    public String anClass;
	    public String order;
	    public String family;
	    public String genus;
	    public String species;
	    public String paragraph;
	    
	}


	private class animalResponse extends AsyncTask<String[], Void, Wrapper> {
		String[] names;
		String aName;
		String animName;
		
		protected Wrapper doInBackground(String[]... strings) {
			Boolean bool = null;
			ArrayList<String> animData = new ArrayList<String>();
			Wrapper w = new Wrapper();
			names = strings[0];
			Log.d("ANIMAL", "In AnimalResponse");
			Log.d("ANIMAL", "LENGTH: " + names.length);
			for(int i = 0; i < names.length; i++) {
				aName = names[i];
				String website = "http://en.wikipedia.org/wiki/" + aName;
				Response res;
				try {
					res = Jsoup.connect(website).execute();
					String html = res.body();
					Document doc = Jsoup.parseBodyFragment(html);
					Element body = doc.body();
					Elements tables = body.getElementsByTag("table");

					//Check if there is an infoBox in the wiki page
					//TODO move to separate class
					for (Element table : tables) {
						if (table.className().contains("infobox")==true) {
							Log.d("HASINFOBOX", "TRUE");
							bool = true;
							break;
						} else {
							Log.d("HTML", "NO HTML");
							bool = false;
						}
					}

					//Check if the infobox contains a kingdom
					//TODO move to separate class
					Elements tdElem = doc.select("td");
					for(Element e : tdElem) {
						Log.d("Element", e.text());
						if(e.text().contains("Kingdom:")) {
							Log.d("NAMEMEM", aName);
							animName = aName;
							aName = Character.toUpperCase(aName.charAt(0)) + aName.substring(1);
							w.name = aName;
							animData.add(aName);
							bool = true;
							break;
						} else {
							continue;
						}
					}

					//Get Items from the wiki page
					//TODO move to separate class
					if(bool == true) {
						//do 2 tests for every element, to get text if it's a link or not.
						String tdKingdom = "Kingdom:   " + doc.select("span[class=kingdom]").select("a[href]").text();
						if(tdKingdom.equals("Kingdom:   ")) { 
							tdKingdom = "Kingdom:   " + doc.select("span[class=kingdom]").text();
						} else if(doc.select("a[href]").text().equals("Animalia")){
							tdKingdom = "Kingdom:   " + doc.select("a[href]").text();
						}

						String tdPhylum = "Phylum:   " + doc.select("span[class=phylum]").select("a[href]").text();
						if(tdPhylum.equals("Phylum:   ")) { 
							tdPhylum = "Phylum:   " + doc.select("span[class=phylum]").text();
						}

						String tdClass = "Class:   " + doc.select("span[class=class]").select("a[href]").text();
						if(tdClass.equals("Class:   ")) { 
							tdClass = "Class:   " + doc.select("span[class=class]").text();
						}

						String tdOrder = "Order:   " + doc.select("span[class=order]").select("a[href]").text();
						if(tdOrder.equals("Order:   ")) { 
							tdOrder = "Order:   " + doc.select("span[class=order]").text();
						}


						String tdFamily = "Family:   " + doc.select("span[class=family]").select("a[href]").text();
						if(tdFamily.equals("Family:   ")) { 
							tdFamily = "Family:   " + doc.select("span[class=family]").text();
						}

						String tdGenus = "Genus:   " + doc.select("span[class=genus]").select("a[href]").text();
						if(tdGenus.equals("Genus:   ")) { 
							tdGenus = "Genus:   " + doc.select("span[class=genus]").text();
						}

						String tdSpecies = "Species:   " + doc.select("span[class=species]").select("a[href]").text();
						if(tdSpecies.equals("Species:   ")) { 
							tdSpecies = "Species:   " + doc.select("span[class=species]").text();
						}

						//Get first sentence of wiki page
						Elements paragraphs = doc.select("p");
						String firstParagraph = paragraphs.get(1).text();
						
						
//						animData.add(tdKingdom);
//						animData.add(tdPhylum);
//						animData.add(tdClass);
//						animData.add(tdOrder);
//						animData.add(tdFamily);
//						animData.add(tdGenus);
//						animData.add(tdSpecies);
						w.kingdom = tdKingdom;
						w.phylum = tdPhylum;
						w.anClass = tdClass;
						w.order = tdOrder;
						w.family = tdFamily;
						w.genus = tdGenus;
						w.species = tdSpecies;
						w.website = website;
						w.paragraph = firstParagraph;
						Log.d("Stringdom?", tdKingdom);
						Log.d("Stringdom?", tdPhylum);
						Log.d("Stringdom?", tdClass);
						Log.d("Stringdom?", tdOrder);
						Log.d("Stringdom?", tdFamily);
						Log.d("Stringdom?", tdGenus);
						Log.d("Stringdom?", tdSpecies);
						//break;
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(animName != null) {
				Log.d("ANIMNAME", animName);
				return w;
			} else {
				return w;
			}
		}

		@Override
		protected void onPostExecute(Wrapper w) {
			tv1.setText("  " + w.name);
			tv6.append("\n" + w.kingdom);
			tv6.append("\n" + w.phylum);
			tv6.append("\n" + w.anClass);
			tv6.append("\n" + w.order);
			tv6.append("\n" + w.family);
			tv6.append("\n" + w.genus);
			tv6.append("\n" + w.species);
			tv7.setText("\n" + w.website);
			tv2.setText(w.paragraph);
			
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
//			int count = 0;
//			Log.d("onpost", data.get(0));
//			Log.d("onpost", "COUNT: " + data.size());
//
//			for(int i = 0; i < data.size(); i++){
//				//get the first element, put it in textView1, make sure it's not a blank element
//				while(count == 0) {
//					while(data.get(i).equals("") || data.get(i).equals(" ") || data.get(i).equals("animal")|| data.get(i).equals("Kingdom:   ")
//							|| data.get(i).equals("Phylum:   ")|| data.get(i).equals("Class:   ")|| data.get(i).equals("Order:   ")|| data.get(i).equals("Family:   ")
//							|| data.get(i).equals("Genus:   ")|| data.get(i).equals("Species:   ")) {
//						i += 1;
//					}
//					tv1.append(" " + data.get(i));
//					count += 1;
//					i += 1;
//				}
//
//				//make sure all other elements aren't just blank, put into textView6
//				while(data.get(i).equals("") || data.get(i).equals(" ") || data.get(i).equals("animal")|| data.get(i).equals("Kingdom:   ")
//						|| data.get(i).equals("Phylum:   ")|| data.get(i).equals("Class:   ")|| data.get(i).equals("Order:   ")|| data.get(i).equals("Family:   ")
//						|| data.get(i).equals("Genus:   ")|| data.get(i).equals("Species:   ")) {
//					if(i+1 > data.size()-1) {
//						i = i;
//					} else {
//						i += 1;
//					}
//					if(i == data.size()-1) {
//						break;
//					}
//				}
//
//				tv6.append("\n" + data.get(i));
//			}
			return;
		}
	}
}
