package com.igarashiisrael.dscatalog.services;

import com.igarashiisrael.dscatalog.entities.Product;
import com.igarashiisrael.dscatalog.repositories.ProductRepository;
import com.igarashiisrael.dscatalog.services.exceptions.DatabaseException;
import com.igarashiisrael.dscatalog.services.exceptions.ResourceNotFoundException;
import com.igarashiisrael.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingId;
    private long NonExistingId;
    private long dependentId;
    private PageImpl <Product> page;
    private Product product;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        NonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(NonExistingId)).thenReturn(Optional.empty());

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(NonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){
        Assertions.assertThrows(DatabaseException.class, ()->{
            service.delete(dependentId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            service.delete(NonExistingId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(NonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists(){
        Assertions.assertDoesNotThrow(() -> {
            service.delete((existingId));
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }


}
