package com.davidmerchan.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val city: String,
    val avatar: String,
    val email: String,
    val phone: String,
    @ColumnInfo(name = "user_name")
    val userName: String,
    val website: String,
    val lat: String,
    val lng: String,
    val street: String,
    val suite: String,
    @ColumnInfo(name = "zip_code")
    val zipCode: String,
    val bs: String,
    @ColumnInfo(name = "catch_phrase")
    val catchPhrase: String,
    @ColumnInfo(name = "company_name")
    val companyName: String,
)
