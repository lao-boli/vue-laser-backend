package com.hquyyp.domain.query;

public class RecordQueryEntity extends BaseQueryEntity {
    private String name;

    public RecordQueryEntity() {
    }

    public RecordQueryEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
