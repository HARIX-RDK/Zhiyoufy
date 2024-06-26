package com.example.zhiyoufy.server.elasticsearch;

import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class JmsJobRunResultFullMapping {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private  ElasticsearchOperations operations;

    public JmsJobRunResultFullMapping(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public void checkJmsJobRunResultFullMapping() {

        var indexOperations = operations.indexOps(JmsJobRunResultFull.class);

        if (indexOperations.exists()) {
            log.info("checking if mapping for JmsJobRunResultFull changed");

            var mappingFromEntity = indexOperations.createMapping();
            var mappingFromEntityNode = objectMapper.valueToTree(mappingFromEntity);
            var mappingFromIndexNode = objectMapper.valueToTree(indexOperations.getMapping());

            if (!mappingFromEntityNode.equals(mappingFromIndexNode)) {
                log.info("mapping for class JmsJobRunResultFull changed!");
                indexOperations.putMapping(mappingFromEntity);
            }
        }
    }
}
