/*
 * AJSONGZipper.java.java
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
package colt.nicity.json.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author Administrator
 */
public class AJSONGZipper extends HttpServletResponseWrapper {
    private GZIPServletOutputStream servletGzipOS = null;
    private PrintWriter pw = null;
    /**
     *
     * @param resp
     */
    public AJSONGZipper(HttpServletResponse resp) {
        super(resp);
    }

    //
    private Object streamUsed = null;
    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // Allow the servlet to access a servlet output stream (SOS)
        // only if the servlet has not already accessed the print writer.
        if ((streamUsed != null) && (streamUsed != servletGzipOS)) {
            throw new IllegalStateException();
        }
        // Wrap the original servlet output stream with our compression SOS.
        // We'll look at this class in a minute.
        if (servletGzipOS == null) {
            servletGzipOS = new GZIPServletOutputStream(getResponse().getOutputStream());
            streamUsed = servletGzipOS;
        }
        return servletGzipOS;
    }
    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        // Allow the servlet to access a print writer only if the
        // servlet has not already accessed the servlet output stream.
        if ((streamUsed != null) && (streamUsed != pw)) {
            throw new IllegalStateException();
        }
        // To make a print writer, we have to first wrap the servlet output stream
        // and then wrap the compression SOS in two additional OS decorators:
        // OutputStreamWriter which converts characters into bytes, and then a
        // PrintWriter on top of the OSWriter object.
        if (pw == null) {
            servletGzipOS = new GZIPServletOutputStream(getResponse().getOutputStream());
            // Use the response charset to create the OSWriter
            OutputStreamWriter osw = new OutputStreamWriter(servletGzipOS,getResponse().getCharacterEncoding());
            // Wrap the OSWriter in the PrintWriter
            pw = new PrintWriter(osw);
            streamUsed = pw;
        }
        return pw;
    }

    /**
     *
     * @param len
     */
    @Override
    public void setContentLength(int len) {
    }

    /**
     *
     * @return
     */
    public GZIPOutputStream getGZIPOutputStream() {
        return this.servletGzipOS.internalGzipOS;
    }

    class GZIPServletOutputStream extends ServletOutputStream {
        GZIPOutputStream internalGzipOS;
        GZIPServletOutputStream(ServletOutputStream sos) throws IOException {
            this.internalGzipOS = new GZIPOutputStream(sos);
        }
        @Override
        public void write(int param) throws IOException {
            internalGzipOS.write(param);
        }
    }
}

