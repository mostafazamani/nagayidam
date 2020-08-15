package com.example.crush.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.crush.MainMenu;
import com.example.crush.MyTwitterApiClient;
import com.example.crush.R;
import com.example.crush.models.UserShow;
import com.example.crush.models.unfollowFind;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeBottomFragment extends Fragment {

    MainMenu m;
    ImageView profile , banner;
    Button follower_num , following_num ;

    private TwitterSession session;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_fragment, container, false);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        banner  = view.findViewById(R.id.banner_profile);
        profile = view.findViewById(R.id.profile_image);
        follower_num = view.findViewById(R.id.follower_num);
        following_num = view.findViewById(R.id.following_num);

        user_info(session);


        MyTwitterApiClient twitterApiClient = new MyTwitterApiClient(session);
        twitterApiClient.getCustomTwitterService().Unfollow().enqueue(new Callback<unfollowFind>() {
            @Override
            public void onResponse(Call<unfollowFind> call, Response<unfollowFind> response) {
                if (response.body() != null) {
                    unfollowFind find = response.body();
                    Toast.makeText(view.getContext(), ""+find.getId(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<unfollowFind> call, Throwable t) {

            }
        });


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
                transaction.replace(R.id.fragment_container, followerYouNotFollow ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();

            }
        });

        return view;
       // return inflater.inflate(R.layout.home_fragment,container,false);
    }

    public void user_info(TwitterSession session){

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().User(session.getUserId() , session.getUserName()).enqueue(new Callback<UserShow>() {
            @Override
            public void onResponse(Call<UserShow> call, Response<UserShow> response) {


                if (response.body() != null) {
                    UserShow show = response.body();
                    /*Toast.makeText(HomeBottomFragment.this, ""+show.getProfile_name() + "\n"
                            +show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();*/

                    String purl = show.getProfile_image_url();
                    String burl = show.getProfile_banner_url();
                    follower_num.setText("Follower\n"+String.valueOf(show.getFollowers_count()));
                    following_num.setText("Following\n"+String.valueOf(show.getFollowings_count()));
                    String url = geturlpic(purl);
                    new HomeBottomFragment.DownloadImageTask(banner)
                            .execute(burl);

                    new HomeBottomFragment.DownloadImageTask(profile)
                            .execute(url);

                }


            }

            @Override
            public void onFailure(Call<UserShow> call, Throwable t) {

            }
        });

    }

    public String geturlpic(String s){
        char[] chars = s.toCharArray();
        String url="";
        for (int i = 0 ; i<chars.length-11;i++){
            url += chars[i];
        }

        url += ".jpg";

        return url;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bannerImage ;

        public DownloadImageTask(ImageView bannerImage ) {
            this.bannerImage = bannerImage;
            //   this.profileImage = profilImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("خطا در بارگیری عکس", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bannerImage.setImageBitmap(result);
            //profileImage.setImageBitmap(result);
        }
    }
}