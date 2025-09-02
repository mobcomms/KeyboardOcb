package com.enliple.keyboard;

public class Test {
    public static final String TEMP_JSON = "{\n" +
            "    \"streamings\": {\n" +
            "        \"id\": \"100201\",\n" +
            "        \"linkUrl\": \"ocbt://com.skmc.okcashbag.home_google/detail/event?url=https%3A%2F%2Fevent.okcashbag.com%2Fevent%2Fzone%2FolabangCheckAttendance%2F169150%2FmainPage.mocb&title=%EC%98%A4!%EB%9D%BC%EB%B0%A9\",\n" +
            "        \"startDate\": 1615129200000,\n" +
            "        \"endDate\": 1641048600000,\n" +
            "        \"liveBoards\": [\n" +
            "            {\n" +
            "                \"eventId\": 1102,\n" +
            "                \"title\": \"오라방 테스트 3(방송 타이틀)\",\n" +
            "                \"imageUrl\": \"http://ocbpoc-ved.skmcgw.com/upload/olabang/20210302093813_031644997.png\",\n" +
            "                \"linkUrl\": \"ocbt://com.skmc.okcashbag.home_google/detail/event?url=https%3A%2F%2Fdev-webview.okcashbag.com%2Fv1.0%2Folabang%2Fdetail.html%3Fid%3D2346&eventId=1102\",\n" +
            "                \"startDate\": 1615906800000,\n" +
            "                \"endDate\": 1617375300000,\n" +
            "                \"episodeId\": \"2346\",\n" +
            "                \"pointText\": \"10P\",\n" +
            "                \"benefitText\": \"20% 할인\",\n" +
            "                \"liveType\": \"J\",\n" +
            "                \"isSoldOut\": false,\n" +
            "                \"liveStartDate\": 1615957200000,\n" +
            "                \"liveEndDate\": 1615962600000,\n" +
            "                \"createDate\": 1612769973000,\n" +
            "                \"isMaster\": true\n" +
            "            },\n" +
            "            {\n" +
            "                \"eventId\": 1103,\n" +
            "                \"title\": \"페스티벌 2일간 50%할인\",\n" +
            "                \"imageUrl\": \"http://ocbpoc-ved.skmcgw.com/upload/olabang/20210302093848_782775154.png\",\n" +
            "                \"linkUrl\": \"ocbt://com.skmc.okcashbag.home_google/detail/event?url=https%3A%2F%2Fdev-webview.okcashbag.com%2Fv1.0%2Folabang%2Fdetail.html%3Fid%3D1414&eventId=1103\",\n" +
            "                \"startDate\": 1615906800000,\n" +
            "                \"endDate\": 1616770500000,\n" +
            "                \"episodeId\": \"1414\",\n" +
            "                \"benefitText\": \"50% 할인\",\n" +
            "                \"liveType\": \"J\",\n" +
            "                \"isSoldOut\": false,\n" +
            "                \"liveStartDate\": 1615968000000,\n" +
            "                \"liveEndDate\": 1615971600000,\n" +
            "                \"createDate\": 1612849461000,\n" +
            "                \"isMaster\": false\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    public static final String TEST_TIME="{\n" +
            "    \"noti\": [\n" +
            "        {\n" +
            "            \"seq\": \"670\",\n" +
            "            \"time\": \"10:32\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"seq\": \"669\",\n" +
            "            \"time\": \"16:34\"\n" +
            "        },\n" +
            "{\n" +
            "            \"seq\": \"679\",\n" +
            "            \"time\": \"19:24\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    public static final String TEST_FREQ = "{\"ratio\":\"90\",\"mobon\":\"2,4,6,8,0\",\"mediation\":\"1,3,5,7,9\",\"banner\":\"-1\",\"coupang\":\"-1\",\"notice\":\"-1\",\"criteo\":\"-1\",\"reward\":\"-1\"}";

    public static final String TEST_CRITEO = "{\n" +
            "    \"image_url\": \"https://pix.as.criteo.net/img/img?c=3&cq=256&h=400&m=0&partner=66997&q=80&r=0&u=http%3A%2F%2Fimg.shoppingntmall.com%2Fgoods%2F867%2F11989867_h.jpg&ups=1&v=3&w=400&s=bkqdSH1NAz4538dxsNHJ-PIE\",\n" +
            "    \"click_url\": \"https://cat.jp2.as.criteo.com/delivery/ckn.php?cppv=3&cpp=6O4LY1D0HqflkHWBUQhnkOyD3UIrUeJzArlo0nKhDpGixJjOJvkJVlo--dwhdMKFnG21LAFh1OiC0CoDrVEMCAcm2f3YDcLTo1cvQY8Swj9nr59HfC9s32lpgqBOorBuOpYCSTbP-SicyLImaCkiQLan0lQV9tqbxmrRCXXrVXBi5Dxyayyyi1rn12is4vJqyKiQt8RE56V8rvrFw5UDZgK-NsvDfRZj5qSqfhkLaLTFN8Zzy3IX_HJSPv6T7UCyHbBsnk2K9iWGWRqFZxMp51pt0q6l2xWSTbnoi-qMgmLKjMEHogDHl65LzlS-JxdCplhlPFrQ76RjBr3TjuOunF7LxtNcWnWCfED_7I_vMHtsc1ziQXVoY27O1PzPQZaXYdhRzFfZ7FNb_aj21h51F1vvg5DwMZfYNSUNmNbj89WESHn_Y7DvBrgJdHvzj8FAbqbg4n_jyV3Tp4oooaZfHUmDo7tWmbHJoqu87w9cDbOqh3qs&maxdest=http%3A%2F%2Fm.shoppingntmall.com%2Finterfaces%2Fgoods%2F11989867%3Futm_source%3Dcriteo%26utm_medium%3Dda%26utm_term%3Dconsideration%26utm_campaign%3Dconsidercam\",\n" +
            "    \"title\": \"국내산 달콤왕 꿀자두 3kg 특과 (개당...\",\n" +
            "    \"description\": \"국내산 달콤왕 꿀자두 3kg 특과 (개당 90g내외)\",\n" +
            "    \"price\": \"20,810원\",\n" +
            "    \"imp_trackers\": [\n" +
            "      \n" +
            "    ],\n" +
            "    \"view_notice_tracker\": \"https://cat.jp2.as.criteo.com/delivery/lgn.php?cppv=3&cpp=CaQwIlD0HqflkHWBUQhnkOyD3UIrUeJzArlo0nKhDpGixJjOJvkJVlo--dwhdMKFnG21LAFh1OiC0CoDrVEMCAcm2f3YDcLTo1cvQY8Swj9nr59HfC9s32lpgqBOorBuOpYCSTbP-SicyLImaCkiQLan0lTNXGmQ79MDCF1_m0r8fqhgXF8OaTtU1ou4lK25LDfKVz16D8Rox44KxcwgwbDp1gWbw2fnSIxynf82Bbl5pDCxVVWjtrIE_43oja1kpWwvYX-us_gFvB9Ojqde9MqfcnywmCU5DgSFoUZ_h-cLHwOGtZRX7ZswiRRuF56sXoWJ31aFuOITBltDmx9G5SI39dCq8Ulgh4UA3B7ahbZGLffzTbE_uRRym5FURukhGnwoB6YOUdGl-NYJL-wXFHlZJ0qOkwMftsS0KJ2iXetV15E2iym9na3CBxWhkYcMQr-OpQ\",\n" +
            "    \"adchoices\": {\n" +
            "      \"image_url\": \"https://static.criteo.net/flash/icon/nai_small.png\",\n" +
            "      \"click_url\": \"https://privacy.as.criteo.com/adchoices?cppv=3&cpp=kbj6-n3R3IpC5Fu7sHcnvtqiA1YP9q4FRC0kN6voRqVZJADDAlCNwCVpNRiLS7V8RtMnG0ZemIutNyJV1UoGvRSCZgeGVT1dthtkny_HPQjunFxeJr-JjdlcjDM01fwgb45HGBnp4zEiLM5djWnifd3d7cf2edQc_zT-SGNVrgMoPAWP\"\n" +
            "    },\n" +
            "    \"logo_url\": \"https://pix.as.criteo.net/img/img?h=1200&m=0&partner=66997&q=80&r=0&u=http%3A%2F%2Fstatic.jp2.as.criteo.net%2Fdesign%2Fdt%2F66997%2F200121%2Fd7ae2c094f8b4b868f02a7b867ca39a9_logo_all_square-shoppingntmall.png&v=3&w=1200&s=3UGU-Q-_WPU5Hhe_Og8I9_ZO\",\n" +
            "    \"cta\": \"바로구매\"\n" +
            "}";

    public static final String TEST_COUPANG_DY = "{\"rCode\":\"0\",\"rMessage\":\"일부 상품이 내부 정책에 의해 필터링 되었습니다.\",\"data\":[\n" +
            "{\"productId\":6388815744,\"productName\":\"궁중비책 유아용 프리뮨 크리미 솝\",\"productPrice\":11730,\"productImage\":\"https:\\/\\/static.coupangcdn.com\\/image\\/retail\\/images\\/2022\\/03\\/11\\/15\\/8\\/ab0fe784-963d-44e7-970e-53bcab82f33e.jpg\",\"productUrl\":\"https:\\/\\/link.coupang.com\\/re\\/AFFSDP?lptag=AF6181865&subid=skocbkbdy&pageKey=6388815744&itemId=13609110244&vendorItemId=80862132009&traceid=V0-193-6f98e074d6421f16&requestid=20221012165119342179164\",\"isRocket\":false}]}";
    public static final String TEST_NEW_COUPANG = "{\n" +
            "\"Result\":\"true\",\n" +
            "\"adData\": {\n" +
            "\"productId\":\"78603903\",\n" +
            "\"image\":\"https:\\/\\/static.coupangcdn.com\\/image\\/product\\/image\\/vendoritem\\/2019\\/04\\/15\\/3617503487\\/7bf0cd7a-da67-4f64-bd17-6892746cef83.jpg\",\n" +
            "\"title\": \"닥터아토 심플클렌저\",\n" +
            "\"description\": \"\",\n" +
            "\"price\": \"5,520원\",\n" +
            "\"adchoices\": {\n" +
            "    \"image_url\": \"\",\n" +
            "    \"click_url\": \"\"\n" +
            "},\n" +
            "\"click_url\": \"https:\\/\\/link.coupang.com\\/re\\/AFFSDP?lptag=AF6181865\",\n" +
            "\"logo_url\": \"https://post-phinf.pstatic.net/MjAxNzAzMTBfNzcg/MDAxNDg5MTMwNzM2NDg0.DOSK4d8epq-aVS4QIZgMspqVB1faKyXNyefJeUWXkfMg.98L5PNMyXe46tY7j9ZVSNbsdJ89-RtGSNM63V_4kXHAg.JPEG/%EB%A1%9C1.jpg?type=w1200\",\n" +
            "\"kind\":\"coupang\",\n" +
            "\"type\":\"product\"\n" +
            "}\n" +
            "}";
    public static final String TEST_NEW_CRITEO = "{\n" +
            "\"Result\":\"true\",\n" +
            "\"adData\": {\n" +
            "\"productId\":\"78603903\",\n" +
            "\"image\":\"https://pix.as.criteo.net/img/img?c=3&cq=256&h=400&m=0&partner=82635&q=80&r=0&u=https%3A%2F%2Fimgb.a-bly.com%2Fdata%2Fgoods%2F20221017_1666015136404592m.jpg&ups=1&v=3&w=400&s=KYXowVHQY21G-GYuzCZRQZDi\",\n" +
            "\"title\": \"루즈핏 반 집업 니트\",\n" +
            "\"description\": \"전 상품 무료배송\",\n" +
            "\"price\": \"41,500원\",\n" +
            "\"adchoices\": {\n" +
            "    \"image_url\": \"https://static.criteo.net/flash/icon/nai_small.png\",\n" +
            "    \"click_url\": \"https://privacy.as.criteo.com/adchoices?cppv=3&cpp=n3vsqMYbSWjrTFlnT9XGuNjeHp8VYeGkAW4dZhpJJSfE1UUq7c8WKj4-68A9flRFKJjPGgVS1QPqkkaembh3x8JtldN-JMvrVACeGy2SK9Ih53xsICB0SgjAQLQB95SM18Bv9DvCqAr4FB3d8unLUtyywtI\"\n" +
            "},\n" +
            "\"click_url\": \"https://cat.jp2.as.criteo.com/delivery/ckn.php?cppv=3&cpp=S3R4zFD0HqflkHWBUQhnkOyD3UIrUeJzArlo0nKhDpGixJjOJvkJVlo--dwhdMKFnG21LL_mYcM39zgi9ka28GNiDm6DLzdxQjZoC6TDU8LcYofV_AtxY9ECV8yMNiArcGJYPGzXfrPSFqfIWBYkBKTMerlDMLNHkKHk4Ds7hBECbBI9_NQCD74saUqPAAZx13DtZDCGJ7w3EHMYuNSRNXyMR0zF3csHdknhOTiTK36XfxQoZ6VMS5wgyb3aoavYlrQ48vcbvrfJb4sdkNc6UVD4FXD-z11BeMTMJ1Tzsdkfse5yY9ffDKxHQHavHF_sjOWA_2mDEQFyCSN6J13jyByCV9XMteYn0EBdkIiaHJ037q6-QUOSWlDpyNxDwRzi6RvqCGWTimqmecvdDfsb56kEawISuT6cGoDyRJ7HKoiZXOXUF3FQfIDMyAlfvN2TgZTT6MbEQ9JYVNTwTjgjdibrCR6k1ZMrjTcRr0Lr50emQyiW&maxdest=https%3A%2F%2Fwww.shoppingntmall.com%2Fdisplay%2Fgoods%2F10227849%3F%26utm_source%3Dcriteo%26utm_medium%3Dda%26utm_term%3Dconsideration%26utm_campaign%3Dconsidercam\",\n" +
            "\"logo_url\": \"\",\n" +
            "\"kind\":\"criteo\",\n" +
            "\"type\":\"product\"\n" +
            "}\n" +
            "}";
    public static final String TEST_NEW_BANNER = "{\n" +
            "\"Result\":\"true\",\n" +
            "\"adData\": {\n" +
            "\"productId\":\"\",\n" +
            "\"image\":\"https://img.mobon.net/servlet/image/otjalnam/720x120.jpg\",\n" +
            "\"title\": \"\",\n" +
            "\"description\": \"\",\n" +
            "\"price\": \"\",\n" +
            "\"adchoices\": {\n" +
            "    \"image_url\": \"https:\\/\\/img.mobon.net\\/newAd\\/img\\/logoImg\\/mobonLogo02.png\",\n" +
            "    \"click_url\": \"https://www.naver.com\"\n" +
            "},\n" +
            "\"click_url\": \"https://img.mobon.net/oasisFeed/oasisFeed.html\",\n" +
            "\"logo_url\": \"\",\n" +
            "\"kind\":\"test\",\n" +
            "\"type\":\"banner\"\n" +
            "}\n" +
            "}";

    public static final String COUPANG_TEST_NO_DATA = "{\n" +
            "    \"rCode\": \"0\",\n" +
            "    \"rMessage\": \"게시글 작성 시, \\\"파트너스 활동을 통해 일정액의 수수료를 제공받을 수 있음\\\"을 기재하셔야 합니다\",\n" +
            "    \"data\": \"\"\n" +
            "}";

    public static final String TEST_SURPRISE_DATA = "{\"Result\":\"true\",\"data\":[{\"icon\":\"https:\\/\\/okcashbag.cashkeyboard.co.kr\\/img\\/spot\\/s_point_icon_keyboard.png\",\"title\":\"키보드 사용\",\"point\":\"0\"},{\"icon\":\"https:\\/\\/okcashbag.cashkeyboard.co.kr\\/img\\/spot\\/s_point_icon_news.png\",\"title\":\"뉴스\",\"point\":\"0\"},{\"icon\":\"https:\\/\\/okcashbag.cashkeyboard.co.kr\\/img\\/spot\\/s_point_icon_banner.png\",\"title\":\"배너 광고\",\"point\":\"0\"},{\"icon\":\"https:\\/\\/okcashbag.cashkeyboard.co.kr\\/img\\/spot\\/s_point_icon_save_shopping.png\",\"title\":\"쇼핑적립\",\"point\":\"0\"},{\"icon\":\"https:\\/\\/okcashbag.cashkeyboard.co.kr\\/img\\/spot\\/s_point_icon_brand.png\",\"title\":\"브랜드 광고\",\"point\":\"0\"}]}";
    public static final String TEST_OFFERWALL_LIST_EMPTY = "{\"result\":0,\"total_user_point\":0.0,\"mission\":[],\"total_count\":0}";
    public static final String TEST_P2_OBJECT = "{\n" +
            "\"mission_class\":\"P2\",\n" +
            "\"intro_img\":\"https:\\/\\/www.pomission.com\\/attach\\/mission\\/mission_thumbnail.png\",\n" +
            "\"mission_id\":\"c7a2a7c5-f946-4341-a599-2d6142c8ca50\",\n" +
            "\"daily_participation_cnt\":1,\n" +
            "\"adver_name\":\"수타이아로마 중문점\",\n" +
            "\"reg_date\":\"2022-11-28T02:56:45.000+00:00\",\n" +
            "\"media_point\":60,\n" +
            "\"daily_participation\":100,\n" +
            "\"user_point\":10,\n" +
            "\"mission_seq\":311,\n" +
            "\"keyword\":\"중문마사지\",\n" +
            "\"thumb_img\":\"https:\\/\\/www.pomission.com\\/attach\\/mission\\/mission_thumbnail.png\",\n" +
            "\"check_url\":\"https://m.smartstore.naver.com/ipodshop/products/5005633608?NaPm=ct%3Dlb0e65yo%7Cci%3Db54dabf8c76360af1e4ce832e3ff82fd011708bb%7Ctr%3Dslsl%7Csn%3D863284%7Chk%3Da111279464050c5a5a308f0b05b7851362de84c2\",\n" +
            "\"check_time\":10,\n" +
            "\"adver_url\":\"https:\\/\\/m.place.naver.com\\/restaurant\\/1098761877\\/home?entry=pll\"\n" +
            "}";
    public static final String TEST_P2_OBJECT1 = "{\n" +
            "      \"mission_class\": \"P2\",\n" +
            "      \"intro_img\": \"https://www.pomission.com/attach/mission/mission_thumbnail.png\",\n" +
            "      \"mission_id\": \"30d9142d-d770-42b8-bd02-7321e260a58b\",\n" +
            "      \"daily_participation_cnt\": 0,\n" +
            "      \"adver_name\": \"청담해정\",\n" +
            "      \"reg_date\": \"2022-11-30T09:36:22.000+00:00\",\n" +
            "      \"media_point\": 3,\n" +
            "      \"daily_participation\": 100,\n" +
            "      \"user_point\": 1,\n" +
            "      \"mission_seq\": 410,\n" +
            "      \"keyword\": \"청담동 맛집\",\n" +
            "      \"thumb_img\": \"https://www.pomission.com/attach/mission/mission_thumbnail.png\",\n" +
            "      \"check_url\": \"https://m.place.naver.com/restaurant/1006524862/home\",\n" +
            "      \"check_time\": 10,\n" +
            "      \"adver_url\": \"https://m.search.naver.com/search.naver?query=%EC%B2%AD%EB%8B%B4%EB%8F%99%EB%A7%9B%EC%A7%91+%EC%B2%AD%EB%8B%B4%ED%95%B4%EC%A0%95&sm=mtp_hty.top&where=m\"\n" +
            "    }";

    public static final String TEST_P3_OBJECT = "{\n" +
            "      \"mission_class\": \"P3\",\n" +
            "      \"target_name\": \"충성 핫팩 손난로 발난로 150g\",\n" +
            "      \"mission_id\": \"fff17242-d52d-4af0-9c4a-c4db4a3170e1\",\n" +
            "      \"daily_participation_cnt\": 0,\n" +
            "      \"adver_name\": \"BRAND ZERO\",\n" +
            "      \"reg_date\": \"2022-12-08T03:03:46.000+00:00\",\n" +
            "      \"media_point\": 6,\n" +
            "      \"daily_participation\": 1000,\n" +
            "      \"user_point\": 3,\n" +
            "      \"mission_seq\": 560,\n" +
            "      \"keyword\": \"쇼핑트레픽 테스트\",\n" +
            "      \"thumb_img\": \"https://www.pomission.com/attach/mission/mission_thumbnail.png\",\n" +
            "      \"check_url\": \"https://smartstore.naver.com/cmad/products/7465894561\",\n" +
            "      \"check_time\": 10,\n" +
            "      \"adver_url\": \"https://msearch.shopping.naver.com/search/all?query=%EC%B6%A9%EC%84%B1%20%ED%95%AB%ED%8C%A9%20%EC%86%90%EB%82%9C%EB%A1%9C%20%EB%B0%9C%EB%82%9C%EB%A1%9C%20150g&frm=NVSHSRC&prevQuery=%EC%B6%A9%EC%84%B1%20%ED%95%AB%ED%8C%A9%20%EC%86%90%EB%82%9C%EB%A1%9C%20%EB%B0%9C%EB%82%9C%EB%A1%9C%20150g\"\n" +
            "    }";
    public static final String TEST_P3_OBJECT1 = "{\n" +
            "      \"mission_class\": \"P3\",\n" +
            "      \"target_name\": \"자가진단 코로나키트 레피젠 신속항원 20회\",\n" +
            "      \"mission_id\": \"599385db-3e8e-49c8-877f-833e283f79dd\",\n" +
            "      \"daily_participation_cnt\": 0,\n" +
            "      \"adver_name\": \"BRAND ZERO\",\n" +
            "      \"reg_date\": \"2022-12-08T03:02:20.000+00:00\",\n" +
            "      \"media_point\": 6,\n" +
            "      \"daily_participation\": 1500,\n" +
            "      \"user_point\": 3,\n" +
            "      \"mission_seq\": 559,\n" +
            "      \"keyword\": \"쇼핑트레픽 테스트\",\n" +
            "      \"thumb_img\": \"https://www.pomission.com/attach/mission/mission_thumbnail.png\",\n" +
            "      \"check_url\": \"https://smartstore.naver.com/cmad/products/6995530102\",\n" +
            "      \"check_time\": 10,\n" +
            "      \"adver_url\": \"https://msearch.shopping.naver.com/search/all?query=%EC%9E%90%EA%B0%80%EC%A7%84%EB%8B%A8%20%EC%BD%94%EB%A1%9C%EB%82%98%ED%82%A4%ED%8A%B8%20%EB%A0%88%ED%94%BC%EC%A0%A0%20%EC%8B%A0%EC%86%8D%ED%95%AD%EC%9B%90%2020%ED%9A%8C&frm=NVSHSRC&prevQuery=%EC%B6%A9%EC%84%B1%20%ED%95%AB%ED%8C%A9%20%EC%86%90%EB%82%9C%EB%A1%9C%20%EB%B0%9C%EB%82%9C%EB%A1%9C%20150g\"\n" +
            "    }";
    public static final String TEST_P3_OBJECT2 = "{\n" +
            "      \"mission_class\": \"P3\",\n" +
            "      \"target_name\": \"잡티 미백 글루타치온 비타민C 앰플 30ml\",\n" +
            "      \"mission_id\": \"2c1feaed-dd6b-4cd3-a4e8-0ddf62c3aa0b\",\n" +
            "      \"daily_participation_cnt\": 0,\n" +
            "      \"adver_name\": \"BRAND ZERO\",\n" +
            "      \"reg_date\": \"2022-12-08T03:00:45.000+00:00\",\n" +
            "      \"media_point\": 6,\n" +
            "      \"daily_participation\": 400,\n" +
            "      \"user_point\": 3,\n" +
            "      \"mission_seq\": 558,\n" +
            "      \"keyword\": \"쇼핑트레픽 테스트\",\n" +
            "      \"thumb_img\": \"https://www.pomission.com/attach/mission/mission_thumbnail.png\",\n" +
            "      \"check_url\": \"https://smartstore.naver.com/cmad/products/7656762688\",\n" +
            "      \"check_time\": 10,\n" +
            "      \"adver_url\": \"https://msearch.shopping.naver.com/search/all?query=%EC%9E%A1%ED%8B%B0%20%EB%AF%B8%EB%B0%B1%20%EA%B8%80%EB%A3%A8%ED%83%80%EC%B9%98%EC%98%A8%20%EB%B9%84%ED%83%80%EB%AF%BCC%20%EC%95%B0%ED%94%8C%2030ml&frm=NVSHSRC&prevQuery=%EC%9E%90%EA%B0%80%EC%A7%84%EB%8B%A8%20%EC%BD%94%EB%A1%9C%EB%82%98%ED%82%A4%ED%8A%B8%20%EB%A0%88%ED%94%BC%EC%A0%A0%20%EC%8B%A0%EC%86%8D%ED%95%AD%EC%9B%90%2020%ED%9A%8C\"\n" +
            "    }";
    public static final String TEST_P3_OBJECT3 = "{\n" +
            "      \"mission_class\": \"P3\",\n" +
            "      \"target_name\": \"원진이펙트 셀쎄라 진정 재생크림 50ml\",\n" +
            "      \"mission_id\": \"80789e4f-6f9f-4450-ab20-3751d743985d\",\n" +
            "      \"daily_participation_cnt\": 0,\n" +
            "      \"adver_name\": \"BRAND ZERO\",\n" +
            "      \"reg_date\": \"2022-12-08T02:59:43.000+00:00\",\n" +
            "      \"media_point\": 6,\n" +
            "      \"daily_participation\": 300,\n" +
            "      \"user_point\": 3,\n" +
            "      \"mission_seq\": 557,\n" +
            "      \"keyword\": \"쇼핑트레픽 테스트\",\n" +
            "      \"thumb_img\": \"https://www.pomission.com/attach/mission/mission_thumbnail.png\",\n" +
            "      \"check_url\": \"https://smartstore.naver.com/cmad/products/7654703828\",\n" +
            "      \"check_time\": 10,\n" +
            "      \"adver_url\": \"https://msearch.shopping.naver.com/search/all?query=%EC%9B%90%EC%A7%84%EC%9D%B4%ED%8E%99%ED%8A%B8%20%EC%85%80%EC%8E%84%EB%9D%BC%20%EC%A7%84%EC%A0%95%20%EC%9E%AC%EC%83%9D%ED%81%AC%EB%A6%BC%2050ml&frm=NVSHSRC&prevQuery=%EC%9B%90%EC%A7%84%EC%9D%B4%ED%8E%99%ED%8A%B8%20%EC%85%80%EC%8E%84%EB%9D%BC%20%EC%A7%84%EC%A0%95%20%EC%9E%AC%EC%83%9D%ED%81%AC%EB%A6%BC%2050ml\"\n" +
            "    }";
    public static final String TEST_P3_OBJECT4 = "{\n" +
            "      \"mission_class\": \"P3\",\n" +
            "      \"target_name\": \"원진이펙트 히알루론산 진정 수분크림 100ml\\t\",\n" +
            "      \"mission_id\": \"823956b2-e69d-49ce-80b9-7270bb7a6157\",\n" +
            "      \"daily_participation_cnt\": 0,\n" +
            "      \"adver_name\": \"BRAND ZERO\",\n" +
            "      \"reg_date\": \"2022-12-08T02:58:40.000+00:00\",\n" +
            "      \"media_point\": 6,\n" +
            "      \"daily_participation\": 250,\n" +
            "      \"user_point\": 3,\n" +
            "      \"mission_seq\": 556,\n" +
            "      \"keyword\": \"쇼핑트레픽 테스트\",\n" +
            "      \"thumb_img\": \"https://www.pomission.com/attach/mission/mission_thumbnail.png\",\n" +
            "      \"check_url\": \"https://smartstore.naver.com/cmad/products/7654522025\",\n" +
            "      \"check_time\": 10,\n" +
            "      \"adver_url\": \"https://msearch.shopping.naver.com/search/all?query=%EC%9B%90%EC%A7%84%EC%9D%B4%ED%8E%99%ED%8A%B8%20%ED%9E%88%EC%95%8C%EB%A3%A8%EB%A1%A0%EC%82%B0%20%EC%A7%84%EC%A0%95%20%EC%88%98%EB%B6%84%ED%81%AC%EB%A6%BC%20100ml%09&frm=NVSHSRC&prevQuery=%EC%9B%90%EC%A7%84%EC%9D%B4%ED%8E%99%ED%8A%B8%20%EC%85%80%EC%8E%84%EB%9D%BC%20%EC%A7%84%EC%A0%95%20%EC%9E%AC%EC%83%9D%ED%81%AC%EB%A6%BC%2050ml\"\n" +
            "    }";
}
