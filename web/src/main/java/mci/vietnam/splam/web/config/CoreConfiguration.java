package mci.vietnam.splam.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mci.vietnam.splam.core.domain.MyBigNumber;

@Configuration
public class CoreConfiguration {
    @Bean
    public MyBigNumber myBigNumber() {
        return new MyBigNumber();
    }
}