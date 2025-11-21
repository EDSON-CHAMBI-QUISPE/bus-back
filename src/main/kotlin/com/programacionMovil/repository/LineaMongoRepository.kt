package com.programacionMovil.repository

import com.programacionMovil.mongo.LineaDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LineaMongoRepository : MongoRepository<LineaDocument, String> {
    fun findByIdLinea(idLinea: Int): LineaDocument?
}