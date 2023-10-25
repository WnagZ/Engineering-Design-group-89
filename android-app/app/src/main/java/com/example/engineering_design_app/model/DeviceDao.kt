package com.example.engineering_design_app.model;

import androidx.room.*

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device")
    fun getAll(): MutableList<Device>

    @Query("SELECT * FROM device WHERE id IN (:deviceIds)")
    fun loadAllByIds(deviceIds: IntArray): MutableList<Device>

    @Query("SELECT * FROM device WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Device

    @Update
    fun updateWaterUsage(device: Device)

    @Insert
    fun insertAll(vararg devices: Device)

    @Delete
    fun delete(device: Device)

    @Query("DELETE FROM device")
    fun deleteAll()
}