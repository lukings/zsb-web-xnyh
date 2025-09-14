package cn.visolink.system.timetask;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */
@Component
@EnableScheduling
public class SynJpushTask {

   // @Scheduled(cron="0/5 * * * * *")
    public void synJpushTask(){

    }

}
