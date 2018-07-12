package org.ericmoshare.uidgenerator.idgenerator;

import org.ericmoshare.uidgenerator.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Types;

/**
 * @author eric.mo
 */
public class MysqlSnowflakeIdGenerator extends AbstractSnowflakeIdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MysqlSnowflakeIdGenerator.class);

    private static final String QUERY_SERVERNO_SQL = "select id from my_server where server_mac = ?";

    private static final String CREATE_SERVERNO_SQL = "INSERT INTO my_server(SERVER_MAC, SERVER_IP) VALUES(?,?)";

    private static volatile Long serverNo = 0L;

    private JdbcTemplate jdbcTemplate;
    private String appName;

    public MysqlSnowflakeIdGenerator(JdbcTemplate jdbcTemplate, String appName) {
        this.jdbcTemplate = jdbcTemplate;
        this.appName = appName;
        init();
    }

    @Override
    protected long getWorkerId() {
        return serverNo;
    }

    private void init() {
        String macStr = appName + NetUtils.getMacString();
        try {
            serverNo = jdbcTemplate.queryForObject(QUERY_SERVERNO_SQL, new Object[]{macStr}, new int[]{Types.VARCHAR}, Long.class);
            logger.info("获取当前服务器机器的serverNo:[{}]", serverNo);
        } catch (IncorrectResultSizeDataAccessException e) {
            String hostAddress = null;
            try {
                hostAddress = Inet4Address.getLocalHost().getHostAddress();
            } catch (UnknownHostException e1) {
                logger.error("获取host失败, " + e.getMessage());
                hostAddress = "";
            }
            logger.info("当前服务器在数据库没有记录,插入机器信息记录,mac[{}],ip[{}]", macStr, hostAddress);
            jdbcTemplate.update(CREATE_SERVERNO_SQL, new Object[]{macStr, hostAddress}, new int[]{Types.VARCHAR, Types.VARCHAR});
            serverNo = jdbcTemplate.queryForObject(QUERY_SERVERNO_SQL, new Object[]{macStr}, new int[]{Types.VARCHAR}, Long.class);
            logger.info("获取当前服务器机器的serverNo:[{}]", serverNo);
        }

    }

}
