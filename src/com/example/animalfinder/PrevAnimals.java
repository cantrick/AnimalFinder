package com.example.animalfinder;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class PrevAnimals extends Activity {

	//No time to finish this. Finals... :(
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prev_animals);
		
		Intent intent = getIntent();
		String photoPath = intent.getStringExtra("com.cantrick.animalfinder.filePath");
		ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
		
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mImageView.setImageBitmap(bitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prev_animals, menu);
		return true;
	}
	
	public void picClick(View v) {
		Intent inten = getIntent();
		String photoPath = inten.getStringExtra("com.cantrick.animalfinder.filePath");
		
		Intent intent = new Intent(this, AnimalInfo.class);
		intent.putExtra("com.cantrick.animalfinder.filePath", photoPath);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
