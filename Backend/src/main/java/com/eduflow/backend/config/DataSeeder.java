package com.eduflow.backend.config;

import com.eduflow.backend.model.Course;
import com.eduflow.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
        
        List<Course> coursesToSeed = Arrays.asList(
            createCourse("Mastering UI/UX Design", "Learn the principles of user-centered design.", 10),
            createCourse("Business Analytics 101", "Transform raw data into actionable business strategies.", 8),
            createCourse("Advanced Python Programming", "Dive deep into object-oriented programming.", 15),
            createCourse("Digital Marketing Strategy", "Learn to build and execute comprehensive digital marketing campaigns.", 8),
            createCourse("Full Stack Web Development", "Master front-end and back-end development.", 20),
            createCourse("Graphic Design Fundamentals", "Discover the core principles of typography, color theory, and layout.", 10),
            createCourse("Machine Learning A-Z", "A complete guide to building machine learning algorithms.", 18),
            createCourse("Product Management Masterclass", "Learn how to build products that users love.", 12),
            createCourse("Cloud Computing with AWS", "Get hands-on experience with Amazon Web Services.", 14),
            createCourse("SEO Optimization Secrets", "Rank higher on Google and drive organic traffic.", 6),
            createCourse("Financial Modeling", "Build complex financial models.", 10),
            createCourse("React Native App Dev", "Develop cross-platform mobile applications.", 15),
            createCourse("AI Ethics & Governance", "Understand the ethical implications of artificial intelligence.", 8)
        );

        int seededCount = 0;
        for (Course c : coursesToSeed) {
            // Check if course with same title exists to avoid duplicates
            boolean exists = false;
            for (Course existing : courseRepository.findAll()) {
                if (existing.getTitle().equals(c.getTitle())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                courseRepository.save(c);
                seededCount++;
            }
        }
        
        if (seededCount > 0) {
            System.out.println(seededCount + " new courses seeded for the catalog.");
        }
    }

    private Course createCourse(String title, String description, int modules) {
        Course c = new Course();
        c.setTitle(title);
        c.setDescription(description);
        c.setModuleCount(modules);
        return c;
    }
}
