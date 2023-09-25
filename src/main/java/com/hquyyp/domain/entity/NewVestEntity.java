package com.hquyyp.domain.entity;

import com.hquyyp.domain.vo.NewVestView;
import lombok.Builder;

@Builder
public class NewVestEntity implements Comparable<NewVestView> {

    /**
     * 人员编号
     */
    private String id;

    /**
     * 人员姓名
     */
    private String name;

    /**
     * 人员性别
     */
    private String gender;

    /**
     * 年龄
     */
    private String age;

    /**
     * 所属单位
     */
    private String unit;

    /**
     * 装备套装编号(device EUI)
     */
    private String equipment;

    public NewVestEntity() {
    }

    public NewVestEntity(String id, String name, String gender, String age, String unit, String equipment) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.unit = unit;
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return "NewVestEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", unit='" + unit + '\'' +
                ", equipment=" + equipment +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    @Override
    public int compareTo(NewVestView o) {
        return Integer.parseInt(this.id) - Integer.parseInt(o.getId());
    }

}
