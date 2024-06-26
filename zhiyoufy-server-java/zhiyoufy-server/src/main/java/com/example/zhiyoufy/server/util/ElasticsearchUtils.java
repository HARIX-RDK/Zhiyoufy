package com.example.zhiyoufy.server.util;

import java.util.ArrayList;
import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;

import org.springframework.data.elasticsearch.core.SearchHits;

public class ElasticsearchUtils {
	public static <T> CommonPage<T> searchHitsToCommonPage(SearchHits<T> searchHits,
			Integer pageSize, Integer pageNum) {
		CommonPage<T> result = new CommonPage<T>();

		Integer totalPage = (int)(searchHits.getTotalHits() + pageSize - 1) / pageSize;
		result.setTotalPage(totalPage);
		result.setPageNum(pageNum);
		result.setPageSize(pageSize);
		result.setTotal(searchHits.getTotalHits());

		List<T> list = new ArrayList<>();
		result.setList(list);

		for (var searchHit : searchHits.getSearchHits()) {
			list.add(searchHit.getContent());
		}

		return result;
	}
}
