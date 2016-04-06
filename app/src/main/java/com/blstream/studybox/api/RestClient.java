package com.blstream.studybox.api;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class RestClient {

    private static final String URL = "http://78.133.154.70:2000/";
    private static final String BASE_URL = "http://private-5f2e4b-studybox2.apiary-mock.com/";
    private RestInterface restInterface;

    public RestClient(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .build();

        restInterface = restAdapter.create(RestInterface.class);
    }

    public RestClient(RequestInterceptor interceptor){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(URL)
                .setRequestInterceptor(interceptor)
                .build();

        restInterface = restAdapter.create(RestInterface.class);
    }

    public RestInterface getService(){
        return restInterface;
    }
}
