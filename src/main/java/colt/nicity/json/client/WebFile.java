/*
 * WebFile.java.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public final class WebFile {

    /**
     *
     * @param _args
     */
    public static void main(String[] _args) {
        try {
            WebFile file   = new WebFile( "http://example.com/example.gif" );
            String MIME    = file.getMIMEType( );
            Object content = file.getContent( );
            if ( MIME.startsWith( "image" ) && content instanceof java.awt.Image )
            {
                java.awt.Image image = (java.awt.Image)content;

            }
        } catch(Exception x) {
            x.printStackTrace();
        }
    }


    // Saved response.
    private Map<String, List<String>> responseHeader = null;
    private URL responseURL = null;
    private int responseCode = -1;
    private String MIMEtype = null;
    private String charset = null;
    private Object content = null;
    /** Open a web file.
     * @param urlString
     * @throws MalformedURLException
     * @throws IOException
     */
    public WebFile(String urlString)
            throws MalformedURLException, IOException {
        // Open a URL connection.
        final URL url = new URL(urlString);
        final URLConnection uconn = url.openConnection();
        if (!(uconn instanceof HttpURLConnection)) {
            throw new IllegalArgumentException("URL protocol must be HTTP.");
        }
        final HttpURLConnection conn =
                (HttpURLConnection) uconn;

        // Set up a request.
        conn.setConnectTimeout(10000);    // 10 sec
        conn.setReadTimeout(10000);       // 10 sec
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-agent", "spider");

        // Send the request.
        conn.connect();

        // Get the response.
        responseHeader = conn.getHeaderFields();
        responseCode = conn.getResponseCode();
        responseURL = conn.getURL();
        final int length = conn.getContentLength();
        final String type = conn.getContentType();
        if (type != null) {
            final String[] parts = type.split(";");
            MIMEtype = parts[0].trim();
            for (int i = 1; i < parts.length && charset == null; i++) {
                final String t = parts[i].trim();
                final int index = t.toLowerCase().indexOf("charset=");
                if (index != -1) {
                    charset = t.substring(index + 8);
                }
            }
        }

        // Get the content.
        final InputStream stream = conn.getErrorStream();
        if (stream != null) {
            content = readStream(length, stream);
        } else if ((content = conn.getContent()) != null &&
                content instanceof InputStream) {
            content = readStream(length, (InputStream) content);
        }
        conn.disconnect();
    }
    /** Read stream bytes and transcode. */
    @SuppressWarnings("empty-statement")
    private Object readStream(int length, InputStream stream)
            throws IOException {
        final int buflen = Math.max(1024, Math.max(length, stream.available()));
        byte[] buf = new byte[buflen];
        ;
        byte[] bytes = null;

        for (int nRead = stream.read(buf); nRead != -1; nRead = stream.read(buf)) {
            if (bytes == null) {
                bytes = buf;
                buf = new byte[buflen];
                continue;
            }
            final byte[] newBytes = new byte[bytes.length + nRead];
            System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
            System.arraycopy(buf, 0, newBytes, bytes.length, nRead);
            bytes = newBytes;
        }

        if (charset == null) {
            return bytes;
        }
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
        }
        return bytes;
    }
    /** Get the content.
     * @return
     */
    public Object getContent() {
        return content;
    }
    /** Get the response code.
     * @return
     */
    public int getResponseCode() {
        return responseCode;
    }
    /** Get the response header.
     * @return
     */
    public Map<String, List<String>> getHeaderFields() {
        return responseHeader;
    }
    /** Get the URL of the received page.
     * @return
     */
    public URL getURL() {
        return responseURL;
    }
    /** Get the MIME type.
     * @return
     */
    public String getMIMEType() {
        return MIMEtype;
    }
}
