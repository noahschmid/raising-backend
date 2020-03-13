package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class Label {
    private final long id;
    private final String name;
    private final String description;
}
