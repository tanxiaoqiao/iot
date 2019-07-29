package com.honeywell.fireiot.fire.repository;

import com.honeywell.fireiot.fire.entity.MTGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:51 PM 12/17/2018
 */
@Repository
public interface MTGatewayRepository extends JpaRepository<MTGateway, Long>, JpaSpecificationExecutor<MTGateway> {

    MTGateway findFirstByIp(String ip);
}
