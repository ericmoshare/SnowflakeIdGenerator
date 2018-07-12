package org.ericmoshare.uidgenerator.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author eric.mo
 * @since 2018/6/6
 */
public class RedisConfig implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    private String password;

    public String getValidPassword() {
        return StringUtils.trimToNull(password);
    }

    public String trimToNullString() {
        if (StringUtils.isBlank(password)) {
            return "null";
        }
        return password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        log.info("trim password from [{}] to [{}]", password, StringUtils.trimToNull(password));
        this.password = StringUtils.trimToNull(password);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
