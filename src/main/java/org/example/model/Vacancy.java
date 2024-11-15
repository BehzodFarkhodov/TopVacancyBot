package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.example.repository.BaseRepository;
import org.jvnet.hk2.annotations.Service;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Vacancy extends BaseModel {
private String title;
private String company;
private String experience;
private String region;
private String phoneNumberCompany;
private String salary;
private String photoUrl;
private boolean isFinished = false;


    public Vacancy(String creatingVacancyTitle, String creatingVacancyCompany, String creatingVacancyExperience, String creatingVacancySalary, String creatingVacancyPhone) {
        super();
    }

    public Vacancy(String title, String company, String experience, String salary, String phoneNumberCompany, String region) {
        this.title = title;
        this.company = company;
        this.experience = experience;
        this.salary = salary;
        this.phoneNumberCompany = phoneNumberCompany;
        this.region = region;

    }


}
