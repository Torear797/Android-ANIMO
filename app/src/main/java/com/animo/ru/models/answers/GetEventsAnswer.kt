package com.animo.ru.models.answers

import com.animo.ru.models.Event

data class GetEventsAnswer(
    var data: MutableMap<Int, Event>
) : BaseAnswer()