package com.subseekerapp;

import java.util.ArrayList;

public class MovieData {
    private  ArrayList<String> movieLangs;
    private  ArrayList<String> movieHashes;
    private  ArrayList<Long> movieByteSizes;
    private  ArrayList<String> SubtitlesToDownload;
    private  ArrayList<String> SubtitlesNames;
    private  ArrayList<String> movieAddresses;

    public void clear(){
        movieLangs.clear();
        movieHashes.clear();
        movieByteSizes.clear();
    }

    public ArrayList<Long> getMovieByteSizes() {
        return movieByteSizes;
    }

    public ArrayList<String> getMovieHashes() {
        return movieHashes;
    }

    public ArrayList<String> getMovieLangs() {
        return movieLangs;
    }

    public ArrayList<String> getSubtitlesToDownload() {
        return SubtitlesToDownload;
    }

    public ArrayList<String> getSubtitlesNames() {
        return SubtitlesNames;
    }

    public ArrayList<String> getMovieAddresses() {
        return movieAddresses;
    }

    public MovieData(){
        movieLangs = new ArrayList<>();
        movieAddresses =  new ArrayList<>();
        movieByteSizes = new ArrayList<>();
        movieHashes = new ArrayList<>();
        SubtitlesToDownload = new ArrayList<>();
        SubtitlesNames = new ArrayList<>();
    }
}
