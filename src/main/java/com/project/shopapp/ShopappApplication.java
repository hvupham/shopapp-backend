package com.project.shopapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@ImportAutoConfiguration(AopAutoConfiguration.class)
//@SpringBootApplication(exclude = KafkaAutoConfiguration.class), disable in "application.yml"
public class ShopappApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShopappApplication.class, args);
	}

}
/*
docker rm -f zookeeper-01 zookeeper-02 zookeeper-03 kafka-broker-01

kafkaPath="/Users/hoangnd/Documents/code/udemy/ShopApp/kafka-deployment.yaml"
docker-compose -f $kafkaPath up -d zookeeper-01
docker-compose -f $kafkaPath up -d zookeeper-02
docker-compose -f $kafkaPath up -d zookeeper-03

sleep 10
docker-compose -f $kafkaPath up -d kafka-broker-01

* */