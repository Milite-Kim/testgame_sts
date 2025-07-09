package com.milite.mapper;

import com.milite.dto.BaseStatDto;
import com.milite.dto.ComStatusDto;

public interface ComStatusMapper {
	public ComStatusDto getInfo(Integer id);
	public BaseStatDto getBase(String race);
	public void updateStatus(ComStatusDto dto);
	public void resetChar(Integer id);
}
