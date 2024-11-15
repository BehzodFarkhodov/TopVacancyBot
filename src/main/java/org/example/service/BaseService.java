package org.example.service;

import org.example.model.BaseModel;
import org.example.repository.BaseRepository;

import java.util.ArrayList;

public abstract class BaseService <T extends BaseModel,R extends BaseRepository<T>> {
protected R repository;

    public BaseService(R repository) {
        this.repository = repository;
    }

    public ArrayList<T> getAll() {
        return repository.getAll();
    }

    public T add(T t) {
        return repository.save(t);
    }

}
