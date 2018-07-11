package com.bytewheel.carrent.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.springframework.cache.support.NullValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

@Configuration
public class RedisConfig {
  @Bean
  @Primary
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    ObjectMapper objectMapper = createObjectMapper();

    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
        objectMapper);

    template.setDefaultSerializer(serializer);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(serializer);
    template.setValueSerializer(serializer);

    return template;
  }

  private ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();

    // simply setting {@code mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)} does not help here since we need
    // the type hint embedded for deserialization using the default typing feature.
    mapper.registerModule(new SimpleModule().addSerializer(new NullValueSerializer(null)));

    mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL,
                               JsonTypeInfo.As.PROPERTY);
    return mapper;
  }

  private static class NullValueSerializer extends StdSerializer<NullValue> {

    private static final long serialVersionUID = 1999052150548658808L;
    private final String classIdentifier;

    /**
     * @param classIdentifier can be {@literal null} and will be defaulted to {@code @class}.
     */
    NullValueSerializer(String classIdentifier) {

      super(NullValue.class);
      this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
    }

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException {

      jgen.writeStartObject();
      jgen.writeStringField(classIdentifier, NullValue.class.getName());
      jgen.writeEndObject();
    }
  }
}
