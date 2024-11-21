package ch.hslu.swda.common.database;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class OffsetDateTimeCodec implements Codec<OffsetDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public void encode(BsonWriter bsonWriter, OffsetDateTime offsetDateTime, EncoderContext encoderContext) {
        if (offsetDateTime == null) {
            bsonWriter.writeNull();
        } else {
            bsonWriter.writeString(offsetDateTime.format(FORMATTER));
        }
    }

    @Override
    public Class<OffsetDateTime> getEncoderClass() {
        return OffsetDateTime.class;
    }

    @Override
    public OffsetDateTime decode(BsonReader bsonReader, DecoderContext decoderContext) {
        if (bsonReader.getCurrentBsonType() == BsonType.NULL) {
            bsonReader.readNull();
            return null;
        }
        String dateTimeString = bsonReader.readString();
        try {
            return OffsetDateTime.parse(dateTimeString, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date time format: " + dateTimeString, e);
        }
    }
}
