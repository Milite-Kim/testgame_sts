package com.milite.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.milite.dto.SkillDto;
import com.milite.mapper.SkillMapper;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class SkillServiceImpl implements SkillService {
	@Setter(onMethod_ = @Autowired)
	private SkillMapper mapper;

	@Override
	public SkillDto getSkillInfo(String SkillID) {
		return mapper.getSkillInfo(SkillID);
	}
}
