package com.smartresume.config;

import com.smartresume.model.EducationLevel;
import com.smartresume.model.JobOpening;
import com.smartresume.model.Resume;
import com.smartresume.model.Role;
import com.smartresume.model.User;
import com.smartresume.repository.JobOpeningRepository;
import com.smartresume.repository.ResumeRepository;
import com.smartresume.repository.UserRepository;
import com.smartresume.service.ResumeParserService;
import com.smartresume.service.SampleResumeGeneratorService;
import com.smartresume.service.ScreeningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Seeds initial database data: default admin user, sample jobs, sample candidate resumes,
 * and generates sample files in the /samples folder.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final JobOpeningRepository jobOpeningRepository;
    private final ResumeRepository resumeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResumeParserService resumeParserService;
    private final ScreeningService screeningService;
    private final SampleResumeGeneratorService sampleResumeGeneratorService;

    @Value("${app.security.admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.security.admin.email:hr@company.com}")
    private String adminEmail;

    @Value("${app.security.admin.full-name:HR Administrator}")
    private String adminFullName;

    public DataInitializer(UserRepository userRepository,
                           JobOpeningRepository jobOpeningRepository,
                           ResumeRepository resumeRepository,
                           PasswordEncoder passwordEncoder,
                           ResumeParserService resumeParserService,
                           ScreeningService screeningService,
                           SampleResumeGeneratorService sampleResumeGeneratorService) {
        this.userRepository = userRepository;
        this.jobOpeningRepository = jobOpeningRepository;
        this.resumeRepository = resumeRepository;
        this.passwordEncoder = passwordEncoder;
        this.resumeParserService = resumeParserService;
        this.screeningService = screeningService;
        this.sampleResumeGeneratorService = sampleResumeGeneratorService;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedJobsAndResumes();
        sampleResumeGeneratorService.generateSampleFiles("./samples");
    }

    private void seedAdminUser() {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User(
                    adminUsername,
                    passwordEncoder.encode(adminPassword),
                    adminEmail,
                    adminFullName,
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            log.info("Default Admin user created successfully: username='{}', password='{}'", adminUsername, adminPassword);
        }
    }

    private void seedJobsAndResumes() {
        if (jobOpeningRepository.count() > 0) {
            return;
        }

        log.info("Seeding sample job openings and candidate resumes...");

        // Job 1: Senior Java Full Stack Engineer
        JobOpening job1 = new JobOpening(
                "Senior Java Full Stack Engineer",
                "Engineering",
                "San Francisco, CA (Hybrid)",
                "We are seeking an experienced Senior Java Full Stack Engineer to lead the design and development " +
                "of high-performance, scalable cloud microservices and intuitive user interfaces. You will work with " +
                "Spring Boot, Spring Data JPA, PostgreSQL, Docker, and REST APIs, while collaborating closely with " +
                "frontend teams using React or Angular. Knowledge of AWS, Kubernetes, and CI/CD pipelines is highly valued.",
                new HashSet<>(Arrays.asList("Java", "Spring Boot", "Spring Data JPA", "PostgreSQL", "Docker", "REST API")),
                new HashSet<>(Arrays.asList("React", "Kubernetes", "AWS", "Redis", "Microservices", "JUnit")),
                4.0,
                EducationLevel.BACHELORS
        );
        JobOpening savedJob1 = jobOpeningRepository.save(job1);

        // Job 2: Lead Data Scientist / Machine Learning Engineer
        JobOpening job2 = new JobOpening(
                "Lead Data Scientist / ML Engineer",
                "AI & Research",
                "Remote / Boston, MA",
                "Join our AI Innovation team building state-of-the-art Natural Language Processing (NLP) pipelines " +
                "and Large Language Model (LLM) workflows. The ideal candidate has deep expertise in Python, PyTorch, " +
                "Machine Learning algorithms, and data modeling with SQL and Pandas. Experience deploying models with " +
                "Docker on AWS and Apache Spark is preferred.",
                new HashSet<>(Arrays.asList("Python", "Machine Learning", "Deep Learning", "NLP", "PyTorch", "SQL")),
                new HashSet<>(Arrays.asList("Apache Spark", "Large Language Models", "AWS", "Docker", "Pandas", "Scikit-Learn")),
                3.0,
                EducationLevel.MASTERS
        );
        JobOpening savedJob2 = jobOpeningRepository.save(job2);

        // Sample Resumes Text Content
        String alexResume = """
                Alex Morgan
                Email: alex.morgan@techmail.com | Phone: +1 (555) 234-5678
                San Francisco, CA | linkedin.com/in/alexmorgan

                SUMMARY
                Senior Full Stack Java Engineer with 6.5+ years of experience architecting and delivering
                high-throughput cloud microservices, REST APIs, and modern responsive frontends.

                TECHNICAL SKILLS
                Languages: Java (Java 8/11/17), Python, SQL, TypeScript, JavaScript
                Frameworks: Spring Boot, Spring Framework, Spring Data JPA, Spring Security, Hibernate, React, Angular
                Databases: PostgreSQL, MySQL, Redis, MongoDB
                Cloud & DevOps: Docker, Kubernetes, AWS (EC2, S3, RDS), CI/CD, Jenkins, Git
                Architecture & Testing: JUnit, Mockito, REST API, Microservices, Design Patterns, Agile, Scrum

                EXPERIENCE
                Senior Software Engineer | FinTech Corp (2021 - Present | 3.5 years)
                - Architected and built 15+ Spring Boot microservices handling over 2M+ daily transactions.
                - Designed PostgreSQL schemas, optimized JPA queries, and implemented Redis caching layer.
                - Containerized services using Docker and managed deployments to Kubernetes clusters on AWS.

                Full Stack Developer | CloudWave Solutions (2018 - 2021 | 3 years)
                - Built enterprise single page applications using React, TypeScript, and Java Spring Boot REST APIs.
                - Automated CI/CD pipelines using Jenkins and GitHub Actions.

                EDUCATION
                Master of Science in Computer Science (M.S.) | University of California, Berkeley (2018)
                """;

        String sarahResume = """
                Sarah Chen
                Email: sarah.chen@devmail.org | Phone: +1 (555) 876-5432
                Seattle, WA

                SUMMARY
                Versatile Full Stack Developer with 4 years of experience building modern web applications,
                RESTful backend services, and interactive user interfaces.

                TECHNICAL SKILLS
                Programming: Java, JavaScript, TypeScript, SQL, HTML5, CSS3, Tailwind CSS
                Backend & Frameworks: Spring Boot, Node.js, Express.js, REST API, Hibernate
                Databases: PostgreSQL, MySQL, Redis
                DevOps & Tools: Docker, Git, JUnit, Postman, Agile

                EXPERIENCE
                Software Developer | TechNova Inc (2021 - Present | 3 years)
                - Developed responsive client web applications using React and Tailwind CSS.
                - Implemented RESTful APIs in Java with Spring Boot and integrated PostgreSQL persistence.
                - Conducted unit testing with JUnit and Mockito.

                Junior Developer | PixelCraft Studio (2020 - 2021 | 1 year)
                - Maintained Java web applications and built Node.js internal utilities.

                EDUCATION
                Bachelor of Science in Information Technology (B.S.) | University of Washington (2020)
                """;

        String davidResume = """
                David Kumar
                Email: david.kumar@datascience.io | Phone: +1 (555) 345-6789
                Boston, MA

                SUMMARY
                Machine Learning Engineer and Data Scientist with 5 years experience specializing in
                Natural Language Processing (NLP), Large Language Models, and scalable machine learning pipelines.

                TECHNICAL SKILLS
                AI & Machine Learning: Python, Machine Learning, Deep Learning, NLP, PyTorch, TensorFlow, Scikit-Learn, Pandas, NumPy, Large Language Models
                Big Data & Cloud: Apache Spark, SQL, PostgreSQL, Docker, AWS (SageMaker, S3), Git, Tableau

                EXPERIENCE
                Lead ML Engineer | NeuralAI Labs (2022 - Present | 2.5 years)
                - Designed and deployed NLP models and LLM prompt pipelines for automated document intelligence.
                - Built distributed PyTorch training workflows on AWS SageMaker.

                Data Scientist | OmniData Analytics (2019 - 2022 | 2.5 years)
                - Developed predictive models with Python, Scikit-Learn, and Apache Spark on big data clusters.
                - Queried data warehouses using SQL and PostgreSQL.

                EDUCATION
                PhD in Computer Science (Machine Learning) | MIT (2019)
                """;

        String emilyResume = """
                Emily Watson
                Email: emily.watson@webdev.net | Phone: +1 (555) 456-7890
                Austin, TX

                SUMMARY
                Enthusiastic Junior Web Developer with 1.5 years experience creating clean, responsive websites
                and consuming RESTful APIs.

                TECHNICAL SKILLS
                Languages & Web: JavaScript, HTML5, CSS3, Python, SQL
                Libraries & Tools: React, Bootstrap, jQuery, Git, Webpack, Postman

                EXPERIENCE
                Junior Frontend Developer | WebSphere (2023 - Present | 1.5 years)
                - Built frontend components using React, JavaScript, HTML5, and CSS3.
                - Integrated frontend views with backend REST APIs.

                EDUCATION
                Bachelor of Science in Computer Science | University of Texas at Austin (2023)
                """;

        String michaelResume = """
                Michael Ross
                Email: michael.ross@cloudops.com | Phone: +1 (555) 789-0123
                Chicago, IL

                SUMMARY
                DevOps and Cloud Infrastructure Engineer with 5.5 years specializing in Kubernetes orchestration,
                AWS cloud architecture, Terraform IaC, and automated CI/CD pipelines.

                TECHNICAL SKILLS
                DevOps & Cloud: Docker, Kubernetes, AWS, Terraform, CI/CD, Jenkins, GitHub Actions, Linux, Ansible, Prometheus, Grafana, Nginx
                Languages & DB: Python, Shell Scripting, Git, PostgreSQL, Microservices

                EXPERIENCE
                Senior DevOps Engineer | SkyScale Cloud (2021 - Present | 3.5 years)
                - Managed multi-region Kubernetes clusters on AWS and automated infrastructure using Terraform.
                - Built zero-downtime CI/CD deployment pipelines using GitHub Actions.

                Cloud Systems Admin | Apex Networks (2019 - 2021 | 2 years)
                - Administered Linux server fleets and automated server configuration with Ansible and Shell Scripting.

                EDUCATION
                Bachelor of Science in Computer Engineering | University of Illinois (2019)
                """;

        Resume r1 = resumeParserService.parseAndSaveRawText("Resume_Alex_Morgan_Senior_Java.pdf", alexResume, null);
        Resume r2 = resumeParserService.parseAndSaveRawText("Resume_Sarah_Chen_FullStack.docx", sarahResume, null);
        Resume r3 = resumeParserService.parseAndSaveRawText("Resume_David_Kumar_DataScientist.pdf", davidResume, null);
        Resume r4 = resumeParserService.parseAndSaveRawText("Resume_Emily_Watson_Junior_Dev.docx", emilyResume, null);
        Resume r5 = resumeParserService.parseAndSaveRawText("Resume_Michael_Ross_DevOps_Cloud.pdf", michaelResume, null);

        // Perform initial screening for all sample candidates against all jobs
        List<Resume> all = Arrays.asList(r1, r2, r3, r4, r5);
        for (Resume r : all) {
            screeningService.screenResumeForJob(savedJob1, r);
            screeningService.screenResumeForJob(savedJob2, r);
        }

        log.info("Database initialized with 2 Jobs, 5 Resumes, and 10 Screening evaluations.");
    }
}
