package com.alexportfolio.jobScraper.repository;

import com.alexportfolio.jobScraper.entity.JobCard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = {"/schema.sql", "/data.sql"})
public class JobRepositoryTest {
    @Autowired
    JobRepository jobRepository;
    @MockBean
    private CommandLineRunner commandLineRunner;

    @Test
    @Transactional
    void savingToDB(){
        var entity = new JobCard("title","company","description","url",null, LocalDateTime.now(),null );
        var saved = jobRepository.save(entity);
        var value = jobRepository.findById(saved.getId());
        if(value.isPresent()){
            value.get().getCompany().equals(entity.getCompany());
        }

    }
    @Test
    void findAllOnlyTitleAndCompanyNameTest(){
        var results = jobRepository.findAllOnlyTitleAndCompanyName();
        var iterator = results.iterator();
        while(iterator.hasNext()){
            var value = iterator.next();
            assert(value.getDescription()==null &&
                    !value.getTitle().isBlank() &&
                    !value.getCompany().isBlank()
                            );
        }
    }

    @Test
    void removeOldCardsFromDB(){
        LocalDateTime threshold = LocalDateTime.now().minusDays(60);
        long before = jobRepository.count();
        jobRepository.deleteAllWhereTime_stampLowerThan(threshold);
        long after = jobRepository.count();
        assert(before>after);
    }
}
