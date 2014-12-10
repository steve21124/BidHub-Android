package com.hsdemo.auction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;


public class LoginActivity extends ActionBarActivity {



	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
    findViewById(R.id.base_tint_darken).setVisibility(View.GONE);
    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    findViewById(R.id.gobutton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doLogin(((TextView) findViewById(R.id.email)).getText().toString(),
                ((TextView) findViewById(R.id.name)).getText().toString());
      }
    });

    if (IdentityManager.getEmail(this).length() > 5 && IdentityManager.getName(this).length() > 3) {
      ((TextView) findViewById(R.id.email)).setText(IdentityManager.getEmail(this));
      ((TextView) findViewById(R.id.name)).setText(IdentityManager.getName(this));
      doLogin(IdentityManager.getEmail(this), IdentityManager.getName(this));
    }
	}

  void doLogin(String email, String name) {

    if (email.length() < 5 || name.length() < 3) {
      Toast.makeText(getApplicationContext(), "Please enter your full name and email address.", Toast.LENGTH_LONG).show();
      return;
    }

    IdentityManager.setEmail(email, this);
    IdentityManager.setName(name, this);

    ParseInstallation.getCurrentInstallation().put("email", email);
    ParseInstallation.getCurrentInstallation().saveInBackground();

    Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);
    startActivity(itemListIntent);
  }
}
