package com.db.dataplatform.techtest.server.persistence.convertor;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.beans.PropertyEditorSupport;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class BlockTypeConverter extends PropertyEditorSupport implements AttributeConverter<BlockTypeEnum, String> {
    @Override
    public String convertToDatabaseColumn(BlockTypeEnum blockTypeEnum) {
        if (blockTypeEnum == null) {
            return null;
        }
        return blockTypeEnum.getType();
    }

    @Override
    public BlockTypeEnum convertToEntityAttribute(String type) {
        if (type == null) {
            return null;
        }
        return Stream.of(BlockTypeEnum.values())
                .filter(c -> c.getType().equals(type))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public void setAsText(final String text) throws IllegalArgumentException {
        setValue(BlockTypeEnum.fromValue(text));
    }
}
