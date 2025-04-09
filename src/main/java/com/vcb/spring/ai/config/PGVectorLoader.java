package com.vcb.spring.ai.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class PGVectorLoader {

    @Value("classpath:pdf/Indian-constitution.pdf")
    private Resource pdfresource;

    private final VectorStore vectorStore;

    private final JdbcClient jdbcClient;

    public PGVectorLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init(){

        Integer count = jdbcClient.sql("select COUNT(*) from vector_store")
                .query(Integer.class)
                .single();

        System.out.println("No. of Documents in the Vector store = "+count);

        if (count == 0){
            PdfDocumentReaderConfig config =
                    PdfDocumentReaderConfig.builder()
                            .withPagesPerDocument(1)
                            .build();

            PagePdfDocumentReader reader =
                    new PagePdfDocumentReader(pdfresource, config);

            var textSplitter = new TokenTextSplitter();

            vectorStore.accept(textSplitter.apply(reader.get()));
            System.out.println("Application is started and ready to Serve!!");
        }
    }
}
