package com.practise.furn_land.data.models

enum class Field(var response: Response) {
    NAME(Response.Success),
    EMAIL(Response.Success),
    MOBILE(Response.Success),
    PASSWORD(Response.Success),
    CONFIRM_PASSWORD(Response.Success),
    ALL(Response.Success)
}