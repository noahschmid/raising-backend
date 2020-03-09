package ch.raising.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Label {
    private final int id;
    private final String name;
    private final String description;
}