package com.alexportfolio.webFace.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="jobs")
public class JobCard{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String company;
    String description;
    String url;
    String notes;
    @Column(name="time_stamp")
    LocalDateTime timeStamp;
    LocalDateTime applied;
    public JobCard() {
    }

    public JobCard(String title, String company, String description, String url, String notes, LocalDateTime timeStamp, LocalDateTime applied) {
        this.title = title;
        this.company = company;
        this.description = description;
        this.url = url;
        this.notes = notes;
        this.timeStamp = timeStamp;
        this.applied = applied;
    }

    public JobCard(String title, String company) {
        this.title = title;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public JobCard setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LocalDateTime getApplied() {
        return applied;
    }

    public void setApplied(LocalDateTime applied) {
        this.applied = applied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobCard jobCard = (JobCard) o;
        return Objects.equals(title, jobCard.title) && Objects.equals(company, jobCard.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, company);
    }

    @Override
    public String toString() {
        return  "\n========================================\n" +
                "JobCard{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", notes='" + notes + '\'' +
                ", date_stamp=" + timeStamp +
                '}';
    }
}
