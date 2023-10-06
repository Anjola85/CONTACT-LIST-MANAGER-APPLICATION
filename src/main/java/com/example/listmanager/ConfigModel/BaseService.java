package com.example.listmanager.ConfigModel;


import com.example.listmanager.util.dto.ServiceResult;

import java.util.UUID;

public interface BaseService<T> {
    ServiceResult<T> create(T dto);

    ServiceResult<T> findAll();

    ServiceResult<T> findById(UUID id);

    ServiceResult<T> update(UUID id, T dto);
}