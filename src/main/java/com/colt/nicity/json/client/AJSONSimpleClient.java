/*
 * AJSONSimpleClient.java.java
 *
 * Created on 01-13-2010 07:11:00 AM
 *
 * Copyright 2010 Jonathan Colt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.colt.nicity.json.client;

import com.colt.nicity.json.core.Jo;
import com.colt.nicity.core.process.IAsyncResponse;
import com.colt.nicity.core.lang.ASetObject;
import com.colt.nicity.core.lang.IOut;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author Administrator
 */
public class AJSONSimpleClient extends ASetObject implements IJSONService {
    private final int OK = 200;// OK: Success!
    private final int NOT_MODIFIED = 304;// Not Modified: There was no new data to return.
    private final int BAD_REQUEST = 400;// Bad Request: The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.
    private final int NOT_AUTHORIZED = 401;// Not Authorized: Authentication credentials were missing or incorrect.
    private final int FORBIDDEN = 403;// Forbidden: The request is understood, but it has been refused.  An accompanying error message will explain why.
    private final int NOT_FOUND = 404;// Not Found: The URI requested is invalid or the resource requested, such as a user, does not exists.
    private final int NOT_ACCEPTABLE = 406;// Not Acceptable: Returned by the Search API when an invalid format is specified in the request.
    private final int INTERNAL_SERVER_ERROR = 500;// Internal Server Error: Something is broken.  Please post to the group so the JSONServlet team can investigate.
    private final int BAD_GATEWAY = 502;// Bad Gateway: JSONServlet is down or being upgraded.
    private final int SERVICE_UNAVAILABLE = 503;// Service Unavailable: The JSONServlet servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.

    private String userName,password,serviceUrl;
    /**
     *
     * @param _userName
     * @param _password
     * @param _serviceUrl
     */
    public AJSONSimpleClient(String _userName,String _password,String _serviceUrl) {
        userName = _userName;
        password = _password;
        serviceUrl = _serviceUrl;
    }
    /**
     *
     * @return
     */
    public Object hashObject() {
        return serviceUrl;
    }
    /**
     *
     * @return
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

     private String getCause(int statusCode){
        String cause = null;
        // http://apiwiki.twitter.com/HTTP-Response-Codes-and-Errors
        switch(statusCode){
            case NOT_MODIFIED:
                break;
            case BAD_REQUEST:
                cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
                break;
            case NOT_AUTHORIZED:
                cause = "Authentication credentials were missing or incorrect.";
                break;
            case FORBIDDEN:
                cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
                break;
            case NOT_FOUND:
                cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
                break;
            case NOT_ACCEPTABLE:
                cause = "Returned by the Search API when an invalid format is specified in the request.";
                break;
            case INTERNAL_SERVER_ERROR:
                cause = "Something is broken.  Please post to the group so the JSONServlet team can investigate.";
                break;
            case BAD_GATEWAY:
                cause = "JSONServlet is down or being upgraded.";
                break;
            case SERVICE_UNAVAILABLE:
                cause = "Service Unavailable: The JSONServlet servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
                break;
            default:
                cause = "";
        }
        return statusCode + ":" + cause;
    }

     /**
      *
      * @param _
      * @param _request
      * @param _responce
      */
     public void request(IOut _, Jo _request,final IAsyncResponse<Jo> _responce) {
        try {
            String stringToReverse = URLEncoder.encode(_request.toString(), "UTF-8");
            URL url = new URL(serviceUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write("JSON=" + stringToReverse);
            out.close();

            AJSONResponce r = new AJSONResponce(connection);
            int responseCode = connection.getResponseCode();
            if (responseCode != OK) {
                if (responseCode < INTERNAL_SERVER_ERROR) {
                    _responce.error(_,new RuntimeException(getCause(responseCode) + "\n"));
                    return;
                }
                // will retry if the status code is INTERNAL_SERVER_ERROR
            } else {
                try {
                    _responce.response(_,r.asJSONObject());
                } catch(Exception x) {
                    _responce.error(_,x);
                }
            }
        }
        catch (Exception x) {
            x.printStackTrace();
        }
    }
}
