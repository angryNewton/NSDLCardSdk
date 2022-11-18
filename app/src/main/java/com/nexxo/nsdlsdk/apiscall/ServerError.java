package com.nexxo.nsdlsdk.apiscall;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public class ServerError extends VolleyError {
    public ServerError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ServerError() {
        super();
    }
}