package jp.yuruga.common;

/**
 * Created by maeda on 2014/09/18.
 */
public class Constants {

    //data
    public static final String PATH_GAME_DATA = "/game_data";
    public static final String KEY_ME = "me";
    public static final String KEY_ENEMIES = "enemies";
    public static final String KEY_META = "meta";

    //message event paths
    public static final String EVENT_ENTER_OUTER = "/enter_outer";
    public static final String EVENT_LEAVE_OUTER = "/leave_outer";
    public static final String EVENT_ENTER_INNER = "/enter_inner";
    public static final String EVENT_LEAVE_INNER = "/leave_inner";
    public static final String EVENT_CAUGHT = "/caught";
    public static final String EVENT_LOCKED_ON = "/locked_on";
    public static final String EVENT_OFF_LOCKED = "/off_locked";
    public static final String EVENT_CATCH_RESULT = "/catch_result";
    public static final String ACTION_LOCK_ON = "/try_lockon";
    public static final String EVENT_LOCK_ON_RESULT = "/lockon_result";
    //TODO: wearable api key and path will be shared here.


}
