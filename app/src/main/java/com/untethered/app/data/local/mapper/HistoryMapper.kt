package com.untethered.app.data.local.mapper

import com.untethered.app.data.local.entity.CommandHistoryEntity
import com.untethered.app.domain.model.CommandHistory

fun CommandHistoryEntity.toDomain() = CommandHistory(
    id = id,
    command = command,
    timestamp = timestamp
)

fun CommandHistory.toEntity() = CommandHistoryEntity(
    id = id,
    command = command,
    timestamp = timestamp
)