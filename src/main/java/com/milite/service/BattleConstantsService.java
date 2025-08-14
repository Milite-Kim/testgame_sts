package com.milite.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.milite.mapper.BattleConstantsMapper;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class BattleConstantsService {

	@Setter(onMethod_ = @Autowired)
	private BattleConstantsMapper mapper;

	private final Map<String, Double> constantsCache = new ConcurrentHashMap<>();
	private volatile boolean isInitialized = false;

	@PostConstruct
	public void initializeConstants() {
		refreshCache();
	}

	public double getConstant(String name) {
		if (!isInitialized) {
			synchronized (this) {
				if (isInitialized) {
					refreshCache();
				}
			}
		}

		Double value = constantsCache.get(name);
		if (value == null) {
			value = mapper.getNameConstants(name);
			if (value != null) {
				constantsCache.put(name, value);
				log.info("캐시 미스 - DB에서 조회 후 캐시 업데이트: " + name + " = " + value);
			} else {
				log.error("상수를 찾을 수 없습니다: " + name);
				throw new IllegalArgumentException("상수를 찾을 수 없습니다: " + name);
			}
		}
		return value;
	}

	public int getIntConstant(String name) {
		return (int) getConstant(name);
	}

	public boolean getBooleanConstant(String name) {
		return getConstant(name) > 0;
	}

	public Map<String, Double> getTypeConstants(String type) {
		Map<String, Double> result = new HashMap<>();
		for (Map.Entry<String, Double> entry : constantsCache.entrySet()) {
			if (entry.getKey().startsWith(type.toLowerCase())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	@Scheduled(fixedRate = 300000)
	public void refreshCache() {
		try {
			List<Map<String, Object>> results = mapper.getAllConstants();
			Map<String, Double> newCache = new HashMap<>();

			for (Map<String, Object> row : results) {
				String name = (String) row.get("name");
				Double value = ((Number) row.get("value")).doubleValue();
				newCache.put(name, value);
			}

			constantsCache.clear();
			constantsCache.putAll(newCache);
			isInitialized = true;

			log.info("전투 상수 캐시 갱신 완료 : " + newCache.size() + " 개");
		} catch (Exception e) {
			log.error("전투 상수 캐시 갱신 실패", e);
		}
	}

	public void forceRefresh() {
		refreshCache();
	}
	
	public int getCacheSize() {
		return constantsCache.size();
	}
	
	public boolean hasConstant(String name) {
		return constantsCache.containsKey(name);
	}
	
	private void validdateCache(Map<String, Double> cache) {
		String[] requiredConstants = { "system_burn_damage", "system_base_dodge_roll", "system_dodge_multiplier",
				"ability_blood_suck_ratio", "artifact_element_stone_bonus" };

		for (String required : requiredConstants) {
			if (!cache.containsKey(required)) {
				throw new IllegalStateException("필수 상수가 누락됨: " + required);
			}
		}
		log.debug("캐시 검증 완료");
	}
}
