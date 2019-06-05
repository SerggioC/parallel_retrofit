package com.sergiocruz.parallelretrofit;

import android.content.Context;

import com.venmo.android.pin.AsyncSaver;
import com.venmo.android.pin.AsyncValidator;
import com.venmo.android.pin.PinFragmentConfiguration;

public class Testing {

    void doSomething(Context context) {

        PinFragmentConfiguration config =
                new PinFragmentConfiguration(context)
                        .pinSaver(new AsyncSaver() {
                            @Override
                            public void save(String pin) {

                            }
                        }).validator(new AsyncValidator() {
                    public boolean isValid(String submission) {
                        // HttpClient client = ...
                        // boolean valid = client.comparePin(submission);
                        boolean valid = true;

                        return valid;
                    }
                });
    }

}
