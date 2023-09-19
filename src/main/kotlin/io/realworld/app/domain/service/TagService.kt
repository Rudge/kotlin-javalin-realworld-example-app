package io.realworld.app.domain.service

import io.realworld.app.domain.TagDTO
import io.realworld.app.domain.repository.TagRepository

class TagService(private val tagRepository: TagRepository) {
    fun findAll(): TagDTO {
        tagRepository.findAll().let { tags ->
            return TagDTO(tags)
        }
    }
}
