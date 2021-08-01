package com.hquyyp.domain.entity;

import lombok.Builder;

@Builder
public class NewRecordEntity implements Comparable<NewRecordEntity>{
    //人员id
    private String id;

    //人员姓名
    private String name;

    //击杀数
    private Integer kill;

    //队伍
    private String team;

    //射击命中数
    private Integer shoot;

    //中弹数
    private Integer beShooted;

    public NewRecordEntity() {
    }

    public NewRecordEntity(String id, String name, Integer kill, String team, Integer shoot, Integer beShooted) {
        this.id = id;
        this.name = name;
        this.kill = kill;
        this.team = team;
        this.shoot = shoot;
        this.beShooted = beShooted;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getKill() {
        return kill;
    }

    public void setKill(Integer kill) {
        this.kill = kill;
    }

    public Integer getBeShooted() {
        return beShooted;
    }

    public void setBeShooted(Integer beShooted) {
        this.beShooted = beShooted;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Integer getShoot() {
        return shoot;
    }

    public void setShoot(Integer shoot) {
        this.shoot = shoot;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NewRecordEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", kill=" + kill +
                ", team='" + team + '\'' +
                ", shoot=" + shoot +
                ", beShooted=" + beShooted +
                '}';
    }

    @Override
    public int compareTo(NewRecordEntity o) {
        int result;
         if (this.getKill()!=o.getKill()){
              result = o.getKill()-this.getKill();
         }else if (this.getShoot()!=o.getShoot()){
              result = o.getShoot()-this.getShoot();
          }else if (this.getBeShooted()!=o.getBeShooted()){
              result = this.getBeShooted()-o.getBeShooted();
         }else {
             result=Integer.parseInt(this.getId())- Integer.parseInt(o.getId());
         }
        return result;
    }
}
