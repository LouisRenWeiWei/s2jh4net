/**
 * Copyright © 2015 - 2017 EntDIY JavaEE Development Framework
 *
 * Site: https://www.entdiy.com, E-Mail: xautlx@hotmail.com
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
package com.entdiy.core.data;

import com.entdiy.core.exception.ServiceException;
import com.entdiy.core.util.DateUtils;
import com.entdiy.core.web.AppContextHolder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据库数据初始化处理基类
 */
public abstract class AbstractDatabaseDataInitializeProcessor {

    private final static Logger logger = LoggerFactory.getLogger(AbstractDatabaseDataInitializeProcessor.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void initialize() {
        logger.debug("Invoking data process for {}", this);
        try {
            initializeInternal();
        } catch (Exception e) {
            throw new ServiceException("data initialize error", e);
        }
        if (AppContextHolder.isDevMode()) {
            //重置恢复模拟数据设置的临时时间
            DateUtils.setCurrentDateTime(null);
        }
    }

    /**
     * 帮助类方法，从当前类的classpath路径下面读取文本内容为String字符串
     *
     * @param fileName 文件名称
     * @return
     */
    protected String getStringFromTextFile(String fileName) {
        InputStream is = this.getClass().getResourceAsStream(fileName);
        try {
            String text = IOUtils.toString(is, "UTF-8");
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 判定实体对象对应表是否为空
     */
    protected boolean isEmptyTable(Class<?> entity) {
        Object count = entityManager.createQuery("select count(1) from " + entity.getSimpleName()).getSingleResult();
        if (count == null || "0".equals(String.valueOf(count))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 各模板数据初始化内部逻辑
     *
     * @throws Exception
     */
    public abstract void initializeInternal() throws Exception;
}
