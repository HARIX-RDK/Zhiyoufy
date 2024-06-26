package com.example.zhiyoufy.server.elasticsearch;

import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultFull;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class JmsJobChildRunResultFullMapping {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private  ElasticsearchOperations operations;

    public JmsJobChildRunResultFullMapping(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public void checkJmsJobChildRunResultFullMapping() {

        var indexOperations = operations.indexOps(JmsJobChildRunResultFull.class);

        if (indexOperations.exists()) {
            log.info("checking if mapping for JmsJobChildRunResultFull changed");

            var mappingFromEntity = indexOperations.createMapping();
            var mappingFromEntityNode = objectMapper.valueToTree(mappingFromEntity);
            var mappingFromIndexNode = objectMapper.valueToTree(indexOperations.getMapping());

            if (!mappingFromEntityNode.equals(mappingFromIndexNode)) {
                log.info("mapping for class JmsJobChildRunResultFull changed!");
                indexOperations.putMapping(mappingFromEntity);
            }
        }
    }
}
