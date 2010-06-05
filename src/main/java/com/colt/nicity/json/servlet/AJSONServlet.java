/*
 * AJSONServlet.java.java
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
package com.colt.nicity.json.servlet;

import com.colt.nicity.json.core.Ja;
import com.colt.nicity.json.core.AJ;
import com.colt.nicity.json.core.JStringReader;
import com.colt.nicity.json.core.Jnull;
import com.colt.nicity.json.core.Jo;
import com.colt.nicity.json.core.Jv;
import com.colt.nicity.json.core.UJson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
abstract public class AJSONServlet extends HttpServlet {
    /**
     *
     */
    public boolean DEBUG = false;//!!
    /**
     *
     * @param request
     * @param _request
     * @return
     * @throws Exception
     */
    abstract public AJ processJSONRequest(HttpServletRequest request,Jo _request) throws Exception;
    /**
     *
     * @param _user
     * @param _password
     * @return
     */
    abstract public String secretkey(String _user, String _password);
    /**
     *
     * @param _oldValidity
     * @return
     */
    public String newSecretKey(String _oldValidity) {
        return _oldValidity;
    }
    /**
     *
     * @param location
     * @param _e
     * @return
     */
    public String[] exception(String location, Exception _e) {
        StackTraceElement[] trace = _e.getStackTrace();
        String[] log = new String[trace.length+1];
        log[0] = location;
        for(int i=0;i<trace.length;i++) {
            log[i+1] = trace[i].toString();
        }
        return log;
    }
    /**
     *
     * @param _address
     * @return
     */
    public long blackListed(String _address) {
        return 0;// never blacklisting
    }
    /**
     *
     * @return
     */
    public boolean zipResponce() { return false; }
    /**
     *
     * @return
     */
    public boolean authenticate() { return true; }

    
    private HashMap<String,String> secretKeys = new HashMap<String,String>();
    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String address = request.getRemoteAddr();
        if (blackListed(address) > 0) {
            returnJSONError(getResponseWriter(response),"ACCESS DENIED Blacklisted");
            return;
        }
        
        Jo data = null;
        String jsonData = null;
        try {
            jsonData = request.getParameter("JSON");
            if (jsonData == null) {
                returnJSONError(getResponseWriter(response),"Error no JSON paramter");
                return;
            }
            data = new JStringReader(jsonData).readJo(null);
            
        } catch (Exception x) {
            returnJSONError(getResponseWriter(response),getServletName(),"Parse JSON Error",jsonData);
            return;
        }

        if (DEBUG) {
            System.out.println(data.toString());
        }
        
        String secretKey = null;
        if (authenticate()) {
            try {
                secretKey = UJson.getString(data,"secretkey",null);
                if (!address.equals(secretKeys.get(secretKey))) {
                    secretKey = secretkey(UJson.getString(data,"username","anonymous"),UJson.getString(data,"password","anonymous"));
                    if (secretKey==null) {
                        returnJSONError(getResponseWriter(response),"ACCESS DENIED Bad username or password");
                        return;
                    }
                    secretKeys.put(secretKey, address);
                }
            } catch (Exception x) {
                returnJSONError(getResponseWriter(response),exception("Parse Error Verifying Requestor", x));
                return;
            }
        }
        

        Jo jsonResponse = new Jo();
        try {
            AJ r = processJSONRequest(request,data);
            if (r == null) UJson.add(jsonResponse,"return", Jnull.cNull());
            else UJson.add(jsonResponse,"return", r);

            if (authenticate()) {
                String newSecretKey = newSecretKey(secretKey);
                UJson.add(data, "secretkey", newSecretKey);
                
                //?? This has the side effect preventing a user from making more than on request at a time
                secretKeys.remove(secretKey);
                secretKeys.put(newSecretKey, address);
            }

        } catch(Exception x) {
            returnJSONError(getResponseWriter(response),exception("Processing Request", x));
            return;
        }

        if (zipResponce()) {
            AJSONGZipper zippedResponce = new AJSONGZipper(response);
            zippedResponce.setHeader("Content-Encoding", "gzip");
            PrintWriter out = zippedResponce.getWriter();
            try {
                configReponce(zippedResponce);
                out.write(jsonResponse.toString());
                
            } catch(Exception x) {
                returnJSONError(zippedResponce.getWriter(),exception("Writing Responce", x));
            }
            out.flush();
            GZIPOutputStream gzos = zippedResponce.getGZIPOutputStream();
            gzos.finish();
        }
        else {
            PrintWriter out = getResponseWriter(response);
            try {
                out.write(jsonResponse.toString());
                out.flush();
            } catch(Exception x) {
                returnJSONError(out,exception("Writing Responce", x));
                return;
            }
        }
    }

    /**
     *
     * @param _out
     * @param _error
     */
    protected void returnJSONError(PrintWriter _out,String... _error) {
        try {
            Ja ja = new Ja();
            for(String e:_error) ja.add(new Jv(e));
            Jo jo = new Jo();
            UJson.add(jo,"error",ja);
            _out.write(jo.toString());
            _out.flush();
        } catch(Exception x) {
            _out.println("");
            _out.flush();
            return;
        }
    }

    /**
     *
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    protected PrintWriter getResponseWriter(HttpServletResponse response) throws ServletException, IOException {
        configReponce(response);
        response.setContentType("application/json; charset=utf-8");
        return response.getWriter();
    }

    private void configReponce(HttpServletResponse response) {
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
        response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    }

    /**
     *
     * @param request
     * @return
     */
    protected String getPostDataAsString(HttpServletRequest request) {

        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        try {
            BufferedReader requestData = request.getReader();
            while ((line = requestData.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return stringBuffer.toString().trim();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "";
    }
}
