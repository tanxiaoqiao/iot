package com.honeywell.fireiot.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName Location
 * @Description TODO
 * @Author monica Z
 * @Date 12/4/2018 3:27 PM
 **/
@Entity
@Table(name = "location")
@Data
@DynamicUpdate
public class Location implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private Long parentId;
    @Column
    private Integer level;
    @Column
    private String name;
    @Column
    private String fullName;
    @Column
    private Integer type;
    @Column
    private Float lat;
    @Column
    private Float lng;
    @Column
    private String safetyContact;
    @Column
    private String safetyOfficer;
    @Column
    private String address;
    @Column
    private byte[] polyline;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleteTime;
    @Transient
    private List<Location> children;

    public String getBuilding(){
        if(fullName != null){
            String[] ss = fullName.split("/");
            if(ss.length >= 3){
                return ss[2];
            }
        }
        return null;
    }

    public String getFloor(){
        if(fullName != null){
            String[] ss = fullName.split("/");
            if(ss.length >= 4){
                return ss[3];
            }
        }
        return null;
    }

    @OneToOne(fetch=FetchType.LAZY)
    private LocationMap locationMap;


}
