package com.untethered.app.data.local.mapper

import com.untethered.app.data.local.entity.CommandSnippetEntity
import com.untethered.app.domain.model.CommandSnippet

fun CommandSnippetEntity.toDomain() = CommandSnippet(
    id = id,
    command = command,
    label = label,
    timestamp = timestamp
)

fun CommandSnippet.toEntity() = CommandSnippetEntity(
    id = id,
    command = command,
    label = label,
    timestamp = timestamp
)