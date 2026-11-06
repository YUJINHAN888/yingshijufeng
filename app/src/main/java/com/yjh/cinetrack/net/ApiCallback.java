package com.yjh.cinetrack.net;

public interface ApiCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}
