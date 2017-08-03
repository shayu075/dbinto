package org.l.dbinto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class DbintoApplication {
	protected static Logger log = LoggerFactory.getLogger(DbintoApplication.class);

	public static void main(String[] args) {
		log.info("服务开始启动！");
		SpringApplication.run(DbintoApplication.class, args);
	}
}
