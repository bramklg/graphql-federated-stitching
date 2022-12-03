package com.example.graphql.productservice

import liquibase.sql.visitor.AppendSqlVisitor
import liquibase.sql.visitor.PrependSqlVisitor
import liquibase.sql.visitor.RegExpReplaceSqlVisitor
import liquibase.sql.visitor.ReplaceSqlVisitor
import org.springframework.aot.hint.ExecutableMode
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints

@SpringBootApplication
@ImportRuntimeHints(ProductServiceRuntimeHelper::class)
class ProductServiceApplication

fun main(args: Array<String>) {
    runApplication<ProductServiceApplication>(*args)
}

/**
 * Attempt to type hint the native compiler and get this working, not enough
 */
class ProductServiceRuntimeHelper : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints
            .reflection()
            .registerType(AppendSqlVisitor::class.java)
            .registerType(PrependSqlVisitor::class.java)
            .registerType(RegExpReplaceSqlVisitor::class.java)
            .registerType(ReplaceSqlVisitor::class.java)

        hints
            .reflection()
            .registerConstructor(AppendSqlVisitor::class.java.getDeclaredConstructor(), ExecutableMode.INVOKE)
            .registerConstructor(PrependSqlVisitor::class.java.getDeclaredConstructor(), ExecutableMode.INVOKE)
            .registerConstructor(RegExpReplaceSqlVisitor::class.java.getDeclaredConstructor(), ExecutableMode.INVOKE)
            .registerConstructor(ReplaceSqlVisitor::class.java.getDeclaredConstructor(), ExecutableMode.INVOKE)

        hints
            .resources()
            .registerPattern("db/*.xml")
            .registerPattern("graphql/*.graphqls")
    }

}
