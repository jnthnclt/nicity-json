/*
 * HelloJSONClient.java.java
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
package com.colt.nicity.json.client.example;

import com.colt.nicity.json.client.IJSONService;
import com.colt.nicity.json.client.JSONServices;
import com.colt.nicity.json.core.Jo;
import com.colt.nicity.json.core.UJson;
import com.colt.nicity.core.process.IAsyncResponse;
import com.colt.nicity.core.lang.IOut;
import com.colt.nicity.core.lang.SysOut;

/**
 *
 * @author Administrator
 */
public class HelloJSONClient {
    /**
     *
     * @param _args
     */
    public static void main(String[] _args) {
        IOut _ = new SysOut();
        IJSONService service = JSONServices.service("User","Password","http://localhost:8080/adk-web-json/HelloJSONServlet");
        
        try {
            Jo jo = new Jo();
            UJson.add(jo,"secretkey","Unknown");
            UJson.add(jo,"username", "User");
            UJson.add(jo,"password","Password");
            UJson.add(jo,"data","Hello JSON Servlet");

            service.request(_, jo, new IAsyncResponse<Jo>() {
                @Override
                public void response(IOut _,Jo json) {
                    System.out.println(json.toString());
                }
                @Override
                public void error(IOut _,Throwable _t) {
                    _t.printStackTrace();
                }
            });
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
