package com.hquyyp.domain.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import java.util.Date;

@Builder
@ApiModel(value = "Map参数")
public class MapEntity {
    private String id;
    private String path;
    private Double leftTopLng;
    private Double leftTopLat;
    private Double rightDownLng;
    private Double rightDownLat;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    private String remark;
    private String name;

    public MapEntity(String id) {
        this.id = id;
    }

    public MapEntity(String id, String path, Double leftTopLng, Double leftTopLat, Double rightDownLng, Double rightDownLat, Date time, String remark, String name) {
        this.id = id;
        this.path = path;
        this.leftTopLng = leftTopLng;
        this.leftTopLat = leftTopLat;
        this.rightDownLng = rightDownLng;
        this.rightDownLat = rightDownLat;
        this.time = time;
        this.remark = remark;
        this.name = name;
    }

    public MapEntity() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = (id == null) ? null : id.trim();
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = (path == null) ? null : path.trim();
    }

    public Double getLeftTopLng() {
        return this.leftTopLng;
    }

    public void setLeftTopLng(Double leftTopLng) {
        this.leftTopLng = leftTopLng;
    }

    public Double getLeftTopLat() {
        return this.leftTopLat;
    }

    public void setLeftTopLat(Double leftTopLat) {
        this.leftTopLat = leftTopLat;
    }

    public Double getRightDownLng() {
        return this.rightDownLng;
    }

    public void setRightDownLng(Double rightDownLng) {
        this.rightDownLng = rightDownLng;
    }

    public Double getRightDownLat() {
        return this.rightDownLat;
    }

    public void setRightDownLat(Double rightDownLat) {
        this.rightDownLat = rightDownLat;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = (remark == null) ? null : remark.trim();
    }
}