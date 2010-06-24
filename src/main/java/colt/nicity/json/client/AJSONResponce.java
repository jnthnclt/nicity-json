/*
 * AJSONResponce.java.java
 *
 * Created on 12-27-2009 03:21:00 PM
 *
 * Copyright 2009 Jonathan Colt
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
package colt.nicity.json.client;

import colt.nicity.json.core.JStringReader;
import colt.nicity.json.core.Ja;
import colt.nicity.json.core.Jo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Administrator
 */
public class AJSONResponce {

    private int statusCode;
    private String responseAsString = null;
    private InputStream is;
    private HttpURLConnection con;
    private boolean streamConsumed = false;


    /**
     *
     * @param con
     * @throws IOException
     */
    public AJSONResponce(HttpURLConnection con) throws IOException {
        this.con = con;
        this.statusCode = con.getResponseCode();
        if (statusCode == 200) {
            is = con.getInputStream();
        } else {
            is = con.getErrorStream();
        }
        if ("gzip".equals(con.getContentEncoding())) {
            System.out.println("Is GZIPPED!");
            // the response is gzipped
            is = new GZIPInputStream(is);
        }
    }

    /**
     *
     * @return
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     *
     * @param name
     * @return
     */
    public String getResponseHeader(String name) {
        return con.getHeaderField(name);
    }

   
    /**
     *
     * @return
     */
    public InputStream asStream() {
        if(streamConsumed){
            throw new IllegalStateException("Stream has already been consumed.");
        }
        return is;
    }

    
    /**
     *
     * @return
     * @throws Exception
     */
    public String asString() throws Exception {
        if(null == responseAsString){
            BufferedReader br;
            try {
                InputStream stream = asStream();
                br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                StringBuffer buf = new StringBuffer();
                String line;
                while (null != (line = br.readLine())) {
                    buf.append(line).append("\n");
                }
                this.responseAsString = buf.toString();
                stream.close();
                con.disconnect();
                streamConsumed = true;
            } catch (NullPointerException npe) {
                // don't remember in which case npe can be thrown
                throw npe;
            } catch (IOException ioe) {
                throw ioe;
            }
        }
        return responseAsString;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Jo asJSONObject() throws Exception {
        String jsonString = asString();
        try {
            return new JStringReader(jsonString).readJo(null);
        } catch(Exception x) {
            System.out.println("..............................................");
            System.out.println("Invalid:"+jsonString);
            System.out.println("..............................................");
            throw x;
        }
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Ja asJSONArray() throws Exception {
        String jsonString = asString();
        try {
            return new JStringReader(jsonString).readJa(null);
        } catch(Exception x) {
            System.out.println("..............................................");
            System.out.println("Invalid:"+jsonString);
            System.out.println("..............................................");
            throw x;
        }
    }

    /**
     *
     */
    public void disconnect(){
        con.disconnect();
    }
}
