package com.acl.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.acl.model.MSDNDetailsDTO;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration

public class RedisConfig {

	private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
	@Value("${redis.host}")
	private String redisHost;
	@Value("${redis.port}")
	private int redisPort;
	@Value("${redis.password}")
	private String redisPassword;

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		try {
			logger.info("Connecting to Redis Server :  " + redisHost + ":" + redisPort + " pwd: " + redisPassword);
			RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost,
					redisPort);
			if (!StringUtils.isEmpty(redisPassword))
				redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPassword));
			logger.info("Connected to Redis Server :  " + redisHost + ":" + redisPort);
			return new JedisConnectionFactory(redisStandaloneConfiguration);
		} catch (Exception e) {
			logger.error("Error comes into redis connection : ", e);
			throw e;
		}

	}

	@Bean
	public RedisTemplate<String, MSDNDetailsDTO> redisTemplate() {
		 Jackson2JsonRedisSerializer<MSDNDetailsDTO> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(MSDNDetailsDTO.class);
	        final RedisTemplate<String, MSDNDetailsDTO> template = new RedisTemplate<>();
	        final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
			objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			//objectMapper.setVisibility(PropertyAccessor.ALL, JSON);
			objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
			jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
	        template.setConnectionFactory(jedisConnectionFactory());
	        template.setKeySerializer(new StringRedisSerializer());
	        template.setHashKeySerializer(new StringRedisSerializer());	        
	        Jackson2JsonRedisSerializer<List<MSDNDetailsDTO>> jackson2JsonRedisSerializerHash = new Jackson2JsonRedisSerializer(Object.class); 
	        jackson2JsonRedisSerializerHash.setObjectMapper(objectMapper);
	        template.setHashValueSerializer(jackson2JsonRedisSerializerHash);
	        template.setDefaultSerializer(jackson2JsonRedisSerializer);
	        template.setValueSerializer(jackson2JsonRedisSerializer);
		return template;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}
