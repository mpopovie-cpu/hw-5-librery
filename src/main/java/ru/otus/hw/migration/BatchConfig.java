package ru.otus.hw.migration;

import ru.otus.hw.models.*;
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
import org.springframework.data.mongodb.core.MongoTemplate;

import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;

@Configuration
public class BatchConfig {

    @Bean
    public ItemReader<BookEntity> bookReader(EntityManagerFactory emf) {
        JpaPagingItemReader<BookEntity> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("SELECT b FROM BookEntity b");
        reader.setPageSize(10);
        return reader;
    }

    @Bean
    public ItemProcessor<BookEntity, BookMongo> bookProcessor() {
        return book -> new BookMongo(
                String.valueOf(book.getId()),
                book.getTitle(),
                String.valueOf(book.getAuthorId()),
                String.valueOf(book.getGenreId())
        );
    }

    @Bean
    public ItemWriter<BookMongo> bookWriter(MongoTemplate mongoTemplate) {
        return items -> {
            for (Object item : items) {
                mongoTemplate.save((BookMongo) item);
            }
        };
    }

    @Bean
    public Step bookStep(JobRepository jobRepository, PlatformTransactionManager txManager, 
                         ItemReader<BookEntity> reader, ItemProcessor<BookEntity, BookMongo> proc, ItemWriter<BookMongo> writer) {
        return new StepBuilder("bookStep", jobRepository)
                .<BookEntity, BookMongo>chunk(10, txManager)
                .reader(reader).processor(proc).writer(writer).build();
    }

    @Bean
    public ItemReader<AuthorEntity> authorReader(EntityManagerFactory emf) {
        JpaPagingItemReader<AuthorEntity> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("SELECT a FROM AuthorEntity a");
        reader.setPageSize(10);
        return reader;
    }

    @Bean
    public ItemProcessor<AuthorEntity, AuthorMongo> authorProcessor() {
        return author -> new AuthorMongo(
                String.valueOf(author.getId()),
                author.getFullName(),
                new ArrayList<>()
        );
    }

    @Bean
    public ItemWriter<AuthorMongo> authorWriter(MongoTemplate mongoTemplate) {
        return items -> {
            for (Object item : items) {
                mongoTemplate.save((AuthorMongo) item);
            }
        };
    }

    @Bean
    public Step authorStep(JobRepository jobRepository, PlatformTransactionManager txManager, 
                            ItemReader<AuthorEntity> reader, ItemProcessor<AuthorEntity, AuthorMongo> proc, ItemWriter<AuthorMongo> writer) {
        return new StepBuilder("authorStep", jobRepository)
                .<AuthorEntity, AuthorMongo>chunk(10, txManager)
                .reader(reader).processor(proc).writer(writer).build();
    }

    @Bean
    public Job migrationJob(JobRepository jobRepository, Step bookStep, Step authorStep) {
        return new JobBuilder("migrationJob", jobRepository)
                .start(bookStep)
                .next(authorStep)
                .build();
    }
}
