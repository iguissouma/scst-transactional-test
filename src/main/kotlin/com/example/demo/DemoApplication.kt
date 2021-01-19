package com.example.demo

import com.example.demo.TestConsumer.Companion.TEST_CONSUMER
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.support.MessageBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.SubscribableChannel
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
@EnableTransactionManagement
@EnableBinding(Processor::class, TestConsumer::class)
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

interface TestConsumer {
    @Input(TEST_CONSUMER)
    fun testConsumer(): SubscribableChannel

    companion object {
        const val TEST_CONSUMER = "test-consumer"
    }
}

@Component
class MyListener(val processor: Processor, val myRepo: MyRepo) {

    @StreamListener(Processor.INPUT)
    @Transactional
    fun process(myMessage: Message<String>) {
        myRepo.updateEmployee(Employee(id = 1, name = "titi"))
        processor.output().send(MessageBuilder.withPayload("hello world").build())
        if (true) {
            throw RuntimeException("MyError")
        }
        processor.output().send(MessageBuilder.withPayload("hello world").build())
    }


}

@Component
class Test(val myRepo: MyRepo) {

    @StreamListener(TEST_CONSUMER)
    fun consume(myMessage: Message<String>) {
        val employee = myRepo.getEmployee(1)
        println("message should not be received here " + myMessage.payload)
        println("employee name still toto == " + employee?.name)
    }
}


@Component
class MyRepo(private val jdbcTemplate: JdbcTemplate) {
    fun updateEmployee(e: Employee) {
        this.jdbcTemplate.update(
            "update TBL_EMPLOYEES  set name = ? where id = ?",
            e.name, e.id
        );
    }

    fun getEmployee(id: Int): Employee? {
        val sql = "SELECT * FROM TBL_EMPLOYEES WHERE ID = ?"
        return jdbcTemplate.queryForObject(
            sql, arrayOf<Any>(id)
        ) { rs, rowNum ->
            Employee(
                rs.getInt("id"),
                rs.getString("name"),
            )
        }
    }

}

data class Employee(val id:Int? =null, val name:String)
