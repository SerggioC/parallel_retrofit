package com.sergiocruz.parallelretrofit.model


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_table")
data class User(

    @Ignore
    @SerializedName("address")
    var address: Address?,

    @Ignore
    @SerializedName("company")
    var company: Company?,

    @SerializedName("email")
    var email: String?,

    @PrimaryKey
    @SerializedName("id")
    var id: Int?,

    @SerializedName("name")
    var name: String?,

    @SerializedName("phone")
    var phone: String?,

    @SerializedName("username")
    var username: String?,

    @SerializedName("website")
    var website: String?
) {
    constructor() : this(null, null, "", 0, "", "", "", "")
}