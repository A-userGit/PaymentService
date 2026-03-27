package com.shop.paymentservice.config.liquibase;

import com.shop.paymentservice.config.mongo.MongoCustomProperties;
import jakarta.annotation.PostConstruct;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LiquibaseRunner {
  private final MongoCustomProperties mongoProperties;
  private final LiquibaseProperties liquibaseProperties;

  @PostConstruct
  public void liquibase() {
    if(!liquibaseProperties.isEnabled()){
      return;
    }
    try (MongoLiquibaseDatabase database = (MongoLiquibaseDatabase) DatabaseFactory.getInstance().openDatabase(
        mongoProperties.getConnectionUri(),
        mongoProperties.getUsername(),
        mongoProperties.getPassword(),
        null,
        new ClassLoaderResourceAccessor()
    )){

      try(Liquibase liquibase = new Liquibase(liquibaseProperties.getChangeLog(), new ClassLoaderResourceAccessor(), database)) {
        liquibase.update();
      } catch (Exception e) {
        throw  e;
      }
    } catch (Exception e) {
      throw new RuntimeException("Error initializing Liquibase for MongoDB", e);
    }
  }

}
