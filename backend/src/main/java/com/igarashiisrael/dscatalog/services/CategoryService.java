package com.igarashiisrael.dscatalog.services;

import com.igarashiisrael.dscatalog.dto.CategoryDTO;
import com.igarashiisrael.dscatalog.entities.Category;
import com.igarashiisrael.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> list = repository.findAll();

        return list.stream().map(x-> new CategoryDTO(x)).collect(Collectors.toList());

    }
}
