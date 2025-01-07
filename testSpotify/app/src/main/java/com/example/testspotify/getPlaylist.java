package com.example.testspotify;


// Many more imports that allow the API to be used
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import org.apache.hc.core5.http.ParseException;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.ArrayList;

public class getPlaylist {
    // Generates an access token
    getAuth token = new getAuth();
    private final String accessToken = token.clientCredentials_Sync();
    private final SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken).build();


    public ArrayList<String> getTrackInfo(String id) {
        // This is some complicated Spotify API configuration. This is from the official GitHub documentation
        final GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(id)
//			          .fields("description")
//			          .limit(10)
//			          .offset(0)
//			          .market(CountryCode.SE)
//			          .additionalTypes("track,episode")
                .build();

        // An arraylist that holds all of the information for each song in the actual playlist
        ArrayList<String> playlist = new ArrayList<String>();
        try {
            // This gets the items of the playlist
            final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();

            // For each item in the playlist...
            for (int i = 0; i < playlistTrackPaging.getTotal(); i++) {

                // The Spotify API does not actually return just the album name or the artist name. It spits out a lot of other garbage that we do not need.
                // The raw, unfiltered string is then split at certain characters to isolate the song, artist and album names. This was achieved through trial and error and trying to figure out
                // which indices of the original string could be split to yield the desired information

                String raw[] = ((Track) playlistTrackPaging.getItems()[i].getTrack()).toString()
                        .split("name=");
                ;
                String raw1[] = ((Track) playlistTrackPaging.getItems()[i].getTrack()).toString()
                        .split(", name=");
                ;
                String songRaw[] = raw[1].split(", artists=");
                String song = songRaw[0].toLowerCase();
                String artistRaw[] = raw[2].split(", externalUrls");
                ;
                String artist = artistRaw[0].toLowerCase();
                String albumRaw[] = raw1[1].split(", albumGroup");
                ;
                String album = albumRaw[0].toLowerCase();
                // Replacing and deleting some illegal characters in the song, artist or album names since they are not present in the website link
                StringBuilder songName = new StringBuilder(song.replace(' ', '-'));
                StringBuilder artistName = new StringBuilder(artist.replace(' ', '-'));
                StringBuilder albumName = new StringBuilder(album.replace(' ', '-'));
                for (int y = 0; y < songName.length(); y++) {
                    if (songName.charAt(y) == '$' || songName.charAt(y) == ',') {
                        songName.deleteCharAt(y);
                    }
                }
                for (int y = 0; y < artistName.length(); y++) {
                    if (artistName.charAt(y) == '$' || artistName.charAt(y) == ',') {
                        artistName.deleteCharAt(y);
                    }
                }
                for (int x = 0; x < albumName.length(); x++) {
                    if (albumName.charAt(x) == '.' || albumName.charAt(x) == ':' || albumName.charAt(x) == '\''
                            || albumName.charAt(x) == '?' || albumName.charAt(x) == '$') {
                        albumName = albumName.deleteCharAt(x);
                    }
                }

                // The song, album, and artist name for a particular song are added to "playlist"
                // Each of these elements will be accessed later
                playlist.add(songName.toString());
                playlist.add(albumName.toString());
                playlist.add(artistName.toString());
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // If there's an error, it will print the error message
            System.out.println("Error: " + e.getMessage());
        }
        return playlist;
    }

    public static void main(String[] args) {

    }
}


