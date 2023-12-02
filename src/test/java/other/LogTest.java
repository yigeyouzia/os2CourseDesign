package other;

import com.cyt.os.kernel.resourse.ResourceManager;
import org.apache.log4j.Logger;

/**
 * @author cyt
 * @date 2023/11/30 12:26
 */
public class LogTest {
    private static final Logger log = Logger.getLogger(LogTest.class);

    public static void main(String[] args) {
        log.info("info");
        log.warn("warn");
        log.error("error");
        log.debug("debug");
        log.fatal("fatal");
    }
}
