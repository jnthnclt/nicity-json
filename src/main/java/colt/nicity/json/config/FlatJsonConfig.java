/*
 * FlatJsonConfig.java.java
 *
 * Created on 12-27-2009 03:42:00 PM
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
package colt.nicity.json.config;

import colt.nicity.json.core.JStringReader;
import colt.nicity.json.core.Jo;
import colt.nicity.core.lang.UFile;
import colt.nicity.core.lang.UText;
import java.io.File;

/**
 * Allows the user to config systems via a flat json file
 */
abstract public class FlatJsonConfig {
    /**
     *
     * @param _config
     */
    abstract public void loadedNewConfig(Jo _config);

    private File configFile;
    private Jo defaultConfig;
    private Jo config;
    private long permissionsLastModified = Long.MIN_VALUE;
    /**
     * 
     * @param _home
     * @param _configFileName
     * @param _defaultConfig
     */
    public FlatJsonConfig(File _home,String _configFileName,Jo _defaultConfig) {
        configFile = new File(_home,"config"+File.separator+_configFileName+".json");
        defaultConfig = _defaultConfig;
    }
    /**
     *
     * @return
     */
    synchronized public Jo getConfig() {
        if (config == null || configFile.exists() && configFile.lastModified() > permissionsLastModified) {
            permissionsLastModified = configFile.lastModified();
            UFile.ensureDirectory(configFile);
            if (configFile.exists()) {
                try {
                    config = new JStringReader(UText.toString(configFile)).readJo(null);
                } catch(Exception x) {
                    x.printStackTrace();
                }
            }
            else {
                UText.saveTextFile(new Object[]{defaultConfig.toString()}, configFile);
            }
            loadedNewConfig(config);
        }
        return config;
    }
    /**
     *
     * @param _config
     */
    synchronized public void setConfig(Jo _config) {
        UText.saveTextFile(new Object[]{_config.toString()}, configFile);
    }
    /**
     * 
     */
    synchronized public void release() {
        config = null;
        permissionsLastModified = Long.MIN_VALUE;
    }
}
