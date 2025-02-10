package com.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import com.product.dto.ProductDto;
import com.product.model.Product;


@Mapper(componentModel = "spring")
public interface ProductMapper {
	
	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateProduceFromDto(ProductDto dto, @MappingTarget Product entity);

}
