package com.honeywell.fireiot.service;

public interface SpotAsynService {

    void createQrImage(long id, String name, String fullName) throws Exception;

    void deleteRelatedTask(long spotId);
}
