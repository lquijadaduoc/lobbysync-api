package cl.lobbysync.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "cl.lobbysync.backend.repository.mongo")
public class MongoConfig {

    @Value("${spring.data.mongodb.host:mongo_db}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port:27017}")
    private String mongoPort;

    @Value("${spring.data.mongodb.database:lobbysync}")
    private String database;

    @Bean
    public MongoClient mongoClient() {
        String connectionString = String.format("mongodb://%s:%s/%s", mongoHost, mongoPort, database);
        ConnectionString cs = new ConnectionString(connectionString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(cs)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), database);
    }
}
