package com.example.eventwiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
/**
 * Activity for the user dashboard, providing options to create events, browse events, and scan QR codes.
 *
 * @author Junkai,yesith
 */
public class DashboardActivity extends AppCompatActivity {

    // Declare UI components
    private Button createEventButton;
    private Button hostedEventsButton;
    private Button browseEventsButton;
    private Button profileButton;

    private FloatingActionButton scanQRButton;
    private TextView tvwelcomeText;

    private ImageView savedPic;

    ImageButton backButton;
    SharedPreferences sp;

    private String currentID;




    /**
     * Called when the activity is first created. Responsible for initializing the UI components,
     * setting up click listeners, and handling various actions within the dashboard.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Initialize ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("User Dashboard");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        retrieveAnonymousUserId();

        // Initialize UI components
        createEventButton = findViewById(R.id.createEvent);
        hostedEventsButton = findViewById(R.id.myHostedEvents);
        browseEventsButton = findViewById(R.id.browseEvents);
        profileButton = findViewById(R.id.myProfile);
        scanQRButton = findViewById(R.id.fabCamera);
        backButton = findViewById(R.id.BackArrow);

        tvwelcomeText = findViewById(R.id.Welcome);
        savedPic = findViewById(R.id.ivProfile);

        // Add this line to your onCreate method
        ImageButton deleteProfileButton = findViewById(R.id.delete_button);


        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method to delete the profile picture
                deleteProfileImage();
            }
        });




        // Set onClickListeners for each button
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to navigate to CreateEventActivity
                Intent intent = new Intent(DashboardActivity.this, AddEventDetailActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to navigate to CreateEventActivity
                Intent intent = new Intent(DashboardActivity.this, ViewProfileActivity.class);
                startActivity(intent);
            }
        });

        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the QRCodeScannerActivity
                Intent intent = new Intent(DashboardActivity.this, QRCodeScannerActivity.class);
                startActivity(intent);
            }
        });

        browseEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DashboardActivity.this, BrowseEventsActivity.class);
                startActivity(intent);
            }
        });



        //manageEventsButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {

                //Intent intent = new Intent(OrganizerDashboardActivity.this, ManageEventsActivity.class);
                //startActivity(intent);
            //}
        //});

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * Method called when the activity is starting. Responsible for loading the user's profile information
     * and displaying a welcome message along with the profile picture.
     */
    public void onStart() {


        super.onStart();
        Toast.makeText(DashboardActivity.this,"Loading Current Profile!", Toast.LENGTH_SHORT).show();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            currentID = user.getUid();

            DocumentReference ref;
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            ref = firestore.collection("Users").document(currentID);
            ref.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                String user = task.getResult().getString("userName");
                                String url = task.getResult().getString("profilePicImage");


                                if (user != null && url != null) {
                                    // Both name and URL are available
                                    tvwelcomeText.setText("Welcome " + user);
                                    Picasso.get().load(url).into(savedPic);
                                } else if (user != null && url == null) {
                                    // Only name is available
                                    tvwelcomeText.setText("Welcome " + user);
                                    // Handle the case when no profile picture is available
                                    // Create a Bitmap with ARGB_8888 configuration
                                    Paint paint = new Paint();
                                    int desiredSizeInDp = 10;  // Adjust this to your desired size in dp

// Convert dp to pixels
                                    float scale = getResources().getDisplayMetrics().density;
                                    int desiredSizeInPixels = (int) (desiredSizeInDp * scale + 0.5f);

                                    Bitmap b = Bitmap.createBitmap(desiredSizeInPixels, desiredSizeInPixels, Bitmap.Config.ARGB_8888);
                                    Canvas c = new Canvas(b);
                                    c.drawColor(ContextCompat.getColor(DashboardActivity.this, R.color.coral));
                                    paint.setAntiAlias(true);
                                    // Calculate initials
                                    String[] strArray = user.split(" ");
                                    StringBuilder initialsBuilder = new StringBuilder();

                                    for (String str : strArray) {
                                        if (!str.isEmpty()) {
                                            initialsBuilder.append(str.charAt(0));
                                        }
                                    }

                                    String initials = initialsBuilder.toString().toUpperCase();

                                    // Draw the initials on the Canvas at the center
                                    float x = (b.getWidth() - paint.measureText(initials)) / 2;
                                    float y = (b.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2);
                                    c.drawText(initials, x, y, paint);
                                    savedPic.setImageBitmap(b);
                                }
                            } else {
                                Toast.makeText(DashboardActivity.this, "Create a Profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
//        startActivity(intent);
//    }

    /**
     * Method to handle the deletion of the user's profile image. If the image exists, it is deleted,
     * and a default profile image is set. If the image is already null, appropriate messages are displayed.
     */
    private void deleteProfileImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentID = user.getUid();
        }
        DocumentReference ref;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        ref = firestore.collection("Users").document(currentID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String profilePicUrl = document.getString("profilePicImage");

                        if (profilePicUrl != null) {
                            // The profile image URL exists, proceed with deletion
                            ref.update("profilePicImage", null)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Image deleted successfully
                                                Toast.makeText(DashboardActivity.this, "Profile image deleted", Toast.LENGTH_SHORT).show();
                                                // Set a default image or handle the absence of an image
                                                // For example, you can set the default image as you did in the onStart method
                                                // or load a placeholder image.
                                                // ...
                                                savedPic.setImageResource(R.drawable.ic_default_profile_icon);
                                                Toast.makeText(DashboardActivity.this, "Go to My Profile to Add picture", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Failed to delete image
                                                Toast.makeText(DashboardActivity.this, "Failed to delete profile image", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // The profile image URL is already null
                            Toast.makeText(DashboardActivity.this, "Profile image already deleted", Toast.LENGTH_SHORT).show();
                            // You may want to handle this case accordingly
                            Toast.makeText(DashboardActivity.this, "Go to My Profile to Add picture", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void retrieveAnonymousUserId() {
        sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentID = sp.getString("anonymousUserId", null);

        if (currentID != null) {
            // Now, 'anonymousUserId' variable contains the anonymous user's ID
            Log.d("SharedPreferences", "Retrieved Anonymous User ID: " + currentID);
        } else {
            Log.e("SharedPreferences", "Failed to retrieve Anonymous User ID");
        }
    }









}
