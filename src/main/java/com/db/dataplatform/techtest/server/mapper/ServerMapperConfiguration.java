package com.db.dataplatform.techtest.server.mapper;

import com.db.dataplatform.techtest.server.dto.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerMapperConfiguration {

    @Bean
    public ModelMapper createModelMapperBean() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true);

        Converter<BlockTypeEnum, String> enumConverter =
                ctx -> ctx.getSource() == null ? null : ctx.getSource().getType();

        PropertyMap<DataHeaderEntity, DataHeader> blockEnumMap =
                new PropertyMap<DataHeaderEntity, DataHeader>() {
                    protected void configure() {
                        using(enumConverter).map(source.getBlocktype()).setBlocktype(null);
                    }
                };

        modelMapper.addMappings(blockEnumMap);

        return modelMapper;
    }
}
