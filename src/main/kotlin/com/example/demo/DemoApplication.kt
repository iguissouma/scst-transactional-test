package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.cloud.stream.messaging.Source
import org.springframework.integration.support.MessageBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
@EnableTransactionManagement
@EnableBinding(Processor::class)
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}


@Component
class MyListener(val processor: Processor, val myRepo: MyRepo) {

    @StreamListener(Processor.INPUT)
    @Transactional
    fun process(myMessage: Message<String>) {
        myRepo.addEmployee(Employee(name = "myName"))
        processor.output().send(MessageBuilder.withPayload("hello transacted 1..").build())
        if (true) {
            throw RuntimeException("MyError")
        }
        processor.output().send(MessageBuilder.withPayload("hello transacted 1..").build())
    }

}

@Component
class MyRepo(private val jdbcTemplate: JdbcTemplate) {
    fun addEmployee(e:Employee) {

    }

}

data class Employee(val id:Int? =null, val name:String)
