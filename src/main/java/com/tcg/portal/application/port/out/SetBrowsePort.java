package com.tcg.portal.application.port.out;

import com.tcg.portal.domain.model.MagicSet;
import com.tcg.portal.domain.model.SetCardPage;

import java.util.List;

public interface SetBrowsePort {
    List<MagicSet> findAllSets();
    SetCardPage findCardsBySetCode(String setCode, int page);
}
