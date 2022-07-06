package com.igarashiisrael.dscatalog.services;

import com.igarashiisrael.dscatalog.dto.ProductDTO;
import com.igarashiisrael.dscatalog.entities.Category;
import com.igarashiisrael.dscatalog.entities.Product;
import com.igarashiisrael.dscatalog.repositories.CategoryRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long NonExistingId;
    private long dependentId;
    private Product product;
    private Category category;
    private ProductDTO productDTO;
    private PageImpl <Product> page;


    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        NonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(NonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getOne(existingId)).thenReturn(product);
        Mockito.when(repository.getOne(NonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(NonExistingId)).thenThrow(EntityNotFoundException.class);


        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(NonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    public void updateShouldThrowResultNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ProductDTO result = service.update(NonExistingId, productDTO);
        });

    }
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.update(existingId, productDTO);
        Assertions.assertNotNull(result);

    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ProductDTO result = service.findById(NonExistingId);
        });
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists(){
        ProductDTO result = service.findById(existingId);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findAllPagedShouldReturnPage(){

        Pageable pageable = PageRequest.of(0,10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
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