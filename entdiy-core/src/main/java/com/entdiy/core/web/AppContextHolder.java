/**
 * Copyright © 2015 - 2017 EntDIY JavaEE Development Framework
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
package com.entdiy.core.web;

import com.entdiy.core.annotation.MetaData;
import com.entdiy.core.context.SpringPropertiesHolder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletContext;
import java.io.File;

public class AppContextHolder {

    private static Logger logger = LoggerFactory.getLogger(AppContextHolder.class);

    private static String FILE_WRITE_ROOT_DIR;

    /**
     * 设置当前WEB_ROOT根目录到配置属性以便在单纯的Service运行环境取到应用根目录获取WEB-INF下相关资源
     */
    private static String WEB_CONTEXT_REAL_PATH;

    public static void init(ServletContext sc) {
        logger.debug("AppContextHolder init...");
        initFileWriteRootDir(sc);
        WEB_CONTEXT_REAL_PATH = sc.getContextPath();
    }

    @MetaData(value = "开发模式", comments = "更宽松的权限控制，更多的日志信息。详见application.properties配置参数定义")
    private static boolean DEV_MODE = false;

    @MetaData(value = "演示模式", comments = "对演示环境进行特殊控制以避免不必要的随意数据修改导致系统混乱")
    private static boolean DEMO_MODE = false;

    @MetaData(value = "构建版本")
    private static String BUILD_VERSION;

    @MetaData(value = "系统名称")
    private static String SYSTEM_NAME;


    public static boolean isDemoMode() {
        return DEMO_MODE;
    }

    public static boolean isDevMode() {
        return DEV_MODE;
    }

    public static String getBuildVersion() {
        if (isDevMode() && BUILD_VERSION.endsWith("BUILD_NUMBER")) {
            // 开发模式，把版本信息设置为当前时间戳以触发JS、CSS等资源刷新加载
            return BUILD_VERSION.replace("BUILD_NUMBER", String.valueOf(System.currentTimeMillis()));
        } else {
            return BUILD_VERSION;
        }
    }

    public static String getSystemName() {
        return SYSTEM_NAME;
    }

    @Value("${build.version}")
    public void setBuildVersion(String buildVersion) {
        BUILD_VERSION = buildVersion;
        logger.info("System runnging at build.version={}", BUILD_VERSION);
    }

    @Value("${demo.mode:false}")
    public void setDemoMode(String demoMode) {
        DEMO_MODE = BooleanUtils.toBoolean(demoMode);
        logger.info("System runnging at demo.mode={}", DEMO_MODE);
    }

    @Value("${dev.mode:false}")
    public void setDevMode(String devMode) {
        DEV_MODE = BooleanUtils.toBoolean(devMode);
        logger.info("System runnging at dev.mode={}", DEV_MODE);
    }

    @Value("${system.name}")
    public void setSystemName(String systemName) {
        SYSTEM_NAME = systemName;
        logger.info("System name={}", SYSTEM_NAME);
    }

    private static void initFileWriteRootDir(ServletContext sc) {
        FILE_WRITE_ROOT_DIR = SpringPropertiesHolder.getProperty("file.write.dir");
        if (StringUtils.isBlank(FILE_WRITE_ROOT_DIR)) {
            FILE_WRITE_ROOT_DIR = sc.getRealPath("/");
        }
        if (FILE_WRITE_ROOT_DIR.endsWith(File.separator)) {
            FILE_WRITE_ROOT_DIR = FILE_WRITE_ROOT_DIR.substring(0, FILE_WRITE_ROOT_DIR.length() - 1);
        }
        File rootDir = new File(FILE_WRITE_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        logger.info("Init file write root dir: {}", FILE_WRITE_ROOT_DIR);
    }

    public static String getFileWriteRootDir() {
        return FILE_WRITE_ROOT_DIR;
    }

    public static String getWebContextRealPath() {
        return WEB_CONTEXT_REAL_PATH;
    }
}