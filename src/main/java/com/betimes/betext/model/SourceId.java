package com.betimes.betext.model;

import java.io.Serializable;
import java.util.Objects;

public class SourceId implements Serializable {
    private String source_id;
    private String created_by;

    public SourceId() { }

    public SourceId(String source_id, String created_by) {
        this.source_id = source_id;
        this.created_by = created_by;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceId sourceId = (SourceId) o;
        return source_id.equals(sourceId.source_id) && created_by.equals(sourceId.created_by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source_id, created_by);
    }
}
