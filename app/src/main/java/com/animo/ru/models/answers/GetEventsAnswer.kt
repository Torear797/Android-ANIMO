package com.animo.ru.models.answers

import com.animo.ru.models.Event

data class GetEventsAnswer(
    val data: MutableMap<Int, Event>
) : BaseAnswer()