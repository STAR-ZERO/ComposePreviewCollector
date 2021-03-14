package com.star_zero.compose.previewcollector.processor

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Visibility

class PreviewCollectorProcessor : SymbolProcessor {
    private lateinit var codeGenerator: CodeGenerator
    private lateinit var logger: KSPLogger

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.codeGenerator = codeGenerator
        this.logger = logger
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(PREVIEW_ANNOTATION_CLASS)
        val previewFunctions = symbols.filterIsInstance<KSFunctionDeclaration>()
            .filter { it.getVisibility() != Visibility.PRIVATE }

        if (previewFunctions.isNotEmpty()) {
            generateCode(previewFunctions)
        }

        return emptyList()
    }

    private fun generateCode(previewFunctions: List<KSFunctionDeclaration>) {
        val deps = Dependencies(true, *previewFunctions.map { it.containingFile!! }.toTypedArray())
        val file = codeGenerator.createNewFile(deps, GENERATE_PACKAGE_NAME, GENERATE_FILE_NAME)

        val previewsCode = previewFunctions.joinToString("\n") {
            """
            ComposePreview(
                "${it.simpleName.asString()}",
                { ${it.qualifiedName!!.asString()}() }
            ),
            """.trimIndent()
        }

        val code = """
        package $GENERATE_PACKAGE_NAME
        
        import androidx.compose.foundation.layout.Column
        import androidx.compose.foundation.layout.PaddingValues
        import androidx.compose.foundation.layout.Spacer
        import androidx.compose.foundation.layout.height
        import androidx.compose.foundation.lazy.LazyColumn
        import androidx.compose.foundation.lazy.items
        import androidx.compose.material.Text
        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.text.font.FontWeight
        import androidx.compose.ui.unit.dp
        import androidx.compose.ui.unit.sp

        private data class ComposePreview(
            val name: String,
            val function: @Composable () -> Unit
        )

        private val previews = listOf<ComposePreview>(
            $previewsCode
        )

        @Composable
        fun PreviewCollection() {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                )
            ) {
                items(previews) { preview ->
                    Column {
                        Text(
                            text = preview.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        preview.function.invoke()

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        """.trimIndent()

        file.write(code.toByteArray())
        file.close()
    }

    companion object {
        private const val PREVIEW_ANNOTATION_CLASS = "androidx.compose.ui.tooling.preview.Preview"
        private const val GENERATE_PACKAGE_NAME = "com.star_zero.compose.previewcolletctor"
        private const val GENERATE_FUNCTION_NAME = "PreviewCollection"
        private const val GENERATE_FILE_NAME = GENERATE_FUNCTION_NAME
    }
}
