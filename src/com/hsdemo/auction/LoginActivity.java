package com.hubspot.disruption.auction;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;


public class LoginActivity extends ActionBarActivity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
    findViewById(R.id.base_tint_darken).setVisibility(View.GONE);

    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    findViewById(R.id.gobutton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        IdentityManager.email =
                ((TextView) findViewById(R.id.email)).getText().toString();

        Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(itemListIntent);
      }
    });


	}
}
