package ru.otus.hw.migration;

import ru.otus.hw.migration.document.UserDocument;
import ru.otus.hw.models.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.List;

@Configuration
public class BatchConfig {

    @Bean
    public ItemReader<User> reader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<User>()
                .name("userReader")
                .entityManagerFactory(emf)
                .query("SELECT u FROM User u")
                .build();
    }

    @Bean
    public ItemProcessor<User, UserDocument> processor() {
        return user -> new UserDocument(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Bean
    public ItemWriter<UserDocument> writer(MongoTemplate mongoTemplate) {
        return items -> {
            List<? extends UserDocument> list = (List<? extends UserDocument>) items;
            for (UserDocument doc : list) {
                mongoTemplate.save(doc);
            }
        };
    }

    @Bean
    public Step migrationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                               ItemReader<User> reader, ItemProcessor<User, UserDocument> processor,
                               ItemWriter<UserDocument> writer) {
        return new StepBuilder("migrationStep", jobRepository)
                .<User, UserDocument>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job migrateUserJob(JobRepository jobRepository, Step migrationStep) {
        return new JobBuilder("migrateUserJob", jobRepository)
                .start(migrationStep)
                .build();
    }
}
