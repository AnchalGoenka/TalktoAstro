package com.mooveall.driver.base.appUpdate

data class AppVersionResponse(
        var data: ArrayList<DataCurrentApiVersion>)

data class DataCurrentApiVersion(
        var user_version: Int,
        var astro_version:Int
)
