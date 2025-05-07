package org.figuramc.figurasvcportrait.config;

import org.figuramc.figura.config.ConfigType;

public class Config {
    public static final ConfigType.BoolConfig SVC_PORTRAIT = 
    new ConfigType.BoolConfig("svc_portrait", org.figuramc.figura.config.Configs.UI, true), 
    SVC_NAMEPLATE = new ConfigType.BoolConfig("svc_nameplate", org.figuramc.figura.config.Configs.UI, true);
}
