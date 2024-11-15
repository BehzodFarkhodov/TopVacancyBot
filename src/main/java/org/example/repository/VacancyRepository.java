package org.example.repository;

import org.example.model.Vacancy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VacancyRepository extends BaseRepository<Vacancy> {
    public VacancyRepository() {
        super.path = "src/main/resources/vacancy.json";
        super.type = Vacancy.class;
    }
    public List<Vacancy> findByIds(Set<UUID> ids) {
        List<Vacancy> allVacancies = getAll();
        List<Vacancy> selectedVacancies = new ArrayList<>();
        for (Vacancy vacancy : allVacancies) {
            if (ids.contains(vacancy.getId())) {
                selectedVacancies.add(vacancy);
            }
        }
        return selectedVacancies;
    }

}
