package org.example.service;

import org.example.model.Vacancy;
import org.example.repository.VacancyRepository;

import java.util.*;
import java.util.stream.Collectors;

public class VacancyService extends BaseService<Vacancy, VacancyRepository> {
    public VacancyService(VacancyRepository repository) {
        super(repository);
    }
    public List<Vacancy> getVacanciesByRegion(String region) {
        return getAll().stream()
                .filter(vacancy -> Objects.equals(vacancy.getRegion(), region))
                .collect(Collectors.toList());
    }

    public void update(Vacancy updated) {
        ArrayList<Vacancy> all = repository.getAll();
        Integer i = 0;
        for (Vacancy vacancy : all) {
            if (Objects.equals(vacancy.getId(), updated.getId())) {
                all.set(i, updated);
                break;
            }
            i++;
        }
        repository.writeData(all);
    }
    public List<Vacancy> getVacanciesByIds(Set<UUID> ids) {
        return repository.findByIds(ids);
    }

    public Vacancy getStartsVacancy() {
        for (Vacancy vacancy : getAll()) {
            if(Objects.equals(vacancy.isFinished(),false)){
                return vacancy;
            }
        }
        return null;
    }
}
