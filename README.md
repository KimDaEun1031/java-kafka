# Apache Kafka 설치 및 설정 & 실행 (12~14번)
+ https://relaxed-it-study.tistory.com/48

# Apache Kafka with SpringBoot

> NOTE

> + Java Kafka API를 이용해 Spring boot Project로 Kafka Consumer와 Kafka Producer를 구현한다.
> + Project는 Multi Module Project로 진행한다. 
> + https://spring.io/guides/gs/multi-module/
> + API를 참고하여 개발한다.
> + https://kafka.apache.org/28/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
> + Kafka Server를 켠 상태로 진행한다.

## Apache Kafka with Spring boot Project Description
#### Project directory tree
```
.
├─ consumer/src/main/java/org/daeun/consumer
│       │                         ├─ ReceiverApplication.java
│       │                         └─ Receiver.java
│       └─ resources
│           └─ application.properties
│  
├─ producer/src/main/java/org/daeun/producer
│       │                         ├─ SenderApplication.java
│       │                         └─ Sender.java
│       └─ resources
│           └─ application.properties
│  
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
└─ target
     ├─classes
     ├─generated-sources ...
```

## 1. Dependency
spring-kafka, kafka-clients, lombok을 추가한다.

#### parent(kafka) pom.cml
```
    <packaging>pom</packaging>
    <modules>
        <module>producer</module>
		<module>consumer</module>
	</modules>

	<groupId>org.daeun</groupId>
	<artifactId>kafka</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>kafka</name>
	<description>kafka access project</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>

		<dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka-clients</artifactId>
			<version>2.8.0</version>
		</dependency>

		<dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka-clients</artifactId>
			<version>2.8.0</version>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>

```

#### consumer & producer pom.xml
```
<parent>
        <artifactId>kafka</artifactId>
        <groupId>org.daeun</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    
    //producer는 producer로 변경
    <artifactId>consumer</artifactId> 

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <packaging>jar</packaging>
```

## 2. Configuration
application.properties에서는 kafka server와 연결한다.
#### producer
```
spring:
  kafka:
    producer:
      bootstrap-servers: (서버 아이피 주소):9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

#### consumer
```
spring:
  kafka: 
    consumer:
      bootstrap-servers: (서버 아이피 주소):9092 
      group-id: foo 
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

## 3. Plugin
Multirun 플러그인을 다운받아준다.
File -> Settings -> Search(Multirun) -> install

## 4. Producer Module
kafka의 topic으로 데이터를 보낸다.
+ Kafka Producer API : https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/producer/KafkaProducer.html
+ Kafka Producer Configs : https://relaxed-it-study.tistory.com/59

#### API 문서 안 예제
```
 Properties props = new Properties();
 props.put("bootstrap.servers", "localhost:9092");
 props.put("acks", "all");
 props.put("retries", 0);
 props.put("linger.ms", 1);
 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

 Producer<String, String> producer = new KafkaProducer<>(props);
 for (int i = 0; i < 100; i++)
     producer.send(new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i)));

 producer.close();
```

#### Sender.java
```
public class Sender {
    private static final String TOPIC = "test";
    private Producer<String, String> producer;

    public Sender() {
        this.producer = new KafkaProducer<String, String>(senderProps());
    }

    public void close() {
        producer.close();
    }

    public void sendMessage(String key, String value) {
        producer.send(new ProducerRecord<>(TOPIC, key, value));
        System.out.printf("%s, Produce message : %s%n", LocalDateTime.now(), value);

    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.28:9092");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

}
```

#### SenderApplication.java
```
public class SenderApplication {

	public static void main(String[] args) throws InterruptedException {
		Sender sender = new Sender();
		for (int i = 0; i < 1000; i++) {
			sender.sendMessage("test", LocalDateTime.now().toString());
			TimeUnit.MILLISECONDS.sleep(1000);
		}
		sender.close();
	}

}
```

#### 실행
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
2021-09-14T22:16:09.020, Produce message : 2021-09-14T22:16:08.759
2021-09-14T22:16:10.021, Produce message : 2021-09-14T22:16:10.021
2021-09-14T22:16:11.022, Produce message : 2021-09-14T22:16:11.022
```

## 4-1. Consumer Module
kafka의 topic에서 데이터를 가져와 출력한다.
+ Kafka Consumer API : https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html
+ Kafka Consumer Configs : https://relaxed-it-study.tistory.com/59

#### API 문서 안 예제
```
Properties props = new Properties();
     props.setProperty("bootstrap.servers", "localhost:9092");
     props.setProperty("group.id", "test");
     props.setProperty("enable.auto.commit", "true");
     props.setProperty("auto.commit.interval.ms", "1000");
     props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
     consumer.subscribe(Arrays.asList("foo", "bar"));
     while (true) {
         ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
         for (ConsumerRecord<String, String> record : records)
             System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
     }
```

#### Receiver.java
```
public class Receiver {

    private static final String TOPIC = "test";
    private KafkaConsumer<String, String> consumer;

    public Receiver() {
        this.consumer = new KafkaConsumer<>(receiverProps());
        consumer.subscribe(Arrays.asList(TOPIC));
    }

    public void close() {
        consumer.close();
    }

    public void poll() {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String, String> record : records)
            System.out.printf("%s, offset = %d, key = %s, value = %s%n", LocalDateTime.now(), record.offset(), record.key(), record.value());
    }

    private Map<String, Object> receiverProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.28:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "foo");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        return props;
    }
}
```

#### ReceiverApplication.java
```
public class ReceiverApplication {

	public static void main(String[] args) throws IOException {
		System.out.println("kafka receiver start");

		Receiver receiver = new Receiver();

		System.out.println("polling...");
		while (true) {
			receiver.poll();
		}

//		receiver.close();
	}
}
```

#### 실행
```
kafka receiver start
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
polling...
2021-09-14T22:17:13.479, offset = 93, key = test, value = 2021-09-14T22:16:29.032
2021-09-14T22:17:13.479, offset = 94, key = test, value = 2021-09-14T22:16:30.032
2021-09-14T22:17:13.479, offset = 95, key = test, value = 2021-09-14T22:16:31.032
```
