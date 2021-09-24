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

#### Sender.java 수정
+ [9/23] 전역변수 TOPIC 제거 
```
public class Sender {
    private Producer<String, String> producer;

    public Sender() {
        this.producer = new KafkaProducer<>(senderProps());
    }

    public void close() {
        producer.close();
    }

    public void sendMessage(String topic, String key, String value) {
        producer.send(new ProducerRecord<>(topic, key, value));
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

#### SenderApplication.java 수정
+ [9/23] topic을 동적으로 할당 할 수 있게 변경 / 코드 리펙토링
```
public class SenderApplication {

    public static void main(String[] args) throws InterruptedException, IOException {
        String topic = getTopic();
        send(topic);
    }

    private static void send(String topic) throws InterruptedException {
        Sender sender = new Sender();

        for (int i = 0; i < 10; i++) {
            sender.sendMessage(topic, "test" + i, LocalDateTime.now().toString());
            TimeUnit.MILLISECONDS.sleep(1000);
        }

        sender.close();
    }

    private static String getTopic() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("topic : ");
        String topic = bufferedReader.readLine();
        bufferedReader.close();
        return topic;
    }
}
```

#### 실행
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
topic : test
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

#### Receiver.java 수정
+ [9/23] 전역변수 TOPIC 제거 / 전역변수 name 추가 / consumer.subscribe의 매개 변수 추가 및 타입 변경 / 출력 메세지 변경 / ConsumerConfig CLIENT_ID_CONFIG 추가
```
public class Receiver {

    private KafkaConsumer<String, String> consumer;
    private String name;

    public Receiver(String name, String group, String topic) {
        this.name = name;
        this.consumer = new KafkaConsumer<>(receiverProps(group));
        //Topic 구독
        consumer.subscribe(Collections.singletonList(topic), new WatchOffsetOnRebalance(name));
    }

    public void close() {
        consumer.close();
    }

    public void poll() {
        //0.1초 대기 후 서버에서 메시지를 읽어온다.
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        //메시지 출력
        for (ConsumerRecord<String, String> record : records)
            System.out.printf("%s, groupId = %s,  name = %s, offset = %d, partition = %d, key = %s, value = %s%n",
                    LocalDateTime.now(),
                    consumer.groupMetadata().groupId(),
                    name,
                    record.offset(),
                    record.partition(),
                    record.key(),
                    record.value());
        consumer.commitSync();
    }

    public Map<String, Object> receiverProps(String group) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.28:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, name);
        return props;
    }
}
```

#### ReceiverApplication.java 수정
+ [9/23] topic, consumer group, name을 동적으로 할당 할 수 있게 변경 / 코드 리펙토링
```
public class ReceiverApplication {

    public static void main(String[] args) throws IOException {
        System.out.println("kafka receiver start");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("group : ");
        String group = bufferedReader.readLine();

        System.out.print("topic : ");
        String topic = bufferedReader.readLine();

        System.out.print("name : ");
        String name = bufferedReader.readLine();

        bufferedReader.close();

        receive(name, group, topic);
    }

    private static void receive(String name, String group, String topic) {
        Receiver receiver = new Receiver(name, group, topic);

        System.out.println("polling...");
        while (true) {
            receiver.poll();
        }

        //receiver.close();
    }
}
```

#### WatchOffsetOnRebalance.java
+ API : https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/consumer/ConsumerRebalanceListener.html
```
public class WatchOffsetOnRebalance implements ConsumerRebalanceListener {

    private String name;

    public WatchOffsetOnRebalance(String name) {
        this.name = name;
    }

    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        // save the offsets in an external store using some custom code not described here
	// 여기에 설명되지 않은 일부 사용자 지정 코드를 사용하여 외부 저장소에 오프셋 저장
        System.out.println(name + " Revoked " + partitions);
    }

    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        // do not need to save the offsets since these partitions are probably owned by other consumers already
	// 이러한 파티션은 이미 다른 소비자가 소유하고 있기 때문에 오프셋을 저장할 필요가 없습니다.
        System.out.println(name + " Lost " + partitions);
    }

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        // read the offsets from an external store using some custom code not described here
	// 여기에 설명되지 않은 일부 사용자 정의 코드를 사용하여 외부 저장소에서 오프셋을 읽습니다.
        System.out.println(name + " Assigned " + partitions);
    }
}
```

#### 실행
Consumer 어플리케이션은 다중 실행 / 실행한 어플리케이션 중 일부
```
kafka receiver start
group : foo
topic : test
name : 1_test
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
polling...
1_test Assigned [test-1, test-0, test-2]
1_test Revoked [test-1, test-0, test-2]
1_test Assigned [test-1, test-0]
1_test Revoked [test-1, test-0]
1_test Assigned [test-0]
1_test Revoked [test-0]
1_test Assigned [test-0]
2021-09-23T10:02:18.478, groupId = foo,  name = 1_test, offset = 1143, partition = 0, key = test0, value = 2021-09-23T10:02:18.196
2021-09-23T10:02:19.460, groupId = foo,  name = 1_test, offset = 1144, partition = 0, key = test1, value = 2021-09-23T10:02:19.447
2021-09-23T10:02:20.462, groupId = foo,  name = 1_test, offset = 1145, partition = 0, key = test2, value = 2021-09-23T10:02:20.448
2021-09-23T10:02:23.462, groupId = foo,  name = 1_test, offset = 1146, partition = 0, key = test5, value = 2021-09-23T10:02:23.450
```

## TEST (1)
WatchOffsetOnRebalance 클래스를 추가했을 때 파티션 배정을 테스트해보았다.
1. 이름 지정 - 이름을 지정하고 실행했을 때와 지정하지 않고 실행했을 때의 파티션 배정 차이
2. 그룹이 하나일 때 컨슈머가 파티션 개수를 초과했을 때 파티션 배정이 어떻게 되는지, 또 배정 후 컨슈머를 삭제하면 후에 어떻게 배정되는지 체크
파티션 배정에 대한 쉬운 설명 : https://blog.voidmainvoid.net/361

#### 1. 이름 지정
![dfsdfsdee](https://user-images.githubusercontent.com/73865700/134744519-9f3dfe10-b7b4-447f-bf92-c004afa73a68.PNG)

#### 2. 컨슈머가 파티션 개수를 초과했을 때의 파티션 배정 결과
![fgvsdfe](https://user-images.githubusercontent.com/73865700/134744583-cb3bb038-7488-4aa7-b192-15eb8d85bbb5.PNG)
