package com.nsoni.flickr;

import com.nsoni.flickr.model.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrClient {

    @GET("/services/rest?method=flickr.photos.search&api_key=675894853ae8ec6c242fa4c077bcf4a0&extras=url_s&format=json&nojsoncallback=1")
    Call<Result> lookupPhotos(@Query("text") String searchWord, @Query("page") int pageNumber);
}
