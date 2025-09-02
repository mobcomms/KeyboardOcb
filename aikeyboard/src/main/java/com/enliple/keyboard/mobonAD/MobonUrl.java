package com.enliple.keyboard.mobonAD;

public class MobonUrl {
    public static final String DOMAIN_ROOT = "www.mediacategory.com";
    public static String DOMAIN_PROTOCOL = "https://";
    protected static final String V20_SERVLET_API = "/servlet/API/ver2.0/JSON/sNo/";
    protected static final String V30_SERVLET_API = "/servlet/API/ver3.0/JSON/sNo/";
    protected static final String API_AUID = DOMAIN_ROOT + "/servlet/auid";
    protected static final String API_MOBILE_BANNER = DOMAIN_ROOT + "/servlet/adbnMobileBanner";
    protected static final String API_AD_IMPRESSION = DOMAIN_ROOT + V30_SERVLET_API;
    protected static final String API_MOBILE_VIDEO = DOMAIN_ROOT + "/servlet/adPlayLinkBanner";
    protected static final String API_SDK_INFO = "addata.mediacategory.com/media/sdkScriptInfo/";
    protected static final String API_PACKAGE_GATHER = DOMAIN_ROOT + "/servlet/MSDKAppPackageGather";
    public static final String API_MOBON_BACON_URL_LIST = DOMAIN_ROOT + V20_SERVLET_API;
    protected static final String API_MOBON_BACON_INFO = DOMAIN_ROOT + V20_SERVLET_API;
    protected static final String API_MOBON_BACON_INSTALL_LOG = DOMAIN_ROOT + "/api/bacon/sdkLog/";
    protected static final String MOBON_IMAGE_URL = "img.mobon.net/ad/imgfile/";

}