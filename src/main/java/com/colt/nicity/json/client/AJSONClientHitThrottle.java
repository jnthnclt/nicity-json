/*
 * AJSONClientHitThrottle.java.java
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
import com.colt.nicity.json.core.UJson;
import com.colt.nicity.core.process.IAsyncResponse;
import com.colt.nicity.core.lang.IOut;
import com.colt.nicity.core.memory.SoftIndex;
import com.colt.nicity.core.time.UTime;

/**
 *
 * @author Administrator
 */
public class AJSONClientHitThrottle {
    private static SoftIndex softThrottles = new SoftIndex();
    /**
     *
     * @param _serviceURL
     * @return
     */
    public static AJSONClientHitThrottle get(String _serviceURL) {
        AJSONClientHitThrottle t = (AJSONClientHitThrottle)softThrottles.get(_serviceURL);
        if (t == null) {
            t = new AJSONClientHitThrottle(_serviceURL);
            softThrottles.set(t,_serviceURL);
        }
        return t;
    }

    String serviceURL;
    long expires;
    long remaining;
    long lastCanHit;
    private AJSONClientHitThrottle(String _serviceURL) {
        serviceURL = _serviceURL;
    }
    /**
     *
     * @param _
     * @return
     * @throws Exception
     */
    synchronized public boolean returnWhenICanHit(IOut _) throws Exception {
        long now = System.currentTimeMillis();
        if (now > expires) {
            update(_);
        }
        long pauseBetweenHits = (expires-now)/remaining;
        now = System.currentTimeMillis();
        if (now-lastCanHit < pauseBetweenHits) {
            long sleep = pauseBetweenHits-(now-lastCanHit);
            System.out.println("Sleeping:"+UTime.elapse(sleep));
            Thread.sleep(sleep);
        }
        remaining--;
        lastCanHit = System.currentTimeMillis();
        System.out.println("("+remaining+") remaining until "+UTime.basicTime(expires));
        return true;
    }
    /**
     *
     * @param _
     * @throws Exception
     */
    public void update(IOut _) throws Exception {
        IJSONService service = JSONServices.service("User","Password",serviceURL);
        
        Jo request = new Jo();
        UJson.add(request,"secretkey","Unknown");
        UJson.add(request,"username","User");
        UJson.add(request,"password","Password");
        UJson.add(request,"request","ratelimit");

        service.request(_,request, new IAsyncResponse<Jo>() {
            @Override
            public void response(IOut _,Jo response) {
                try {
                    expires = UJson.getLong(response,"expires");
                    remaining = UJson.getLong(response,"remaining");
                } catch(Exception x) {
                    error(_,x);
                }
            }
            @Override
            public void error(IOut _,Throwable _t) {
                _t.printStackTrace();
            }
        });

    }
}
