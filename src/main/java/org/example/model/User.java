package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.example.enumerators.UserState;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseModel {
    private String username;
    private String lastname;
    private String number;
    private UserState state;
    private Long chatId;
    private Location location;
    private String email;

    private String resumeName;
    private String resumeAge;
    private String resumeEducation;
    private String resumeExperience;
    private String creatingVacancyTitle;
    private String creatingVacancyCompany;
    private String creatingVacancyExperience;
    private String creatingVacancySalary;
    private String creatingVacancyPhone;
    private String creatingVacancyRegion;
    private Set<UUID> appliedVacancies = new HashSet<>();

    public void addAppliedVacancy(UUID vacancyId) {
        this.appliedVacancies.add(vacancyId);
    }




}
