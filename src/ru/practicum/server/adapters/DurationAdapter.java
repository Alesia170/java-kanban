package ru.practicum.server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(duration.toMinutes());
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();

        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return Duration.ofMinutes(Long.parseLong(value));
        } catch (NumberFormatException e) {
            throw new IOException("Некорректный формат duration: " + value, e);
        }
    }
}
