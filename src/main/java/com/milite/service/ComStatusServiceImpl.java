package com.milite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milite.dto.BaseStatDto;
import com.milite.dto.ComStatusDto;
import com.milite.mapper.ComStatusMapper;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class ComStatusServiceImpl implements ComStatusService {

	@Setter(onMethod_ = @Autowired)
	private ComStatusMapper mapper;

	@Override
	public ComStatusDto getInfo(Integer id) {
		ComStatusDto dto = mapper.getInfo(id);
		return dto;
	}

	@Override
	public void updateStatus(ComStatusDto dto) {
		mapper.updateStatus(dto);
	}

	@Override
	public void resetChar(Integer id) {
		ComStatusDto dto = mapper.getInfo(id);
		BaseStatDto base = getBase(dto.getRace());
		
		ComStatusDto resetDto = new ComStatusDto();
		
		resetDto.setId(id);
		resetDto.setName(dto.getName());
		resetDto.setRace(dto.getRace());
		resetDto.setType(dto.getType());
		resetDto.setMax_hp(base.getHp());
		resetDto.setCurr_hp(base.getHp());
		resetDto.setAtk(base.getAtk());
		resetDto.setDef(base.getDef());
		resetDto.setCri(base.getCri());
		resetDto.setAcc(base.getAcc());
		resetDto.setEva(base.getEva());
		resetDto.setAgi(base.getAgi());
		resetDto.setGold(0);
		resetDto.setLevel(1);
		resetDto.setCurr_exp(0);
		resetDto.setMax_exp(10);

		mapper.updateStatus(resetDto);
	}

	@Override
	public BaseStatDto getBase(String race) {
		BaseStatDto dto = mapper.getBase(race);
		return dto;
	}
}
