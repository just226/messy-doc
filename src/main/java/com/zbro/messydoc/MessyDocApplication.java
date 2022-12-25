package com.zbro.messydoc;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.util.ArrayUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableAutoConfiguration
public class MessyDocApplication {
	public static void main(String[] args) {
		try{
			ApplicationArguments applicationArguments =new DefaultApplicationArguments(args);
			if(applicationArguments.containsOption("mode")){
				String mode = applicationArguments.getOptionValues("mode").get(0);
				switch (mode) {
					case "unified":
						log.info("start master service with a worker");
						SpringApplication.run(MessyDocMasterWithWorkerApplication.class, args);
						break;
					case "master":
						log.info("start master service");
						SpringApplication.run(MessyDocMasterApplication.class, args);
						break;
					case "worker":
						log.info("start worker service");
						SpringApplication.run(MessyDocWorkerApplication.class, args);
						break;
					default:
						log.error("invalid mode specified, you can run one of the mode [unified|master|worker] with argument --mode=xxx");
						break;
				}
			}else {
				log.info("start default master service with a worker");
				log.info("you can run one of the mode [unified|master|worker] with argument --mode=xxx");
				SpringApplication.run(MessyDocMasterWithWorkerApplication.class, ArrayUtils.concat(args, new String[]{"--mode=unified"}));
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
