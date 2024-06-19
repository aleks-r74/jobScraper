package com.alexportfolio.jobScraper.config;


import com.alexportfolio.jobScraper.daemon.JobAssessmentDaemon;
import com.alexportfolio.jobScraper.daemon.JobDescriptionSummarizationDaemon;
import com.alexportfolio.jobScraper.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class PostProcessor implements BeanPostProcessor {
    @Value("${summarization.beanName}")
    private String summarizationBeanName;
    @Value("${assessment.beanName}")
    private String assessmentBeanName;

    @Autowired
    @Qualifier("llmServiceOllama")
    private LlmService llmOllama;

    @Autowired
    @Qualifier("llmServiceOpenAi")
    private LlmService llmOpenAi;

    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionSummarizationDaemon.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof JobDescriptionSummarizationDaemon daemon) {
            if ("llmServiceOllama".equals(summarizationBeanName)) {
                daemon.setLlmService(llmOllama);
                logger.info("llmServiceOllama bean is set for summarization");
            } else if ("llmServiceOpenAi".equals(summarizationBeanName)) {
                daemon.setLlmService(llmOpenAi);
                logger.info("llmServiceOpenAi bean is set for summarization");
            } else {
                daemon.setLlmService(null);
                logger.info("summarization is OFF");
            }
        }

        if (bean instanceof JobAssessmentDaemon daemon) {
            if ("llmServiceOllama".equals(assessmentBeanName)) {
                daemon.setLlmService(llmOllama);
                logger.info("llmServiceOllama bean is set for assessment");
            } else if ("llmServiceOpenAi".equals(assessmentBeanName)) {
                daemon.setLlmService(llmOpenAi);
                logger.info("llmServiceOpenAi bean is set for assessment");
            } else {
                daemon.setLlmService(null);
                logger.info("assessment is OFF");
            }
        }
        return bean;
    }
}
