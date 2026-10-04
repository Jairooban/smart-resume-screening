package com.smartresume.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generates realistic PDF and DOCX sample resumes for testing and demonstration.
 */
@Service
public class SampleResumeGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(SampleResumeGeneratorService.class);

    public void generateSampleFiles(String targetDirectory) {
        try {
            Path dir = Paths.get(targetDirectory);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            // 1. Alex Morgan (Senior Java Full Stack - PDF)
            generatePdf(
                    dir.resolve("Resume_Alex_Morgan_Senior_Java.pdf").toFile(),
                    "Alex Morgan",
                    "alex.morgan@techmail.com | +1 (555) 234-5678 | San Francisco, CA | linkedin.com/in/alexmorgan",
                    "Senior Full Stack Java Engineer with 6.5+ years of experience designing and scaling microservices.",
                    new String[]{
                            "Languages: Java (Java 8/11/17), Python, SQL, TypeScript, JavaScript",
                            "Frameworks: Spring Boot, Spring Data JPA, Spring Security, Hibernate, React, Angular",
                            "Databases: PostgreSQL, MySQL, Redis, MongoDB",
                            "Cloud & DevOps: Docker, Kubernetes, AWS (EC2, S3, RDS), CI/CD, Jenkins, Git",
                            "Testing & Architecture: JUnit 5, Mockito, REST API, Microservices, Design Patterns, Agile"
                    },
                    new String[]{
                            "Senior Software Engineer - FinTech Corp (2021 - Present | 3.5 yrs): Architected cloud-native Spring Boot microservices handling 2M+ daily transactions. Led migration from monolithic architecture to Docker & Kubernetes on AWS.",
                            "Full Stack Developer - CloudWave Solutions (2018 - 2021 | 3 yrs): Developed enterprise dashboards using React, TypeScript, and Java Spring Boot REST APIs with PostgreSQL."
                    },
                    "Master of Science in Computer Science (M.S.) - University of California, Berkeley (2018)"
            );

            // 2. Sarah Chen (Full Stack Engineer - DOCX)
            generateDocx(
                    dir.resolve("Resume_Sarah_Chen_FullStack.docx").toFile(),
                    "Sarah Chen",
                    "sarah.chen@devmail.org | +1 (555) 876-5432 | Seattle, WA",
                    "Dynamic Full Stack Developer with 4 years of experience building modern web apps and RESTful services.",
                    new String[]{
                            "Technical Skills: Java, Spring Boot, React, Node.js, Express.js, TypeScript, PostgreSQL, Docker, Git, REST API, HTML5, CSS3, Tailwind CSS, Jest, JUnit"
                    },
                    new String[]{
                            "Software Developer - TechNova Inc (2021 - Present | 3 yrs): Built responsive React frontends and Spring Boot microservices. Integrated PostgreSQL and Redis caching.",
                            "Junior Developer - PixelCraft Studio (2020 - 2021 | 1 yr): Developed REST APIs using Node.js and maintained Java backend services."
                    },
                    "Bachelor of Science in Information Technology (B.S.) - University of Washington (2020)"
            );

            // 3. David Kumar (Data Scientist / ML Engineer - PDF)
            generatePdf(
                    dir.resolve("Resume_David_Kumar_DataScientist.pdf").toFile(),
                    "David Kumar",
                    "david.kumar@datascience.io | +1 (555) 345-6789 | Boston, MA",
                    "Machine Learning Engineer & Data Scientist with 5 years experience specializing in NLP and LLMs.",
                    new String[]{
                            "AI & ML: Python, Machine Learning, Deep Learning, NLP, PyTorch, TensorFlow, Scikit-Learn, Pandas, NumPy, LLM, LangChain",
                            "Big Data & Cloud: Apache Spark, SQL, PostgreSQL, Docker, AWS (SageMaker, S3), Git, Tableau"
                    },
                    new String[]{
                            "Lead ML Engineer - NeuralAI Labs (2022 - Present | 2.5 yrs): Built NLP document classification pipelines and fine-tuned Large Language Models for automated semantic analysis.",
                            "Data Scientist - OmniData Analytics (2019 - 2022 | 2.5 yrs): Developed predictive models using Scikit-Learn, PyTorch, and Apache Spark on AWS."
                    },
                    "PhD in Computer Science (Machine Learning) - MIT (2019)"
            );

            // 4. Emily Watson (Junior Web Developer - DOCX)
            generateDocx(
                    dir.resolve("Resume_Emily_Watson_Junior_Dev.docx").toFile(),
                    "Emily Watson",
                    "emily.watson@webdev.net | +1 (555) 456-7890 | Austin, TX",
                    "Enthusiastic Junior Web Developer with 1.5 years experience creating responsive web interfaces.",
                    new String[]{
                            "Skills: JavaScript, HTML5, CSS3, React, Bootstrap, Git, Python, REST API, jQuery, Webpack"
                    },
                    new String[]{
                            "Junior Frontend Developer - WebSphere (2023 - Present | 1.5 yrs): Created client web pages with React, JavaScript, and Bootstrap. Connected with REST API endpoints."
                    },
                    "Bachelor of Science in Computer Science - University of Texas at Austin (2023)"
            );

            // 5. Michael Ross (DevOps & Cloud Engineer - PDF)
            generatePdf(
                    dir.resolve("Resume_Michael_Ross_DevOps_Cloud.pdf").toFile(),
                    "Michael Ross",
                    "michael.ross@cloudops.com | +1 (555) 789-0123 | Chicago, IL",
                    "DevOps and Cloud Infrastructure Engineer with 5.5 years specializing in Kubernetes, AWS, and CI/CD automation.",
                    new String[]{
                            "DevOps & Infrastructure: Docker, Kubernetes, AWS, Terraform, CI/CD, Jenkins, GitHub Actions, Linux, Ansible, Prometheus, Grafana, Nginx",
                            "Programming & Tools: Python, Shell Scripting, Git, PostgreSQL, Microservices"
                    },
                    new String[]{
                            "Senior DevOps Engineer - SkyScale Cloud (2021 - Present | 3.5 yrs): Managed multi-cluster Kubernetes deployments on AWS. Implemented automated CI/CD pipelines with GitHub Actions and Terraform.",
                            "Cloud Systems Admin - Apex Networks (2019 - 2021 | 2 yrs): Automated server provisioning with Ansible, Linux shell scripts, and monitored services using Prometheus and Grafana."
                    },
                    "Bachelor of Science in Computer Engineering - University of Illinois (2019)"
            );

            log.info("5 sample resumes (PDF/DOCX) created successfully in {}", targetDirectory);
        } catch (Exception e) {
            log.warn("Could not write physical sample files to disk: {}", e.getMessage());
        }
    }

    private void generatePdf(File file, String name, String contact, String summary,
                            String[] skills, String[] experience, String education) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
                float y = 740;

                // Name
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                stream.newLineAtOffset(50, y);
                stream.showText(name);
                stream.endText();
                y -= 20;

                // Contact
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA, 10);
                stream.newLineAtOffset(50, y);
                stream.showText(contact);
                stream.endText();
                y -= 25;

                // Summary
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                stream.newLineAtOffset(50, y);
                stream.showText("PROFESSIONAL SUMMARY");
                stream.endText();
                y -= 15;

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA, 10);
                stream.newLineAtOffset(50, y);
                stream.showText(summary);
                stream.endText();
                y -= 25;

                // Skills
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                stream.newLineAtOffset(50, y);
                stream.showText("TECHNICAL SKILLS");
                stream.endText();
                y -= 15;

                for (String sk : skills) {
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA, 9);
                    stream.newLineAtOffset(50, y);
                    stream.showText("• " + sk);
                    stream.endText();
                    y -= 14;
                }
                y -= 10;

                // Experience
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                stream.newLineAtOffset(50, y);
                stream.showText("PROFESSIONAL EXPERIENCE");
                stream.endText();
                y -= 15;

                for (String exp : experience) {
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA, 9);
                    stream.newLineAtOffset(50, y);
                    String text = exp.length() > 95 ? exp.substring(0, 95) + "..." : exp;
                    stream.showText("• " + text);
                    stream.endText();
                    y -= 20;
                }
                y -= 10;

                // Education
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                stream.newLineAtOffset(50, y);
                stream.showText("EDUCATION");
                stream.endText();
                y -= 15;

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA, 10);
                stream.newLineAtOffset(50, y);
                stream.showText(education);
                stream.endText();
            }

            doc.save(file);
        }
    }

    private void generateDocx(File file, String name, String contact, String summary,
                             String[] skills, String[] experience, String education) throws IOException {
        try (XWPFDocument doc = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(file)) {

            // Header Name
            XWPFParagraph titlePara = doc.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(name);
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("Arial");

            // Contact
            XWPFParagraph contactPara = doc.createParagraph();
            XWPFRun contactRun = contactPara.createRun();
            contactRun.setText(contact);
            contactRun.setFontSize(10);
            contactRun.setColor("555555");

            // Summary
            XWPFParagraph summaryHeader = doc.createParagraph();
            XWPFRun shRun = summaryHeader.createRun();
            shRun.setText("PROFESSIONAL SUMMARY");
            shRun.setBold(true);
            shRun.setFontSize(12);

            XWPFParagraph summaryPara = doc.createParagraph();
            XWPFRun sumRun = summaryPara.createRun();
            sumRun.setText(summary);
            sumRun.setFontSize(10);

            // Skills
            XWPFParagraph skillsHeader = doc.createParagraph();
            XWPFRun skhRun = skillsHeader.createRun();
            skhRun.setText("TECHNICAL SKILLS");
            skhRun.setBold(true);
            skhRun.setFontSize(12);

            for (String sk : skills) {
                XWPFParagraph skPara = doc.createParagraph();
                XWPFRun skRun = skPara.createRun();
                skRun.setText("• " + sk);
                skRun.setFontSize(10);
            }

            // Experience
            XWPFParagraph expHeader = doc.createParagraph();
            XWPFRun exhRun = expHeader.createRun();
            exhRun.setText("WORK EXPERIENCE");
            exhRun.setBold(true);
            exhRun.setFontSize(12);

            for (String exp : experience) {
                XWPFParagraph expPara = doc.createParagraph();
                XWPFRun expRun = expPara.createRun();
                expRun.setText("• " + exp);
                expRun.setFontSize(10);
            }

            // Education
            XWPFParagraph eduHeader = doc.createParagraph();
            XWPFRun edhRun = eduHeader.createRun();
            edhRun.setText("EDUCATION");
            edhRun.setBold(true);
            edhRun.setFontSize(12);

            XWPFParagraph eduPara = doc.createParagraph();
            XWPFRun eduRun = eduPara.createRun();
            eduRun.setText(education);
            eduRun.setFontSize(10);

            doc.write(out);
        }
    }
}
