package com.vcb.spring.ai.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

//@Configuration
public class VectorLoader {

    @Value("classpath:pdf/Indian-constitution.pdf")
    private Resource pdfresource;

    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        File vectorStoreFile =
                new File("G:\\FullStack\\Generative-AI\\spring-ai\\src\\main\\resources\\vector_store.json");
        if (vectorStoreFile.exists()){
            System.out.println("Loaded vector store file!");
        } else {
            System.out.println("Creating Vector Store!");

            PdfDocumentReaderConfig config =
                    PdfDocumentReaderConfig.builder()
                            .withPagesPerDocument(1)
                            .build();

            PagePdfDocumentReader reader =
                    new PagePdfDocumentReader(pdfresource, config);

            var textSplitter = new TokenTextSplitter();
            List<Document> docs = textSplitter.apply(reader.get());

            vectorStore.add(docs);
            vectorStore.save(vectorStoreFile);

            System.out.println("Vector store created!");

        }

        return vectorStore;
    }
}
