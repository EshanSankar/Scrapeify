# Scrapeify
This is a collaboration between myself and a peer. This was submitted as a Computer Science 30 final project. To view the code, navigate: Scrapeify.zip\testSpotify\app\src\main\java\com\example\testspotify

# Background
We are both huge music fans and we wanted a way to quantify our listening habits. Every year, Spotify releases "Wrapped", which is a summary of a user's favourite songs from the past year. However, we did not want to wait 1 full year to judge our music tastes. Instead, we decided to create a program to do that automatically.

# Initial concept
We wanted to create an app that would give a numerical rating for a user's Spotify playlist. We decided that the best way to go about doing this was to find the average rating for the album that each song in the playlist was from. For example, consider a playlist with 2 songs: "One More Time" by Daft Punk from their "Discovery" album; and "EARFQUAKE" by Tyler, The Creator from his "IGOR" album. The program would need to find the rating of "Discovery" (74/100 on Metacritic) and "IGOR" (81/100 on Metacritic), add those values together, and divide them by the number of songs in the playlist, which is 2. The calculation that the program would make is as follows: (74+81)/2 = 77.5

# Learning Spotify API
To get the tracklist of a user-inputted playlist, we needed to use the Spotify Web API. It took quite a lot of troubleshooting to properly integrate it into our program, since factors such as generating a client ID were somewhat difficult to learn about. Ultimately, we could retrieve all of the required information about a particular song, such as its artist and album.

# Learning web scraping
To get the actual album ratings, we decided to use a website called "Metacritic", which is an enormous database for album ratings. Each Metacritic page for an album has a very distinct icon for its rating, so we knew that we could target that value in our web scraping. With the information retrieved using the Spotify API, we could generate a link to an album's specific Metacritic page, and then scrape the value of its rating. We learned JSoup in order to retrieve this value, and at the time, we considered it to be quite difficult, since neither of us had comprehensive knowledge of HTML and the properties of a website.

# The final products
Initially, we had created a simple .jar file with a simple GUI. It could compute a user-inputted Spotify playlist and provide a rating for it, as we had intended! However, we decided to take things a step further, and created an Android app using Android Studio, featuring a more comprehensive GUI. We also decided to include more features that would help our project feel more like Spotify Wrapped, such as finding the most occuring artist and album in the playlist. We even took our web-scraping efforts one step futher and scraped another website, "Chosic", that shows a list of songs that are similar to any specified song (although we had initially wanted to implement this using our own methods for finding similar genres and types of music).

# Learnings and areas of improvement
We developed knowledge of APIs and web scraping from the ground-up, and building this project was a very informative experience. Some aspects, such as finding the most played album/artist, were implemented in very unique ways (more information can be found in our code, with specific comments). However, the algorithm was rather inefficient, and it could take many minutes to calculate all of the information if a playlist were to have hundreds of songs. Although this was most likely due to our program being rate limited by Spotify's servers, due to its reliance on the API, more efficient methods could definitely be implemented. Furthermore, we had wanted to include a feature that would convert Spotify playlists to YouTube playlists, but the YouTube API had many restrictions, especially due to the account security measures that we would have needed to implement.
