package com.hquyyp.domain.query;


public class MapQueryEntity extends BaseQueryEntity{
    private String name;

    public MapQueryEntity() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public MapQueryEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
