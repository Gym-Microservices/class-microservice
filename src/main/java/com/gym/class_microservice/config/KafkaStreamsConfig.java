package com.gym.class_microservice.config;

import com.gym.class_microservice.model.ClassOccupation;
import com.gym.class_microservice.model.GymClass;
import com.gym.class_microservice.model.TrainingData;
import com.gym.class_microservice.repository.ClassRepository;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

	@Value("${spring.kafka.bootstrap-servers:kafka:9092}")
	private String bootstrapServers;

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public KafkaStreamsConfiguration kStreamsConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG, "class-microservice-streams");
		props.put(org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerde.class);
		return new KafkaStreamsConfiguration(props);
	}

	@Bean
	public KStream<String, TrainingData> trainingDataTopology(StreamsBuilder builder, ClassRepository classRepository) {
		// Serdes
		org.springframework.kafka.support.serializer.JsonSerde<ClassOccupation> occupationSerde = new org.springframework.kafka.support.serializer.JsonSerde<>(ClassOccupation.class);
		org.springframework.kafka.support.serializer.JsonSerde<TrainingData> trainingSerde = new org.springframework.kafka.support.serializer.JsonSerde<>(TrainingData.class);

		KStream<String, ClassOccupation> occupationStream = builder.stream(
				"class-occupation",
				Consumed.with(Serdes.String(), occupationSerde)
		);

		KStream<String, TrainingData> trainingStream = occupationStream.mapValues(occ -> {
			GymClass gymClass = classRepository.findById(occ.getClassId()).orElse(null);
			if (gymClass == null) {
				return null;
			}
			TrainingData td = new TrainingData();
			td.setClassId(gymClass.getId());
			td.setClassName(gymClass.getName());
			td.setCoachId(gymClass.getCoachId());
			td.setMaxCapacity(gymClass.getMaxCapacity());
			td.setCurrentEnrollment(occ.getCurrentOccupation());
			td.setSchedule(gymClass.getSchedule());
			td.setEventTimestamp(occ.getTimestamp());
			return td;
		}).filter((k,v) -> v != null);

		trainingStream.to("training-data", Produced.with(Serdes.String(), trainingSerde));
		return trainingStream;
	}
}


