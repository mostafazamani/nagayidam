package com.op.crush.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import com.google.firebase.firestore.QuerySnapshot;
import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;
import com.op.crush.MainMenu;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.models.UserShow;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeBottomFragment extends Fragment {

    MainMenu m;
    ImageView profile, banner;
    Button follower_num, following_num;
    SharedPreferences preferences;
    TwitterSession session;

    private WheelView wheelView;
    private String[] colors = {"#123456 , #654321 , #908765,#142524"};
    int size;
    public FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private List<DocumentSnapshot> querySnapshots;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        preferences = view.getContext().getSharedPreferences("Courser", Context.MODE_PRIVATE);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        TelephonyManager telephoneManager = (TelephonyManager) view.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = telephoneManager.getNetworkCountryIso();

        banner = view.findViewById(R.id.banner_profile);
        profile = view.findViewById(R.id.profile_image);
        follower_num = view.findViewById(R.id.follower_num);
        following_num = view.findViewById(R.id.following_num);

        wheelView = view.findViewById(R.id.wheelview);
        wheelView.setEmptyItemDrawable(R.drawable.avatar);

        user_info(session, view.getContext());

        Map<String, Object> map = new HashMap<>();
        map.put("id1", countryCode);

        add_crush(map);
        read_crushs();


        follower_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*NextFragment nextFrag= new NextFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.Layout_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();*/

                Fragment followerYouNotFollow = new FollowerYouNotFollow();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //FragmentManager.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
                transaction.replace(R.id.fragment_container, followerYouNotFollow); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();

            }
        });

        following_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowingNotFollowYou();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        // return inflater.inflate(R.layout.home_fragment,container,false);


        return view;
    }


    public void add_crush(Map<String, Object> map) {
        firestore.collection("crush")
                .document(String.valueOf(session.getId())).collection("cr").document().set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("firebase", "saved");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("firebase", "not saved" + e.getMessage());

            }
        });
    }

    public void read_crushs() {
        collectionReference = firestore.collection("crush").
                document(String.valueOf(session.getId())).collection("cr");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {


            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                querySnapshots = queryDocumentSnapshots.getDocuments();
                size = querySnapshots.size();

                ShapeDrawable[] shapeDrawables = new ShapeDrawable[size];
                for (int i = 0; i < size; i++) {
                    shapeDrawables[i] = new ShapeDrawable(new OvalShape());
                    //  shapeDrawables[i].getPaint().setColor(Color.parseColor(color[i]));
                }

                Picasso.with(getContext()).load("https://etc.usf.edu/techease/wp-content/uploads/2017/12/Robot-73-Juggler-C.jpg")
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                wheelView.setWheelItemCount(size);
                                wheelView.setAdapter(new WheelAdapter() {
                                    @Override
                                    public Drawable getDrawable(int position) {
                                        Resources res = getContext().getResources();
                                        Drawable myImage = ResourcesCompat.getDrawable(res, R.drawable.avatar, null);
                                        Drawable drawable = new BitmapDrawable(bitmap);
                                        return drawable;
                                    }

                                    @Override
                                    public int getCount() {
                                        return size;
                                    }
                                });
                                wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
                                    @Override
                                    public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {

                                    }
                                });

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void user_info(TwitterSession session, final Context context) {

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().User(session.getUserId(), session.getUserName()).enqueue(new Callback<UserShow>() {
            @Override
            public void onResponse(Call<UserShow> call, Response<UserShow> response) {


                if (response.body() != null) {
                    UserShow show = response.body();
                    /*Toast.makeText(HomeBottomFragment.this, ""+show.getProfile_name() + "\n"
                            +show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();*/
                    int cf = show.getFollowers_count() + show.getFollowings_count();
                    preferences.edit().putInt("CP", cf).apply();

                    String purl = show.getProfile_image_url();
                    String burl = show.getProfile_banner_url();
                    follower_num.setText("Follower\n" + String.valueOf(show.getFollowers_count()));
                    following_num.setText("Following\n" + String.valueOf(show.getFollowings_count()));
                    String url = geturlpic(purl);


                    Picasso.with(context).load(burl).into(banner);
                    RequestCreator d = Picasso.with(context).load(url);
                    d.into(profile);
                  d.into(new Target() {
                      @Override
                      public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {



                      }

                      @Override
                      public void onBitmapFailed(Drawable errorDrawable) {

                      }

                      @Override
                      public void onPrepareLoad(Drawable placeHolderDrawable) {

                      }
                  });
                   


                }


            }

            @Override
            public void onFailure(Call<UserShow> call, Throwable t) {

            }
        });

    }

    public String geturlpic(String s) {
        char[] chars = s.toCharArray();
        String url = "";
        for (int i = 0; i < chars.length - 11; i++) {
            url += chars[i];
        }

        url += ".jpg";

        return url;
    }

}