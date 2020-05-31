package com.vikey.webserve;

import java.util.HashMap;

/**
 * 系统常量
 */
public class Constant {
    public static final String DELETE = "1";
    public static final String NOT_DELETE = "0";

    public static final boolean ACCOUNT_ENABLE = true;
    public static final boolean ACCOUNT_UNENABLE = false;

    public static final String FAST_TASK_NAME_PREFIX = "快速翻译 ";

    public static final String ANNEXE_STATUS_UNPROCESSED = "0";
    public static final String ANNEXE_STATUS_PROCESSED = "1";

    public static final Integer PAGESIZE = 10;

    public static final HashMap<String, String> LANGUAGE_ZH = new HashMap() {{
        put("zh", "中文");
        put("en", "英文");
        put("vi", "越南文");
        put("th", "泰文");
    }};

    public static final HashMap<String, String> ANNEXE_STATUS_ZH = new HashMap() {{
        put("0", "未完成");
        put("1", "已完成");
    }};
}
