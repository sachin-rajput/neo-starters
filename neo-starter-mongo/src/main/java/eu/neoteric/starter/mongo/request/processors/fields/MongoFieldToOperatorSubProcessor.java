package eu.neoteric.starter.mongo.request.processors.fields;

import com.google.common.collect.ImmutableList;
import eu.neoteric.starter.mongo.request.FieldMapper;
import eu.neoteric.starter.request.RequestField;
import eu.neoteric.starter.request.RequestObjectType;
import eu.neoteric.starter.request.RequestOperator;
import eu.neoteric.starter.mongo.request.Mappings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@Slf4j
public class MongoFieldToOperatorSubProcessor implements MongoFieldSubProcessor<RequestOperator> {

    private final DateTimeFormatter dateTimeFormatter;

    private final List<OperatorValueParser> operatorValueParsers = ImmutableList.of(
            new ZonedDateTimeValueParser(), new GeneralOperatorValueParser());

    public MongoFieldToOperatorSubProcessor(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Boolean apply(RequestObjectType key) {
        return RequestObjectType.OPERATOR.equals(key);
    }

    @Override
    // TODO : how to check value, it can be String, Boolean, Integer, List !!, Double and maybe more
    public Criteria build(RequestField field, RequestOperator operator, Object operatorValue, FieldMapper fieldMapper) {
        String remappedName = fieldMapper.get(field.getFieldName());
        Criteria fieldCriteria = Criteria.where(remappedName);

        Object parsedValue = operatorValueParsers.stream()
                .filter(operatorValueParser -> operatorValueParser.apply(operatorValue))
                .findFirst()
                .map(operatorValueParser -> operatorValueParser.parse(operatorValue)).get();
        return Mappings.OPERATORS.get(operator.getOperator()).apply(fieldCriteria, parsedValue);
    }

    interface OperatorValueParser {
        boolean apply(Object operatorValue);

        Object parse(Object operatorValue);
    }

    class ZonedDateTimeValueParser implements OperatorValueParser {

        @Override
        public boolean apply(Object operatorValue) {
            try {
                dateTimeFormatter.parse(operatorValue.toString());
                return true;
            } catch (DateTimeParseException ex) {
                LOG.trace("Unable to parse DateTime. ZonedDateTimeValueParser won't apply.", ex);
                return false;
            }
        }

        @Override
        public Object parse(Object operatorValue) {
            TemporalAccessor temporalAccessor = dateTimeFormatter.parse(operatorValue.toString());
            return ZonedDateTime.from(temporalAccessor);
        }
    }

    class GeneralOperatorValueParser implements OperatorValueParser {

        @Override
        public boolean apply(Object operatorValue) {
            return true;
        }

        @Override
        public Object parse(Object operatorValue) {
            return operatorValue;
        }
    }
}