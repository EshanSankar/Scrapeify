package com.example.testspotify;
//a lot of imports
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.Arrays;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

//instance variables
    String formattedString = "";
    private EditText editPlaylistLink;
    String rawLink;
    String formattedLink;
    private LinearLayout getYoutubeLink;
    private LinearLayout getSimilar;
    private LinearLayout getRating;
    String artistRelated;
    String songRelated;
    String website = "";
    String ytLink = "";
    String relatedLink = "";
    double average = 0;
    int count = 0;
    String song = "";
    String artist = "";
    String album = "";
    String compliment = "";
    ArrayList<String> websiteList = new ArrayList<String>();
    ArrayList<String> ytList = new ArrayList<String>();
    ArrayList<String> relatedList = new ArrayList<String>();
    String metaContent;
    String combinedElements = "0";
    Elements pRating;
    Elements mRating;
    Elements nRating;
    TextView scoreBig;
    TextView linkBig;
    TextView albumBig;
    TextView similarBig;
    int currentLink = 0;
    ArrayList<String> videos = new ArrayList<String>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //onCreate method to initialize variables and set Strictmode for network access to permit all
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSimilar = findViewById(R.id.getSimilar);
        getYoutubeLink = findViewById(R.id.getYoutubeLink);
        getRating = findViewById(R.id.getRating);
        editPlaylistLink = findViewById(R.id.editPlaylistLink);
        scoreBig = findViewById(R.id.scoreBig);
        albumBig = findViewById(R.id.albumID);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
//click listener for getSimilar
        findViewById(R.id.getSimilar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on click it will execute getRelated and open the pop-up
                new getRelated().execute();
                openSimilarP(v);
            }
        });
        //click listener
        findViewById(R.id.getYoutubeLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //execute the scraper and open popup
                ScrapeWebsite neck1 = new ScrapeWebsite();
                neck1.execute();
                openLinkP(v);
            }
        });
        findViewById(R.id.getRating).setOnClickListener(new View.OnClickListener() {
            @Override
            //click listener
            public void onClick(View v) {
                //execute the scraper and open popup
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                ScrapeMetaRating neck = new ScrapeMetaRating();
                neck.execute();
                openRatingP(v);
            }
        });
    }
    public void openRatingP(View view) {
        //method to open the rating popup
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewRatingP = layoutInflater.inflate(R.layout.resultsp, null);
//dimensions
        final PopupWindow ratingPop = new PopupWindow(viewRatingP, 900, 500, true);
        ratingPop.showAtLocation(view, Gravity.CENTER, 0, 0);
        //initialize variables
        scoreBig = (TextView) viewRatingP.findViewById(R.id.scoreBig);
        albumBig = (TextView) viewRatingP.findViewById(R.id.albumID);
//click listener
        viewRatingP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //close popup
                ratingPop.dismiss();
                return true;
            }
        });
    }
    //exact same thing as above but displays different data
    public void openSimilarP(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewSimilarP = layoutInflater.inflate(R.layout.similarp, null);

        final PopupWindow similarPop = new PopupWindow(viewSimilarP, 900, 500, true);
        similarPop.showAtLocation(view, Gravity.CENTER, 0, 0);
        similarBig = (TextView) viewSimilarP.findViewById(R.id.similarBig);


        viewSimilarP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                similarPop.dismiss();
                return true;
            }
        });
    }
//youtube links popup
    public void openLinkP(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewLinksP = layoutInflater.inflate(R.layout.linksp, null);

        final PopupWindow linksPop = new PopupWindow(viewLinksP, 900, 500, true);
        linksPop.showAtLocation(view, Gravity.CENTER, 0, 0);
        linkBig = (TextView) viewLinksP.findViewById(R.id.linkBig);
        Button before;
        Button after;
        before = (Button) viewLinksP.findViewById(R.id.before);
        after = (Button) viewLinksP.findViewById(R.id.after);
        // WHEN THE BUTTONs ARE CLICKED, A COUNTER IS INCREMENTED OR DECREMENTED AND THE ASSOCIATED YOUTUBE LINK FROM THE LIST IS DISPLAYED
        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLink != 0) {
                    currentLink --;
                    linkBig.setText(videos.get(currentLink));
                }

            }
        });
        after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLink != videos.size() - 1) {
                    currentLink ++;
                    linkBig.setText(videos.get(currentLink));
                }

            }
        });
        //closes popup
        viewLinksP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                linksPop.dismiss();
                return true;
            }
        });
    }
//getRelated songs AsyncTask (network operations dont work on main for some reason)
    private class getRelated extends AsyncTask<Void, Void, Void> {
        String relatedContent;
        @Override
        //doInBackground to bypass network policies
        protected Void doInBackground(Void... voids) {
            getPlaylist getItems = new getPlaylist();
            try {
                // Gets the link from the textfield
                String linkP = String.valueOf(editPlaylistLink.getText());
                // Splits the link to only include the playlist ID at the end of the URL
                String splitId1[] = linkP.split("/");
                String splitId2[] = splitId1[4].split("\\?");
                String id = splitId2[0];
                String[] songList = new String[getItems.getTrackInfo(id).size() / 3];
                String[] albumList = new String[getItems.getTrackInfo(id).size() / 3];
                String[] artistList = new String[getItems.getTrackInfo(id).size() / 3];
                count = 0;
                average = 0;
                int m = 0;
                // For each item in the playlist, it scrapes the rating of its album
                for (int i = 1; i < getItems.getTrackInfo(id).size(); i += 3) {
                    song = getItems.getTrackInfo(id).get(i - 1);
                    songList[m] = song;
                    album = getItems.getTrackInfo(id).get(i);
                    albumList[m] = album;
                    artist = getItems.getTrackInfo(id).get(i + 1);
                    artistList[m] = artist;
                    relatedLink = "https://www.chosic.com/similar-songs/"+ song + "-by-" + artist + "/";
                    relatedList.add(relatedLink);
                    m ++;
                }
                for(int i = 0; i < relatedList.size(); i++){
                    //connect to each website in the relatedList
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(relatedList.get(i))
                            .build();
                    Response response = client.newCall(request).execute();
                    relatedContent = response.body().string();
                    Document document = Jsoup.parse(relatedContent);
                    //parse and find elements in the according class
                    Elements links = document.getElementsByClass("track-list-item-info-text");
                    int k = 0;
                    //loop through all the info with the according class and format them to only include the necessary data
                    for (Element link : links) {
                        artistRelated = link.toString();
                        songRelated = link.toString();
                        if( artistRelated.length() > link.toString().indexOf("data-artist-name")) {
                            artistRelated = link.toString().substring(link.toString().indexOf("data-artist-name"));
                            artistRelated = artistRelated.substring(0, artistRelated.indexOf("\"></i><i class=\"fab fa-spotify icon-img spotify-icon\" title=\"Play on Spotify\""));
                        }
                        if( songRelated.length() > link.toString().indexOf("data-track-name")) {
                            songRelated = link.toString().substring(link.toString().indexOf("data-track-name"));
                            songRelated = songRelated.substring(0, songRelated.indexOf("data-artist-name"));
                        }
                        //k==1 to only get 1 related song per song in the playlist
                        if(k == 1) {
                            //formatting string
                            formattedString=formattedString+("\n");

                            formattedString=formattedString+"\"" + (songRelated.substring(17));
                            formattedString=formattedString+("\n");
                            formattedString=formattedString+"By " + (artistRelated.substring(18));
                            break;
                        }
                        k++;
                    }
                }


                //set the textview to formattedString
                similarBig.setText(formattedString);
                similarBig.setMovementMethod(new ScrollingMovementMethod());
            } catch (Exception p) {
                // Error message if the link is invalid
                System.out.println(p);
            }
            return null;
        }
    }
//scrape metacritic rating (completely revamped) and also get most played artist/album
    private class ScrapeMetaRating extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
                getPlaylist getItems = new getPlaylist();
                try {
                    // Gets the link from the textfield
                    String link = String.valueOf(editPlaylistLink.getText());
                    // Splits the link to only include the playlist ID at the end of the URL
                    String splitId1[] = link.split("/");
                    String splitId2[] = splitId1[4].split("\\?");
                    String id = splitId2[0];

                    // CREATING NEW ARRAYS THAT ONLY HOLD SONG/ALBUM/ARTIST NAME
                    String[] songList = new String[getItems.getTrackInfo(id).size() / 3];
                    String[] albumList = new String[getItems.getTrackInfo(id).size() / 3];
                    String[] artistList = new String[getItems.getTrackInfo(id).size() / 3];
                    count = 0;
                    average = 0;
                    int m = 0;
                    // For each item in the playlist, it scrapes the rating of its album
                    // The associated song, album, and artist names are added to the new arrays
                    for (int i = 1; i < getItems.getTrackInfo(id).size(); i += 3) {
                        song = getItems.getTrackInfo(id).get(i - 1);
                        songList[m] = song;
                        album = getItems.getTrackInfo(id).get(i);
                        albumList[m] = album;
                        artist = getItems.getTrackInfo(id).get(i + 1);
                        artistList[m] = artist;
                        // The album and artist names are appended to the metacritic link, and the link is added to an arraylist
                        website = "https://www.metacritic.com/music/" + album + "/" + artist;
                        websiteList.add(website);
                        // A Google search query for the song/artist name is created and added to a list, to be used later for the YT link getter
                        ytLink = "https://www.google.com/search?q=" + song + "+" + artist + "+youtube";
                        ytList.add(ytLink);
                        m ++;
                    }
                    // For each element in the list of metacritic links...
                    for(int i = 0; i < websiteList.size(); i++){
                        try {
                            // Connects to the website using okhttp, and sets the value of metaContent to the entire HTML contents of the webpage
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(websiteList.get(i))
                                    .build();
                            Response response = client.newCall(request).execute();
                            metaContent = response.body().string();
                            //parses the data using jsoup and then searches for the possible ratings on the website using its associated class
                            Document document = Jsoup.parse(metaContent);
                            pRating = document.getElementsByClass("metascore_w xlarge album positive");
                            mRating = document.getElementsByClass("metascore_w xlarge album mixed");
                            nRating = document.getElementsByClass("metascore_w xlarge album negative");
                            combinedElements = (pRating.text()) + (mRating.text()) + (nRating.text());
                            average += Double.valueOf(combinedElements);
                            count++;

                        } catch (Exception except) {
                            // If the rating cannot be found, nothing is incremented
                            average += 0;
                        }
                    }
                    // The final average is calculated, with some personalized messages depending on the average
                    average = (int) average/count;
                    if (average <= 50) {
                        compliment = "terrible";
                    } else if (average > 50 && average <= 70) {
                        compliment = "decent";
                    } else if (average > 70 && average <=85) {
                        compliment = "very good";
                    } else {
                        compliment = "perfect";
                    }
                    // The score is displayed on the pop-up window
                   scoreBig.setText(formatRating(String.valueOf(average)));

                    // BELOW IS THE CODE FOR MOST PLAYED ARTIST/ALBUM.
                    //This is explained in more detail on the written assignment

                    // These two arrays are sorted alphabetically
                    Arrays.sort(albumList);
                    Arrays.sort(artistList);
                    // New arraylists are created which hold the "counts" of each song's album/artist
                    ArrayList<String> alCountStr = new ArrayList<String>();
                    ArrayList<String> arCountStr = new ArrayList<String>();

                    // For the entirety of the arrays, it checks to see if the next element is the same as the current one
                    // If so, that means the albums/artists are the same, so a counter is incremented
                    int z = 0;
                    int albumC;
                    for (int u = 0; u < albumList.length; u ++) {
                        albumC = 0;
                        for (int innerC = u; innerC < albumList.length - 1; innerC ++) {
                            if (albumList[innerC].equals(albumList[innerC + 1])) {
                                albumC ++;
                            }
                        }
                        albumC ++;
                        // The count for that particular album/artist is added to an arraylist holding each individual count
                        alCountStr.add(u, Integer.toString(albumC));
                    }

                    // The contents of the arraylist, holding strings, are converted into a new array, holding integers
                    // This makes it possible to sort
                    int[] alModeC = new int[alCountStr.size()];
                    for (int n = 0; n < alModeC.length; n ++) {
                        alModeC[n] = Integer.valueOf(alCountStr.get(n));
                    }

                    // Bubble sorts the new array
                    int x = 0;
                    int temp;
                    String t;
                    while (x < (alModeC.length - 1)) {
                        for (int i = 0; i < alModeC.length - 1; i++) {

                            if (alModeC[i] > alModeC[i + 1]) {
                                temp = alModeC[i + 1];
                                alModeC[i + 1] = alModeC[i];
                                alModeC[i] = temp;
                                // Simultaneously rearranges the list of albums/artists according to the bubble sort above
                                // This ensures that, across the 2 arrays, each number corresponds to an actual artist/album in the other array
                                t = albumList[i+1];
                                albumList[i+1] = albumList[i];
                                albumList[i] = t;
                            }
                        }
                        x++;
                    }

                    // Same thing as above, for the artist array
                    z = 0;
                    int artistC;
                    for (int u = 0; u < artistList.length; u ++) {
                        z = u;
                        artistC = 0;
                        while (z < artistList.length - 1 && artistList[z].equals(artistList[z+1])) {
                            artistC ++;
                            z ++;
                        }
                        artistC ++;
                        arCountStr.add(Integer.toString(artistC));
                    }

                    int[] arModeC = new int[arCountStr.size()];
                    for (int n = 0; n < alModeC.length; n ++) {
                        arModeC[n] = Integer.valueOf(arCountStr.get(n));
                    }
                    x = 0;
                    int temp1;
                    String t1;
                    while (x < (arModeC.length - 1)) {
                        for (int i = 0; i < arModeC.length - 1; i++) {
                            if (arModeC[i] > arModeC[i + 1]) {
                                temp1 = arModeC[i + 1];
                                arModeC[i + 1] = arModeC[i];
                                arModeC[i] = temp1;
                                t1 = artistList[i+1];
                                artistList[i+1] = artistList[i];
                                artistList[i] = t1;
                            }
                        }
                        x++;
                    }

                    // Displays final message showing all of the data!
                    albumBig.setText("You have a " + compliment + " music taste!" +"\n"+ "Favourite Artist: " + artistList[artistList.length - 1].replaceAll("-"," ") + "\n" + "Favourite Album: " + albumList[albumList.length - 1].replaceAll("-"," "));

                } catch (Exception p) {
                    // Error message if the link is invalid
                    System.out.println(p);
                }

            return null;
        }
    }
//scrape google for youtube link
    private class ScrapeWebsite extends AsyncTask<Void, Void, Void> {
        String websiteContent;
        @Override
        protected Void doInBackground(Void... voids) {
                getPlaylist getItems = new getPlaylist();
                try {
                    // Gets the link from the textfield
                    String linkP = String.valueOf(editPlaylistLink.getText());
                    // Splits the link to only include the playlist ID at the end of the URL
                    String splitId1[] = linkP.split("/");
                    String splitId2[] = splitId1[4].split("\\?");
                    String id = splitId2[0];
                    String[] songList = new String[getItems.getTrackInfo(id).size() / 3];
                    String[] artistList = new String[getItems.getTrackInfo(id).size() / 3];
                    int m = 0;

                    for (int i = 1; i < getItems.getTrackInfo(id).size(); i += 3) {
                        song = getItems.getTrackInfo(id).get(i - 1);
                        songList[m] = song;
                        artist = getItems.getTrackInfo(id).get(i + 1);
                        artistList[m] = artist;
                        //formatting and adding each playlist item to a google link to search
                        ytLink = "https://www.google.com/search?q=" + song + "+" + artist + "+youtube";
                        ytList.add(ytLink);

                        m ++;
                    }
                    int sC = 0;
                    for(int i = 0; i < ytList.size(); i++){
                        //connect and scrape each google search for the first occurring youtube video and assocated link
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(ytList.get(i))
                                .build();
                        Response response = client.newCall(request).execute();
                        websiteContent = response.body().string();
                        Document document = Jsoup.parse(websiteContent);
                        Elements links = document.select("a[href]");
                        StringBuilder allLinks = new StringBuilder();
                        int j = 0;
                        for (Element link : links) {
                        //youtube link for first video is always the 19th link, hence adding the 19th link to the allLinks stringbuilder
                            if(j == 19){
                                //
                                allLinks.append(link.attr("href")).append("\n");
                                break;
                            }
                            j++;
                        }
                        //catching errors
                        if(!allLinks.toString().contains("url?q=https://www.youtube.com/watch")){
                            websiteContent = "Could not find";
                        }else {
                        //formatting
                            rawLink = allLinks.toString();
                            formattedLink = songList[sC].replaceAll("-"," ") + ": ";
                            formattedLink = formattedLink + "\n";
                            formattedLink = formattedLink + formatLink(rawLink);

                            //adding to videos array
                            videos.add(formattedLink);


                        }
                        sC ++;
                    }
                    //setting link to first link in videos
                    linkBig.setText(videos.get(0));
                } catch (Exception p) {
                    // Error message if the link is invalid
                    System.out.println(p);
                }
            return null;
        }
    }
    public String formatLink(String rawLink){
        //formatting google scraped youtube links
        rawLink = rawLink.substring(7);
        int indexToSplit = rawLink.indexOf('&');
        rawLink = rawLink.substring(0, indexToSplit);
        rawLink = rawLink.replaceAll("watch%3Fv%3D", "watch?v=");
        return rawLink ;
    }
    //formatting the rating from metacritic
    public String formatRating(String rating){
        rating = rating.replaceAll(".0", "%");
        return rating;
    }
}