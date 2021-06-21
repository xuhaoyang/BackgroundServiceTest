package hk.xhy.backgroundtest.utils;

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/16
 * Time: 2:50 下午
 * Description: No Description
 */
public class Constants {

    private static      int baseNFID        = 1;
    public final static int NF_FOREGROUD_ID = baseNFID++;

    private static      int BASE_JOB_ID      = 1;
    public final static int JOB_APP_START_ID = BASE_JOB_ID++;
    public final static int JOB_TEST_ID      = BASE_JOB_ID++;
    public final static int JOB_CYCLE_ID     = BASE_JOB_ID++;
    public final static int JOB_PERIOD_ID    = BASE_JOB_ID++;


    public static final String EXTRA_MODE_STATIC_SERVICE = "static_service_mode";
    private static      int    BASE_SERVICE_MODE         = 1;
    public static final int    CYCLE_MODE                = BASE_SERVICE_MODE++;
    public static final int    MUST_STATIC_MODE          = BASE_SERVICE_MODE++;

}
