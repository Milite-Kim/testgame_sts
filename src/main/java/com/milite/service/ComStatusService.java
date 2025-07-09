package com.milite.service;

import com.milite.dto.BaseStatDto;
import com.milite.dto.ComStatusDto;

public interface ComStatusService {
	public ComStatusDto getInfo(Integer id);

	public BaseStatDto getBase(String race);

	public void updateStatus(ComStatusDto dto);

	public void resetChar(Integer id);
}
