package com.honeywell.fireiot.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:11 PM 4/22/2019
 */
@Entity
@Table(name = "location_map")
@Data
@DynamicUpdate
public class LocationMap extends BaseEntity<LocationMap>{

    private String mapFile;

    @OneToOne(mappedBy = "locationMap")
    private Location location;

    public LocationMap(String mapFile) {
        this.mapFile = mapFile;
    }

    public LocationMap() {
    }
}
