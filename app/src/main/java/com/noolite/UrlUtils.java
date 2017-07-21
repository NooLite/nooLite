package com.noolite;

import com.noolite.settings.SettingsValues;

/**
 * Created by urix on 7/19/2017.
 */

public class UrlUtils {
    private static String PROTOCOL = "http://";
    private static String APP = "/api.htm?";
    private static String CH = "ch=";
    private static String CMD = "&cmd=";
    private static String FM = "&fm=";
    private static String BR = "&br=";

    public static String getSettingsUrl() {
        return new StringBuilder(PROTOCOL)
                .append(SettingsValues.getIP())
                .append("/")
                .append(NooLiteDefs.NOO_SETTINGS_BIN)
                .toString();
    }

    /**
     * http://192.168.0.168:8080/api.htm?ch=0&cmd=8
     *
     * @param ch
     * @param cmd
     * @return url
     */
    public static String getCmdUrl(int ch, int cmd) {
        return new StringBuilder(PROTOCOL)
                .append(SettingsValues.getIP())
                .append(APP)
                .append(CH).append(ch)
                .append(CMD).append(cmd)
                .toString();

    }


    /**
     * "http://" + SettingsValues.getIP() + "/api.htm?ch="
     //						+ (current.getId() - 1) + "&cmd=6&fm=3&br="
     //						+ seekBarDimmedChannel.getProgress();
     * @param ch
     * @param cmd
     * @param fm
     * @param br
     * @return
     */
    public static String getFmBarUrl(int ch, int cmd, int fm, int br) {
        return new StringBuilder(getCmdUrl(ch, cmd))
                .append(FM).append(fm)
                .append(BR).append(br)
                .toString();
    }

    public static String getBarUrl(int ch, int cmd, int br) {
        return new StringBuilder(getCmdUrl(ch, cmd))
                .append(BR).append(br)
                .toString();


    }


}
