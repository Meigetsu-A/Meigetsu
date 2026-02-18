package com.meigetsu.core.domain.repository

import com.meigetsu.core.common.Resource
import com.meigetsu.core.model.ExternalSource
import kotlinx.coroutines.flow.Flow

interface EverythingMoeRepository {
    fun getSources(): Flow<Resource<List<ExternalSource>>>
}
