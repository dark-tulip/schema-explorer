package ru.anvera.factory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.stereotype.Component;

@Component
public class DynamicMongoClientFactory {

    public MongoClient createMongoClient(String url, String username, String password) {
        // Create a new MongoClient instance dynamically
        return MongoClients.create(
            MongoClientSettings.builder()
                               .applyConnectionString(new ConnectionString(url))
                               .build()
        );
    }
}
