
package com.example.testspotify;

// Imports that are necessary for the authorization to work.
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;
import java.io.IOException;

// Using some personal account details, an access token can be generated
// This allows for the rest of the code to actually work
// Most of getAuth is from the official Spotify API GitHub. We configured the client-specific info., as well as modified parts of clientCredentials_Sync
// in order to generate a new access token each time the program is loaded, and pass the token into the other files for the program to work
public class getAuth {
    private static final String clientId = "a5c5c9180b8c4842ae5ab40949dda428";
    private static final String clientSecret = "ebf0b0464da648a2a3ef9dd17acee11f";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId)
            .setClientSecret(clientSecret).build();
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

    public static String clientCredentials_Sync() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            return clientCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {

    }
}