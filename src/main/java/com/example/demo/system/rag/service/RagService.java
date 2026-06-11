package com.example.demo.system.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class RagService {

    @Value("${agent.deepseek.api-key}")
    private String deepseekApiKey;

    @Value("${agent.deepseek.base-url}")
    private String deepseekBaseUrl;

    @Value("${agent.deepseek.model-name}")
    private String deepseekModelName;

    @Value("${agent.siliconflow.api-key}")
    private String siliconflowApiKey;

    private EmbeddingModel embeddingModel;
    private EmbeddingStore<TextSegment> embeddingStore;
    private OpenAiChatModel chatModel;
    private DocumentParser documentParser;
    private DocumentSplitter documentSplitter;
    private Assistant assistant;

    @PostConstruct
    public void init() {
        // Embedding 模型（硅基流动）
        this.embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(siliconflowApiKey)
                .baseUrl("https://api.siliconflow.cn/v1")
                .modelName("BAAI/bge-m3")
                .build();

        // 内存向量存储（开发阶段）
        this.embeddingStore = new InMemoryEmbeddingStore<>();

        // 对话模型（DeepSeek）
        this.chatModel = OpenAiChatModel.builder()
                .apiKey(deepseekApiKey)
                .baseUrl(deepseekBaseUrl)
                .modelName(deepseekModelName)
                .temperature(0.7)
                .build();

        // 文档解析器（Tika）
        this.documentParser = new ApacheTikaDocumentParser();

        // 分块器（512 字符，50 字符重叠）
        this.documentSplitter = DocumentSplitters.recursive(512, 50);

        // 构建 RAG Assistant
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .maxResults(5)
                        .build())
                .build();
    }

    /**
     * 上传并处理文档：解析 → 分块 → 向量化 → 存储
     */
    public void ingestDocument(String filePath) throws IOException {
        File file = new File(filePath);
        Document document = documentParser.parse(new FileInputStream(file));
        EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build()
                .ingest(document);
    }

    /**
     * RAG 问答
     */
    public String chat(String question) {
        return assistant.chat(question);
    }

    interface Assistant {
        @dev.langchain4j.service.SystemMessage("你是一个知识库助手，基于提供的参考资料回答用户问题。如果参考资料中没有相关信息，请说明你不确定。回答时请引用来源。")
        String chat(String userMessage);
    }
}
